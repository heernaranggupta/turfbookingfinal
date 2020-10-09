package com.Turfbooking.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateUpdatePasswordRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String phoneNumber;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
