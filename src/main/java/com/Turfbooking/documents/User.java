package com.Turfbooking.documents;

import com.Turfbooking.models.common.Address;
import com.Turfbooking.models.common.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Getter
@Setter
@Document
public class User {

    @Id
    private String _id;
    private String nameOfUser;
    private String username;
    private String firstName;
    private String middleName;
    private String password; //not req
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String countryCode;
    @Indexed(name = "user_phone_number", direction = IndexDirection.DESCENDING, background = true, unique = true)
    private String phoneNumber;
    private Address address;
    private Location location;
    private Location latestLocation;
    @Indexed(name = "user_email_id", direction = IndexDirection.DESCENDING, background = true)
    private String emailId;
    private String displayImageUrl;
    private String role;

    public User(User user) {
        this.nameOfUser = user.getNameOfUser();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.password = user.getPassword();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.countryCode = user.getCountryCode();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.location = user.getLocation();
        this.latestLocation = user.getLatestLocation();
        this.emailId = user.getEmailId();
        this.displayImageUrl = user.getDisplayImageUrl();
        this.role = user.getRole();
    }

    public User(String nameOfUser, String password, String gender, LocalDate dateOfBirth, String countryCode, String phoneNumber, String emailId, String displayImageUrl, String role) {
        this.nameOfUser = nameOfUser;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.countryCode = countryCode;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.displayImageUrl = displayImageUrl;
        this.role = role;
    }
}
