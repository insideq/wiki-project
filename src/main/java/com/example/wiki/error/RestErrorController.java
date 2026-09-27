package com.example.wiki.error;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Profile("front")
@RestControllerAdvice
public class RestErrorController implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    private HttpStatus getStatus(HttpServletRequest request) {
        final Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        final HttpStatus status = (code != null) ? HttpStatus.resolve(code) : null;
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseEntity<AdviceErrorBody> handleException(Throwable throwable, HttpStatusCode httpCode) {
        return new ResponseEntity<>(new AdviceErrorBody(httpCode.value(), throwable.getMessage()), httpCode);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<AdviceErrorBody> handleAuthenticationException(Throwable throwable) {
        return handleException(throwable, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<AdviceErrorBody> handleAccessDeniedException(Throwable throwable) {
        return handleException(throwable, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<AdviceErrorBody> handleNotFoundException(Throwable throwable) {
        return handleException(throwable, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AdviceErrorBody> handleDataIntegrityViolationException(Throwable throwable) {
        return handleException(throwable, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<AdviceErrorBody> handleAnyException(HttpServletRequest request, Throwable ex) {
        final HttpStatus status = getStatus(request);
        return handleException(ex, status);
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        final ResponseEntity<AdviceErrorBody> responseEntity = handleAuthenticationException(authException);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(mapper.writeValueAsString(responseEntity.getBody()));
    }
}
