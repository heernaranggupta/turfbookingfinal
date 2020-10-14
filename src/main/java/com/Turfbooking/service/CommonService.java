package com.Turfbooking.service;

import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;

public interface CommonService {

    CreateResponse generateOtp(GenerateOtpRequest otpRequest);

    ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest);
}
