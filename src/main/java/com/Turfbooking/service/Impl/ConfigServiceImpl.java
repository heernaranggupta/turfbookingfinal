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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        OpenCloseTime openCloseTime = null;
        List<StartEndTime> startEndTimeList = new ArrayList<>();
        if (null != strDate) {
            LocalDate date = LocalDate.parse(strDate);
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
                OpenCloseTime openCloseTime = openCloseTimeRepository.findByDay(configRequest.getDay().toUpperCase());
                List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDay(configRequest.getDay().toUpperCase());
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
                        .slotDuration(configRequest.getSlotDuration())
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
                            .minAmountForBooking(startEndTimeRequest.getMinAmountForBooking())
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
        List<StartEndTime> deletedStartEndTimeList = null;
        OpenCloseTime deleteOpenCloseTime = openCloseTimeRepository.findByDate(date);
        if (null != deleteOpenCloseTime) {
            openCloseTimeRepository.delete(deleteOpenCloseTime);
            deletedStartEndTimeList = startEndTimeRepository.findByDate(date);
            startEndTimeRepository.deleteAll(deletedStartEndTimeList);
            deletedStartEndTimeList = startEndTimeRepository.findByDate(date);
            deleteOpenCloseTime = openCloseTimeRepository.findByDate(date);
        } else {
            deleteOpenCloseTime = openCloseTimeRepository.findByDay(day);
            if (null != deleteOpenCloseTime) {
                openCloseTimeRepository.delete(deleteOpenCloseTime);
                deletedStartEndTimeList = startEndTimeRepository.findByDay(day);
                startEndTimeRepository.deleteAll(deletedStartEndTimeList);
                deletedStartEndTimeList = startEndTimeRepository.findByDay(day);
                deleteOpenCloseTime = openCloseTimeRepository.findByDay(day);
            }
        }

        if (deletedStartEndTimeList.size() == 0 && null == deleteOpenCloseTime) {
            return "deleted successfully";
        }
        return "error in deletion";
    }

    @Override
    public List<Double> minPayPrice(String stringDate) {
        LocalDate date = LocalDate.parse(stringDate);
        List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDate(date);
        if (startEndTimeList.size() == 0) {
            String day = date.getDayOfWeek().toString();
            startEndTimeList = startEndTimeRepository.findByDay(day.toUpperCase());
            if (startEndTimeList.size() == 0) {
                throw new GeneralException("No data found for date" + date, HttpStatus.BAD_REQUEST);
            }
        }
        List<Double> minPayPriceList = startEndTimeList.stream().map(x -> x.getMinAmountForBooking()).collect(Collectors.toList());
        return minPayPriceList;
    }
}
