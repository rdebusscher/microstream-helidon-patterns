package be.rubus.microstream.helidon.demo.controller;


import be.rubus.microstream.helidon.demo.model.Book;
import be.rubus.microstream.helidon.demo.service.BookService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/book")
@Produces(MediaType.APPLICATION_JSON)
public class BookController {

    @Inject
    private BookService bookService;


    @GET()
    public Collection<Book> getAll() {
        return bookService.getAll();
    }


}
