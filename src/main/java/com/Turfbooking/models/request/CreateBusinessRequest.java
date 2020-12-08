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

    @NotEmpty(message = "please enter username")
    private String username;
    @NotEmpty(message = "please enter password")
    private String password;
    private String phoneNumber;
    private String companyName;
    @NotEmpty(message = "please enter role")
    private String role;
}
