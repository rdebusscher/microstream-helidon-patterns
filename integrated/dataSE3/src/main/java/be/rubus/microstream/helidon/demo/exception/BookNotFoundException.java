package be.rubus.microstream.helidon.demo.exception;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(String isbn) {
        super("The book with ISBN " + isbn + " could not be found.");
    }
}
