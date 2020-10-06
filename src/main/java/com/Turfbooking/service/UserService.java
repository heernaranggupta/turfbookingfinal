package com.Turfbooking.service;

import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.response.CreateUserResponse;

public interface UserService {

    CreateUserResponse createNewUser(CreateUserRequest createUserRequest);
}
