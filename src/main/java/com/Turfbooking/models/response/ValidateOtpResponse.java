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

    private String otpStatus;
    private String userStatus;
    private String nameOfTheUser;
    private UserResponse user;
    private String token;
    private String refreshToken;
    private Boolean isUpdate;//what is the use of this field
    private Boolean isUpdateAllowed;//what is the use of this field

}
