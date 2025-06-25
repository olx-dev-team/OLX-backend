package uz.pdp.backend.olxapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotificationNotFoundException extends RuntimeException {

//  private final HttpStatus status;

    public NotificationNotFoundException(String message) {
        super(message);
    }
}
