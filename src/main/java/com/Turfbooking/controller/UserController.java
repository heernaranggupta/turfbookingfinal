package com.Turfbooking.controller;

import com.Turfbooking.models.common.Slot;
import com.Turfbooking.models.request.*;
import com.Turfbooking.models.response.*;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public CommonResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        CreateUserResponse userResponse = userService.createNewUser(createUserRequest);
        CommonResponse response = new CommonResponse<>(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/login")
    public CommonResponse<CreateUserLoginResponse> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        CreateUserLoginResponse userResponse = userService.userLogin(userLoginRequest);
        CommonResponse response = new CommonResponse(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PutMapping("/update-profile")
    public CommonResponse<CustomerProfileUpdateResponse> updateProfile(@Valid @RequestBody CustomerProfileUpdateRequest customerProfileUpdateRequest) {
        CustomerProfileUpdateResponse customerProfileUpdateResponse = userService.updateProfile(customerProfileUpdateRequest);
        CommonResponse commonResponse = new CommonResponse<>(customerProfileUpdateRequest);
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

//    @CacheEvict(
//            value = "listOfSlotsByTurfIdAndDate",
//            allEntries = true,
//            condition = "#cancelOrUnavailableSlotRequest.turfId != null")
@PostMapping("cancel-booking")
public CommonResponse cancelBookedSlot(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
    TimeSlotResponse timeSlotResponse = userService.cancelBookedSlot(cancelOrUnavailableSlotRequest);
    CommonResponse response = new CommonResponse(timeSlotResponse);
    return response;
}

    //    @CacheEvict(
//            value = "listOfSlotsByTurfIdAndDate",
//            allEntries = true,
//            condition = "#updateBookedTimeSlotRequest.turfId != null")
    @PostMapping("update-booking")
    public CommonResponse updateBookedSlot(@Valid @RequestBody UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest) {
        TimeSlotResponse timeSlotResponse = userService.updateBookedSlot(updateBookedTimeSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    //view user booking history
    @GetMapping("/booking-history")
    public CommonResponse<AllBookedSlotByUserResponse> allBookedSlots(@RequestParam String userPhoneNumber) {
        CommonResponse response = new CommonResponse(userService.getAllBookedSlots(userPhoneNumber));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/get-all-slots-by-date")
    public CommonResponse getAllSlotsByDate(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
//        System.out.println(getAllSlotsRequest);
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDate(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/cart")
    public CommonResponse addToCart(@Valid @RequestBody CartRequest cartRequest){
        CommonResponse response = new CommonResponse(userService.addToCart(cartRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/cart")
    public CommonResponse getCart(@RequestParam(required = false) String phoneNumber,
                                  @RequestParam(required = false) String cartId){
        CommonResponse response = new CommonResponse(userService.getCart(phoneNumber,cartId));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/cart/remove")
    public CommonResponse removeFromCart(@Valid @RequestBody RemoveCartRequest removeCartRequest){
        CommonResponse response = new CommonResponse(userService.removeFromCart(removeCartRequest));
        return ResponseUtilities.createSucessResponseWithMessage(response,"Slot successfully removed");
    }

    @PostMapping("/get-avai-slots")
    public CommonResponse<GetAllSlotsResponse> getAvailableSlots(@Valid @RequestBody GetAvailableSlotsRequest getAvailableSlotsRequest)
    {
        LocalTime before = LocalTime.now();
        CommonResponse<GetAllSlotsResponse> response = new CommonResponse<>(userService.getAvailableSlot(getAvailableSlotsRequest),200,"Avaialble SLots are Here",true, LocalDateTime.now());

        LocalTime after = LocalTime.now();

        System.out.println("Before :"+before+"\t After :"+after);
        System.out.println("Difference is :"+before.until(after, ChronoUnit.SECONDS));
        System.out.println("Difference is :"+before.until(after, ChronoUnit.MILLIS));
            return response;
    }

}
