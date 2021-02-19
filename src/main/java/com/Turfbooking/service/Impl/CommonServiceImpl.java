package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Cart;
import com.Turfbooking.documents.Order;
import com.Turfbooking.documents.Otp;
import com.Turfbooking.documents.SlotsInBookingTemp;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.miscellaneous.StringConstants;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.enums.OtpActiveStatus;
import com.Turfbooking.models.enums.OtpStatus;
import com.Turfbooking.models.enums.Roles;
import com.Turfbooking.models.enums.UserStatus;
import com.Turfbooking.models.externalCalls.ExternalOtpCallResponse;
import com.Turfbooking.models.mics.CustomUserDetails;
import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.OrderRequest;
import com.Turfbooking.models.request.SlotValidationRequest;
import com.Turfbooking.models.request.TimeSlotRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.OrderResponse;
import com.Turfbooking.models.response.SlotValidationResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.CartRepository;
import com.Turfbooking.repository.OrderRepository;
import com.Turfbooking.repository.OtpRepository;
import com.Turfbooking.repository.SlotsInBookingTempRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.CommonService;
import com.Turfbooking.service.PaymentService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    public static final String OTP_SENT_SUCCESS = "Otp Generated Successfully";

    private JwtTokenUtil jwtTokenUtil;
    private OtpRepository otpRepository;
    private RestTemplate restTemplate;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
    private CartRepository cartRepository;
    private PaymentService paymentService;
    private SlotsInBookingTempRepository slotsInBookingTempRepository;

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

    @Autowired
    public CommonServiceImpl(JwtTokenUtil jwtTokenUtil, OtpRepository otpRepository, RestTemplate restTemplate, UserRepository userRepository, OrderRepository orderRepository, BookedTimeSlotRepository bookedTimeSlotRepository, CartRepository cartRepository, PaymentService paymentService, SlotsInBookingTempRepository slotsInBookingTempRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.otpRepository = otpRepository;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cartRepository = cartRepository;
        this.paymentService = paymentService;
        this.slotsInBookingTempRepository = slotsInBookingTempRepository;
    }

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
        msg.setSubject("Turf Booking Otp");
        msg.setText("Your Verification code is : " + otp);
        javaMailSender.send(msg);
        return 1;
    }

    @SneakyThrows
    private int sendOtp(String phoneNumber, Integer otp) throws GeneralException {
        String messageWithOTP = StringConstants.baseMessageForOTPSMS.replace(StringConstants.otpReplacementPart, otp.toString());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(StringConstants.baseURLForOTPService)
                .queryParam("user", "REBOUNCE")
                .queryParam("password", "REBOUNCE")
                .queryParam("senderid", "REBOUN")
                .queryParam("channel", "Trans")
                .queryParam("DCS", 0)
                .queryParam("flashsms", 0)
                .queryParam("number", phoneNumber)
                .queryParam("text", messageWithOTP)
                .queryParam("route", 07);
        String uri = builder.toUriString();
//        http://msg.balajitech.co.in/api/mt/SendSMS?user=REBOUNCE&password=REBOUNCE&senderid=REBOUN&channel=Trans&DCS=0&flashsms=0&number=+919724500674&text=793287+is+your+Rebounce+Turf+verification+code.+Enjoy+on+Surat's+biggest+and+tallest+turf.+Don't+forget+to+explore+Rebounce+from+inside+too.+Keep+Bouncing!&route=7
        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        builder.toUriString(),
                        String.class);
        JSONObject jsonResponse = new JSONObject(response);
        String str = jsonResponse.getString("body");
        jsonResponse = new JSONObject(str);
        ObjectMapper mapper = new ObjectMapper();
        ExternalOtpCallResponse externalOtpCallResponse = mapper.readValue(jsonResponse.toString(), ExternalOtpCallResponse.class);
        if (!response.getStatusCode().name().equalsIgnoreCase(HttpStatus.OK.name())) {
            log.error(externalOtpCallResponse.toString());
            throw new GeneralException("Error in sending OTP, please try again after sometime.", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return 1;
        }
    }

    @Override
    public ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest) {
        String phoneNumber = validateOtpRequest.getPhoneNumber();
        String countryCode = validateOtpRequest.getCountryCode();
        String phoneNumberWithCountryCode = null;
        if (StringUtils.isNotBlank(phoneNumber) && StringUtils.isNotBlank(countryCode))
            phoneNumberWithCountryCode = StringUtils.join(countryCode, phoneNumber);
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
            if (validateOtpResponse.getOtpStatus().equalsIgnoreCase(OtpStatus.VALID.name())) {
                CustomUserDetails customUserDetails = new CustomUserDetails(isUserOrNot);
                token = jwtTokenUtil.generateToken(phoneNumber, customUserDetails, accessSecret, accessTokenValidity);
                refreshToken = jwtTokenUtil.generateToken(phoneNumber, customUserDetails, refreshSecret, refreshTokenValidity);
                validateOtpResponse.setToken(token);
                validateOtpResponse.setRefreshToken(refreshToken);
                UserResponse userResponse = new UserResponse(isUserOrNot);
                validateOtpResponse.setUser(userResponse);
            }
            validateOtpResponse.setNameOfTheUser(isUserOrNot.getNameOfUser());
            validateOtpResponse.setUserStatus(UserStatus.EXISTINGUSER.name());
        } else {
            validateOtpResponse.setUserStatus(UserStatus.USERDOESNOTEXIST.name());
        }
        return validateOtpResponse;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) throws GeneralException {
        User isUserExist = userRepository.findByPhoneNumber(orderRequest.getUserId());
        if (null == isUserExist) {
            throw new GeneralException("User does not exist.", HttpStatus.NOT_FOUND);
        }
        List<TimeSlotRequest> timeSlotRequests = new ArrayList<>();
        List<TimeSlotResponse> timeSlotResponses = new ArrayList<>();
        for (TimeSlotRequest request : orderRequest.getTimeSlots()) {
            BookedTimeSlot slot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(request.getTurfId(), LocalDateTime.of(request.getDate(), request.getStartTime()), request.getDate());
            if (null == slot) {
                timeSlotRequests.add(request);
            } else {
                throw new GeneralException("slot with start time " + slot.getStartTime() + " on date " + slot.getDate() + " is already booked or in booking process", HttpStatus.CONFLICT);
            }
        }
        List<BookedTimeSlot> bookedTimeSlotList = bookSlot(timeSlotRequests, orderRequest.getUserId());
        List<String> bookingIdList = bookedTimeSlotList.stream()
                .map(x -> x.getBookingId())
                .collect(Collectors.toList());
        Order saveOrder = Order.builder()
                .userId(orderRequest.getUserId())
                .timeSlots(bookingIdList)
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                .build();
        Order savedOrder = orderRepository.save(saveOrder);
        List<Cart> isDeleted = cartRepository.deleteByUserPhoneNumber(orderRequest.getUserId());
        String paymentId = null;
        if (null != savedOrder) {
            paymentId = paymentService.addPaymentDetails(orderRequest.getTransactionId(), savedOrder.get_id(), orderRequest.getUserId());
        }
        if (null == isDeleted) {
            throw new GeneralException("Error while deleting cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        OrderResponse response = new OrderResponse(savedOrder);
        for (BookedTimeSlot bookedTimeSlot : bookedTimeSlotList) {
            bookedTimeSlot.setOrderId(savedOrder.get_id());
            bookedTimeSlot = bookedTimeSlotRepository.save(bookedTimeSlot);
            TimeSlotResponse timeSlotResponse = new TimeSlotResponse(bookedTimeSlot);
            timeSlotResponses.add(timeSlotResponse);
            //delete from temp table
            SlotsInBookingTemp slot = slotsInBookingTempRepository.findByTurfIdAndStartTimeAndDate(bookedTimeSlot.getTurfId(), bookedTimeSlot.getStartTime(), bookedTimeSlot.getDate());
            slotsInBookingTempRepository.delete(slot);
        }
        response.setTimeSlots(timeSlotResponses);
        response.setOrderId(savedOrder.get_id());
        return response;
    }

    @Transactional
    private List<BookedTimeSlot> bookSlot(List<TimeSlotRequest> timeSlotRequestList, String userId) throws GeneralException {
        List<BookedTimeSlot> bookedTimeSlotList = new ArrayList<>();
        for (TimeSlotRequest timeSlotRequest : timeSlotRequestList) {
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .userId(userId)
                    .bookingId(CommonUtilities.getAlphaNumericString(5))
                    .date(timeSlotRequest.getDate())
                    .price(timeSlotRequest.getPrice())
                    .turfId(timeSlotRequest.getTurfId())
                    .startTime(LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getStartTime()))
                    .endTime(LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getEndTime()))
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            User user = userRepository.findByPhoneNumber(userId);
            if (null != user && user.getRole().equalsIgnoreCase(Roles.USER.name())) {
                addNewBookedTimeSlot.setStatus(BookingStatus.BOOKED_BY_USER.name());
            } else if (null != user && user.getRole().equalsIgnoreCase(Roles.ADMIN.name())) {
                addNewBookedTimeSlot.setStatus(BookingStatus.BOOKED_BY_BUSINESS.name());
            }
            BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.insert(addNewBookedTimeSlot);
            bookedTimeSlotList.add(bookedTimeSlot);
        }
        return bookedTimeSlotList;
    }

    @Override
    public SlotValidationResponse validateSlotAvailableOrNot(SlotValidationRequest slotValidationRequest, String userID) throws GeneralException {
        List<TimeSlotResponse> timeSlotResponses = new ArrayList<>();
        for (TimeSlotRequest timeSlotRequest : slotValidationRequest.getTimeSlotRequestList()) {
            Boolean flag = true;
            BookedTimeSlot isBookedTimeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(timeSlotRequest.getTurfId(), LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getStartTime()), timeSlotRequest.getDate());
            SlotsInBookingTemp slotsInBookingTemp = slotsInBookingTempRepository.findByTurfIdAndStartTimeAndDate(timeSlotRequest.getTurfId(), LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getStartTime()), timeSlotRequest.getDate());
            if (null != isBookedTimeSlot) {
                TimeSlotResponse timeSlotResponse = new TimeSlotResponse(timeSlotRequest);
                timeSlotResponse.setStatus(BookingStatus.NOT_AVAILABLE.name());
                timeSlotResponses.add(timeSlotResponse);
                flag = false;
            } else {
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
                LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
                if (timeSlotRequest.getDate().isBefore(today) || timeSlotRequest.getDate().equals(today)) {
                    if (timeSlotRequest.getStartTime().isBefore(now) || timeSlotRequest.getStartTime().equals(now)) {
                        TimeSlotResponse timeSlotResponse = new TimeSlotResponse(timeSlotRequest);
                        timeSlotResponse.setStatus(BookingStatus.NOT_AVAILABLE.name());
                        timeSlotResponses.add(timeSlotResponse);
                        flag = false;
                    }
                }
            }
            if (flag) {
                TimeSlotResponse timeSlotResponse = new TimeSlotResponse(timeSlotRequest);
                timeSlotResponse.setStatus(BookingStatus.AVAILABLE.name());
                //check this slots are exist in temp table and in booked table
                if (null != slotsInBookingTemp) {
                    if (slotsInBookingTemp.getUserId().equalsIgnoreCase(userID)) {
                        timeSlotResponse.setStatus(BookingStatus.AVAILABLE.name());
                    } else {
                        timeSlotResponse.setStatus(BookingStatus.NOT_AVAILABLE.name());
                    }
                } else {
                    //add slots in temp table
                    SlotsInBookingTemp addToTemp = new SlotsInBookingTemp(
                            timeSlotRequest.getTurfId(),
                            userID,
                            timeSlotRequest.getPrice(),
                            timeSlotRequest.getDate(),
                            LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getStartTime()),
                            LocalDateTime.of(timeSlotRequest.getDate(), timeSlotRequest.getEndTime()),
                            LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                    );
                    addToTemp = slotsInBookingTempRepository.save(addToTemp);
                }
                timeSlotResponses.add(timeSlotResponse);
            }
        }
        SlotValidationResponse slotValidationResponse = new SlotValidationResponse();
        slotValidationResponse.setTimeSlotResponses(timeSlotResponses);
        return slotValidationResponse;
    }

    @Override
    public List<TimeSlotResponse> getAllBookedSlotsByOrderId(String orderId) throws GeneralException {
        List<BookedTimeSlot> bookedTimeSlots = bookedTimeSlotRepository.findByOrderId(orderId);
        List<TimeSlotResponse> slotResponses = new ArrayList<>();
        if (bookedTimeSlots.size() > 0) {
            bookedTimeSlots.stream().forEach(bookedTimeSlot -> {
                TimeSlotResponse response = new TimeSlotResponse(bookedTimeSlot);
                slotResponses.add(response);
            });
            return slotResponses;
        } else {
            throw new GeneralException("No booked slots with order id :" + orderId, HttpStatus.NOT_FOUND);
        }
    }

    //CRON 5 min
    @Scheduled(cron = "0 0/3 * * * ?") //0 30 11 * * ? - ss mm hh DD MM YYYY
    public void deleteSlotsFromTempCart() throws GeneralException {
        log.info("Deleted carts -- START");
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        try {
            List<SlotsInBookingTemp> list = slotsInBookingTempRepository.findByTimestamp(time.minusMinutes(3));
            slotsInBookingTempRepository.deleteAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Deleted carts -- END");
    }

}
