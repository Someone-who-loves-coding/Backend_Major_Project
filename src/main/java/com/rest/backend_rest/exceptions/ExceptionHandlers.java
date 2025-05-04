package com.rest.backend_rest.exceptions;
import com.rest.backend_rest.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException ex){
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userAlreadyExists(UserAlreadyExists ex){
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthenticatedException(NotAuthenticatedException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegarArgument(IllegalArgument ex){
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),HttpStatus.CONFLICT);
    }
}
