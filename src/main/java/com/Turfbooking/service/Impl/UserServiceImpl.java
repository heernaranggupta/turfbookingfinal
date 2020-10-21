package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Order;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.exception.UserNotFoundException;
import com.Turfbooking.models.common.Address;
import com.Turfbooking.models.common.Location;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.OrderRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.GetAllSlotsByUserResponse;
import com.Turfbooking.models.response.OrderResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.CartRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private OtpRepository otpRepository;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
    private CartRepository cartRepository;
    @Value("${jwt.secret.accessToken}")
    private String accessSecret;
    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;
    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;
    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Autowired
    public UserServiceImpl(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, OtpRepository otpRepository, BookedTimeSlotRepository bookedTimeSlotRepository, CartRepository cartRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CreateUserResponse createNewUser(CreateUserRequest createUserRequest) throws GeneralException {

        User isExist = userRepository.findByPhoneNumber(createUserRequest.getPhoneNumber());
        if (isExist != null) {
            throw new GeneralException("User exist with this phone number.", HttpStatus.BAD_REQUEST);
        }

        User addUser = User.builder()
                .nameOfUser(createUserRequest.getName())
                .gender(createUserRequest.getGender())
                .dateOfBirth(createUserRequest.getDateOfBirth())
                .countryCode(createUserRequest.getCountryCode())
                .password(CommonUtilities.getEncryptedPassword(createUserRequest.getPassword()))
                .phoneNumber(createUserRequest.getPhoneNumber())
                .emailId(createUserRequest.getEmailId())
                .displayImageUrl(createUserRequest.getDisplayImageUrl())
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
    public CustomerProfileUpdateResponse updateProfile(CustomerProfileUpdateRequest customerProfileUpdateRequest) throws GeneralException {

        User userDocument = userRepository.findByPhoneNumber(customerProfileUpdateRequest.getPhoneNumber());

        if (userDocument != null) {
            userDocument.setNameOfUser(customerProfileUpdateRequest.getName());
            userDocument.setGender(customerProfileUpdateRequest.getGender());
            userDocument.setDateOfBirth(customerProfileUpdateRequest.getDateOfBirth());
            userDocument.setAddress(new Address(customerProfileUpdateRequest.getAddressLine(), customerProfileUpdateRequest.getZipCode(), customerProfileUpdateRequest.getCity(), customerProfileUpdateRequest.getState(), "INDIA"));
            userDocument.setEmailId(customerProfileUpdateRequest.getEmailId());
            User newCreatedUser = userRepository.save(userDocument);
            UserResponse userResponse = new UserResponse(newCreatedUser);
            CustomerProfileUpdateResponse customerProfileUpdateResponse = CustomerProfileUpdateResponse.builder()
                    .user(userResponse)
                    .build();
            return customerProfileUpdateResponse;
        } else {
            throw new GeneralException("User does not exist with this PhoneNumber ", HttpStatus.OK);
        }
    }

    @Override
    public BookTimeSlotResponse cancelBookedSlot(CancelOrUnavailableSlotRequest cancelRequest) {

        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByDateAndSlotNumber(cancelRequest.getSlotNumber(), cancelRequest.getDate());

        if (null != timeSlot) {
            timeSlot = BookedTimeSlot.builder()
                    ._id(timeSlot.get_id())
                    .bookingId(timeSlot.getBookingId())
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
            throw new GeneralException("No booked slot.", HttpStatus.OK);
        }
    }

    @Override
    public BookTimeSlotResponse updateBookedSlot(UpdateBookedTimeSlotRequest updateRequest) throws GeneralException {

        BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.findByBookingId(updateRequest.getBookingId());
        BookedTimeSlot isSlotBooked = bookedTimeSlotRepository.findByDateAndSlotNumber(updateRequest.getSlotNumber(), updateRequest.getDate());
        if (null != isSlotBooked) {
            throw new GeneralException("Slot which you want to book is already booked.", HttpStatus.OK);
        }

        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(updateRequest.getUserId())
                    .slotNumber(updateRequest.getSlotNumber())
                    .turfId(updateRequest.getTurfId())
                    .date(LocalDateTime.of(updateRequest.getDate(), LocalTime.of(00, 00)))
                    .status(BookingStatus.RESCHEDULED_BY_USER.name())
                    .startTime(updateRequest.getStartTime())
                    .endTime(updateRequest.getEndTime())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();

            BookedTimeSlot updatedBookedSlot = bookedTimeSlotRepository.save(bookedTimeSlot);
            BookTimeSlotResponse response = new BookTimeSlotResponse(updatedBookedSlot);
            return response;
        } else {
            throw new GeneralException("Invalid booking id.", HttpStatus.OK);
        }
    }

    @Override
    public GetAllSlotsByUserResponse getAllSlotsByDate(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int days = getAllSlotsRequest.getDate().compareTo(today);

        if (days >= 0) { //means today or in future
            List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsRequest.getDate(), getAllSlotsRequest.getTurfId());
            List<BookTimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(getAllSlotsRequest.getTurfId(), getAllSlotsRequest.getDate(), getAllSlotsRequest.getOpenTime(), getAllSlotsRequest.getCloseTime(), getAllSlotsRequest.getSlotDuration());

            List<Integer> integerList = slotFromDB.stream()
                    .map(x -> x.getSlotNumber())
                    .collect(Collectors.toList());

            allSlotList.stream().
                    forEach((response) -> {
                        if (integerList.contains(response.getSlotNumber())) {
                            slotFromDB.stream().forEach((bookedSlot) -> {
                                if (response.getSlotNumber() == bookedSlot.getSlotNumber()) {
                                    BookTimeSlotResponse bookedResponse = new BookTimeSlotResponse(bookedSlot);
                                    allSlotList.set(response.getSlotNumber() - 1, bookedResponse);
                                }
                            });
                        }
                    });

            GetAllSlotsByUserResponse response = new GetAllSlotsByUserResponse(allSlotList);
            return response;
        } else {
            throw new GeneralException("Date should be not in past.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<BookTimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String turfId, LocalDate date, LocalDateTime openTime, LocalDateTime closeTime, int durationInMinutes) {
        List<BookTimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalDateTime slotStartTime = openTime;
        LocalDateTime slotEndTime;
        int count = 1;

        //slot end time should be before close time.
        while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime)) {
            slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
            timeSlotsList.add(new BookTimeSlotResponse(turfId, count, BookingStatus.AVAILABLE.name(), date, slotStartTime, slotEndTime));
            slotStartTime = slotEndTime;
            count++;
        }
        return timeSlotsList;
    }

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) throws GeneralException {
        User isUserExist = userRepository.findByPhoneNumber(orderRequest.getUserId());
        if (null == isUserExist) {
            throw new GeneralException("User does not exist.", HttpStatus.OK);
        }
        //Whenever user create account, his cart is created and set null.

        List<BookTimeSlotRequest> bookTimeSlotRequests = new ArrayList<>();
        for (BookTimeSlotRequest request : orderRequest.getTimeSlots()) {
            BookedTimeSlot slot = bookedTimeSlotRepository.findByDateAndSlotNumberAndTurfId(request.getSlotNumber(), request.getDate(), request.getTurfId());
            if (null == slot) {
                bookTimeSlotRequests.add(request);
            } else {
                throw new GeneralException("slot with slot number " + slot.getSlotNumber() + " on date " + slot.getDate() + " is alredy booked.", HttpStatus.OK);
            }
        }

        List<BookedTimeSlot> bookedTimeSlotList = bookSlot(bookTimeSlotRequests, orderRequest.getUserId());
        List<String> bookingIdList = bookedTimeSlotList.stream()
                .map(x -> x.getBookingId())
                .collect(Collectors.toList());

        Order saveOrder = Order.builder()
                .userId(orderRequest.getUserId())
                .timeSlots(bookingIdList)
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                .build();

        Order savedOrder = cartRepository.save(saveOrder);
        OrderResponse response = new OrderResponse(savedOrder);
        response.setTimeSlots(bookedTimeSlotList);
        return response;

    }


    private List<BookedTimeSlot> bookSlot(List<BookTimeSlotRequest> bookTimeSlotRequestList, String userId) throws GeneralException {
        List<BookedTimeSlot> bookedTimeSlotList = new ArrayList<>();
        for (BookTimeSlotRequest bookTimeSlotRequest : bookTimeSlotRequestList) {
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .userId(userId)
                    .bookingId(CommonUtilities.getAlphaNumericString(5))
                    .date(LocalDateTime.of(bookTimeSlotRequest.getDate(), LocalTime.of(00, 00)))
                    .slotNumber(bookTimeSlotRequest.getSlotNumber())
                    .turfId(bookTimeSlotRequest.getTurfId())
                    .status(BookingStatus.BOOKED_BY_USER.name())
                    .startTime(bookTimeSlotRequest.getStartTime())
                    .endTime(bookTimeSlotRequest.getEndTime())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();

            BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.insert(addNewBookedTimeSlot);
            bookedTimeSlotList.add(bookedTimeSlot);
        }
        return bookedTimeSlotList;
    }


}
