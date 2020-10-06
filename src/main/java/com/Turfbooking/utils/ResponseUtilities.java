package com.Turfbooking.utils;

import com.Turfbooking.models.response.CommonResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ResponseUtilities {
    public static CommonResponse createSuccessResponse(CommonResponse commonResponse) {
        commonResponse.setCode(200);
        commonResponse.setMessage(null);
        commonResponse.setSuccess(true);
        commonResponse.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return commonResponse;
    }

    public static CommonResponse createFailureResponse(CommonResponse commonResponse) {
        commonResponse.setCode(200);
        commonResponse.setMessage(null);
        commonResponse.setSuccess(false);
        commonResponse.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return commonResponse;
    }

    public static CommonResponse createFailureResponseWith200(CommonResponse commonResponse, String message) {
        commonResponse.setCode(200);
        commonResponse.setMessage(message);
        commonResponse.setSuccess(false);
        commonResponse.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return commonResponse;
    }

    public static CommonResponse createSucessResponseWithMessage(CommonResponse commonResponse, String message) {
        commonResponse.setCode(200);
        commonResponse.setMessage(message);
        commonResponse.setSuccess(true);
        commonResponse.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return commonResponse;
    }

    public static CommonResponse createFailureResponseWithCustomizedCode(CommonResponse commonResponse, String message, Integer code) {
        commonResponse.setCode(code);
        commonResponse.setMessage(message);
        commonResponse.setSuccess(false);
        commonResponse.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return commonResponse;
    }


}
