package com.Turfbooking.models.response;

import com.Turfbooking.models.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserLoginResponse {
    private UserResponse user;
    private String token;
    private String refreshToken;
}
