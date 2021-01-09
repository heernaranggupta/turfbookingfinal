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
import com.Turfbooking.models.request.*;
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

import java.sql.Struct;
import java.sql.Time;
import java.time.DayOfWeek;
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
            CustomUserDetails customUserDetails = new CustomUserDetails(isExist);
            String token = jwtTokenUtil.generateToken(username, customUserDetails, accessSecret, accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(username, customUserDetails, refreshSecret, refreshTokenValidity);

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

        //get all turfs which requested for slots
        List<String> turfs = getAllSlotsRequest.getTurfIds();
        GetAllSlotsResponse finalResponse = new GetAllSlotsResponse();
        if (days >= 0) { //means today or in future
            List<List<TimeSlotResponse>> responseList = new ArrayList<>();
            for (String turf : turfs) {
                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsRequest.getDate(), turf);
//                System.out.println(slotFromDB);
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

    @Override
    public GetAllSlotsResponse getAvailableSlot(GetAvailableSlotsRequest getAvailableSlotsRequest) {



        List<List<String>> bookturfSlotsId= new ArrayList<>();
        List<List<String>> allturfSlotsId= new ArrayList<>();
//        System.out.println();

        GetAllSlotsRequest getAllSlotsRequest = new GetAllSlotsRequest(getAvailableSlotsRequest);
        GetAllSlotsResponse allSlotsByDate = this.getAllSlotsByDate(getAllSlotsRequest);

        List<TimeSlotResponse> turf01 = allSlotsByDate.getTurf01();
        List<TimeSlotResponse> turf02 = allSlotsByDate.getTurf02();
        List<TimeSlotResponse> turf03 =  allSlotsByDate.getTurf03();

        if(null!=turf01)
            allturfSlotsId.add(this.generateSlotId(turf01));

        if(null!=turf02)
        allturfSlotsId.add(this.generateSlotId(turf02));

        if(null!=turf03)
        allturfSlotsId.add(this.generateSlotId(turf03));


//        System.out.println("\n\tAll TurfId\n");
//        System.out.println(allturfSlotsId);

        for(String turf:getAvailableSlotsRequest.getTurfIds()) {
//            System.out.println("HIHI");
            List<BookedTimeSlot> bookedTimeSlots = bookedTimeSlotRepository.findByDateAndTurfId(getAvailableSlotsRequest.getDate(),turf);

            List<TimeSlotResponse> bookingslots = new ArrayList<>();

            for(BookedTimeSlot bst:bookedTimeSlots){
                TimeSlotResponse bookedslotresponse = TimeSlotResponse.builder()
                        .bookingId(bst.getBookingId())
                        .userId(bst.getUserId())
                        .turfId(bst.getTurfId())
                        .price(bst.getPrice())
                        .status(bst.getStatus())
                        .date(bst.getDate())
                        .startTime(bst.getStartTime())
                        .endTime(bst.getEndTime())
                        .timestamp(bst.getTimeStamp())
                        .build();

                bookingslots.add(bookedslotresponse);



            }

//            System.out.println();

            bookturfSlotsId.add(this.generateSlotId(bookingslots));
//            allturfSlotsId.add(this.generateSlotId())
        }
//        System.out.println(bookturfSlotsId);

//        System.out.println("\n\tAll Slots\n");
        List<List<String>> allCopy = new ArrayList<>(allturfSlotsId);
        List<String> avaSlotsIds = new ArrayList<>();
        for(List<String> alls:allturfSlotsId) {

                for(List<String> books:bookturfSlotsId)
                {
                    for(String book:books)
                    {
                        if(alls.contains(book))
                        {
                            alls.remove(book);
                        }

                    }
                }


        }

//        System.out.println();
        return this.generateSlotsFromId(allturfSlotsId);
//        return null;
    }

    private GetAllSlotsResponse generateSlotsFromId(List<List<String>> tempSlotIds)
    {
        List<String> turf01Ids=new ArrayList<>();
        List<String> turf02Ids=new ArrayList<>();
        List<String> turf03Ids=new ArrayList<>();

        List<TimeSlotResponse> turf01 = new ArrayList<>();
        List<TimeSlotResponse> turf02= new ArrayList<>();
        List<TimeSlotResponse> turf03= new ArrayList<>();

        for(List<String> tempSlotId:tempSlotIds)
        {
            for(String temp:tempSlotId)
            {
                if(temp.startsWith("01"))
                {
                    turf01Ids.add(temp);
                }
                else if(temp.startsWith("02"))
                {
                    turf02Ids.add(temp);
                }
                else if(temp.startsWith("03"))
                {
                    turf03Ids.add(temp);
                }
            }
        }

        int hrs=0,min=0,day=0,month=0,yr=0;

//        String tempHrs="",tempMin="",tempDay="",tempMonth="";

        for(String temp01 : turf01Ids)
        {
//            System.out.println("Size\t"+turf01Ids.size());
//            tempHrs = ;
            hrs = Integer.parseInt(""+temp01.charAt(2)+temp01.charAt(3));
            min = Integer.parseInt(""+temp01.charAt(4)+temp01.charAt(5));
            day = Integer.parseInt(""+temp01.charAt(6)+temp01.charAt(7));
            month = Integer.parseInt(""+temp01.charAt(8)+temp01.charAt(9));
            yr = Integer.parseInt(""+temp01.charAt(10)+temp01.charAt(11)+temp01.charAt(12)+temp01.charAt(13));

            LocalTime time = LocalTime.of(hrs,min);
            LocalDate date = LocalDate.of(yr,month,day);
//            System.out.println(date+"\t"+time);
//            System.out.println(time+""+date);
            TimeSlotResponse build = new TimeSlotResponse();
                    build.setBookingId("null");
                    build.setUserId("null");
                    build.setTurfId(Turfs.TURF01.name());
                    build.setPrice(0D);
                    build.setStatus(BookingStatus.AVAILABLE.name());
                    build.setDate(date);
                    build.setStartTime(time);
                    build.setEndTime(time.plusMinutes(30));
                    build.setTimestamp(LocalDateTime.now());

//            System.out.println(build);
            turf01.add(build);
        }



//        String tempHrs="",tempMin="",tempDay="",tempMonth="";

        for(String temp02 : turf02Ids)
        {
//            tempHrs = ;
            hrs = Integer.parseInt(""+temp02.charAt(2)+temp02.charAt(3));
            min = Integer.parseInt(""+temp02.charAt(4)+temp02.charAt(5));
            day = Integer.parseInt(""+temp02.charAt(6)+temp02.charAt(7));
            month = Integer.parseInt(""+temp02.charAt(8)+temp02.charAt(9));
            yr = Integer.parseInt(""+temp02.charAt(10)+temp02.charAt(11)+temp02.charAt(12)+temp02.charAt(13));

            LocalTime time = LocalTime.of(hrs,min);
            LocalDate date = LocalDate.of(yr,month,day);
//            System.out.println(time+""+date);
            TimeSlotResponse build = TimeSlotResponse.builder()
                    .bookingId("null")
                    .userId("null")
                    .turfId(Turfs.TURF02.name())
                    .price(0D)
                    .status(BookingStatus.AVAILABLE.name())
                    .date(date)
                    .startTime(time)
                    .endTime(time.plusMinutes(30))
                    .timestamp(LocalDateTime.now())
                    .build();
            turf02.add(build);
        }

        for(String temp03 : turf03Ids)
        {
//            tempHrs = ;
            hrs = Integer.parseInt(""+temp03.charAt(2)+temp03.charAt(3));
            min = Integer.parseInt(""+temp03.charAt(4)+temp03.charAt(5));
            day = Integer.parseInt(""+temp03.charAt(6)+temp03.charAt(7));
            month = Integer.parseInt(""+temp03.charAt(8)+temp03.charAt(9));
            yr = Integer.parseInt(""+temp03.charAt(10)+temp03.charAt(11)+temp03.charAt(12)+temp03.charAt(13));

            LocalTime time = LocalTime.of(hrs,min);
            LocalDate date = LocalDate.of(yr,month,day);
//            System.out.println(time+""+date);
            TimeSlotResponse build = TimeSlotResponse.builder()
                    .bookingId("null")
                    .userId("null")
                    .turfId(Turfs.TURF03.name())
                    .price(0D)
                    .status(BookingStatus.AVAILABLE.name())
                    .date(date)
                    .startTime(time)
                    .endTime(time.plusMinutes(30))
                    .timestamp(LocalDateTime.now())
                    .build();

            turf03.add(build);
        }
        GetAllSlotsResponse getAllSlotsResponse = new GetAllSlotsResponse();

        getAllSlotsResponse.setTurf01(turf01);
        getAllSlotsResponse.setTurf02(turf02);
        getAllSlotsResponse.setTurf03(turf03);

        return getAllSlotsResponse;


//        return null;
    }

    private List<String> generateSlotId(List<TimeSlotResponse> timeSlotResponses)
    {
        List<String> slotIds = new ArrayList<>();
        String turfId="";

        for(TimeSlotResponse temp:timeSlotResponses)
        {
            if(temp.getTurfId().equals("turf01"))
            {
                turfId = "01";
            }
            else if(temp.getTurfId().equals("turf02"))
            {
                turfId = "02";
            }
            if(temp.getTurfId().equals("turf03"))
            {
                turfId = "03";
            }

            String day = ""+temp.getDate().getDayOfMonth();
            String month = ""+temp.getDate().getMonthValue();
            String hrs = ""+temp.getStartTime().getHour();
            String min = ""+temp.getStartTime().getMinute();
            String yr = ""+temp.getDate().getYear();
            if(day.length()<2)
            {
                day="0"+day;
            }


            if(month.length()<2){
                month = "0"+month;
            }


            if(hrs.length()<2){
                hrs = "0"+hrs;
            }


            if(min.length()<2){
                min = "0"+min;
            }
            slotIds.add(turfId+hrs+min+day+month+yr);
//            System.out.println();
//            System.out.println();
//            System.out.println("Turfid :"+turfId+"\tDay :"+day+"\tMonth"+month+"\tStartTime :"+"  hrs :"+hrs+"  minutes :"+min+"\tEndTime :"+temp.getEndTime());
        }

        return slotIds;
    }

    @Scheduled(cron = "0 15 10 1 * ?", zone = "Asia/Kolkata")
    public void deleteNonUsedCart(){
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        time = time.minusDays(30);
        List<Cart> listDeletedCarts = cartRepository.deleteNonUsedCarts(time);
        log.info("Deleted carts",listDeletedCarts.toString());
    }
}
