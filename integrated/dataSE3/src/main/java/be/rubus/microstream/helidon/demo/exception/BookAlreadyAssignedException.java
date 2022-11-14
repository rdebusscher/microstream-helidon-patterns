package be.rubus.microstream.helidon.demo.exception;

public class BookAlreadyAssignedException extends BusinessException {

    public BookAlreadyAssignedException(String name) {
        super("The book with name '" + name + "' already exists.");
    }
}
