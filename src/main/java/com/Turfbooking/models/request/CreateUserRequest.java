package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Valid
public class CreateUserRequest {

    @NotNull
    private String name;
    private String password;
    private String countryCode;
    @NotEmpty(message = "Please provide contact number")
    private String phoneNumber;

    private String emailId;
    private Double latitude;
    private Double longitude;

}
