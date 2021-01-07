package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.common.StartEndTimeRequest;
import com.Turfbooking.models.request.ConfigRequest;
import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.ConfigResponse;
import com.Turfbooking.models.response.StartEndTimeResponse;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    private OpenCloseTimeRepository openCloseTimeRepository;
    private StartEndTimeRepository startEndTimeRepository;

    @Autowired
    public ConfigServiceImpl(OpenCloseTimeRepository openCloseTimeRepository, StartEndTimeRepository startEndTimeRepository) {
        this.openCloseTimeRepository = openCloseTimeRepository;
        this.startEndTimeRepository = startEndTimeRepository;
    }

    @Override
    public ConfigResponse getConfig(String day, String strDate) throws GeneralException {
        LocalDate date = LocalDate.parse(strDate);
        OpenCloseTime openCloseTime = null;
        List<StartEndTime> startEndTimeList = new ArrayList<>();
        if (null != date) {
            openCloseTime = openCloseTimeRepository.findByDate(date);
            if (null != openCloseTime) {
                startEndTimeList = startEndTimeRepository.findByDate(date);
            }
        } else {
            openCloseTime = openCloseTimeRepository.findByDay(day);
            if (null != openCloseTime) {
                startEndTimeList = startEndTimeRepository.findByDay(day);
            }
        }
        ConfigResponse response;
        if (null != openCloseTime && null != startEndTimeList) {
            List<StartEndTimeResponse> startEndTimeResponseList = new ArrayList<>();
            startEndTimeList.stream().forEach(startEndTimeResponse ->
                    {
                        StartEndTimeResponse timeResponse = new StartEndTimeResponse(startEndTimeResponse);
                        startEndTimeResponseList.add(timeResponse);
                    }
            );

            response = new ConfigResponse(openCloseTime, startEndTimeResponseList, "successfully loaded");
        } else {
            response = new ConfigResponse(null, null, "no data found");
        }
        return response;
    }

    @Override
    @Transactional
    public List<ConfigResponse> addOrUpdateConfig(ConfigRequests configRequests) throws GeneralException {
        List<ConfigResponse> configResponses = new ArrayList<>();
        for (ConfigRequest configRequest : configRequests.getConfigRequests()) {
            boolean flag = false;
            if (null != configRequest.getDate()) {
                OpenCloseTime openCloseTime = openCloseTimeRepository.findByDate(configRequest.getDate());
                List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDate(configRequest.getDate());
                if (null != openCloseTime && null != startEndTimeList) {
                    openCloseTimeRepository.delete(openCloseTime);
                    startEndTimeRepository.deleteAll(startEndTimeList);
                }
                flag = true;
            } else if (null != configRequest.getDay()) {
                OpenCloseTime openCloseTime = openCloseTimeRepository.findByDay(configRequest.getDay());
                List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDay(configRequest.getDay());
                if (null != openCloseTime && null != startEndTimeList) {
                    openCloseTimeRepository.delete(openCloseTime);
                    startEndTimeRepository.deleteAll(startEndTimeList);
                }
                flag = true;
            }
            ConfigResponse configResponse = null;
            if (flag) {
                OpenCloseTime saveOpenCloseTime = OpenCloseTime.builder()
                        .openTime(configRequest.getOpenTime())
                        .closeTime(configRequest.getCloseTime())
                        .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                        .build();

                if (null != configRequest.getDate()) {
                    saveOpenCloseTime.setDate(configRequest.getDate());
                } else if (null != configRequest.getDay()) {
                    saveOpenCloseTime.setDay(configRequest.getDay().toUpperCase());
                }

                List<StartEndTime> saveStartEndTimeList = new ArrayList<>();
                for (StartEndTimeRequest startEndTimeRequest : configRequest.getStartEndTimeRequestList()) {
                    StartEndTime startEndTime = StartEndTime.builder()
                            .turfId(startEndTimeRequest.getTurfId())
                            .startTime(startEndTimeRequest.getStartTime())
                            .endTime(startEndTimeRequest.getEndTime())
                            .price(startEndTimeRequest.getPrice())
                            .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                            .build();
                    if (null != configRequest.getDate()) {
                        startEndTime.setDate(configRequest.getDate());
                    } else if (null != configRequest.getDay()) {
                        startEndTime.setDay(configRequest.getDay().toUpperCase());
                    }

                    saveStartEndTimeList.add(startEndTime);
                }
                OpenCloseTime savedOpenCloseTime = openCloseTimeRepository.save(saveOpenCloseTime);
                List<StartEndTime> savedStartEndTime = startEndTimeRepository.saveAll(saveStartEndTimeList);
                List<StartEndTimeResponse> startEndTimeResponseList = new ArrayList<>();
                savedStartEndTime.stream().forEach(response ->
                        {
                            StartEndTimeResponse timeResponse = new StartEndTimeResponse(response);
                            startEndTimeResponseList.add(timeResponse);
                        }
                );
                configResponse = new ConfigResponse(savedOpenCloseTime, startEndTimeResponseList, "Config successfully saved");
            }
            configResponses.add(configResponse);
        }
        return configResponses;
    }

    @Override
    @Transactional
    public String deleteConfigByDate(String day, String strDate) {
        LocalDate date = LocalDate.parse(strDate);
        List<StartEndTime> deletedConfig = null;
        deletedConfig = startEndTimeRepository.deleteByDate(date);
        if (null != deletedConfig) {
            deletedConfig = startEndTimeRepository.deleteByDay(day);
        }

        if (null != deletedConfig) {
            return "deleted successfully";
        }
        return "error in deletion";
    }

}
