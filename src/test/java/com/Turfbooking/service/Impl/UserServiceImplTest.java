package com.Turfbooking.service.Impl;

import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void createNewUser() {
        assertEquals(1,1);

        final CreateUserRequest request = CreateUserRequest.builder()
                .name("user01")
                .gender("male")
                .password("1234")
                .countryCode("+91")
                .phoneNumber("9925299419")
                .emailId("user01@spt.com")
                .displayImageUrl("URL")
                .build();
        //final UserResponse userResponse = new UserResponse("user01","male",null,"+91","9925299419",null,null,null,"user01@spt.com",null,null,"URL");

//        final CreateUserResponse response = new CreateUserResponse(userResponse);

        assertEquals("","");


    }

    @Test
    void userLogin() {
//        Mockito.when()
    }

    @Test
    void bookSlot() {
    }

    @Test
    void getAllBookedSlots() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void cancelBookedSlot() {
    }

    @Test
    void updateBookedSlot() {
    }

    @Test
    void getAllSlotsByDate() {
    }
}

//    final User user = new User(null,"user01","username","firstname","middlename","password","lastname",
//            "male",null,"+91","9925299419",null,null,null,"user01@turf.com","display");
