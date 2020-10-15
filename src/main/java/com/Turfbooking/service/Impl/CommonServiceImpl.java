package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.Otp;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.miscellaneous.StringConstants;
import com.Turfbooking.models.enums.OtpActiveStatus;
import com.Turfbooking.models.enums.OtpStatus;
import com.Turfbooking.models.enums.UserStatus;
import com.Turfbooking.models.externalCalls.ExternalOtpCallResponse;
import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.repository.OtpRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.CommonService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    public static final String OTP_SENT_SUCCESS = "Otp Generated Successfully";

    private JwtTokenUtil jwtTokenUtil;
    private OtpRepository otpRepository;
    private RestTemplate restTemplate;
    private UserRepository userRepository;


    @Autowired
    public CommonServiceImpl(JwtTokenUtil jwtTokenUtil, OtpRepository otpRepository, RestTemplate restTemplate, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.otpRepository = otpRepository;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @Value("${jwt.secret.accessToken}")
    private String accessSecret;

    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;

    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;

    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Value("${otp.active.minutes}")
    private String otpActiveMinutes;

    @Autowired
    private Environment environment;
    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public CreateResponse generateOtp(GenerateOtpRequest otpRequest) throws GeneralException {
        String username = null;
        username = otpRequest.getPhoneNumber();
        String emailOrPhoneNumber = null;
        emailOrPhoneNumber = CommonUtilities.findEmailIdOrPasswordValidator(username);
        Otp otpDocument = null;

        if (StringUtils.equals(emailOrPhoneNumber, "email"))
            otpDocument = otpRepository.findByPhoneNumber(username);
        else {
            String countryCode = otpRequest.getCountryCode();
            username = StringUtils.join(countryCode, username);
            otpDocument = otpRepository.findByPhoneNumber(username);
        }

        String responseMessage;
        Integer otp;
        int isOtpSent;
        long otpdeltedCount;

        if (null != otpDocument && LocalDateTime.now().isBefore(otpDocument.getTimeTillActive())) {
            otp = otpDocument.getOtp();
        } else {
            //delete inactive otp
            if (otpDocument != null)
                otpdeltedCount = otpRepository.deleteByPhoneNumber(otpDocument.getPhoneNumber());
            otp = CommonUtilities.generate4DigitOTP();
            otpRepository.insert(new Otp(username, otp, LocalDateTime.now().plusMinutes(Integer.valueOf(otpActiveMinutes)), OtpActiveStatus.ACTIVE.name()));
        }

        if (StringUtils.equals(emailOrPhoneNumber, "email"))
            isOtpSent = sendMail(username, otp);
        else
            isOtpSent = sendOtp(username, otp);

        //if otp not sent
        if (isOtpSent == 1) {
            responseMessage = OTP_SENT_SUCCESS;
        } else
            throw new GeneralException("Otp Not send", HttpStatus.BAD_REQUEST);

        CreateResponse response = CreateResponse.builder()
                .message(responseMessage)
                .build();

        return response;
    }

    //    convert it to lambda expression
    private int sendMail(String mailId, Integer otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mailId);

        msg.setSubject("Turf Booking turn Otp");
        msg.setText("Your Verification code is : " + otp);

        javaMailSender.send(msg);

        return 1;
    }

    private int sendOtp(String phoneNumber, Integer otp) throws GeneralException {
        String messageWithOTP = StringConstants.baseMessageForOTPSMS.replace(StringConstants.otpReplacementPart, otp.toString());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(StringConstants.baseURLForOTPService)
                .queryParam("token", "3429179825ec3aed88b7758.96328390")
                .queryParam("user_id", "39305273")
                .queryParam("route", "OT")
                .queryParam("template_id", "2342")
                .queryParam("sender_id", "MYTURN")
                .queryParam("language", "EN")
                .queryParam("template", messageWithOTP)
                .queryParam("contact_numbers", phoneNumber);

        String uri = builder.toUriString();

//http://m1.sarv.com/api/v2.0/sms_campaign.php?token=3429179825ec3aed88b7758.96328390&user_id=39305273&route=EN&template_id=&sender_id=TSTMSG&language=EN&template=My+Message+is+this+test&contact_numbers=8511108666

        ResponseEntity<ExternalOtpCallResponse> response =
                restTemplate.getForEntity(
                        builder.toUriString(),
                        ExternalOtpCallResponse.class);
        ExternalOtpCallResponse externalOtpCallResponse = response.getBody();

        if (externalOtpCallResponse.getCode() != HttpStatus.OK.value()) {
            log.error(externalOtpCallResponse.toString());
            throw new GeneralException("Error in sending OTP, please try again after sometime.", HttpStatus.BAD_GATEWAY);
        } else return 1;
    }
/*

    @Override
    public ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest) {

        String phoneNumber = validateOtpRequest.getPhoneNumber();
        String emailOrPhoneNumber = CommonUtilities.findEmailIdOrPasswordValidator(phoneNumber);
        String countryCode = validateOtpRequest.getCountryCode();

        String phoneNumberWithCountryCode = null;
        if (StringUtils.equals(emailOrPhoneNumber, "email"))
            phoneNumberWithCountryCode = phoneNumber;

        else {
            phoneNumberWithCountryCode = StringUtils.join(countryCode, phoneNumber);

        }
        Integer userOtp = validateOtpRequest.getOtp();
        ValidateOtpResponse validateOtpResponse = new ValidateOtpResponse();
        Otp otp = otpRepository.findByPhoneNumberAndOtp(phoneNumberWithCountryCode, validateOtpRequest.getOtp());

        //set otp status valid or not
        if (null != otp && validateOtpRequest.getOtp().intValue() == userOtp.intValue() && LocalDateTime.now().isBefore(otp.getTimeTillActive())) {
            //delete otp entry from database
            long otpdeltedCount = otpRepository.deleteByPhoneNumber(otp.getPhoneNumber());
            validateOtpResponse.setOtpStatus(OtpStatus.VALID.name());
        } else
            validateOtpResponse.setOtpStatus(OtpStatus.INVALID.name());

        //check if user or business login
        Boolean isBusiness = validateOtpRequest.getIsBusiness();
        String token;
        String refreshToken;
        if (isBusiness) {
//            Business businessDocument = businessRepository.findByPrimaryPhoneNumber(phoneNumber);
//            if (null != businessDocument && StringUtils.equals(validateOtpRequest.getCountryCode(), businessDocument.getCountryCode())) {
//                token = jwtTokenUtil.generateToken(phoneNumber, accessSecret, accessTokenValidity);
//                refreshToken = jwtTokenUtil.generateToken(phoneNumber, refreshSecret, refreshTokenValidity);
//                validateOtpResponse.setToken(token);
//                validateOtpResponse.setRefreshToken(refreshToken);
//                validateOtpResponse.setUserStatus(UserStatus.EXISTINGUSER.name());
//                validateOtpResponse.setNameOfTheUser(businessDocument.getBusinessDisplayName());
//                validateOtpResponse.setCompanyUser(FullBusinessResponse.getFullBusinessResponseFromBusinessDocument(businessDocument));
//            } else
//                validateOtpResponse.setUserStatus(UserStatus.USERDOESNOTEXIST.name());

        } else {
            User userDocument = userRepository.findByPhoneNumber(phoneNumber);
            if (null != userDocument && StringUtils.equals(validateOtpRequest.getCountryCode(), userDocument.getCountryCode())) {
                token = jwtTokenUtil.generateToken(phoneNumber, accessSecret, accessTokenValidity);
                refreshToken = jwtTokenUtil.generateToken(phoneNumber, refreshSecret, refreshTokenValidity);
                validateOtpResponse.setToken(token);
                validateOtpResponse.setRefreshToken(refreshToken);
                validateOtpResponse.setUserStatus(UserStatus.EXISTINGUSER.name());
                validateOtpResponse.setNameOfTheUser(userDocument.getFirstName());
                UserResponse userResponse = new UserResponse(userDocument);
                validateOtpResponse.setUser(userResponse);
            } else
                validateOtpResponse.setUserStatus(UserStatus.USERDOESNOTEXIST.name());
        }
        return validateOtpResponse;
    }
*/

    @Override
    public ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest) {

        String phoneNumber = validateOtpRequest.getPhoneNumber();
        String countryCode = validateOtpRequest.getCountryCode();

        String phoneNumberWithCountryCode = null;

        if (StringUtils.isNotBlank(phoneNumber) && StringUtils.isNotBlank(countryCode))
            phoneNumberWithCountryCode = phoneNumber;
        else {
            throw new GeneralException("Phone number or county code is invalid.", HttpStatus.OK);
        }

        Integer userOtp = validateOtpRequest.getOtp();
        ValidateOtpResponse validateOtpResponse = new ValidateOtpResponse();
        Otp otp = otpRepository.findByPhoneNumberAndOtp(phoneNumberWithCountryCode, validateOtpRequest.getOtp());

        if (null != otp && validateOtpRequest.getOtp().intValue() == userOtp.intValue() && LocalDateTime.now().isBefore(otp.getTimeTillActive())) {
            //delete otp entry from database
            long otpdeltedCount = otpRepository.deleteByPhoneNumber(otp.getPhoneNumber());
            validateOtpResponse.setOtpStatus(OtpStatus.VALID.name());
        } else {
            validateOtpResponse.setOtpStatus(OtpStatus.INVALID.name());
        }

        //logic for is this user exist or not.
        User isUserOrNot = userRepository.findByPhoneNumber(phoneNumber);
        String token;
        String refreshToken;

        if (null != isUserOrNot) {
            token = jwtTokenUtil.generateToken(phoneNumber, accessSecret, accessTokenValidity);
            refreshToken = jwtTokenUtil.generateToken(phoneNumber, refreshSecret, refreshTokenValidity);
            validateOtpResponse.setToken(token);
            validateOtpResponse.setRefreshToken(refreshToken);
            validateOtpResponse.setUserStatus(UserStatus.EXISTINGUSER.name());
            validateOtpResponse.setNameOfTheUser(isUserOrNot.getFirstName());
            UserResponse userResponse = new UserResponse(isUserOrNot);
            validateOtpResponse.setUser(userResponse);
        } else {
            validateOtpResponse.setUserStatus(UserStatus.USERDOESNOTEXIST.name());
        }
        return validateOtpResponse;

    }

}
