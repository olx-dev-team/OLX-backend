package uz.pdp.backend.olxapp.exception;

import org.springframework.http.HttpStatus;
import uz.pdp.backend.olxapp.exception.base.BaseException;

public class SamePasswordException extends BaseException {
    public SamePasswordException(String message, HttpStatus status) {
        super(message, status);
    }
}
