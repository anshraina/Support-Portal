package com.ansh.supportportal.exception;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

import javax.persistence.NoResultException;

import com.ansh.supportportal.domain.HttpResponse;
import com.auth0.jwt.exceptions.TokenExpiredException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administrator";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this end point. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occured while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled";
    private static final String ERROR_PROCESSSING_FILE = "Error occured while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You dont have enough permission";
    public static final String ERROR_PATH = "/error";
    
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
            message.toUpperCase());
            return new ResponseEntity<>(httpResponse, httpStatus);
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokedExpiredException(TokenExpiredException e) {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emalExistsException(EmailExistsException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<HttpResponse> usernameExistsException(UsernameExistsException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        HttpMethod supportedMethod = Objects.requireNonNull(e.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerException(Exception e) {
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> noResultException(NoResultException e) {
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> ioException(IOException e) {
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSSING_FILE);
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> resourceNotFound() {
        return createHttpResponse(HttpStatus.NOT_FOUND, "This resource is not available");
    }

}
