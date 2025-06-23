package uz.pdp.backend.olxapp.exception;

import org.springframework.http.HttpStatus;
import uz.pdp.backend.olxapp.exception.base.BaseException;

public class PasswordMismatchException extends BaseException {
    public PasswordMismatchException(String message, HttpStatus status) {
        super(message, status);
    }
}
