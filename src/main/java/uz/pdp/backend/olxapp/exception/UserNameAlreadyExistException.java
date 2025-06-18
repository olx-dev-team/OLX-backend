package uz.pdp.backend.olxapp.exception;

public class UserNameAlreadyExistException extends RuntimeException {
    public UserNameAlreadyExistException(String usernameAlreadyExist) {
        super(usernameAlreadyExist);
    }
}
