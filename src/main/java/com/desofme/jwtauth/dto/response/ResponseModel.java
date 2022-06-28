package com.desofme.jwtauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel<T> {
    private T response;
    private ResponseStatus status;
}
