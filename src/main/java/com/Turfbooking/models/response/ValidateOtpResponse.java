package com.Turfbooking.models.response;

import com.Turfbooking.models.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOtpResponse {
    private String refreshToken;
    private String otpStatus;
    private String userStatus;
    private String nameOfTheUser;
    private UserResponse user;
//    private FullBusinessResponse companyUser;
    private String token;
    private Boolean isUpdate;
    private Boolean isUpdateAllowed;

}
