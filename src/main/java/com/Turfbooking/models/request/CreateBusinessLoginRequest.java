package com.Turfbooking.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@Builder
public class CreateBusinessLoginRequest {
    @NotEmpty(message = "User name cannot be empty")
    private String username;

    @NotEmpty(message = " Password cannot be empty ")
    private String password;

}
