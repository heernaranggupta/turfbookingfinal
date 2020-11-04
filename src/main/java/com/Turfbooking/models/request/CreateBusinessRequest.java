package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Valid
public class CreateBusinessRequest {

    @NotNull @NotEmpty
    private String username;
    @NotNull @NotEmpty
    private String password;
    private String phoneNumber;
    private String companyName;
    @NotNull @NotEmpty
    private String role;
}
