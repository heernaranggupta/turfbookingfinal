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
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.CancelledSlotRepository;
import com.Turfbooking.repository.CartRepository;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
    private CartRepository cartRepository;
    private CancelledSlotRepository cancelledSlotRepository;
    private OpenCloseTimeRepository openCloseTimeRepository;
    private StartEndTimeRepository startEndTimeRepository;

    @Value("${jwt.secret.accessToken}")
    private String accessSecret;
    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;
    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;
    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Autowired
    public UserServiceImpl(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, BookedTimeSlotRepository bookedTimeSlotRepository, CartRepository cartRepository, CancelledSlotRepository cancelledSlotRepository, OpenCloseTimeRepository openCloseTimeRepository, StartEndTimeRepository startEndTimeRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cartRepository = cartRepository;
        this.cancelledSlotRepository = cancelledSlotRepository;
        this.openCloseTimeRepository = openCloseTimeRepository;
        this.startEndTimeRepository = startEndTimeRepository;
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
            isExist = userRepository.findByEmailIdAndPassword(username, password);
        } else {
            isExist = userRepository.findByPhoneNumberAndPassword(username, password);
        }
        if (null != isExist) {
            CustomUserDetails customUserDetails = new CustomUserDetails(isExist);
            String token = jwtTokenUtil.generateToken(username, customUserDetails, accessSecret, accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(username, customUserDetails, refreshSecret, refreshTokenValidity);

            Cart getCart = cartRepository.findBy_cartId(userLoginRequest.getCartId());
            if (null != getCart) {
                Cart usersCart = cartRepository.findByUserPhoneNumber(username);
                Cart mergeCart = null;
                if (null != usersCart) {
                    List<Slot> slotList = new ArrayList<>();
                    if (null != usersCart.getSelectedSlots() && usersCart.getSelectedSlots().size() != 0) {
                        slotList.addAll(usersCart.getSelectedSlots());
                    }
                    if (null != getCart.getSelectedSlots() && getCart.getSelectedSlots().size() != 0) {
                        slotList.addAll(getCart.getSelectedSlots());
                    }
                    usersCart.setSelectedSlots(slotList);
                    mergeCart = cartRepository.save(usersCart);
                } else {
                    getCart.setUserPhoneNumber(username);
                    mergeCart = cartRepository.save(getCart);
                }
                if (null != mergeCart) {
                    cartRepository.delete(getCart);
                } else {
                    throw new GeneralException("Error in cart merging", HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }
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
            List<TimeSlotResponse> timeSlotResponses = new ArrayList<>();
            for (BookedTimeSlot bookedTimeSlot : bookedTimeSlots) {
                TimeSlotResponse response = new TimeSlotResponse(bookedTimeSlot);
                timeSlotResponses.add(response);
            }
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
    @Transactional
    public TimeSlotResponse cancelBookedSlot(CancelOrUnavailableSlotRequest cancelRequest) {
        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(cancelRequest.getTurfId(), cancelRequest.getStartTime(), cancelRequest.getDate());
        if (null != timeSlot) {
            CancelledSlot cancelledSlot = new CancelledSlot(timeSlot);
            cancelledSlot.setStatus(BookingStatus.CANCELLED_BY_USER.name());
            bookedTimeSlotRepository.deleteById(timeSlot.get_id());
            CancelledSlot savedInDB = cancelledSlotRepository.insert(cancelledSlot);
            if (null != savedInDB) {
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
        BookedTimeSlot isSlotBooked = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(updateRequest.getTurfId(), updateRequest.getStartTime(), updateRequest.getDate());
        if (null != isSlotBooked) {
            throw new GeneralException("Slot which you want to book is already booked.", HttpStatus.OK);
        }
        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(updateRequest.getUserId())
                    .turfId(updateRequest.getTurfId())
                    .price(updateRequest.getPrice())
                    .date(updateRequest.getDate())
                    .status(BookingStatus.RESCHEDULED_BY_USER.name())
                    .startTime(updateRequest.getStartTime())
                    .endTime(updateRequest.getEndTime())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            BookedTimeSlot updatedBookedSlot = bookedTimeSlotRepository.save(bookedTimeSlot);
            TimeSlotResponse response = new TimeSlotResponse(updatedBookedSlot);
            return response;
        } else {
            throw new GeneralException("Invalid booking id.", HttpStatus.OK);
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

        TimeZone timeZone = TimeZone.getDefault();

        LocalDateTime ldt1 = openCloseTime.getOpenTime().atDate(getAllSlotsRequest.getDate());
        LocalDateTime ldt2 = openCloseTime.getCloseTime().atDate(getAllSlotsRequest.getDate());
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        Instant instantOpen = ldt1.atZone(zoneId).toInstant();
        Instant instantClose = ldt2.atZone(zoneId).toInstant();
        LocalTime openTime = instantOpen.atZone(ZoneOffset.UTC).toLocalTime();
        LocalTime closeTime = instantClose.atZone(ZoneOffset.UTC).toLocalTime();

        openCloseTime.setOpenTime(openTime);
        openCloseTime.setCloseTime(closeTime);

        //get all turfs which requested for slots
        List<String> turfs = getAllSlotsRequest.getTurfIds();
        GetAllSlotsResponse finalResponse = new GetAllSlotsResponse();
        if (days >= 0) { //means today or in future
            List<List<TimeSlotResponse>> responseList = new ArrayList<>();
            for (String turf : turfs) {
                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsRequest.getDate(), turf);
                List<TimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(turf, getAllSlotsRequest.getDate(), openCloseTime.getOpenTime(), openCloseTime.getCloseTime(), getAllSlotsRequest.getSlotDuration());
                List<LocalTime> startDateTimeList = slotFromDB.stream()
                        .map(x -> x.getStartTime())
                        .collect(Collectors.toList());

                for (int i = 0; i < allSlotList.size(); i++) {
                    if (startDateTimeList.contains(allSlotList.get(i).getStartTime())) {
                        for (int j = 0; j < slotFromDB.size(); j++) {
                            if (allSlotList.get(i).getStartTime() == slotFromDB.get(j).getStartTime()) {
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

    private List<TimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String turfId, LocalDate date, LocalTime openTime, LocalTime closeTime, int durationInMinutes) {
        List<StartEndTime> startEndTimeList = startEndTimeRepository.findByDate(date);
        if (startEndTimeList.size() == 0) {
            DayOfWeek day = date.getDayOfWeek();
            startEndTimeList = startEndTimeRepository.findByDay(day.toString());
        }

        List<TimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalTime slotStartTime = openTime;
        LocalTime slotEndTime = null;

        Double price = null;
        for (StartEndTime startEndTime : startEndTimeList) {
            if (startEndTime.getTurfId().equalsIgnoreCase(turfId)) {
                //slot end time should be before close time.
                while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime.plusNanos(1))) {
                    slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
                    if ((startEndTime.getStartTime().equals(slotStartTime) || startEndTime.getStartTime().isAfter(slotStartTime)) && slotStartTime.isBefore(startEndTime.getEndTime()) && startEndTime.getTurfId().equalsIgnoreCase(turfId)) {
                        if (null != startEndTime.getPrice()) {
                            price = startEndTime.getPrice();
                        }
                    }
                    timeSlotsList.add(new TimeSlotResponse(turfId, price, BookingStatus.AVAILABLE.name(), date, slotStartTime, slotEndTime));
                    slotStartTime = slotEndTime;
                }
            }
        }
        return timeSlotsList;
    }

    @Override
    public CartResponse addToCart(CartRequest cartRequest) throws GeneralException {

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
    public CartResponse getCart(String phoneNumber, String cartId) throws GeneralException {
       if(null != phoneNumber){
           Cart cart = cartRepository.findByUserPhoneNumber(phoneNumber);
           if(null != cart){
               CartResponse response = new CartResponse(cart);
               return response;
           }
       } else if (null != cartId){
           Cart cart = cartRepository.findBy_cartId(cartId);
           if(null != cart){
               CartResponse response = new CartResponse(cart);
               return response;
           }
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
                cart.setCartTotal(cart.getCartTotal()-deductPrice);
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
                cartWithoutUser.setCartTotal(cartWithoutUser.getCartTotal()-deductPrice);
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

    @Scheduled(cron = "0 15 10 1 * ?", zone = "Asia/Kolkata")
    public void deleteNonUsedCart(){
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        time = time.minusDays(30);
        List<Cart> listDeletedCarts = cartRepository.deleteNonUsedCarts(time);
        log.info("Deleted carts",listDeletedCarts.toString());
    }
}
