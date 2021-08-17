package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.common.StartEndTimeRequest;
import com.Turfbooking.models.enums.Turfs;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final OpenCloseTimeRepository openCloseTimeRepository;
    private final StartEndTimeRepository startEndTimeRepository;

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

    @Override
    public List<ConfigResponse> getConfigBetweenDates(String strStartDate, String strEndDate, String turfId) {
        LocalDate startDate = LocalDate.parse(strStartDate);
        LocalDate endDate = LocalDate.parse(strEndDate);
        List<ConfigResponse> configResponseList = new ArrayList<>();
        List<String> turfIds = new ArrayList<>();
        if (turfId == null || turfId == "") {
            turfIds.add(Turfs.TURF01.name());
            turfIds.add(Turfs.TURF02.name());
            turfIds.add(Turfs.TURF03.name());
            //open close time of all turfs between start and end dates
            List<OpenCloseTime> openCloseTimeList = openCloseTimeRepository.findByDateBetween(startDate, endDate);
            //start end time of all turfs between start and end dates
            List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDateBetween(startDate, endDate);
            this.addConfig(openCloseTimeList, startEndTimeList, configResponseList);
        } else {
            if (turfId.contains(Turfs.TURF01.name())) {
                List<StartEndTime> turf01 = startEndTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF01.name(), startDate, endDate);
                List<OpenCloseTime> openCloseTimeList = openCloseTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF01.name(), startDate, endDate);
//                add config to config list
                this.addConfig(openCloseTimeList, turf01, configResponseList);
                turfIds.add(Turfs.TURF01.name());
            }
            if (turfId.contains(Turfs.TURF02.name())) {
                List<StartEndTime> turf02 = startEndTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF02.name(), startDate, endDate);
                List<OpenCloseTime> openCloseTimeList = openCloseTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF02.name(), startDate, endDate);
//                add config to config list
                this.addConfig(openCloseTimeList, turf02, configResponseList);
                turfIds.add(Turfs.TURF02.name());
            }
            if (turfId.contains(Turfs.TURF03.name())) {
                List<StartEndTime> turf03 = startEndTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF03.name(), startDate, endDate);
                List<OpenCloseTime> openCloseTimeList = openCloseTimeRepository.findByTurfIdEqualsAndDateBetween(Turfs.TURF03.name(), startDate, endDate);
//                add config to config list
                this.addConfig(openCloseTimeList, turf03, configResponseList);
                turfIds.add(Turfs.TURF03.name());
            }
        }

        configResponseList = this.addResponseOfTheDateWhichAreNotInDB(configResponseList, endDate, strStartDate, turfIds);
        //sort configResponseList
        Collections.sort(configResponseList, Comparator.comparing(ConfigResponse::getDate));
        return configResponseList;
    }

    private List<ConfigResponse> addResponseOfTheDateWhichAreNotInDB(List<ConfigResponse> configResponseList, LocalDate endDate, String strStartDate, List<String> turfIds) {
        //get all open close time and start end time data
        //make config response list of all week

        List<LocalDate> localDateList = configResponseList.stream().map(x -> x.getDate()).collect(Collectors.toList());
        LocalDate tempDate = LocalDate.parse(strStartDate);
        List<ConfigResponse> responseList = new ArrayList<>();
        while (tempDate.isBefore(endDate) || tempDate.isEqual(endDate)) {
            if (!localDateList.contains(tempDate)) {
                String day = tempDate.getDayOfWeek().toString();
                List<String> days = responseList.stream().map(x -> x.getDay()).collect(Collectors.toList());
                if (days.contains(day)) {
                    List<ConfigResponse> d = new ArrayList<>();
                    for (ConfigResponse response : responseList) {
                        if (response.getDay().equalsIgnoreCase(day)) {
                            ConfigResponse res = new ConfigResponse();
                            res.setDay(day);
                            res.setDate(tempDate);
                            res.setOpenTime(response.getOpenTime());
                            res.setCloseTime(response.getCloseTime());
                            res.setSlotDuration(response.getSlotDuration());
                            res.setStartEndTimeResponseList(response.getStartEndTimeResponseList());
                            res.setMessage(response.getMessage());
                            configResponseList.add(res);
                        }
                    }

                    for (ConfigResponse res : d) {
                        if (res != null && res.getDay().equalsIgnoreCase(day)) {
                            res.setDate(tempDate);
                            configResponseList.add(res);
                        }
                    }
                } else {
                    ConfigResponse time = this.getConfig(day, null);
                    responseList.add(time);
                    //add that config in config response
                    time.setDate(tempDate);
                    configResponseList.add(time);
                }
            }
            tempDate = tempDate.plusDays(1);
        }
        return configResponseList;
    }

    void addConfig(List<OpenCloseTime> openCloseTimeList, List<StartEndTime> startEndTimeList, List<ConfigResponse> configResponseList) {
        List<ConfigResponse> responseList = new ArrayList<>();
        for (OpenCloseTime openCloseTime : openCloseTimeList) {
            List<StartEndTime> startEndTimesOfToday = new ArrayList<>();
            for (StartEndTime startEndTime : startEndTimeList) {
                if (startEndTime.getDate().equals(openCloseTime.getDate())) {
                    startEndTimesOfToday.add(startEndTime);
                }
            }
            List<StartEndTimeResponse> startEndTimeResponseList = new ArrayList<>();
            startEndTimesOfToday.stream().forEach(startEndTime ->
                    {
                        StartEndTimeResponse timeResponse = new StartEndTimeResponse(startEndTime);
                        startEndTimeResponseList.add(timeResponse);
                    }
            );
            ConfigResponse response = new ConfigResponse(openCloseTime, startEndTimeResponseList, null);
            responseList.add(response);
        }
        configResponseList.addAll(responseList);
    }


}

