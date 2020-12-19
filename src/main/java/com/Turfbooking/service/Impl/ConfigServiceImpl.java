package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.request.OpenCloseTimeRequest;
import com.Turfbooking.models.request.StartEndTimeRequest;
import com.Turfbooking.models.response.OpenCloseTimeResponse;
import com.Turfbooking.models.response.StartEndTimeResponse;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public OpenCloseTimeResponse addOpenCloseTimeConfig(OpenCloseTimeRequest openCloseTimeRequest) throws GeneralException {


        return null;
    }

    @Override
    public StartEndTimeResponse addStartEndTimeConfig(StartEndTimeRequest startEndTimeRequest) throws GeneralException{

//        StartEndTime startEndTime = startEndTimeRepository.

        return null;
    }
}
