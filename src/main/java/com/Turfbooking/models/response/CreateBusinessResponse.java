package com.Turfbooking.models.response;

import com.Turfbooking.documents.Business;
import com.Turfbooking.documents.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessResponse {

    private BusinessResponse businessResponse;
    private String token;
    private String refreshToken;

    public CreateBusinessResponse(BusinessResponse businessResponse, String token, String refreshToken) {
        this.businessResponse = businessResponse;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
