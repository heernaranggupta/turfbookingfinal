package com.Turfbooking.controller;

import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.GetBusinessConfigRequest;
import com.Turfbooking.models.response.UpdateBusinessConfigResponse;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.service.CommonService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/common")
public class CommonController {

    private CommonService commonService;

    @Autowired
    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostMapping("/generate-otp")
    public CommonResponse<CreateResponse> generateOTP(@RequestBody GenerateOtpRequest request) throws IOException, MessagingException {
        CommonResponse commonResponse = new CommonResponse<>(commonService.generateOtp(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/validate-otp")
    public CommonResponse<ValidateOtpResponse> validateOTP(@RequestBody ValidateOtpRequest request) {
        CommonResponse commonResponse = new CommonResponse<>(commonService.validateOTP(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);

    }

    @PostMapping("/get-business-config")
    public CommonResponse<UpdateBusinessConfigResponse> getBusinessConfig(@Valid @RequestBody GetBusinessConfigRequest getBusinessConfigRequest){
        CommonResponse response = new CommonResponse<>(commonService.getBusinessConfig(getBusinessConfigRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

}
