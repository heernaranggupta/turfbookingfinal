package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Business;
import com.Turfbooking.documents.BusinessConfig;
import com.Turfbooking.documents.TurfSlotPricing;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.response.UpdateBusinessConfigResponse;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.BusinessResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.request.UpdateBusinessConfigRequest;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.repository.BusinessConfigRepository;
import com.Turfbooking.repository.BusinessRepository;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.CommonUtilities;
import com.Turfbooking.utils.JwtTokenUtil;
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

@Service
public class BusinessServiceImpl implements BusinessService {

    private BusinessRepository businessRepository;
    private JwtTokenUtil jwtTokenUtil;
    private BookedTimeSlotRepository bookedTimeSlotRepository;
    private BusinessConfigRepository businessConfigRepository;

    @Autowired
    public BusinessServiceImpl(JwtTokenUtil jwtTokenUtil, BusinessRepository businessRepository, BookedTimeSlotRepository bookedTimeSlotRepository, BusinessConfigRepository businessConfigRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.businessRepository = businessRepository;
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
        this.businessConfigRepository = businessConfigRepository;
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
    public CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest) throws GeneralException {
        String username = createBusinessLoginRequest.getUsername();
        String password = CommonUtilities.getEncryptedPassword(createBusinessLoginRequest.getPassword());
        Business business = businessRepository.findByUsernameAndPassword(username, password);
        if (business != null) {
            String token = jwtTokenUtil.generateToken(business.getPhoneNumber(), accessSecret, (accessTokenValidity));
            String refreshToken = jwtTokenUtil.generateToken(business.getPhoneNumber(), refreshSecret, (refreshTokenValidity));
            BusinessResponse businessResponse = new BusinessResponse(business);
            CreateBusinessLoginResponse response = CreateBusinessLoginResponse.builder()
                    .businessResponse(businessResponse)
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();
            return response;
        } else {
            throw new GeneralException("Invalid Username  and Password", HttpStatus.UNAUTHORIZED);
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
    public BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest) throws GeneralException {

        Business isExistBusiness = businessRepository.findByPhoneNumber(bookTimeSlotRequest.getUserId());

        if(null == isExistBusiness) {
            throw new GeneralException("Invalid user id.",HttpStatus.OK);
        }
        //GET SLOT BY DATE AND SLOT NUMBER
        BookedTimeSlot slot = bookedTimeSlotRepository.findByDateAndSlotNumber(bookTimeSlotRequest.getSlotNumber(), bookTimeSlotRequest.getDate());

        if (slot == null) {
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .bookingId(CommonUtilities.getAlphaNumericString(5))
                    .userId(bookTimeSlotRequest.getUserId())
                    .date(LocalDateTime.of(bookTimeSlotRequest.getDate(), LocalTime.of(00,00)))
                    .slotNumber(bookTimeSlotRequest.getSlotNumber())
                    .turfId(bookTimeSlotRequest.getTurfId())
                    .status(BookingStatus.BOOKED_BY_BUSINESS.name())
                    .startTime(bookTimeSlotRequest.getStartTime())
                    .endTime(bookTimeSlotRequest.getEndTime())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();

            BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.insert(addNewBookedTimeSlot);
            BookTimeSlotResponse bookTimeSlotResponse = new BookTimeSlotResponse(bookedTimeSlot);
            return bookTimeSlotResponse;

        } else {
            throw new GeneralException("Slot already booked.", HttpStatus.CONFLICT);
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
    public RescheduleBookingResponse rescheduleBooking(CreateRescheduleBookingRequest createRescheduleBookingRequest)throws GeneralException {

        BookedTimeSlot bookedTimeSlot = bookedTimeSlotRepository.findByDateAndSlotNumber(createRescheduleBookingRequest.getSlotNumber(),createRescheduleBookingRequest.getDate());

        if (null != bookedTimeSlot) {
            bookedTimeSlot = BookedTimeSlot.builder()
                    ._id(bookedTimeSlot.get_id())
                    .bookingId(CommonUtilities.getAlphaNumericString(6))
                    .userId(createRescheduleBookingRequest.getUserId())
                    .slotNumber(createRescheduleBookingRequest.getSlotNumber())
                    .turfId(createRescheduleBookingRequest.getTurfId())
                    .date(LocalDateTime.of(createRescheduleBookingRequest.getDate(),LocalTime.of(00,00)))
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

        if (days >= 0) { //means today or in future
            List<List<BookTimeSlotResponse>> responseList = new ArrayList<>();
            for(String turf : turfs){

                List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDateAndTurfId(getAllSlotsBusinessRequest.getDate(),turf);
                List<BookTimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(turf, getAllSlotsBusinessRequest.getDate(), getAllSlotsBusinessRequest.getOpenTime(), getAllSlotsBusinessRequest.getCloseTime(), getAllSlotsBusinessRequest.getSlotDuration());

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
                    responseList.add(allSlotList);
            }
            GetAllSlotsResponse finalResponse = new GetAllSlotsResponse(responseList);
            return finalResponse;
        } else {
            throw new GeneralException("Date should be not in past.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BookTimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelRequest) {
        BookedTimeSlot timeSlot = bookedTimeSlotRepository.findByDateAndSlotNumber(cancelRequest.getSlotNumber(),cancelRequest.getDate());

        if (null != timeSlot) {
            timeSlot = BookedTimeSlot.builder()
                    ._id(timeSlot.get_id())
                    .bookingId(timeSlot.getBookingId())
                    .userId(timeSlot.getUserId())
                    .slotNumber(null)
                    .turfId(timeSlot.getTurfId())
                    .status(BookingStatus.CANCELLED_BY_BUSINESS.name())
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
            throw new GeneralException("No booked slot with booking id: " + cancelRequest.getTurfId() , HttpStatus.OK);
        }
    }

    private List<BookTimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String turfId, LocalDate date, LocalDateTime openTime, LocalDateTime closeTime, int durationInMinutes) {
           List<BookTimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalDateTime slotStartTime = openTime;
        LocalDateTime slotEndTime;
        int count = 1;

        Double price = null;

        BusinessConfig businessConfigList = businessConfigRepository.findByDate(date);
        if(null == businessConfigList){
            String day = date.getDayOfWeek().toString();
            businessConfigList = businessConfigRepository.findByDay(day);
        }
        List<TurfSlotPricing> pricing = businessConfigList.getPricing();
        List<Integer> numberList = pricing.stream()
                .map(x -> x.getSlotNumber())
                .collect(Collectors.toList());

        //slot end time should be before close time.
        while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime)) {
            slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
            if(numberList.contains(count)){
                price =pricing.get(count).getPrice();
                timeSlotsList.add(new BookTimeSlotResponse(turfId, count, BookingStatus.AVAILABLE.name(), date, price, slotStartTime, slotEndTime));
            }else {
                timeSlotsList.add(new BookTimeSlotResponse(turfId, count, BookingStatus.AVAILABLE.name(), date, 350.00/*default price*/, slotStartTime, slotEndTime));
            }
            slotStartTime = slotEndTime;
            count++;
        }

        return timeSlotsList;
    }

    @Override
    public BookTimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest makeUnavailableSlotRequest) {

        BookedTimeSlot slotExist = bookedTimeSlotRepository.findByDateAndSlotNumber(makeUnavailableSlotRequest.getSlotNumber(),makeUnavailableSlotRequest.getDate());

        if(null != slotExist){
            slotExist = BookedTimeSlot.builder()
                    ._id(slotExist.get_id())
                    .bookingId(slotExist.getBookingId())
                    .userId(slotExist.getUserId())
                    .turfId(slotExist.getTurfId())
                    .slotNumber(slotExist.getSlotNumber())
                    .date(LocalDateTime.of(makeUnavailableSlotRequest.getDate(),LocalTime.of(00,00)))
                    .startTime(slotExist.getStartTime())
                    .endTime(slotExist.getEndTime())
                    .status(BookingStatus.NOT_AVAILABLE.name()) //this is cancelled by business and made unavailable.
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .build();
            BookedTimeSlot cancelledAndUnavailableSlot = bookedTimeSlotRepository.save(slotExist);
            BookTimeSlotResponse response = new BookTimeSlotResponse(cancelledAndUnavailableSlot);
            return response;
        }else{
            slotExist = BookedTimeSlot.builder()
                    .slotNumber(makeUnavailableSlotRequest.getSlotNumber())
                    .turfId(makeUnavailableSlotRequest.getTurfId())
                    .date(LocalDateTime.of(makeUnavailableSlotRequest.getDate(),LocalTime.of(00,00)))
                    .status(BookingStatus.NOT_AVAILABLE.name())
                    .timeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))
                    .startTime(makeUnavailableSlotRequest.getStartTime())
                    .endTime(makeUnavailableSlotRequest.getEndTime())
                    .build();

            BookedTimeSlot unavailableSlot = bookedTimeSlotRepository.insert(slotExist);
            BookTimeSlotResponse response = new BookTimeSlotResponse(unavailableSlot);
            return response;
        }
    }

    @Override
    public UpdateBusinessConfigResponse updateBusinessConfig(UpdateBusinessConfigRequest updateRequest) {

        Business isBusinessExist = businessRepository.findByUsername(updateRequest.getBusinessId());

        if(null != isBusinessExist){
            BusinessConfig saveConfig = BusinessConfig.builder()
                    .day(updateRequest.getDay())
                    .date(updateRequest.getDate())
                    .openTime(updateRequest.getOpenTime())
                    .closeTime(updateRequest.getCloseTime())
                    .pricing(updateRequest.getPricings())
                    .build();

            BusinessConfig savedConfig = businessConfigRepository.save(saveConfig);
            UpdateBusinessConfigResponse response = new UpdateBusinessConfigResponse(savedConfig);
            return  response;
        }else {
            throw new GeneralException("Please provide valid business id.",HttpStatus.OK);
        }
    }


}
