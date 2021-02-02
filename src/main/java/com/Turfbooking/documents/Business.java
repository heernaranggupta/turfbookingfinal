package com.Turfbooking.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Business {

    @Id
    private String _id;
    private String username;
    private String password;
    @Indexed
    private String phoneNumber;
    private String companyName;
    private String role;

    public Business(Business business){
        this._id = business.get_id();
        this.username = business.getUsername();
        this.password =business.getPassword();
        this.phoneNumber = business.getPhoneNumber();
        this.companyName = business.getCompanyName();
        this.role = business.getRole();
    }

    public Business(String username, String password, String phoneNumber, String companyName, String role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.role = role;
    }
}
