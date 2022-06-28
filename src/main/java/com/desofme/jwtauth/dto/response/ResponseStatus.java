package com.desofme.jwtauth.dto.response;

import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatus {
    private Integer code;
    private String message;


    public static ResponseStatus getSuccess(){
        return ResponseStatus.builder()
                .code(StatusCode.SUCCESS)
                .message(StatusMessage.SUCCESS)
                .build();
    }
}
