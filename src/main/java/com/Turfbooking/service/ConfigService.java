package com.Turfbooking.service;

import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.ConfigResponse;

import java.util.List;

public interface ConfigService {

    List<ConfigResponse> addOrUpdateConfig(ConfigRequests configRequest);

    ConfigResponse getConfig(String day, String date);

    String deleteConfigByDate(String day, String date);

    List<Double> minPayPrice(String date);

    List<ConfigResponse> getConfigBetweenDates(String strStartDate, String strEndDate, String turfId);

    CommonResponse deleteConfigByTurfIdAndDate(String turfId, String date);

}
