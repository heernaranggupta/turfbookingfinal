package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.common.StartEndTimeRequest;
import com.Turfbooking.models.request.ConfigRequest;
import com.Turfbooking.models.request.ConfigRequests;
import com.Turfbooking.models.response.ConfigResponse;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    public ConfigResponse getConfig(String day, LocalDate date) throws GeneralException{

        OpenCloseTime openCloseTime = null;
        List<StartEndTime> startEndTimeList = new ArrayList<>();

        if (null != date) {
            openCloseTime = openCloseTimeRepository.findByDate(date);
            if(null != openCloseTime) {
                startEndTimeList = startEndTimeRepository.findByDate(date);
            }
        } else {
            openCloseTime = openCloseTimeRepository.findByDay(day);
            if(null != openCloseTime){
                startEndTimeList = startEndTimeRepository.findByDay(day);
            }
        }

        ConfigResponse response;
        if (null != openCloseTime && null != startEndTimeList) {
            response = new ConfigResponse(openCloseTime, startEndTimeList, "successfully loaded");
        } else {
            response = new ConfigResponse(null, null, "no data found");
        }
        return response;
    }

    @Override
    public List<ConfigResponse> addConfig(ConfigRequests configRequests) throws GeneralException {

        List<ConfigResponse> configResponses = new ArrayList<>();
        for (ConfigRequest configRequest : configRequests.getConfigRequests()) {
            OpenCloseTime openCloseTime = null;
            List<StartEndTime> startEndTimeList = null;
            boolean flag = true;
            if (null != configRequest.getDate()) {
                openCloseTime = openCloseTimeRepository.findByDate(configRequest.getDate());
                if(null != openCloseTime){
                    startEndTimeList = startEndTimeRepository.findByDate(configRequest.getDate());
                }
            } else if (null != configRequest.getDay()){
                openCloseTime = openCloseTimeRepository.findByDay(configRequest.getDay());
                if(null != openCloseTime){
                    startEndTimeList = startEndTimeRepository.findByDay(configRequest.getDay());
                }
            } else {
                throw new GeneralException("Provide day or date to save config", HttpStatus.BAD_REQUEST);
            }

            if(null != openCloseTime && null != startEndTimeList){
                for (StartEndTimeRequest startEndTimeRequest : configRequest.getStartEndTimeRequestList()) {
                    for (StartEndTime startEndTime : startEndTimeList) {
                        if ((startEndTimeRequest.getStartTime().isBefore(startEndTime.getStartTime()) && (startEndTimeRequest.getEndTime().isBefore(startEndTime.getStartTime()) || startEndTimeRequest.getEndTime().isEqual(startEndTime.getStartTime())))
                                || ((startEndTimeRequest.getStartTime().isAfter(startEndTime.getStartTime()) || startEndTimeRequest.getStartTime().isEqual(startEndTime.getStartTime())) && startEndTimeRequest.getEndTime().isAfter(startEndTime.getEndTime())) && startEndTimeRequest.getTurfId().equals(startEndTime.getTurfId())) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    }
                }
            }
            ConfigResponse configResponse;
            if (flag) {
                OpenCloseTime saveOpenCloseTime = OpenCloseTime.builder()
                        .date(configRequest.getDate())
                        .day(configRequest.getDay())
                        .openTime(configRequest.getOpenTime())
                        .closeTime(configRequest.getCloseTime())
                        .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                        .build();
                if(null != openCloseTime && null != openCloseTime.get_id()){
                    saveOpenCloseTime.set_id(openCloseTime.get_id());
                }
                List<StartEndTime> saveStartEndTimeList = new ArrayList<>();
                for ( StartEndTimeRequest startEndTimeRequest : configRequest.getStartEndTimeRequestList()) {
                    StartEndTime startEndTime = StartEndTime.builder()
                            .date(configRequest.getDate())
                            .day(configRequest.getDay())
                            .turfId(startEndTimeRequest.getTurfId())
                            .startTime(startEndTimeRequest.getStartTime())
                            .endTime(startEndTimeRequest.getEndTime())
                            .price(startEndTimeRequest.getPrice())
                            .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                            .build();
                    saveStartEndTimeList.add(startEndTime);
                }
                OpenCloseTime savedOpenCloseTime = openCloseTimeRepository.save(saveOpenCloseTime);
                List<StartEndTime> savedStartEndTime = startEndTimeRepository.saveAll(saveStartEndTimeList);
                configResponse = new ConfigResponse(savedOpenCloseTime,savedStartEndTime,"Config successfully saved");
            } else {
                configResponse = new ConfigResponse(openCloseTime,startEndTimeList,"Conflict while saving config");
            }
            configResponses.add(configResponse);
        }

        return configResponses;
    }

    public String deleteConfigByDate(LocalDate date) throws GeneralException {
        List<StartEndTime> deletedConfig = startEndTimeRepository.deleteByDate(date);
        if(null != deletedConfig){
            return "deleted successfully";
        }
        return "error in deletion";
    }


    @Override
    public ConfigResponse updateConfigByDate(ConfigRequest configRequest) throws GeneralException {

        OpenCloseTime saveOpenCloseTime = OpenCloseTime.builder()
                .date(configRequest.getDate())
                .day(configRequest.getDay())
                .openTime(configRequest.getOpenTime())
                .closeTime(configRequest.getCloseTime())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                .build();
        List<StartEndTime> saveStartEndTimeList = new ArrayList<>();
        for ( StartEndTimeRequest startEndTimeRequest : configRequest.getStartEndTimeRequestList()) {
            StartEndTime startEndTime = StartEndTime.builder()
                    .date(configRequest.getDate())
                    .day(configRequest.getDay())
                    .turfId(startEndTimeRequest.getTurfId())
                    .startTime(startEndTimeRequest.getStartTime())
                    .endTime(startEndTimeRequest.getEndTime())
                    .price(startEndTimeRequest.getPrice())
                    .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            saveStartEndTimeList.add(startEndTime);
        }
        OpenCloseTime savedOpenCloseTime = openCloseTimeRepository.save(saveOpenCloseTime);
        List<StartEndTime> savedStartEndTime = startEndTimeRepository.saveAll(saveStartEndTimeList);
        ConfigResponse configResponse = new ConfigResponse(savedOpenCloseTime,savedStartEndTime,"Config successfully saved");

        return configResponse;
    }

}
