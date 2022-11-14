package be.rubus.microstream.helidon.demo.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String userId) {
        super("The user with id '" + userId + "' could not be found");
    }
}
