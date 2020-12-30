package com.Turfbooking.models.request;

import com.Turfbooking.models.common.StartEndTimeRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConfigRequests {

    List<ConfigRequest> configRequests;

}
