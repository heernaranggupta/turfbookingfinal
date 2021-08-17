package com.Turfbooking.controller;

import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping
    public CommonResponse healthCheck() {
        return ResponseUtilities.createSuccessResponse(new CommonResponse(""));
    }
}
