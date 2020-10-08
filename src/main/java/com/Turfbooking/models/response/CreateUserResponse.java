package com.Turfbooking.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateUserResponse {

    private String status; // no need of status.
    private UserResponse user;
    private String token;
    private String refreshToken;

    public CreateUserResponse(UserResponse user, String status, String token, String refreshToken) {
        this.status = status;
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public CreateUserResponse(String status) {
        this.status = status;

    }


}
