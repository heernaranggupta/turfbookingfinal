package com.Turfbooking.controller;

import com.Turfbooking.models.request.ConfigRequest;
import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.ConfigResponse;
import com.Turfbooking.service.ConfigService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/config")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConfigController {

    private ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/add")
    public CommonResponse<List<ConfigResponse>> addConfig(@RequestBody ConfigRequest configRequest) {
        List<ConfigRequest> configRequestList = new ArrayList<>();
        configRequestList.add(configRequest);
        ConfigRequests configRequests = new ConfigRequests();
        configRequests.setConfigRequests(configRequestList);
        CommonResponse response = new CommonResponse(configService.addOrUpdateConfig(configRequests));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/get")
    public CommonResponse getConfig(@RequestParam(required = false) String day,
                                    @RequestParam(required = false) String date) {
        CommonResponse response = new CommonResponse(configService.getConfig(day, date));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @DeleteMapping("/delete")
    public CommonResponse deleteConfig(@RequestParam(required = false) String day,
                                       @RequestParam(required = false) String date) {
        CommonResponse response = new CommonResponse(configService.deleteConfigByDate(day, date));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/min_pay_price")
    public CommonResponse getMinPayPrice(@RequestParam String date,
                                         HttpServletResponse httpServletResponse) {
        CommonResponse response = new CommonResponse(configService.minPayPrice(date));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/get_for_month")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public CommonResponse getBookedBetweenDates(@RequestParam(required = false) String turfIds,
                                                @RequestParam String startDate,
                                                @RequestParam String endDate,
                                                HttpServletResponse httpServletResponse) {
        CommonResponse response = new CommonResponse(configService.getConfigBetweenDates(startDate, endDate, turfIds));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @DeleteMapping("/delete_by_date_turf_id")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public CommonResponse deleteConfigByTurfIdAndDate(@RequestParam String turfId,
                                                      @RequestParam(required = false) String date,
                                                      HttpServletResponse httpServletResponse) {
        return configService.deleteConfigByTurfIdAndDate(turfId, date);
    }


}
