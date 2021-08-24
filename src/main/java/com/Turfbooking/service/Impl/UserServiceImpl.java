package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.CancelledSlot;
import com.Turfbooking.documents.Cart;
import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.exception.UserNotFoundException;
import com.Turfbooking.models.common.Address;
import com.Turfbooking.models.common.Location;
import com.Turfbooking.models.common.Slot;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.enums.Roles;
import com.Turfbooking.models.enums.Turfs;
import com.Turfbooking.models.mics.CustomUserDetails;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CartRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.RemoveCartRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.CartResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.GetAllSlotsResponseForPhoneUser;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.razorpay.response.RefundResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.CancelledSlotRepository;
import com.Turfbooking.repository.CartRepository;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.ConfigService;
import com.Turfbooking.service.RazorPayService;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final BookedTimeSlotRepository bookedTimeSlotRepository;
    private final CartRepository cartRepository;
    private final CancelledSlotRepository cancelledSlotRepository;
    private final OpenCloseTimeRepository openCloseTimeRepository;
    private final StartEndTimeRepository startEndTimeRepository;
    private final RazorPayService razorPayService;
    private final ConfigService configService;

    @Value("${jwt.secret.accessToken}")
    private String accessSecret;
    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;
    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;
    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Autowired
    public UserServiceImpl(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, BookedTimeSlotRepository bookedTimeSlotRepository, CartRepository cartRepository, CancelledSlotRepository cancelledSlotRepository, OpenCloseTimeRepository openCloseTimeRepository, StartEndTimeRepository startEndTimeRepository, RazorPayService razorPayService, ConfigService configService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cartRepository = cartRepository;
        this.cancelledSlotRepository = cancelledSlotRepository;
        this.openCloseTimeRepository = openCloseTimeRepository;
        this.startEndTimeRepository = startEndTimeRepository;
        this.razorPayService = razorPayService;
        this.configService = configService;
    }

    @Override
    public CreateUserResponse createNewUser(CreateUserRequest createUserRequest) throws GeneralException {
        User isExist = userRepository.findByPhoneNumber(createUserRequest.getPhoneNumber());
        if (isExist != null) {
            throw new GeneralException("User exist with this phone number.", HttpStatus.BAD_REQUEST);
        }
        User addUser = new User(createUserRequest.getName(),
                CommonUtilities.getEncryptedPassword(createUserRequest.getPassword()),
                createUserRequest.getGender(),
                createUserRequest.getDateOfBirth(),
                createUserRequest.getCountryCode(),
                createUserRequest.getPhoneNumber(),
                createUserRequest.getEmailId(),
                createUserRequest.getDisplayImageUrl(),
                createUserRequest.getRole()
        );
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
        CustomUserDetails customUserDetails = new CustomUserDetails(newCreatedUser);
        String token = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), customUserDetails, accessSecret, accessTokenValidity);
        String refreshToken = jwtTokenUtil.generateToken(newCreatedUser.getPhoneNumber(), customUserDetails, refreshSecret, refreshTokenValidity);
        Cart getCart = cartRepository.findBy_cartId(createUserRequest.getCartId());
        if (null != getCart) {
            getCart.setUserPhoneNumber(newCreatedUser.getPhoneNumber());
            getCart = cartRepository.save(getCart);
        }
        CreateUserResponse response = new CreateUserResponse(userResponse, token, refreshToken);
        return response;
    }

    @Override
    public CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest) throws GeneralException {
        String username = userLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(userLoginRequest.getPassword());
        String userLoginType = CommonUtilities.findEmailIdOrPasswordValidator(userLoginRequest.getUsername());
        User isExist = null;
        if (StringUtils.equals(userLoginType, "email")) {
//            isExist = userRepository.findByEmailIdAndPassword(username, password);
            throw new GeneralException("Enter valid phone number", HttpStatus.BAD_REQUEST);
        } else {
            isExist = userRepository.findByPhoneNumberAndPassword(username, password);
        }
        if (null != isExist) {
            CustomUserDetails customUserDetails = new CustomUserDetails(isExist);
            String token = jwtTokenUtil.generateToken(username, customUserDetails, accessSecret, accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(username, customUserDetails, refreshSecret, refreshTokenValidity);
            Cart getCart = cartRepository.findBy_cartId(userLoginRequest.getCartId());
            Cart usersCart = cartRepository.findByUserPhoneNumber(username);
            if (null != getCart && 0 != getCart.getSelectedSlots().size()) {
                Cart mergeCart = null;
                if (null != usersCart) {
                    List<LocalTime> startTimeList = usersCart.getSelectedSlots().stream().map(x -> x.getStartTime()).collect(Collectors.toList());
                    List<LocalDate> dateList = usersCart.getSelectedSlots().stream().map(x -> x.getDate()).collect(Collectors.toList());
                    List<Slot> slotList = new ArrayList<>();
                    if (null != usersCart.getSelectedSlots() && usersCart.getSelectedSlots().size() != 0) {
                        slotList.addAll(usersCart.getSelectedSlots());
                    }
                    if (null != getCart.getSelectedSlots() && getCart.getSelectedSlots().size() != 0) {
                        for (Slot slot : getCart.getSelectedSlots()) {
                            if (startTimeList.contains(slot.getStartTime())) {
                                if (dateList.contains(slot.getDate())) {
                                    continue;
                                } else {
                                    slotList.add(slot);
                                    usersCart.setCartTotal(usersCart.getCartTotal() + slot.getPrice());
                                }
                            } else {
                                slotList.add(slot);
                                usersCart.setCartTotal(usersCart.getCartTotal() + slot.getPrice());
                            }
                        }
                    }
                    usersCart.setSelectedSlots(slotList);
                    mergeCart = cartRepository.save(usersCart);
                } else {
                    getCart.setUserPhoneNumber(username);
                    usersCart = cartRepository.save(getCart);
                }
                if (null != mergeCart) {
                    cartRepository.delete(getCart);
                } else {
                    if (null == usersCart && getCart != null) {
                        throw new GeneralException("Error in cart merging", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
            UserResponse userResponse = new UserResponse(isExist);
            userResponse.setRole(isExist.getRole());
            CreateUserLoginResponse loginResponse = new CreateUserLoginResponse(userResponse, token, refreshToken);
            return loginResponse;
        } else {
            throw new UserNotFoundException("Username and password does not matched.");
        }
    }

    @Override
    public UserResponse getUser(String userPhoneNumber) throws GeneralException {
        User user = userRepository.findByPhoneNumber(userPhoneNumber);
        if (null != user) {
            UserResponse response = new UserResponse(user);
            return response;
        } else {
            throw new GeneralException("No user found with phone number :" + userPhoneNumber, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public AllBookedSlotByUserResponse getAllBookedSlots(String userId) throws GeneralException {
        User isExist = userRepository.findByPhoneNumber(userId);
        if (null != isExist) {
            List<BookedTimeSlot> bookedTimeSlots = bookedTimeSlotRepository.findByUserId(userId);
            List<CancelledSlot> cancelledSlotList = cancelledSlotRepository.findByUserId(userId);
            List<TimeSlotResponse> timeSlotResponses = new ArrayList<>();
            for (BookedTimeSlot bookedTimeSlot : bookedTimeSlots) {
                TimeSlotResponse response = new TimeSlotResponse(bookedTimeSlot);
                timeSlotResponses.add(response);
            }
            for (CancelledSlot cancelledSlot : cancelledSlotList) {
                TimeSlotResponse timeSlotResponse = new TimeSlotResponse(cancelledSlot);
                timeSlotResponses.add(timeSlotResponse);
            }
            timeSlotResponses.sort(Comparator.comparing(TimeSlotResponse::getTimestamp));/*sorted((t1, t2 ) -> t1.getTimestamp().compareTo(t2.getTimestamp()));*/
            AllBookedSlotByUserResponse response = new AllBookedSlotByUserResponse(timeSlotResponses);
            return response;
        } else {
            throw new GeneralException("No user found with user id: " + userId, HttpStatus.OK);
        }
    }

    @Override
    public CustomerProfileUpdateResponse updateProfile(CustomerProfileUpdateRequest customerProfileUpdateRequest) throws GeneralException {
        User userDocument = userRepository.findByPhoneNumber(customerProfileUpdateRequest.getPhoneNumber());
        if (userDocument != null) {
            if (null != customerProfileUpdateRequest.getName()) {
                userDocument.setNameOfUser(customerProfileUpdateRequest.getName());
            }
            if (null != customerProfileUpdateRequest.getGender()) {
                userDocument.setGender(customerProfileUpdateRequest.getGender());
            }
            if (null != customerProfileUpdateRequest.getEmailId()) {
                userDocument.setEmailId(customerProfileUpdateRequest.getEmailId());
            }
            if (null != customerProfileUpdateRequest.getDateOfBirth()) {
                userDocument.setDateOfBirth(customerProfileUpdateRequest.getDateOfBirth());
            }
            if (null != customerProfileUpdateRequest.getAddressLine() && null != customerProfileUpdateRequest.getZipCode() && null != customerProfileUpdateRequest.getCity() && null != customerProfileUpdateRequest.getState()) {
                userDocument.setAddress(new Address(customerProfileUpdateRequest.getAddressLine(), customerProfileUpdateRequest.getZipCode(), customerProfileUpdateRequest.getCity(), customerProfileUpdateRequest.getState(), "INDIA"));
            }
            if (null != customerProfileUpdateRequest.getDownloadUrl()) {
                userDocument.setDisplayImageUrl(customerProfileUpdateRequest.getDownloadUrl());
            }
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
    @Transactional
    public TimeSlotResponse cancelBookedSlot(CancelOrUnavailableSlotRequest cancelRequest, String userID) throws GeneralException, RazorpayException {
        if (cancelRequest.getDate().isBefore(LocalDate.now(ZoneId.of("Asia/Kolkata")))) {
            throw new GeneralException("slot cannot be cancelled with date : " + cancelRequest.getDate().toString(), HttpStatus.BAD_REQUEST);
        }
        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(cancelRequest.getTurfId(), LocalDateTime.of(cancelRequest.getDate(), cancelRequest.getStartTime()), cancelRequest.getDate());
        User user = userRepository.findByPhoneNumber(userID);
        if (null != timeSlot && null != user) {
            CancelledSlot cancelledSlot = new CancelledSlot(timeSlot);
            if (user.getRole().equalsIgnoreCase(Roles.ADMIN.name())) {
                cancelledSlot.setStatus(BookingStatus.CANCELLED_BY_BUSINESS.name());
            } else if (user.getRole().equalsIgnoreCase(Roles.USER.name())) {
                cancelledSlot.setStatus(BookingStatus.CANCELLED_BY_USER.name());
            }
            //call api for refund
            RefundResponse refundResponse = razorPayService.initRefund(timeSlot.getOrderId(), timeSlot.getPayedAmount().toString());
            if (null == refundResponse.getId()) {
                throw new GeneralException("error while initiating refund", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            cancelledSlot.setRefundId(refundResponse.getId());
            CancelledSlot savedInDB = cancelledSlotRepository.insert(cancelledSlot);
            if (null != savedInDB) {
                bookedTimeSlotRepository.deleteById(timeSlot.get_id());
                TimeSlotResponse response = new TimeSlotResponse(savedInDB);
                return response;
            } else {
                throw new GeneralException("Error in cancellation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralException("No booked slot.", HttpStatus.OK);
        }
    }

    @Override
    public TimeSlotResponse updateBookedSlot(UpdateBookedTimeSlotRequest updateRequest) throws GeneralException {
        BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.findByBookingId(updateRequest.getBookingId());
        BookedTimeSlot isSlotBooked = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(updateRequest.getTurfId(), LocalDateTime.of(updateRequest.getDate(), updateRequest.getStartTime()), updateRequest.getDate());
        User user = userRepository.findByPhoneNumber(updateRequest.getUserId());
        if (null != isSlotBooked) {
            throw new GeneralException("Slot which you want to book is already booked.", HttpStatus.OK);
        }
        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(updateRequest.getUserId())
                    .turfId(updateRequest.getTurfId())
                    .payedAmount(updateRequest.getPrice())
                    .date(updateRequest.getDate())
                    .startTime(LocalDateTime.of(updateRequest.getDate(), updateRequest.getStartTime()))
                    .endTime(LocalDateTime.of(updateRequest.getDate(), updateRequest.getEndTime()))
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            if (user.getRole().equalsIgnoreCase(Roles.ADMIN.name())) {
                bookedTimeSlot.setStatus(BookingStatus.RESCHEDULED_BY_BUSINESS.name());
            } else if (user.getRole().equalsIgnoreCase(Roles.ADMIN.name())) {
                bookedTimeSlot.setStatus(BookingStatus.RESCHEDULED_BY_USER.name());
            }
            BookedTimeSlot updatedBookedSlot = bookedTimeSlotRepository.save(bookedTimeSlot);
            TimeSlotResponse response = new TimeSlotResponse(updatedBookedSlot);
            return response;
        } else {
            throw new GeneralException("Invalid booking id.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public GetAllSlotsResponse getAllSlotsByDate(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int days = getAllSlotsRequest.getDate().compareTo(today);
        OpenCloseTime openCloseTime = openCloseTimeRepository.findByDate(getAllSlotsRequest.getDate());
        if (null == openCloseTime) {
            DayOfWeek day = getAllSlotsRequest.getDate().getDayOfWeek();
            openCloseTime = openCloseTimeRepository.findByDay(day.toString());
        }
        //get all turfs which requested for slots
        List<String> turfs = getAllSlotsRequest.getTurfIds();
        GetAllSlotsResponse finalResponse = new GetAllSlotsResponse();
        if (days >= 0) { //means today or in future
            List<List<TimeSlotResponse>> responseList = new ArrayList<>();
            for (String turf : turfs) {
                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsRequest.getDate(), turf);
                List<TimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(turf, getAllSlotsRequest.getDate(), LocalDateTime.of(getAllSlotsRequest.getDate(), openCloseTime.getOpenTime()), LocalDateTime.of(getAllSlotsRequest.getDate(), openCloseTime.getCloseTime()), openCloseTime.getSlotDuration());
                List<LocalTime> startDateTimeList = slotFromDB.stream()
                        .map(x -> x.getStartTime().toLocalTime())
                        .collect(Collectors.toList());
                for (int i = 0; i < allSlotList.size(); i++) {
                    if (startDateTimeList.contains(allSlotList.get(i).getStartTime())) {
                        for (int j = 0; j < slotFromDB.size(); j++) {
                            if (allSlotList.get(i).getStartTime().equals(slotFromDB.get(j).getStartTime().toLocalTime())) {
                                TimeSlotResponse bookedResponse = new TimeSlotResponse(slotFromDB.get(j));
                                allSlotList.set(i, bookedResponse);
                            }
                        }
                    }
                }
                if (allSlotList.size() != 0 && allSlotList.get(0).getTurfId().equals(Turfs.TURF01.getValue())) {
                    finalResponse.setTurf01(allSlotList);
                } else if (allSlotList.size() != 0 && allSlotList.get(0).getTurfId().equals(Turfs.TURF02.getValue())) {
                    finalResponse.setTurf02(allSlotList);
                } else if (allSlotList.size() != 0 && allSlotList.get(0).getTurfId().equals(Turfs.TURF03.getValue())) {
                    finalResponse.setTurf03(allSlotList);
                }
            }
            return finalResponse;
        } else {
            throw new GeneralException("Date should be not in past.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<TimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String turfId, LocalDate date, LocalDateTime openTime, LocalDateTime closeTime, int durationInMinutes) {
        List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDate(date);
        if (startEndTimeList.size() == 0) {
            DayOfWeek day = date.getDayOfWeek();
            startEndTimeList = startEndTimeRepository.findByDay(day.toString());
        }
        List<TimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalDateTime slotStartTime = openTime;
        LocalDateTime slotEndTime = null;
        Double price = null;
        for (StartEndTime startEndTime : startEndTimeList) {
            LocalDateTime startTime = LocalDateTime.of(date, startEndTime.getStartTime());
            LocalDateTime endTime = LocalDateTime.of(date, startEndTime.getEndTime());
            if (startEndTime.getTurfId().equalsIgnoreCase(turfId)) {
                //slot end time should be before close time.
                while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime.plusNanos(1))) {
                    slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
                    if ((startTime.equals(slotStartTime) || startTime.isAfter(slotStartTime)) && slotStartTime.isBefore(endTime) && startEndTime.getTurfId().equalsIgnoreCase(turfId)) {
                        if (null != startEndTime.getPrice()) {
                            price = startEndTime.getPrice();
                        }
                    }
                    timeSlotsList.add(new TimeSlotResponse(turfId, price, BookingStatus.AVAILABLE.name(), date, slotStartTime.toLocalTime(), slotEndTime.toLocalTime()));
                    slotStartTime = slotEndTime;
                }
            }
        }
        return timeSlotsList;
    }

    @Override
    public CartResponse addToCart(CartRequest cartRequest, Boolean guest) throws GeneralException {
        for (Slot slot : cartRequest.getSelectedSlots()) {
            BookedTimeSlot isBookedTimeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(slot.getTurfId(), LocalDateTime.of(slot.getDate(), slot.getStartTime()), slot.getDate());
            if (null != isBookedTimeSlot) {
                throw new GeneralException("slot with date :" + slot.getDate() + " and time :" + slot.getStartTime() + "is already booked or unavailable", HttpStatus.CONFLICT);
            }
        }
        if (null != cartRequest.getUserPhoneNumber()) {
            Cart cart = cartRepository.findByUserPhoneNumber(cartRequest.getUserPhoneNumber());
            if (null != cart) {
                Double cartTotal = cartRequest.getSelectedSlots().stream().map(x -> x.getPrice())
                        .collect(Collectors.summingDouble(Double::intValue));
                cartTotal = cartTotal + cart.getCartTotal();
                List<Slot> selectedSlotList = cart.getSelectedSlots();
                selectedSlotList.addAll(cartRequest.getSelectedSlots());
                Cart saveCart = new Cart(cart.get_cartId(),
                        cartRequest.getUserPhoneNumber(),
                        selectedSlotList,
                        cartTotal,
                        LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                );
                Cart savedCart = cartRepository.save(saveCart);
                CartResponse response = new CartResponse(savedCart);
                return response;
            } else {
                Double cartTotal = cartRequest.getSelectedSlots().stream().map(x -> x.getPrice())
                        .collect(Collectors.summingDouble(Double::intValue));
                Cart saveCart = new Cart(
                        cartRequest.getUserPhoneNumber(),
                        cartRequest.getSelectedSlots(),
                        cartTotal,
                        LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                );
                Cart savedCart = cartRepository.insert(saveCart);
                CartResponse response = new CartResponse(savedCart);
                return response;
            }
        } else {
            Cart cart = cartRepository.findBy_cartId(cartRequest.getCartId());
            if (null != cart) {
                Double cartTotal = cartRequest.getSelectedSlots().stream().map(x -> x.getPrice())
                        .collect(Collectors.summingDouble(Double::intValue));
                cartTotal = cartTotal + cart.getCartTotal();
                List<Slot> selectedSlotList = cart.getSelectedSlots();
                selectedSlotList.addAll(cartRequest.getSelectedSlots());
                Cart saveCart = new Cart(cart.get_cartId(),
                        cartRequest.getUserPhoneNumber(),
                        selectedSlotList,
                        cartTotal,
                        LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                );
                Cart savedCart = cartRepository.save(saveCart);
                CartResponse response = new CartResponse(savedCart);
                return response;
            } else {
                Double cartTotal = cartRequest.getSelectedSlots().stream().map(x -> x.getPrice())
                        .collect(Collectors.summingDouble(Double::intValue));
                Cart saveCart = new Cart(
                        cartRequest.getUserPhoneNumber(),
                        cartRequest.getSelectedSlots(),
                        cartTotal,
                        LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                );
                Cart savedCart = cartRepository.insert(saveCart);
                CartResponse response = new CartResponse(savedCart);
                return response;
            }
        }
    }

    @Override
    public CommonResponse getCart(String phoneNumber, String cartId) throws GeneralException {
        String successMessage = "";
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        List<Slot> slotToBeDeleted = new ArrayList<>();
        Double cartTotalToBeDeducted = 0D;
        Cart cart = null;
        if (null != phoneNumber) {
            cart = cartRepository.findByUserPhoneNumber(phoneNumber);
            if (null != cart) {
                //if slot is unavailable or booked remove it from cart
                cart.getSelectedSlots().stream().forEach(slot -> {
                    BookedTimeSlot isBooked = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(slot.getTurfId(), LocalDateTime.of(slot.getDate(), slot.getStartTime()), slot.getDate());
                    if (null != isBooked) {
                        slotToBeDeleted.add(slot);
                    } else if (slot.getDate().isBefore(today) || (slot.getDate().equals(today) && slot.getStartTime().isBefore(now))) {
                        slotToBeDeleted.add(slot);
                    }
                });
                cartTotalToBeDeducted = slotToBeDeleted.stream().mapToDouble(x -> x.getPrice()).sum();
                if (slotToBeDeleted.size() != 0) {
                    Double newTotal = cart.getCartTotal() - cartTotalToBeDeducted;
                    cart.setCartTotal(newTotal);
                    cart.getSelectedSlots().removeAll(slotToBeDeleted);
                    cart = cartRepository.save(cart);
                    successMessage = "slot which are of past date and booked are removed from cart";
                }
            }
        } else if (null != cartId) {
            cart = cartRepository.findBy_cartId(cartId);
            if (null != cart) {
                //if slot is unavailable or booked remove it from cart
                cart.getSelectedSlots().stream().forEach(slot -> {
                    BookedTimeSlot isBooked = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(slot.getTurfId(), LocalDateTime.of(slot.getDate(), slot.getStartTime()), slot.getDate());
                    if (null != isBooked) {
                        slotToBeDeleted.add(slot);
                    } else if (slot.getDate().isBefore(today) || (slot.getDate().equals(today) && slot.getStartTime().isBefore(now))) {
                        slotToBeDeleted.add(slot);
                    }
                });
                cartTotalToBeDeducted = slotToBeDeleted.stream().mapToDouble(x -> x.getPrice()).sum();
                if (slotToBeDeleted.size() != 0) {
                    Double newTotal = cart.getCartTotal() - cartTotalToBeDeducted;
                    cart.setCartTotal(newTotal);
                    cart.getSelectedSlots().removeAll(slotToBeDeleted);
                    cart = cartRepository.save(cart);
                    successMessage = "slot which are of past date and booked are removed from cart";
                }
            }
        }
        if (cart != null) {
            CartResponse response = new CartResponse(cart);
            //pay now and pay at site
            Double payNow = 0D;
            for (Slot slot : response.getSelectedSlots()) {
                List<Double> priceList = configService.minPayPrice(slot.getDate().toString());
                if (slot.getTurfId().equalsIgnoreCase(Turfs.TURF01.name())) {
                    payNow = payNow + priceList.get(0);
                } else if (slot.getTurfId().equalsIgnoreCase(Turfs.TURF02.name())) {
                    payNow = payNow + priceList.get(1);
                } else if (slot.getTurfId().equalsIgnoreCase(Turfs.TURF03.name())) {
                    payNow = payNow + priceList.get(2);
                } else {
                    throw new GeneralException("Turf id is not valid :" + slot.getTurfId(), HttpStatus.BAD_REQUEST);
                }
            }
            response.setPayNow(payNow);
            response.setPayAtSite(response.getCartTotal() - payNow);
            CommonResponse commonResponse = new CommonResponse(response);
            return ResponseUtilities.createSucessResponseWithMessage(commonResponse, successMessage);
        }
        return null;
    }

    @Override
    public CartResponse removeFromCart(RemoveCartRequest removeCartRequest) throws GeneralException {
        if (null != removeCartRequest.getUserPhoneNumber()) {
            Cart cart = cartRepository.findByUserPhoneNumber(removeCartRequest.getUserPhoneNumber());
            List<Slot> removeSlotFromCart = new ArrayList<>();
            Double deductPrice = 0D;
            if (null != cart) {
                for (Slot slot : cart.getSelectedSlots()) {
                    if (slot.getDate().equals(removeCartRequest.getRemoveSlot().getDate()) && slot.getTurfId().equals(removeCartRequest.getRemoveSlot().getTurfId()) && slot.getStartTime().equals(removeCartRequest.getRemoveSlot().getStartTime())) {
                        removeSlotFromCart.add(slot);
                        deductPrice += slot.getPrice();
                    }
                }
                cart.setCartTotal(cart.getCartTotal() - deductPrice);
                cart.getSelectedSlots().removeAll(removeSlotFromCart);
                Cart savedCart = cartRepository.save(cart);
                CartResponse cartResponse = new CartResponse(savedCart);
                return cartResponse;
            } else {
                throw new GeneralException("Cart is empty", HttpStatus.NOT_FOUND);
            }
        } else if (null != removeCartRequest.getCartId()) {
            Cart cartWithoutUser = cartRepository.findBy_cartId(removeCartRequest.getCartId());
            List<Slot> removeSlotFromCart = new ArrayList<>();
            Double deductPrice = 0D;
            if (null != cartWithoutUser) {
                for (Slot slot : cartWithoutUser.getSelectedSlots()) {
                    if (slot.getDate().equals(removeCartRequest.getRemoveSlot().getDate()) && slot.getTurfId().equals(removeCartRequest.getRemoveSlot().getTurfId()) && slot.getStartTime().equals(removeCartRequest.getRemoveSlot().getStartTime())) {
                        removeSlotFromCart.add(slot);
                        deductPrice += slot.getPrice();
                    }
                }
                cartWithoutUser.setCartTotal(cartWithoutUser.getCartTotal() - deductPrice);
                cartWithoutUser.getSelectedSlots().removeAll(removeSlotFromCart);
                Cart savedCart = cartRepository.save(cartWithoutUser);
                CartResponse cartResponse = new CartResponse(savedCart);
                return cartResponse;
            } else {
                throw new GeneralException("Identification required", HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

    @Override
    public GetAllSlotsResponseForPhoneUser getAllSlotsByDatePhoneUser(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        List<String> turfIDs = Arrays.asList("turf01", "turf02", "turf03");
        getAllSlotsRequest.setTurfIds(turfIDs);
        GetAllSlotsResponse allSlotsByDate = this.getAllSlotsByDate(getAllSlotsRequest);
        GetAllSlotsResponseForPhoneUser response = new GetAllSlotsResponseForPhoneUser();
        List<TimeSlotResponse> commonSlotList = new LinkedList<>();
        commonSlotList.addAll(allSlotsByDate.getTurf01());
        commonSlotList.addAll(allSlotsByDate.getTurf02());
        commonSlotList.addAll(allSlotsByDate.getTurf03());

        Map<String, List<TimeSlotResponse>> map = new LinkedHashMap<>();
        commonSlotList.stream().forEach(slot -> {
            if (map.containsKey(slot.getStartTime().toString())) {
                List<TimeSlotResponse> temp = map.get(slot.getStartTime().toString());
                temp.add(slot);
            } else {
                List<TimeSlotResponse> temp = new LinkedList<>();
                temp.add(slot);
                map.put(slot.getStartTime().toString(), temp);
            }
        });
        response.setSlotList(map);
        return response;
    }

    @Override
    public GetAllSlotsResponseForPhoneUser getAllSlotsByDateCommon(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        List<String> turfIDs = Arrays.asList("turf01", "turf02", "turf03");
        getAllSlotsRequest.setTurfIds(turfIDs);
        GetAllSlotsResponse allSlotsByDate = this.getAllSlotsByDate(getAllSlotsRequest);
        GetAllSlotsResponseForPhoneUser response = new GetAllSlotsResponseForPhoneUser();
        List<TimeSlotResponse> commonSlotList = new LinkedList<>();
        commonSlotList.addAll(allSlotsByDate.getTurf01());
        commonSlotList.addAll(allSlotsByDate.getTurf02());
        commonSlotList.addAll(allSlotsByDate.getTurf03());

        Map<String, List<TimeSlotResponse>> map = new LinkedHashMap<>();
        commonSlotList.stream().forEach(slot -> {
            if (map.containsKey(slot.getStartTime().toString())) {
                List<TimeSlotResponse> temp = map.get(slot.getStartTime().toString());
                temp.add(slot);
            } else {
                List<TimeSlotResponse> temp = new LinkedList<>();
                temp.add(slot);
                map.put(slot.getStartTime().toString(), temp);
            }
        });

        List<String> keys = new ArrayList<>();
        map.forEach((key, value) -> {
            value.stream().forEach(slot -> {
                if (!slot.getStatus().equalsIgnoreCase(BookingStatus.AVAILABLE.name())) {
                    keys.add(key);
                }
            });
        });
        keys.forEach(key -> {
            map.remove(key);
        });
        response.setSlotList(map);
        return response;
    }

    @Override
    public CommonResponse getCartForPhoneUsers(String userID) throws GeneralException {
        CommonResponse<CartResponse> cartResponse = this.getCart(userID, null);
        List<Slot> slotList = cartResponse.getBody().getSelectedSlots();
        Map<String, List<Slot>> map = new LinkedHashMap<>();
        slotList.stream().forEach(slot -> {
            if (map.containsKey(slot.getStartTime().toString())) {
                List<Slot> temp = map.get(slot.getStartTime().toString());
                temp.add(slot);
            } else {
                List<Slot> temp = new LinkedList<>();
                temp.add(slot);
                map.put(slot.getStartTime().toString(), temp);
            }
        });
        CommonResponse response = new CommonResponse<Map>(map);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Kolkata") //0 30 11 * * ? - ss mm hh DD MM YYYY
    public void deleteNonUsedCart() {
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        time = time.minusDays(30);
        List<Cart> listDeletedCarts = cartRepository.deleteNonUsedCarts(time);
        log.info("Deleted carts", listDeletedCarts.toString());
    }
}
