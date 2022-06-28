package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.ConfirmationToken;
import com.desofme.jwtauth.auth.RefreshToken;
import com.desofme.jwtauth.auth.Role;
import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.request.LoginRequest;
import com.desofme.jwtauth.dto.request.RefreshTokenRequest;
import com.desofme.jwtauth.dto.request.UserRequest;
import com.desofme.jwtauth.dto.response.*;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.filter.JwtManager;
import com.desofme.jwtauth.repository.ConfirmationTokenRepo;
import com.desofme.jwtauth.repository.RefreshTokenRepo;
import com.desofme.jwtauth.repository.UserRepo;
import com.desofme.jwtauth.service.AuthService;
import com.desofme.jwtauth.service.ConfirmationTokenService;
import com.desofme.jwtauth.service.RefreshTokenService;
import com.desofme.jwtauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JavaMailSender javaMailSender;
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final ConfirmationTokenService confirmationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final RefreshTokenService refreshTokenService;
    @Value("${domain}")
    private String domain;

    @Value("${refresh.expired.time}")
    private long REFRESH_EXPIRED_TIME;
    @Override
    @Transactional
    public ResponseModel<UserResponse> register(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        if(userRepo.findByUsername(user.getEmail()).isPresent())
            throw new CustomException(StatusMessage.USER_ALREADY_EXISTS, StatusCode.USER_ALREADY_EXISTS);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> authorities = new ArrayList<>();
        authorities.add(roleService.getRoleByName("USER"));
        user.setRoles(authorities);
        User savedUser = userRepo.save(user);
        ConfirmationToken token = confirmationTokenRepo.save(new ConfirmationToken(savedUser));
        sendConfirmMail(token);
        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        return ResponseModel.<UserResponse>builder()
                .response(userResponse)
                .status(ResponseStatus.getSuccess())
                .build();
    }

    @Override
    @Transactional
    public void confirm(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
        if(!confirmationTokenService.isValid(confirmationToken))
            throw new CustomException(StatusMessage.TOKEN_HAS_EXPIRED, StatusCode.TOKEN_HAS_EXPIRED);
        confirmationToken.getUser().setEnabled(true);
        confirmationTokenRepo.delete(confirmationToken);
    }

    @Override
    @Transactional
    public ResponseModel<JwtResponse> login(LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            User user = (User) auth.getPrincipal();
            String accessToken = jwtManager.generateToken(user);
            Date expiredAt = new Date(System.currentTimeMillis()+REFRESH_EXPIRED_TIME*1000);
            RefreshToken token = new RefreshToken(user, expiredAt);
            RefreshToken refreshToken = refreshTokenRepo.save(token);
            JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken.getToken(), user.getId());
            return ResponseModel.<JwtResponse>builder()
                    .response(jwtResponse)
                    .status(ResponseStatus.getSuccess())
                    .build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            throw new CustomException(StatusMessage.USERNAME_OR_PASSWORD_IS_INVALID, StatusCode.USERNAME_OR_PASSWORD_IS_INVALID);
        }
    }

    @Override
    public ResponseModel<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        User user = userRepo.findById(refreshTokenRequest.getUserId())
                .orElseThrow(()->new CustomException(StatusMessage.USER_NOT_FOUND, StatusCode.USER_NOT_FOUND));
        if(!refreshTokenService.isValidToken(refreshTokenRequest.getRefreshToken()))
            throw new CustomException(StatusMessage.REFRESH_TOKEN_IS_NOT_VALID, StatusCode.REFRESH_TOKEN_IS_NOT_VALID);
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenRequest.getRefreshToken());
        if(!refreshToken.getUser().getId().equals(user.getId()))
            throw new CustomException(StatusMessage.REFRESH_TOKEN_USER_IS_NOT_INVALID, StatusCode.REFRESH_TOKEN_USER_IS_NOT_INVALID);
        String accessToken = jwtManager.generateToken(user);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(user.getId(), accessToken, refreshToken.getToken());
        return ResponseModel.<RefreshTokenResponse>builder()
                .response(refreshTokenResponse)
                .status(ResponseStatus.getSuccess())
                .build();
    }

    @Async
    public void sendConfirmMail(ConfirmationToken confirmationToken){
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom("Desofme");
            helper.setTo(confirmationToken.getUser().getEmail());
            helper.setSubject("Confirm your mail address");
            String link = domain + "/auth/confirm/"+confirmationToken.getToken();
            helper.setText(getHtml(confirmationToken.getUser().getName(), link), true);
            javaMailSender.send(message);
        } catch (MessagingException ex){
            log.error(ex.getMessage());
            throw new CustomException(ex.getMessage(), StatusCode.EMAIL_HAS_NOT_SENT);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new CustomException(StatusMessage.INTERNAL_SERVER_ERROR, StatusCode.INTERNAL_SERVER_ERROR);
        }

    }

    public String getHtml(String name, String link){
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                "  <title>Email Confirmation</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <style type=\"text/css\">\n" +
                "  @media screen {\n" +
                "    @font-face {\n" +
                "      font-family: 'Source Sans Pro';\n" +
                "      font-style: normal;\n" +
                "      font-weight: 400;\n" +
                "      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n" +
                "    }\n" +
                "\n" +
                "    @font-face {\n" +
                "      font-family: 'Source Sans Pro';\n" +
                "      font-style: normal;\n" +
                "      font-weight: 700;\n" +
                "      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  body,\n" +
                "  table,\n" +
                "  td,\n" +
                "  a {\n" +
                "    -ms-text-size-adjust: 100%; /* 1 */\n" +
                "    -webkit-text-size-adjust: 100%; /* 2 */\n" +
                "  }\n" +
                "\n" +
                "  table,\n" +
                "  td {\n" +
                "    mso-table-rspace: 0pt;\n" +
                "    mso-table-lspace: 0pt;\n" +
                "  }\n" +
                "\n" +
                "  img {\n" +
                "    -ms-interpolation-mode: bicubic;\n" +
                "  }\n" +
                "  a[x-apple-data-detectors] {\n" +
                "    font-family: inherit !important;\n" +
                "    font-size: inherit !important;\n" +
                "    font-weight: inherit !important;\n" +
                "    line-height: inherit !important;\n" +
                "    color: inherit !important;\n" +
                "    text-decoration: none !important;\n" +
                "  }\n" +
                "  div[style*=\"margin: 16px 0;\"] {\n" +
                "    margin: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  body {\n" +
                "    width: 100% !important;\n" +
                "    height: 100% !important;\n" +
                "    padding: 0 !important;\n" +
                "    margin: 0 !important;\n" +
                "  }\n" +
                "  table {\n" +
                "    border-collapse: collapse !important;\n" +
                "  }\n" +
                "\n" +
                "  a {\n" +
                "    color: #1a82e2;\n" +
                "  }\n" +
                "\n" +
                "  img {\n" +
                "    height: auto;\n" +
                "    line-height: 100%;\n" +
                "    text-decoration: none;\n" +
                "    border: 0;\n" +
                "    outline: none;\n" +
                "  }\n" +
                "  </style>\n" +
                "\n" +
                "</head>\n" +
                "<body style=\"background-color: #e9ecef;\">\n" +
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
                "              <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"margin: 0;\">Hi " + name + ", you successfully registered.We need to confirm your email address<br>Please click confirm buton and confirm your mail adress</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\">\n" +
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tr>\n" +
                "                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" +
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                      <tr>\n" +
                "                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" +
                "                          <a href=\""+ link +"\" target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Confirm account</a>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";
        return html;
    }
}
