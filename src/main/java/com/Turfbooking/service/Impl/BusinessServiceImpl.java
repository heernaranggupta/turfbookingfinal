package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.repository.BookedTimeSlotRepository;
import com.Turfbooking.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private BookedTimeSlotRepository bookedTimeSlotRepository;

    @Autowired
    public BusinessServiceImpl(BookedTimeSlotRepository bookedTimeSlotRepository) {
        this.bookedTimeSlotRepository = bookedTimeSlotRepository;
    }

    @Override
    public BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest) throws GeneralException {

        //GET SLOT BY DATE AND SLOT NUMBER
        BookedTimeSlot slot = bookedTimeSlotRepository.findByDateAndSlotNumber(bookTimeSlotRequest.getSlotNumber(), bookTimeSlotRequest.getDate());

        if (slot == null) {
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .userId(bookTimeSlotRequest.getUserId())
                    .date(bookTimeSlotRequest.getDate())
                    .slotNumber(bookTimeSlotRequest.getSlotNumber())
                    .companyId(bookTimeSlotRequest.getCompanyId())
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
    public GetAllSlotsResponse getAllSlots(GetAllSlotsRequest getAllSlotsRequest) throws GeneralException {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int days = getAllSlotsRequest.getDate().compareTo(today);

        if (days >= 0) { //means today or in future
            List<BookedTimeSlot> slotFromDB = bookedTimeSlotRepository.findByDate(getAllSlotsRequest.getDate());
            List<Integer> integerList = new ArrayList();
            List<BookTimeSlotResponse> allSlotList = getTimeSlotByStartAndEndTimeAndSlotDuration(getAllSlotsRequest.getCompanyId(), getAllSlotsRequest.getDate(), getAllSlotsRequest.getOpenTime(), getAllSlotsRequest.getCloseTime(), getAllSlotsRequest.getSlotDuration());

//            for(BookedTimeSlot slot: slotFromDB){
//                integerList.add(slot.getSlotNumber());
//            }

                for (int i = 0; i < slotFromDB.size(); i++) {
                    integerList.add(slotFromDB.get(i).getSlotNumber());
                }


            for (BookTimeSlotResponse slotResponse : allSlotList) {
                if (integerList.contains(slotResponse.getSlotNumber())) {
                    for(BookedTimeSlot bookedTimeSlot: slotFromDB){
                        if(slotResponse.getSlotNumber() == bookedTimeSlot.getSlotNumber()){

                            BookTimeSlotResponse bookedResponse = new BookTimeSlotResponse(bookedTimeSlot);

                            allSlotList.set(slotResponse.getSlotNumber() - 1, bookedResponse);

                        }
                    }// replace slots - for loop end
                }
            }// all slots - for loop end
            GetAllSlotsResponse response = new GetAllSlotsResponse(allSlotList);
            return response;
        } else {
            throw new GeneralException("Date should be not in past.", HttpStatus.BAD_REQUEST);
        }

    }


    private List<BookTimeSlotResponse> getTimeSlotByStartAndEndTimeAndSlotDuration(String companyId, LocalDate date, LocalDateTime openTime, LocalDateTime closeTime, int durationInMinutes) {
        List<BookTimeSlotResponse> timeSlotsList = new ArrayList<>();
        LocalDateTime slotStartTime = openTime;
        LocalDateTime slotEndTime;
        int count = 1;

        //slot end time should be before close time.
        while (slotStartTime.plusMinutes(durationInMinutes).isBefore(closeTime)) {
            slotEndTime = slotStartTime.plusMinutes(durationInMinutes);
            timeSlotsList.add(new BookTimeSlotResponse(companyId, count, date, slotStartTime, slotEndTime));

//            timeSlotsList.add(new BookTimeSlotResponse(LocalDate.now(ZoneId.of("Asia/Kolkata")), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")), slotStartTime), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")), slotEndTime)));
//            timeSlotsList.add(new BookTimeSlotResponse(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(1), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(1), slotStartTime), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(1), slotEndTime)));
//            timeSlotsList.add(new BookTimeSlotResponse(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(2), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(2), slotStartTime), LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(2), slotEndTime)));
            slotStartTime = slotEndTime;
            count++;
        }
        return timeSlotsList;
    }
}
