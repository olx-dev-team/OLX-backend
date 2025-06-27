package uz.pdp.backend.olxapp.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseStatusEnum {

    // ✅ Password Reset
    TOKEN_SENT("Password reset token sent successfully", HttpStatus.OK),
    TOKEN_ALREADY_SENT("A valid token has already been sent to your email", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED_AND_REPLACED("Previous token expired. New token sent", HttpStatus.OK),
    EMAIL_NOT_FOUND("Email address not found", HttpStatus.BAD_REQUEST),
    TOKEN_SEND_FAILED("Failed to send password reset token", HttpStatus.INTERNAL_SERVER_ERROR),

    // ✅ Common
    SUCCESS("Operation successful", HttpStatus.OK),
    INTERNAL_ERROR("Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("You are not authorized to perform this action", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    ResponseStatusEnum(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
