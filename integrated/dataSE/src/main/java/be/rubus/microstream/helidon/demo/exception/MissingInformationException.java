package be.rubus.microstream.helidon.demo.exception;

public class MissingInformationException extends BusinessException {
    public MissingInformationException(String requiredPropertyName) {
        super("The request body is missing the JSON property " + requiredPropertyName);
    }
}
