package com.Turfbooking.controller;

import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.service.RazorPayService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RazorPayController {

    @GetMapping("/")
    String welcome(){
        return "Welcome to turf bookings";
    }

}
