package com.Turfbooking.exception;

import com.Turfbooking.models.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//
//        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//
//        CommonResponse<T> response =new CommonResponse("Validation Failed",HttpStatus.BAD_REQUEST.value(),message,false,LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
//
//        return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
//    }

    //Exception
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public final CommonResponse<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        return new CommonResponse<>(exceptionResponse, HttpStatus.NOT_FOUND.value(), ex.getMessage(), false, LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        //  return new CommonResponse<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    //GeneralException
    @ExceptionHandler(GeneralException.class)
    public final ResponseEntity<CommonResponse> handleUserNotFoundException(GeneralException ex, WebRequest request) {
        ex.printStackTrace();
        log.error(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        CommonResponse commonResponse= new CommonResponse(exceptionResponse, ex.getStatus().value(), ex.getMessage(), false, LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        return new ResponseEntity(commonResponse, ex.getStatus());
    }

}
