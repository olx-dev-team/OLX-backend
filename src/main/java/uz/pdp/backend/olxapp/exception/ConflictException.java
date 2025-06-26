package uz.pdp.backend.olxapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends RuntimeException {

    private final HttpStatus status;

    public ConflictException(String emailAlreadyExist, HttpStatus status) {
        super(emailAlreadyExist);
        this.status = status;
    }
}
