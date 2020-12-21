package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Business;
import com.Turfbooking.documents.CancelledSlot;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.enums.Turfs;
import com.Turfbooking.models.mics.CustomBusinessUserDetails;
import com.Turfbooking.models.request.*;
import com.Turfbooking.models.response.*;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.BusinessRepository;
import com.Turfbooking.repository.CancelledSlotRepository;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    private BusinessRepository businessRepository;
    private JwtTokenUtil jwtTokenUtil;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
    private CancelledSlotRepository cancelledSlotRepository;

    @Value("${jwt.secret.accessToken}")
    private String accessSecret;
    @Value("${jwt.secret.refreshToken}")
    private String refreshSecret;
    @Value("${jwt.accessToken.validity}")
    private long accessTokenValidity;
    @Value("${jwt.refreshToken.validity}")
    private long refreshTokenValidity;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository, JwtTokenUtil jwtTokenUtil, BookedTimeSlotRepository bookedTimeSlotRepository, CancelledSlotRepository cancelledSlotRepository) {
        this.businessRepository = businessRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.cancelledSlotRepository = cancelledSlotRepository;
    }

    @Override
    public CreateBusinessResponse createBusinessUser(CreateBusinessRequest createBusinessRequest) {
        Business isBusinessExist = businessRepository.findByUsername(createBusinessRequest.getUsername());
        if(null == isBusinessExist){
            Business saveBusiness = new Business( createBusinessRequest.getUsername(),
                    createBusinessRequest.getPassword(),
                    createBusinessRequest.getPhoneNumber(),
                    createBusinessRequest.getCompanyName(),
                    createBusinessRequest.getRole() );

            Business savedBusiness = businessRepository.save(saveBusiness);
            CustomBusinessUserDetails customBusinessUserDetails = new CustomBusinessUserDetails(savedBusiness);
            String token = jwtTokenUtil.generateToken(createBusinessRequest.getUsername(),customBusinessUserDetails,accessSecret,accessTokenValidity);
            String refreshToken = jwtTokenUtil.generateToken(createBusinessRequest.getUsername(),customBusinessUserDetails,refreshSecret,refreshTokenValidity);
            CreateBusinessResponse response = new CreateBusinessResponse(new BusinessResponse(savedBusiness),token,refreshToken);
            return response;

        } else {
            throw new GeneralException("Username already exist",HttpStatus.OK);
        }

    }

    @Override
    public CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest) throws GeneralException {
        String username = createBusinessLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(createBusinessLoginRequest.getPassword());
        Business business = businessRepository.findByUsernameAndPassword(username, password);
        if (business != null) {
            CustomBusinessUserDetails businessUserDetails = new CustomBusinessUserDetails(business);
            String token = jwtTokenUtil.generateToken(business.getUsername(), businessUserDetails, accessSecret, (accessTokenValidity));
            String refreshToken = jwtTokenUtil.generateToken(business.getUsername(), businessUserDetails, refreshSecret, (refreshTokenValidity));
            BusinessResponse businessResponse = new BusinessResponse(business);
            CustomBusinessUserDetails customBusinessUserDetails = new CustomBusinessUserDetails(business);
            CreateBusinessLoginResponse response = CreateBusinessLoginResponse.builder()
                    .businessResponse(businessResponse)
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();
            return response;
        } else {
            throw new GeneralException("Invalid username and password", HttpStatus.UNAUTHORIZED);
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

    @Override
    public CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest) throws GeneralException {
        Business business = businessRepository.findByPhoneNumber(updateBusinessRequest.getPhoneNumber());
        if (business != null) {
            business.setUsername(updateBusinessRequest.getUsername());
            business.setCompanyName(updateBusinessRequest.getCompanyName());
            Business businessInserted = businessRepository.save(business);

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

        BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTime(createRescheduleBookingRequest.getTurfId(),createRescheduleBookingRequest.getStartTime());

        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(createRescheduleBookingRequest.getUserId())
                    .turfId(createRescheduleBookingRequest.getTurfId())
                    .date(createRescheduleBookingRequest.getDate())
                    .status(BookingStatus.RESCHEDULED_BY_BUSINESS.name())
                    .startTime(createRescheduleBookingRequest.getStartTime())
                    .endTime(createRescheduleBookingRequest.getEndTime())
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
        //get all turfs which requested for slots
        List<String> turfs = getAllSlotsBusinessRequest.getTurfIds();

        GetAllSlotsResponse finalResponse = new GetAllSlotsResponse();

        if (days >= 0) { //means today or in future
            List<List<TimeSlotResponse>> responseList = new ArrayList<>();
            for (String turf : turfs) {

                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsBusinessRequest.getDate(), turf);
                List<TimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(turf, getAllSlotsBusinessRequest.getDate(), getAllSlotsBusinessRequest.getOpenTime(), getAllSlotsBusinessRequest.getCloseTime(), getAllSlotsBusinessRequest.getSlotDuration());

                List<LocalDateTime> startDateTimeList = slotFromDB.stream()
                        .map(x -> x.getStartTime())
                        .collect(Collectors.toList());


//                allSlotList.stream().
//                        forEach((response) -> {
//                            if (integerList.contains(response.getSlotNumber())) {
//                                slotFromDB.stream().forEach((bookedSlot) -> {
//                                    if (response.getSlotNumber() == bookedSlot.getSlotNumber()) {
//                                        TimeSlotResponse bookedResponse = new TimeSlotResponse(bookedSlot);
//                                        allSlotList.set(response.getSlotNumber() - 1, bookedResponse);
//                                    }
//                                });
//                            }
//                        });


                for( int i=0;i<allSlotList.size();i++){
                    if (startDateTimeList.contains(allSlotList.get(i).getStartTime())) {
                        for (int j=0; j<slotFromDB.size();j++){
                            if (allSlotList.get(i).getStartTime() == slotFromDB.get(j).getStartTime()) {
                                TimeSlotResponse bookedResponse = new TimeSlotResponse(slotFromDB.get(j));
                                allSlotList.set(i,bookedResponse);
                            }
                        }
                    }
                }


                if(allSlotList.get(0).getTurfId().equals(Turfs.TURF01.getValue())){
                    finalResponse.setTurf01(allSlotList);
                }else if(allSlotList.get(0).getTurfId().equals(Turfs.TURF02.getValue())){
                    finalResponse.setTurf02(allSlotList);
                }else if(allSlotList.get(0).getTurfId().equals(Turfs.TURF03.getValue())){
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
        if (null != status) {
            bookedList = bookedTimeSlotRepository.findAllByDateAndStatus(fromDate, toDate, status);
            List<TimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            //    BusinessViewAllBookingResponse response = new BusinessViewAllBookingResponse(responseList);
            return responseList;
        } else if (status == null) {
            bookedList = bookedTimeSlotRepository.findAllByDate(fromDate, toDate);
            List<TimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                TimeSlotResponse response = new TimeSlotResponse(slot);
                responseList.add(response);
            }
            //BusinessViewAllBookingResponse response = new BusinessViewAllBookingResponse(responseList);
            return responseList;
        } else {
            throw new GeneralException("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelRequest) {
        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByTurfIdAndStartTime(cancelRequest.getTurfId(),cancelRequest.getStartTime());
        if (null != timeSlot) {
            CancelledSlot cancelledSlot = new CancelledSlot(timeSlot);
            cancelledSlot.setStatus(BookingStatus.CANCELLED_BY_USER.name());
            CancelledSlot savedInDB = cancelledSlotRepository.insert(cancelledSlot);
            BookedTimeSlot cancelled = bookedTimeSlotRepository.deleteBySlotId(timeSlot.get_id());
            if (null != cancelled && null != savedInDB) {
                TimeSlotResponse response = new TimeSlotResponse(savedInDB);
                return response;
            } else {
                throw new GeneralException("Error in cancellation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralException("No booked slot with booking id: " + cancelRequest.getTurfId(), HttpStatus.OK);
        }
    }

    private List<TimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String turfId, LocalDate date, LocalDateTime openTime, LocalDateTime closeTime, int durationInMinutes) {
        List<TimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalDateTime slotStartTime = openTime;
        LocalDateTime slotEndTime;

        //slot end time should be before close time.
        while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime)) {
            slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
            timeSlotsList.add(new TimeSlotResponse(turfId, 200.00, BookingStatus.AVAILABLE.name(), date, slotStartTime, slotEndTime));
            slotStartTime = slotEndTime;
        }

        return timeSlotsList;
    }

    @Override
    public TimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest makeUnavailableSlotRequest) {

        BookedTimeSlot slotExist = bookedTimeSlotRepository.findByTurfIdAndStartTime(makeUnavailableSlotRequest.getTurfId(),makeUnavailableSlotRequest.getStartTime());

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
                    .startTime(makeUnavailableSlotRequest.getStartTime())
                    .endTime(makeUnavailableSlotRequest.getEndTime())
                    .build();

            BookedTimeSlot unavailableSlot = bookedTimeSlotRepository.insert(slotExist);
            TimeSlotResponse response = new TimeSlotResponse(unavailableSlot);
            return response;
        }
    }
}
