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

@Document
@Builder
@Getter
@Setter
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
}
