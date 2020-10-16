package com.Turfbooking.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class CustomerProfileUpdateResponse {

    private UserResponse user;

    public CustomerProfileUpdateResponse(UserResponse user) {

        this.user = user;
    }


}
