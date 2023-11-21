package ru.putilin.cloud_storage.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import ru.putilin.cloud_storage.dto.ExceptionDTO;
import ru.putilin.cloud_storage.exception.ExceptionRelatedHandleFile;
import ru.putilin.cloud_storage.exception.IncorrectInput;
import ru.putilin.cloud_storage.exception.NotAuthorizedException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncorrectInput.class)
    public ResponseEntity<ExceptionDTO> handleIncorrectInput(IncorrectInput e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ExceptionDTO> handleAuthorizeException(NotAuthorizedException e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExceptionRelatedHandleFile.class)
    public ResponseEntity<ExceptionDTO> exceptionWithHandleFile(ExceptionRelatedHandleFile e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }





}
