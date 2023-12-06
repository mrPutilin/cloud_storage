package ru.putilin.cloud_storage.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.putilin.cloud_storage.dto.ExceptionDTO;
import ru.putilin.cloud_storage.exception.ExceptionRelatedHandleFile;
import ru.putilin.cloud_storage.exception.IncorrectInputException;
import ru.putilin.cloud_storage.exception.NotAuthorizedException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static int id = 1;

    @ExceptionHandler({IncorrectInputException.class, UsernameNotFoundException.class})
    public ResponseEntity<ExceptionDTO> handleIncorrectInput(IncorrectInputException e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage(), id++), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotAuthorizedException.class, AccessDeniedException.class, JWTVerificationException.class
    , HttpClientErrorException.Unauthorized.class, HttpClientErrorException.Forbidden.class})
    public ResponseEntity<ExceptionDTO> handleAuthorizeException(Exception e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage(), id++), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ExceptionRelatedHandleFile.class} )
    public ResponseEntity<ExceptionDTO> exceptionWithHandleFile(ExceptionRelatedHandleFile e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage(), id++), HttpStatus.INTERNAL_SERVER_ERROR);
    }





}
