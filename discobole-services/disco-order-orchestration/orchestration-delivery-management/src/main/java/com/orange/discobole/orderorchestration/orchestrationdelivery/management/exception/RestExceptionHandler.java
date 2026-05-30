package com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.orange.discobole.orderorchestration.exception.model.enums.ErrorCodeEnum.INTERNAL_ERROR;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
    // this is added for security protection
    // so if any error fails with an exception that we didn't handle the internal info is not exposed
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR.getCode(),
                INTERNAL_ERROR.getStatus(),
                "An unexpected error occurred. Please contact support."
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
