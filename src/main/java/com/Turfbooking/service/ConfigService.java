package com.Turfbooking.service;

import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.models.request.OpenCloseTimeRequest;
import com.Turfbooking.models.request.StartEndTimeRequest;
import com.Turfbooking.models.response.OpenCloseTimeResponse;
import com.Turfbooking.models.response.StartEndTimeResponse;

public interface ConfigService {

    OpenCloseTimeResponse addOpenCloseTimeConfig(OpenCloseTimeRequest openCloseTimeRequest);

    StartEndTimeResponse addStartEndTimeConfig(StartEndTimeRequest startEndTimeRequest);

}
