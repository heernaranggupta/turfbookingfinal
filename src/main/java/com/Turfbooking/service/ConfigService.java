package com.Turfbooking.service;

import com.Turfbooking.models.request.ConfigRequest;
import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.ConfigResponse;

import java.time.LocalDate;
import java.util.List;

public interface ConfigService {

    List<ConfigResponse> addConfig(ConfigRequests configRequest);

    ConfigResponse getConfig(String day, LocalDate date);

    ConfigResponse updateConfigByDate(ConfigRequest configRequest);

}
