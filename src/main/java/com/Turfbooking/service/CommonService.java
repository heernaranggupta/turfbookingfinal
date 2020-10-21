package com.Turfbooking.service;

import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.GetBusinessConfigRequest;
import com.Turfbooking.models.response.UpdateBusinessConfigResponse;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;

public interface CommonService {

    CreateResponse generateOtp(GenerateOtpRequest otpRequest);

    ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest);

    UpdateBusinessConfigResponse getBusinessConfig(GetBusinessConfigRequest getBusinessConfigRequest);
}
