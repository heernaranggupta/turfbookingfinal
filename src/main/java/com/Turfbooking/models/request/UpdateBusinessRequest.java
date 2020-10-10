package com.Turfbooking.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UpdateBusinessRequest {
    private String username;
    @NotNull(message = "Phone Number is Required")
    private String phoneNumber;
    private String companyName;

}
