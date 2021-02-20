package com.Turfbooking.controller;

import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CartRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.RemoveCartRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @GetMapping
    public CommonResponse getUser(Authentication authentication) {
        CommonResponse response = new CommonResponse(userService.getUser(authentication.getName()));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PutMapping("/update-profile")
    public CommonResponse<CustomerProfileUpdateResponse> updateProfile(@Valid @RequestBody CustomerProfileUpdateRequest customerProfileUpdateRequest, Authentication authentication) {
        customerProfileUpdateRequest.setPhoneNumber(authentication.getName());
        CommonResponse commonResponse = new CommonResponse<>(userService.updateProfile(customerProfileUpdateRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("cancel-booking")
    public CommonResponse cancelBookedSlot(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest, Authentication authentication) throws RazorpayException {
        TimeSlotResponse timeSlotResponse = userService.cancelBookedSlot(cancelOrUnavailableSlotRequest, authentication.getName());
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return response;
    }

    @PostMapping("update-booking")
    public CommonResponse updateBookedSlot(@Valid @RequestBody UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest, Authentication authentication) {
        updateBookedTimeSlotRequest.setUserId(authentication.getName());
        TimeSlotResponse timeSlotResponse = userService.updateBookedSlot(updateBookedTimeSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/booking-history")
    public CommonResponse<AllBookedSlotByUserResponse> allBookedSlots(Authentication authentication) {
        CommonResponse response = new CommonResponse(userService.getAllBookedSlots(authentication.getName()));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/get-all-slots-by-date")
    public CommonResponse getAllSlotsByDate(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDate(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/cart")
    public CommonResponse addToCart(@Valid @RequestBody CartRequest cartRequest, Authentication authentication) {
        cartRequest.setUserPhoneNumber(authentication.getName());
        CommonResponse response = new CommonResponse(userService.addToCart(cartRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/cart/guest")
    public CommonResponse addToGuestCart(@Valid @RequestBody CartRequest cartRequest) {
//        System.out.println("################ CartID :" + cartRequest.getCartId());
        CommonResponse response = new CommonResponse(userService.addToCart(cartRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/cart/guest")
    public CommonResponse getCart(@RequestParam(required = false) String cartId, @RequestParam(required = false) String phoneNumber) {
//        System.out.println("################ CartID :" + cartId);
        return userService.getCart(phoneNumber, cartId);
    }


    @GetMapping("/cart")
    public CommonResponse getCart(@RequestParam(required = false) String cartId, @RequestParam(required = false) String phoneNumber, Authentication authentication) {
        return userService.getCart(authentication.getName(), cartId);
    }

    @PostMapping("/cart/remove")
    public CommonResponse removeFromCart(@Valid @RequestBody RemoveCartRequest removeCartRequest, Authentication authentication) {
        removeCartRequest.setUserPhoneNumber(authentication.getName());
        CommonResponse response = new CommonResponse(userService.removeFromCart(removeCartRequest));
        return ResponseUtilities.createSucessResponseWithMessage(response, "Slot successfully removed");
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/cart/guest/remove")
    public CommonResponse removeFromGuestCart(@Valid @RequestBody RemoveCartRequest removeCartRequest) {
//        System.out.println("################ CartID :" + removeCartRequest.getCartId());
        CommonResponse response = new CommonResponse(userService.removeFromCart(removeCartRequest));
        return ResponseUtilities.createSucessResponseWithMessage(response, "Slot successfully removed");
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/mobile/get-all-slots-by-date")
    public CommonResponse getAllSlotsByDatePhoneUser(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDatePhoneUser(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/get-all-slots-by-date/common")
    public CommonResponse getAllSlotsByDateCommon(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDateCommon(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @GetMapping("/mobile/cart")
    public CommonResponse getCartForPhoneUsers(Authentication authentication) {
        return userService.getCartForPhoneUsers(authentication.getName());
    }

}
