package com.Turfbooking.models.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Document
@Builder
@Getter
public class Address {
    private String addressLine;
    private String zipCode;
    private String city;
    private String state;
    private String country;
}
