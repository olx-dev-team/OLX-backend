package uz.pdp.backend.olxapp.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends RuntimeException {

    private HttpStatus status;

    private String message;

    public EntityNotFoundException(String message, HttpStatus status) {
        this.status = status;
        this.message = message;
    }
}
