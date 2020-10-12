package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Otp;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.exception.UserNotFoundException;
import com.Turfbooking.models.common.Location;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.enums.OtpStatus;
import com.Turfbooking.models.enums.UserStatus;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.OtpRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private OtpRepository otpRepository;
    private BookedTimeSlotRepository bookedTimeSlotRepository;

    @Autowired
    public UserServiceImpl(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, OtpRepository otpRepository, BookedTimeSlotRepository bookedTimeSlotRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
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
    public CreateUserResponse createNewUser(CreateUserRequest createUserRequest) throws GeneralException {

        User isExist = userRepository.findByPhoneNumber(createUserRequest.getPhoneNumber());
        if (isExist != null) {
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

        User newCreatedUser = userRepository.insert(addUser);
        UserResponse userResponse = new UserResponse(newCreatedUser);

        String token = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), accessSecret, accessTokenValidity);
        String refreshToken = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), refreshSecret, refreshTokenValidity);

        CreateUserResponse response = new CreateUserResponse(userResponse, token, refreshToken);
        return response;
    }

    @Override
    public CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest) {

        String username = userLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(userLoginRequest.getPassword());
        String userLoginType = CommonUtilities.findEmailIdOrPasswordValidator(userLoginRequest.getUsername());
        User isExist = null;
        if (StringUtils.equals(userLoginType, "email")) {
            isExist = userRepository.findByEmailIdAndPassword(username, password);
        } else {
            isExist = userRepository.findByPhoneNumberAndPassword(username, password);
        }

        if (null != isExist) {
            String token = jwtTokenUtil.generateToken(username, accessSecret, accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(username, refreshSecret, refreshTokenValidity);

            UserResponse userResponse = new UserResponse(isExist);
            CreateUserLoginResponse loginResponse = new CreateUserLoginResponse(userResponse, token, refreshToken);
            return loginResponse;

        } else {
            throw new UserNotFoundException("Username and password does not matched.");
        }
    }

    @Override
    public AllBookedSlotByUserResponse getAllBookedSlots(String userId) throws GeneralException {

        User isExist = userRepository.findByPhoneNumber(userId);

        if (null != isExist) {
            List<BookedTimeSlot> bookedTimeSlots = bookedTimeSlotRepository.findByUserId(userId);
            AllBookedSlotByUserResponse response = new AllBookedSlotByUserResponse(bookedTimeSlots);
            return response;

        } else {
            throw new GeneralException("No user found with user id: " + userId, HttpStatus.OK);
        }

    }

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

    @Override
    public BookTimeSlotResponse cancelBookedSlot(String bookingId) {

        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByBookingId(bookingId);

        if (null != timeSlot) {
            timeSlot = BookedTimeSlot.builder()
                    ._id(timeSlot.get_id())
                    .BookingId(timeSlot.getBookingId())
                    .userId(timeSlot.getUserId())
                    .slotNumber(null)
                    .turfId(timeSlot.getTurfId())
                    .status(BookingStatus.CANCELLED_BY_USER.name())
                    .date(timeSlot.getDate())
                    .startTime(timeSlot.getStartTime())
                    .endTime(timeSlot.getEndTime())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();

            BookedTimeSlot cancelled = bookedTimeSlotRepository.save(timeSlot);

            if (null != cancelled) {

                BookTimeSlotResponse response = new BookTimeSlotResponse(cancelled);
                return response;

            } else {
                throw new GeneralException("Error in cancellation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralException("No booked slot with booking id: " + bookingId, HttpStatus.OK);
        }
    }


}