package uz.pdp.backend.olxapp.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.pdp.backend.olxapp.exception.UserNameAlreadyExistException;
import uz.pdp.backend.olxapp.payload.errors.ErrorDTO;
import uz.pdp.backend.olxapp.payload.errors.FieldErrorDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token muddati tugagan");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Token format noto‘g‘ri");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token imzosi noto‘g‘ri");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Login yoki parol noto‘g‘ri");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Noma’lum server xatosi: " + e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handle(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();

        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            fieldErrors.add(new FieldErrorDTO(fieldName, message));
        }

        ErrorDTO errorDTO = new ErrorDTO(
                400,
                "field not valid",
                fieldErrors
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNameAlreadyExistException.class)
    public ResponseEntity<?> handleUserNameAlreadyExistException(UserNameAlreadyExistException e) {
        return buildResponse(HttpStatus.CONFLICT, "username already exist");
    }


    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }


}
