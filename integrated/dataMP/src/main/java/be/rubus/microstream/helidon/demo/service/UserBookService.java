package be.rubus.microstream.helidon.demo.service;

import be.rubus.microstream.helidon.demo.exception.BookAlreadyAssignedException;
import be.rubus.microstream.helidon.demo.exception.BookNotFoundException;
import be.rubus.microstream.helidon.demo.exception.UserNotFoundException;
import be.rubus.microstream.helidon.demo.model.Book;
import be.rubus.microstream.helidon.demo.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class UserBookService extends AbstractService {

    private static final Object USER_BOOK_LOCK = new Object();

    @Inject
    UserService userService;

    @Inject
    BookService bookService;

    public void addBookToUser(String id, String isbn) {
        synchronized (USER_BOOK_LOCK) {
            Optional<User> byId = userService.getById(id);
            if (byId.isEmpty()) {
                throw new UserNotFoundException();
            }
            Optional<Book> bookByISBN = bookService.getBookByISBN(isbn);
            if (bookByISBN.isEmpty()) {
                throw new BookNotFoundException();
            }

            User user = byId.get();
            Book book = bookByISBN.get();
            if (user.getBooks().contains(book)) {
                throw new BookAlreadyAssignedException();
            }
            root.addBookToUser(user, book);
        }
    }
}
