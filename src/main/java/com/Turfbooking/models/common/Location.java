package com.Turfbooking.models.common;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class Location {
    public String type;
    public Double[] coordinates;
}
