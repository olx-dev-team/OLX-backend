package uz.pdp.backend.olxapp.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uz.pdp.backend.olxapp.exception.*;
import uz.pdp.backend.olxapp.exception.base.BaseException;
import uz.pdp.backend.olxapp.payload.errors.ErrorDTO;
import uz.pdp.backend.olxapp.payload.errors.FieldErrorDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {IllegalActionException.class})
    public ResponseEntity<?> handleIllegalActionException(IllegalActionException e) {
        return buildResponse(e.getStatus(), e.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "access denied");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token expired");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Token format not correct");
    }

    @ExceptionHandler(value = SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token signature is invalid");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Login or password incorrect");
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

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleEmailAlreadyExistException(ConflictException e) {
        return buildResponse(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(AttachmentSaveException.class)
    public ResponseEntity<?> handleAttachmentSaveException(AttachmentSaveException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "attachment save exception", e.getMessage());
    }

    @ExceptionHandler(FileDeletionException.class)
    public ResponseEntity<?> handleFileDeletionException(FileDeletionException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "file deletion exception", e.getMessage());
    }

    @ExceptionHandler(FileNotFountException.class)
    public ResponseEntity<?> handleFileNotFountException(FileNotFountException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "file not found", e.getMessage());

    }

    @ExceptionHandler(InvalidImageFileException.class)
    public ResponseEntity<?> handleInvalidImageFileException(InvalidImageFileException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "invalid image file type", e.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException e) {
        return buildResponse(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponse(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "page not found");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getBody().getTitle() + ": " + e.getResourcePath());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Noma’lum server xatosi: ", e.getMessage());
    }


    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", details); // foydali texnik izoh
        return new ResponseEntity<>(body, status);
    }


}
