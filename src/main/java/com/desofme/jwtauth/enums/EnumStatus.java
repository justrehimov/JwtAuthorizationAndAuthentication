package com.desofme.jwtauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumStatus {
    ACTIVE(1), DEACTIVE(0);
    private final Integer value;
}
