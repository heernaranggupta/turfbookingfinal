package com.Turfbooking.models.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Data
@Valid
public class UserLoginRequest {

    @NotEmpty(message = "User name cannot be empty")
    private String username;
    @NotEmpty(message = "Password should be not empty.")
    private String password;

}
