package com.dodream.common.exception;

import com.dodream.common.response.ErrorResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ErrorController {

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(ErrorResponse.from(e.getErrorCode()));
    }

    // @valid 에서 binding error 발생
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> params = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            params.add(fieldError.getField() + ":" + fieldError.getDefaultMessage());
        }

        String errorMessage = String.join(", ", params);

        ErrorResponse response = ErrorResponse.from(ErrorCode.VALIDATION_FAILED);
        response.changeMessage(errorMessage);

        return response;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    protected ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());

        return ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}