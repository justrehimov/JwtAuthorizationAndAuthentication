package com.desofme.jwtauth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleStatus<T>{
    T status;
}
