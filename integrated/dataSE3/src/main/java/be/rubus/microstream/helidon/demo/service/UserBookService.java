package be.rubus.microstream.helidon.demo.service;

import be.rubus.microstream.helidon.demo.exception.BookAlreadyAssignedException;
import be.rubus.microstream.helidon.demo.exception.BookNotFoundException;
import be.rubus.microstream.helidon.demo.exception.UserNotFoundException;
import be.rubus.microstream.helidon.demo.model.Book;
import be.rubus.microstream.helidon.demo.model.User;

import java.util.Optional;

public class UserBookService extends AbstractService {

    private static final Object USER_BOOK_LOCK = new Object();

    private final UserService userService;

    private final BookService bookService;

    public UserBookService(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    public void addBookToUser(String id, String isbn) {
        synchronized (USER_BOOK_LOCK) {
            Optional<User> byId = userService.getById(id);
            if (byId.isEmpty()) {
                throw new UserNotFoundException(id);
            }
            Optional<Book> bookByISBN = bookService.getBookByISBN(isbn);
            if (bookByISBN.isEmpty()) {
                throw new BookNotFoundException(isbn);
            }

            User user = byId.get();
            Book book = bookByISBN.get();
            if (user.getBooks().contains(book)) {
                throw new BookAlreadyAssignedException(book.getName());
            }
            root.addBookToUser(user, book);
        }
    }
}
