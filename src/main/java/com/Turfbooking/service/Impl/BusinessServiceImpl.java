package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.CancelledSlot;
import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import com.Turfbooking.documents.User;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.common.Location;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.enums.Roles;
import com.Turfbooking.models.enums.Turfs;
import com.Turfbooking.models.mics.CustomUserDetails;
import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.CancelledSlotRepository;
import com.Turfbooking.repository.OpenCloseTimeRepository;
import com.Turfbooking.repository.StartEndTimeRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
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
    public BusinessServiceImpl(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, BookedTimeSlotRepository bookedTimeSlotRepository, CancelledSlotRepository cancelledSlotRepository, OpenCloseTimeRepository openCloseTimeRepository, StartEndTimeRepository startEndTimeRepository) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cancelledSlotRepository = cancelledSlotRepository;
        this.openCloseTimeRepository = openCloseTimeRepository;
        this.startEndTimeRepository = startEndTimeRepository;
    }

    @Override
    public CreateUserResponse createBusinessUser(CreateUserRequest createUserRequest) {
        User isBusinessExist = userRepository.findByPhoneNumber(createUserRequest.getPhoneNumber());
        if (null == isBusinessExist) {
            User saveBusiness = new User(createUserRequest.getName(),
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
            saveBusiness.setLocation(userLocation);
            User createdNewBusinessUser = userRepository.insert(saveBusiness);
            UserResponse userResponse = new UserResponse(createdNewBusinessUser);
            CustomUserDetails customUserDetails = new CustomUserDetails(createdNewBusinessUser);
            String token = jwtTokenUtil.generateToken(createdNewBusinessUser.getPhoneNumber(), customUserDetails, accessSecret, accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(createdNewBusinessUser.getPhoneNumber(), customUserDetails, refreshSecret, refreshTokenValidity);

            CreateUserResponse response = new CreateUserResponse(userResponse, token, refreshToken);
            return response;
        } else {
            throw new GeneralException("Username already exist", HttpStatus.CONFLICT);
        }

    }

    @Override
    public CreateUserResponse businessLogin(UserLoginRequest UserLoginRequest) throws GeneralException {
        String username = UserLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(UserLoginRequest.getPassword());
        User business = userRepository.findByPhoneNumberAndPassword(username, password);
        if (business != null && business.getRole().equalsIgnoreCase(Roles.ADMIN.name())) {
            UserResponse userResponse = new UserResponse(business);
            CustomUserDetails customBusinessUserDetails = new CustomUserDetails(business);
            String token = jwtTokenUtil.generateToken(business.getPhoneNumber(), customBusinessUserDetails, accessSecret, (accessTokenValidity));
            String refreshToken = jwtTokenUtil.generateToken(business.getPhoneNumber(), customBusinessUserDetails, refreshSecret, (refreshTokenValidity));
            CreateUserResponse businessUserResponse = new CreateUserResponse(userResponse, token, refreshToken);
            return businessUserResponse;
        } else {
            throw new GeneralException("Not authorised to login here", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest) {

        String phoneNumber = createUpdatePasswordRequest.getPhoneNumber();
        User business = userRepository.findByPhoneNumber(phoneNumber);

        if (business != null) {
            String password = CommonUtilities.getEncryptedPassword(createUpdatePasswordRequest.getPassword());
            business.setPassword(password);
            userRepository.save(business);
            CreatePasswordResponse response = CreatePasswordResponse.builder()
                    .message("Password reset successfully")
                    .build();
            return response;
        } else {
            throw new GeneralException("Incorrect UserName", HttpStatus.UNAUTHORIZED);
        }
    }

    //need to be fixed
    @Override
    public CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest) throws GeneralException {
        User business = userRepository.findByPhoneNumber(updateBusinessRequest.getPhoneNumber());
        if (business != null) {
            business.setUsername(updateBusinessRequest.getUsername());
            User businessInserted = userRepository.save(business);
            CreateBusinessUpdateResponse createBusinessUpdateResponse = CreateBusinessUpdateResponse.builder()
                    .message("Data updated Successfully")
                    .build();
            return createBusinessUpdateResponse;
        } else {
            throw new GeneralException("Please Provide phone number for update", HttpStatus.OK);
        }
    }

    @Override
    public RescheduleBookingResponse rescheduleBooking(CreateRescheduleBookingRequest createRescheduleBookingRequest) throws GeneralException {
        BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(createRescheduleBookingRequest.getTurfId(), LocalDateTime.of(createRescheduleBookingRequest.getDate(), createRescheduleBookingRequest.getStartTime()), createRescheduleBookingRequest.getDate());
        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(createRescheduleBookingRequest.getUserId())
                    .turfId(createRescheduleBookingRequest.getTurfId())
                    .date(createRescheduleBookingRequest.getDate())
                    .status(BookingStatus.RESCHEDULED_BY_BUSINESS.name())
                    .startTime(LocalDateTime.of(createRescheduleBookingRequest.getDate(), createRescheduleBookingRequest.getStartTime()))
                    .endTime(LocalDateTime.of(createRescheduleBookingRequest.getDate(), createRescheduleBookingRequest.getEndTime()))
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();

            BookedTimeSlot updatedBookedSlot = bookedTimeSlotRepository.save(bookedTimeSlot);
            RescheduleBookingResponse response = new RescheduleBookingResponse(updatedBookedSlot);
            return response;
        } else {
            throw new GeneralException("Invalid booking id.", HttpStatus.OK);
        }
    }

    @Override
    public GetAllSlotsResponse getAllSlots(GetAllSlotsBusinessRequest getAllSlotsBusinessRequest) throws GeneralException {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int days = getAllSlotsBusinessRequest.getDate().compareTo(today);

        OpenCloseTime openCloseTime = openCloseTimeRepository.findByDate(getAllSlotsBusinessRequest.getDate());
        if (null == openCloseTime) {
            DayOfWeek day = getAllSlotsBusinessRequest.getDate().getDayOfWeek();
            openCloseTime = openCloseTimeRepository.findByDay(day.toString());
        }

        //get all turfs which requested for slots
        List<String> turfs = getAllSlotsBusinessRequest.getTurfIds();
        GetAllSlotsResponse finalResponse = new GetAllSlotsResponse();
        if (days >= 0) { //means today or in future
            List<List<TimeSlotResponse>> responseList = new ArrayList<>();
            for (String turf : turfs) {
                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsBusinessRequest.getDate(), turf);
                List<TimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(turf, getAllSlotsBusinessRequest.getDate(), openCloseTime.getOpenTime(), openCloseTime.getCloseTime(), getAllSlotsBusinessRequest.getSlotDuration());
                List<LocalDateTime> startDateTimeList = slotFromDB.stream()
                        .map(x -> x.getStartTime())
                        .collect(Collectors.toList());

                for (int i = 0; i < allSlotList.size(); i++) {
                    if (startDateTimeList.contains(allSlotList.get(i).getStartTime())) {
                        for (int j = 0; j < slotFromDB.size(); j++) {
                            if (allSlotList.get(i).getStartTime().equals(slotFromDB.get(j).getStartTime())) {
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

    @Override
    public List<TimeSlotResponse> viewAllBooking(BusinessViewAllBookingRequest businessViewAllBookingRequest) {
        LocalDate fromDate = (null != businessViewAllBookingRequest.getFromDate()) ? businessViewAllBookingRequest.getFromDate() : LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate toDate = (null != businessViewAllBookingRequest.getToDate()) ? businessViewAllBookingRequest.getToDate() : LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(7);
        String status = businessViewAllBookingRequest.getStatus();
        List<BookedTimeSlot> bookedList = new ArrayList<>();
        if (null != status && status.equalsIgnoreCase("BOOKED")) {
            bookedList = bookedTimeSlotRepository.findAllByDate(fromDate, toDate);
            List<TimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            Collections.reverse(responseList);
            return responseList;
        } else if (null != status && status.equalsIgnoreCase("CANCELLED")) {
            List<CancelledSlot> cancelledSlotList = cancelledSlotRepository.findAllByDate(fromDate, toDate);
            List<TimeSlotResponse> responseList = new ArrayList<>();
            for (CancelledSlot slot : cancelledSlotList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            Collections.reverse(responseList);
            return responseList;
        } else if (null != status && status.equalsIgnoreCase("ALL")) {
            bookedList = bookedTimeSlotRepository.findAllByDate(fromDate, toDate);
            List<TimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            List<CancelledSlot> cancelledSlotList = cancelledSlotRepository.findAllByDate(fromDate, toDate);
            for (CancelledSlot slot : cancelledSlotList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            responseList.sort(Comparator.comparing(TimeSlotResponse::getTimestamp));
            Collections.reverse(responseList);
            return responseList;
        } else {
            throw new GeneralException("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelRequest) {
        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(cancelRequest.getTurfId(), LocalDateTime.of(cancelRequest.getDate(), cancelRequest.getStartTime()), cancelRequest.getDate());
        if (null != timeSlot) {
            CancelledSlot cancelledSlot = new CancelledSlot(timeSlot);
            cancelledSlot.setStatus(BookingStatus.CANCELLED_BY_BUSINESS.name());
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
    public TimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest makeUnavailableSlotRequest) {

        BookedTimeSlot slotExist = bookedTimeSlotRepository.findByTurfIdAndStartTimeAndDate(makeUnavailableSlotRequest.getTurfId(), LocalDateTime.of(makeUnavailableSlotRequest.getDate(), makeUnavailableSlotRequest.getStartTime()), makeUnavailableSlotRequest.getDate());

        if (null != slotExist) {
            slotExist = BookedTimeSlot.builder()
                    ._id(slotExist.get_id())
                    .bookingId(slotExist.getBookingId())
                    .userId(slotExist.getUserId())
                    .turfId(slotExist.getTurfId())
                    .date(makeUnavailableSlotRequest.getDate())
                    .startTime(slotExist.getStartTime())
                    .endTime(slotExist.getEndTime())
                    .status(BookingStatus.NOT_AVAILABLE.name()) //this is cancelled by business and made unavailable.
                    .status(BookingStatus.CANCELLED_BY_BUSINESS.name() + " AND " + BookingStatus.NOT_AVAILABLE) //this is cancelled by business and made unavailable.
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            BookedTimeSlot cancelledAndUnavailableSlot = bookedTimeSlotRepository.save(slotExist);
            TimeSlotResponse response = new TimeSlotResponse(cancelledAndUnavailableSlot);
            return response;
        } else {
            slotExist = BookedTimeSlot.builder()
                    .turfId(makeUnavailableSlotRequest.getTurfId())
                    .date(makeUnavailableSlotRequest.getDate())
                    .status(BookingStatus.NOT_AVAILABLE.name())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .startTime(LocalDateTime.of(makeUnavailableSlotRequest.getDate(), makeUnavailableSlotRequest.getStartTime()))
                    .endTime(LocalDateTime.of(makeUnavailableSlotRequest.getDate(), makeUnavailableSlotRequest.getEndTime()))
                    .build();

            BookedTimeSlot unavailableSlot = bookedTimeSlotRepository.insert(slotExist);
            TimeSlotResponse response = new TimeSlotResponse(unavailableSlot);
            return response;
        }
    }

    @Override
    public List<UserResponse> getAllBusinessUsers() throws GeneralException {
        List<User> allBusinessUsers = userRepository.findByRole("ADMIN");
        List<UserResponse> businessResponses = new ArrayList<>();
        if (0 != allBusinessUsers.size()) {
            for (User business : allBusinessUsers) {
                UserResponse userResponse = new UserResponse(business);
                businessResponses.add(userResponse);
            }
        }
        return businessResponses;
    }
}
