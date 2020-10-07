package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class GenerateOtpRequest {

    @NotNull
    private String countryCode;
    @NotNull(message = "Phone Number Required")
    private String phoneNumber;
    private Boolean isUpdate;

}
