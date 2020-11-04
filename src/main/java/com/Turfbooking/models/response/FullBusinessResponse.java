package com.Turfbooking.models.response;

import com.Turfbooking.documents.Business;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullBusinessResponse {

    private  String _id;
    private String username;
    private String password;
    private String phoneNumber;
    private String companyName;
    private String role;

    public FullBusinessResponse(Business business) {
        this._id = business.get_id();
        this.username = business.getUsername();
        this.password = business.getPassword();
        this.phoneNumber = business.getPhoneNumber();
        this.companyName = business.getCompanyName();
        this.role = business.getRole();
    }
}
