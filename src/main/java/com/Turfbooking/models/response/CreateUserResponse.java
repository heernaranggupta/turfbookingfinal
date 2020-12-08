package com.Turfbooking.models.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserResponse {

    //    private String status; // no need of status.
    private UserResponse user;
    private String token;
    private String refreshToken;

    public CreateUserResponse(UserResponse user, String token, String refreshToken) {
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }


}
