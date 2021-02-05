package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Valid
@Getter
@Setter
@Builder
public class CustomerProfileUpdateRequest {
    @NotNull(message = "name should be not null")
    private String name;
    @NotEmpty(message = "Please provide contact number")
    private String phoneNumber;
    @NotNull(message = "gender should be not null")
    private String gender;
    @NotNull(message = "dateOfBirth should be not null")
    private LocalDate dateOfBirth;
    @NotNull(message = "addressLine should be not null")
    private String addressLine;
    @NotNull(message = "zipCode should be not null")
    private String zipCode;
    @NotNull(message = "city should be not null")
    private String city;
    @NotNull(message = "state should be not null")
    private String state;
    @NotNull(message = "latitude should be not null")
    private Double latitude;
    @NotNull(message = "longitude should be not null")
    private Double longitude;
    @NotNull(message = "emailId should be not null")
    private String emailId;
    @NotNull(message = "downloadUrl should be not null")
    private String downloadUrl;

}
