package com.desofme.jwtauth.filter;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.response.ResponseStatus;
import com.desofme.jwtauth.dto.response.SingleStatus;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/auth/login") || request.getServletPath().startsWith("/auth/confirm/")
        || request.getServletPath().equals("/auth/refresh") || request.getServletPath().equals("/auth/register")
        || request.getServletPath().equals("/auth/logout")){
            filterChain.doFilter(request,response);
        }else{
            try {
                String header = request.getHeader("Authorization");
                String username = null;
                String token = null;
                if (header != null && header.contains("Bearer ")) {
                    token = header.substring(7);
                    if (!jwtManager.isValidToken(token))
                        throw new CustomException(StatusMessage.JWT_IS_NOT_VALID, StatusCode.JWT_IS_NOT_VALID);
                    username = jwtManager.getUsernameToken(token);
                    User user = userService.getUser(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } else {
                    ResponseStatus responseStatus = new ResponseStatus(StatusCode.JWT_IS_NOT_VALID, StatusMessage.JWT_IS_NOT_VALID);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),new SingleStatus<ResponseStatus>(responseStatus));
                }
            }catch (JwtException ex){
                log.error(ex.getMessage());
                ResponseStatus responseStatus = new ResponseStatus(StatusCode.JWT_HAS_EXPIRED, ex.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),new SingleStatus<ResponseStatus>(responseStatus));
            }
        }
    }
}
