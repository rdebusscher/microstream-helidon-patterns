package be.rubus.microstream.helidon.demo.exception;

public class UserAlreadyExistsException extends BusinessException {

    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists.");
    }
}
