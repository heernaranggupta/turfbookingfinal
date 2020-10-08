package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.Business;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.response.BusinessResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.repository.BusinessRepository;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl  implements BusinessService {

    private BusinessRepository businessRepository;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository,JwtTokenUtil jwtTokenUtil) {
        this.businessRepository = businessRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Value("${jwt.secret.accessToken}")
    private String accessSecret;

    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;

    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;

    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Override
    public CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest) throws GeneralException {
        String username = createBusinessLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(createBusinessLoginRequest.getPassword());
        Business business = businessRepository.findByUsernameAndPassword(username, password);
        if (business != null) {
                String token = jwtTokenUtil.generateToken(business.getPhoneNumber(), accessSecret, (accessTokenValidity));
                String refreshToken = jwtTokenUtil.generateToken(business.getPhoneNumber(), refreshSecret, (refreshTokenValidity));
                BusinessResponse businessResponse = new BusinessResponse(business);
                CreateBusinessLoginResponse response = CreateBusinessLoginResponse.builder()
                        .businessResponse(businessResponse)
                        .token(token)
                        .refreshToken(refreshToken)
                        .build();
                return response;
            } else {
                throw new GeneralException("Invalid Username  and Password", HttpStatus.UNAUTHORIZED);
            }
        }

    @Override
    public CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest) {

        String phoneNumber = createUpdatePasswordRequest.getPhoneNumber();
        Business business = businessRepository.findByPhoneNumber(phoneNumber);

        if (business != null) {
            String password = CommonUtilities.getEncryptedPassword(createUpdatePasswordRequest.getPassword());
            business.setPassword(password);
            businessRepository.save(business);

            CreatePasswordResponse response = CreatePasswordResponse.builder()
                    .message("Password reset successfully")
                    .build();
            return response;
        } else {
            throw new GeneralException("Incorrect UserName", HttpStatus.UNAUTHORIZED);
        }
    }

}


