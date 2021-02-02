package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
public class CustomerProfileUpdateRequest {
    @NotNull
    private String name;
    //    private String password;
//    private String countryCode;
    @NotEmpty(message = "Please provide contact number")
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String addressLine;
    private String zipCode;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private String emailId;
    private String downloadUrl;

}
