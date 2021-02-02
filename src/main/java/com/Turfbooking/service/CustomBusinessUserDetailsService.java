package com.Turfbooking.service;

import com.Turfbooking.documents.Business;
import com.Turfbooking.documents.User;
import com.Turfbooking.models.mics.CustomBusinessUserDetails;
import com.Turfbooking.models.mics.CustomUserDetails;
import com.Turfbooking.repository.BusinessRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.utils.CommonUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomBusinessUserDetailsService implements UserDetailsService {

    private BusinessRepository businessRepository;
    private UserRepository userRepository;

    @Autowired
    public CustomBusinessUserDetailsService(BusinessRepository businessRepository, UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String usernameOrPhoneNumber = CommonUtilities.findUsernameOrPhoneNumber(username);

        if (StringUtils.equals(usernameOrPhoneNumber, "PhoneNumber")) {

            User user = userRepository.findByPhoneNumber(username);

            CustomUserDetails customUserDetails;
            if (user != null) {
                customUserDetails = new CustomUserDetails(user);
            } else {
                throw new UsernameNotFoundException("User not found.");
            }
            return customUserDetails;

        } else {
            Business business = businessRepository.findByUsername(username);

            CustomBusinessUserDetails customBusinessUserDetails;
            if (business != null) {
                customBusinessUserDetails = new CustomBusinessUserDetails(business);
            } else {
                throw new UsernameNotFoundException("Business not found.");
            }
            return customBusinessUserDetails;
        }
    }

}

