package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.Otp;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.exception.UserNotFoundException;
import com.Turfbooking.models.LambdaInterfaces.SendMail;
import com.Turfbooking.models.common.Location;
import com.Turfbooking.models.enums.OtpActiveStatus;
import com.Turfbooking.models.enums.UserStatus;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.repository.OtpRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    public static final String OTP_SENT_SUCCESS = "Otp Generated Successfully";
    public static final String ACCOUNT_SID = "AC8816014df293294f856c6acc153d7a50";
    public static final String AUTH_TOKEN = "735b60dc0b74b158dbb2a39c0364ff19";
    public static final String TWILIO_NUMBER = "+12137844747";

    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private OtpRepository otpRepository;

    @Autowired
    public UserServiceImpl(JwtTokenUtil jwtTokenUtil,UserRepository userRepository,OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.otpRepository = otpRepository;
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
    public CreateUserResponse createNewUser(CreateUserRequest createUserRequest) throws GeneralException {

        User isExist = userRepository.findByPhoneNumber(createUserRequest.getPhoneNumber());
        String responseStatus = null;
        if(isExist!=null){
            throw new GeneralException("User exist with this phone number.", HttpStatus.BAD_REQUEST);
        }

        User addUser = User.builder()
                .nameOfUser(createUserRequest.getName())
                .countryCode(createUserRequest.getCountryCode())
                .password(CommonUtilities.getEncryptedPassword(createUserRequest.getPassword()))
                .phoneNumber(createUserRequest.getPhoneNumber())
                .emailId(createUserRequest.getEmailId())
                .build();
        Location userLocation = new Location();
        if (null != createUserRequest && null != createUserRequest.getLatitude() && null != createUserRequest.getLongitude()) {
            userLocation.type = "Point";
            Double[] locationArray = new Double[2];
            locationArray[0] = createUserRequest.getLongitude();
            locationArray[1] = createUserRequest.getLatitude();
            userLocation.setCoordinates(locationArray);
        }
        addUser.setLocation(userLocation);

        User newCreatedUser =userRepository.insert(addUser);
        UserResponse userResponse = new UserResponse(newCreatedUser);
        responseStatus = UserStatus.NEWUSERCREATED.name();

        String token = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), accessSecret, accessTokenValidity);
        String refreshToken = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), refreshSecret, refreshTokenValidity);

        CreateUserResponse response=new CreateUserResponse(userResponse,responseStatus,token,refreshToken);
        return response;
    }

    @Override
    public CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest) {

        String username = userLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(userLoginRequest.getPassword());
        String userLoginType = CommonUtilities.findEmailIdOrPasswordValidator(userLoginRequest.getUsername());
        User isExist = null;
        if(StringUtils.equals(userLoginType,"email")){
            isExist = userRepository.findByEmailIdAndPassword(username,password);
        }else {
            isExist = userRepository.findByPhoneNumberAndPassword(username,password);
        }

        if(null != isExist){
                String token = jwtTokenUtil.generateToken(username, accessSecret, accessTokenValidity);
                String refreshToken = jwtTokenUtil.generateToken(username, refreshSecret, refreshTokenValidity);

                UserResponse userResponse = new UserResponse(isExist);
                CreateUserLoginResponse loginResponse = new CreateUserLoginResponse(userResponse,token,refreshToken);
                return loginResponse;

        }else{
            throw new UserNotFoundException("Username and password does not matched.");
        }
    }


    @Override
    public CreateResponse generateOtp(GenerateOtpRequest otpRequest) throws GeneralException {
        this.environment.getActiveProfiles();
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

        msg.setSubject("My turn Otp");
        msg.setText("Your Verification code is : " + otp);

        javaMailSender.send(msg);

        return 1;
    }

//     convert it to lambda expression
    private int sendOtp(String phoneNumberWithCountryCode, Integer otp) throws GeneralException {

        //  int otp = otpService.generateOTP(phoneNumberWithCountryCode);
        //to send sms
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(phoneNumberWithCountryCode),
                new PhoneNumber(TWILIO_NUMBER),
                "Your Verification code is : " + otp)
                .create();

        System.out.println(message);
        return 1;
    }


}
