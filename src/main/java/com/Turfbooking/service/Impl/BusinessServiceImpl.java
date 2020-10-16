package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.Business;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.enums.BookingStatus;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.BusinessResponse;
import com.Turfbooking.models.response.BusinessViewAllBookingResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    private BusinessRepository businessRepository;
    private JwtTokenUtil jwtTokenUtil;
    private BookedTimeSlotRepository bookedTimeSlotRepository;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository, JwtTokenUtil jwtTokenUtil, BookedTimeSlotRepository bookedTimeSlotRepository) {
        this.businessRepository = businessRepository;
        this.jwtTokenUtil = jwtTokenUtil;
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

        //GET SLOT BY DATE AND SLOT NUMBER
        BookedTimeSlot slot = bookedTimeSlotRepository.findByDateAndSlotNumber(bookTimeSlotRequest.getSlotNumber(), bookTimeSlotRequest.getDate());

        if (slot == null) {
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .bookingId(CommonUtilities.getAlphaNumericString(5))
                    .userId(bookTimeSlotRequest.getUserId())
                    .date(bookTimeSlotRequest.getDate())
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
    public GetAllSlotsResponse getAllSlots(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int days = getAllSlotsRequest.getDate().compareTo(today);

        if (days >= 0) { //means today or in future
            List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDate(getAllSlotsRequest.getDate());
//            List<Integer> integerList = new ArrayList();
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

            GetAllSlotsResponse response = new GetAllSlotsResponse(allSlotList);
            return response;
        } else {
            throw new GeneralException("Date should be not in past.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<BookTimeSlotResponse> viewAllBooking(BusinessViewAllBookingRequest businessViewAllBookingRequest) {
        LocalDate fromDate = (null != businessViewAllBookingRequest.getFromDate()) ? businessViewAllBookingRequest.getFromDate() : LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate toDate = (null != businessViewAllBookingRequest.getToDate()) ? businessViewAllBookingRequest.getToDate() : LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(7);
        String status = businessViewAllBookingRequest.getStatus();
        List<BookedTimeSlot> bookedList = new ArrayList<>();
        if (null != status) {
            bookedList = bookedTimeSlotRepository.findAllByDateAndStatus(fromDate, toDate, status);
            List<BookTimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                BookTimeSlotResponse response = new BookTimeSlotResponse(slot);
                responseList.add(response);
            }
        //    BusinessViewAllBookingResponse response = new BusinessViewAllBookingResponse(responseList);
            return responseList;
        } else if (status == null) {
            bookedList = bookedTimeSlotRepository.findAllByFromDateAndToDate(fromDate, toDate);
            List<BookTimeSlotResponse> responseList = new ArrayList<>();
            for (BookedTimeSlot slot : bookedList) {
                BookTimeSlotResponse response = new BookTimeSlotResponse(slot);
                responseList.add(response);
            }
            //BusinessViewAllBookingResponse response = new BusinessViewAllBookingResponse(responseList);
            return responseList;
        } else {
            throw new GeneralException("Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
}
