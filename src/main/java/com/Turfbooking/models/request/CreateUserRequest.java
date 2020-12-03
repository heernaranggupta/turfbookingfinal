package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Getter
@Valid
public class CreateUserRequest {

    @NotEmpty(message = "Please provide name of user")
    private String name;
    private String gender;
    private LocalDate dateOfBirth;
    private String password;
    @NotEmpty(message = "Please provide country code")
    private String countryCode;
    @NotEmpty(message = "Please provide contact number")
    private String phoneNumber;
    private String emailId;
    private Double latitude;
    private Double longitude;
    private String displayImageUrl;
    @NotEmpty(message = "Please provide user role")
    private String role;

}
