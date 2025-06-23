package uz.pdp.backend.olxapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IllegalActionException extends RuntimeException {

    private final HttpStatus status;

    public IllegalActionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
