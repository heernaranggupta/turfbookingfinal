package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ValidateOtpRequest {
    @NotNull
    private String countryCode;

    @NotNull
    private String phoneNumber;
    // @ValueOfEnum(enumClass = OtpStatus.class)
    private Integer otp;
    private Boolean isBusiness;

    private Boolean isUpdate;
}
