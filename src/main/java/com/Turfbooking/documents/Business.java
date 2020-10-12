package com.Turfbooking.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@AllArgsConstructor
public class Business {
    @Id
    private String _id;
    private String username;
    private String password;
    private String phoneNumber;
    private String companyName;
}
