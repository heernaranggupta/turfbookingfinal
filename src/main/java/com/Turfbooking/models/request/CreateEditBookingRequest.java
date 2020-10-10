package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Valid
public class CreateEditBookingRequest {

    @NotNull
    private String userId;
    private String companyId;
}
