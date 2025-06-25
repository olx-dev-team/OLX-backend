package uz.pdp.backend.olxapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private HttpStatus status;

    private String message;

    public EntityNotFoundException(String message, HttpStatus status) {
        this.status = status;
        this.message = message;
    }
}
