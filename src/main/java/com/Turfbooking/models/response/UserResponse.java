package com.Turfbooking.models.response;


import com.Turfbooking.documents.User;
import com.Turfbooking.models.common.Address;
import com.Turfbooking.models.common.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
@Getter
@Setter
public class UserResponse {

    private String name;
    private String gender;
    private LocalDate dateOfBirth;
    private String countryCode;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private Location latestLocation;
    private String emailId;
    private String addressLine;
    private String zipCode;
    private String displayImageUrl;
    private String role;
    private Boolean cartCreated;

    public UserResponse(User userDocument) {
        this.name = userDocument.getNameOfUser();
        this.gender = userDocument.getGender();
        this.dateOfBirth = userDocument.getDateOfBirth();
        this.countryCode = userDocument.getCountryCode();
        this.phoneNumber = userDocument.getPhoneNumber();
        if (userDocument.getLocation() != null) {
            if (userDocument.getLocation().getCoordinates() != null) {
                this.longitude = userDocument.getLocation().getCoordinates()[0];
                this.latitude = userDocument.getLocation().getCoordinates()[1];
            }
        }
        this.latestLocation = userDocument.getLatestLocation();
        this.emailId = userDocument.getEmailId();
        if (userDocument.getAddress() != null) {
            Address address = userDocument.getAddress();
            this.addressLine = address.getAddressLine();
            this.zipCode = address.getZipCode();
        }
        if (userDocument.getDisplayImageUrl() != null) {
            this.displayImageUrl = userDocument.getDisplayImageUrl();
        }
    }

    public UserResponse(String name, String gender, LocalDate dateOfBirth, String countryCode, String phoneNumber, Location latestLocation, String emailId, String addressLine, String zipCode, String displayImageUrl) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.latestLocation = latestLocation;
        this.emailId = emailId;
        this.addressLine = addressLine;
        this.zipCode = zipCode;
        this.displayImageUrl = displayImageUrl;
    }

}
