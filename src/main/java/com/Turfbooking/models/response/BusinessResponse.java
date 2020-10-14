package com.Turfbooking.models.response;

import com.Turfbooking.documents.Business;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class BusinessResponse {
    private String username;
    private String phoneNumber;
    private String companyName;

    public BusinessResponse(Business business) {
        this.username = business.getUsername();
        this.phoneNumber = business.getPhoneNumber();
        this.companyName = business.getCompanyName();
    }

}
