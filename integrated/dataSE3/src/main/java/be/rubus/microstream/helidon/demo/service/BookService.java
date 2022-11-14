package be.rubus.microstream.helidon.demo.service;

import be.rubus.microstream.helidon.demo.model.Book;

import java.util.List;
import java.util.Optional;

public class BookService extends AbstractService {

    public List<Book> getAll() {
        return root.getBooks();
    }

    public Optional<Book> getBookByISBN(String isbn) {
        return root.getBooks().stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findAny();
    }

}
