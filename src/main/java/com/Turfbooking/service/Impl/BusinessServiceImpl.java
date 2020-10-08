package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.repository.TimeSlotRepository;
import com.Turfbooking.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    private TimeSlotRepository timeSlotRepository;

    @Autowired
    public BusinessServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest) throws GeneralException {

        //GET SLOT BY DATE AND SLOT NUMBER
        BookedTimeSlot slot = timeSlotRepository.findByDateAndSlotNumber(bookTimeSlotRequest.getSlotNumber(),bookTimeSlotRequest.getDate());

        if(slot == null){
            BookedTimeSlot addNewBookedTimeSlot = BookedTimeSlot.builder()
                    .userId(bookTimeSlotRequest.getUserId())
                    .date(bookTimeSlotRequest.getDate())
                    .slotNumber(bookTimeSlotRequest.getSlotNumber())
                    .companyId(bookTimeSlotRequest.getCompanyId())
                    .startTime(bookTimeSlotRequest.getStartTime())
                    .endTime(bookTimeSlotRequest.getEndTime())
                    .build();

            BookedTimeSlot bookedTimeSlot = timeSlotRepository.insert(addNewBookedTimeSlot);

            BookTimeSlotResponse bookTimeSlotResponse = new BookTimeSlotResponse(bookedTimeSlot);

            return bookTimeSlotResponse;

        }else{
            throw new GeneralException("Slot already booked.", HttpStatus.CONFLICT);
        }

    }

}
