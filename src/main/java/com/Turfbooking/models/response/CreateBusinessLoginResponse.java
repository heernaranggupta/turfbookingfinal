package com.Turfbooking.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateBusinessLoginResponse {

    private BusinessResponse businessResponse;
    private String token;
    private String refreshToken;
}
