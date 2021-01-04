package com.Turfbooking.controller;

import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.ConfigResponse;
import com.Turfbooking.service.ConfigService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;


    @PostMapping("/add")
    public CommonResponse<List<ConfigResponse>> addConfig(@RequestBody ConfigRequests configRequests){
        CommonResponse response = new CommonResponse(configService.addOrUpdateConfig(configRequests));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/get")
    public CommonResponse getConfig(@RequestParam(required = false) String day,
                                    @RequestParam(required = false) String date) {
        CommonResponse response = new CommonResponse(configService.getConfig(day, date));
        return ResponseUtilities.createSuccessResponse(response);
    }
}
