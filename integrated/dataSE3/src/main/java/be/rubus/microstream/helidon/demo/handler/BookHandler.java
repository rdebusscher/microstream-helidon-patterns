package be.rubus.microstream.helidon.demo.handler;

import be.rubus.microstream.helidon.demo.json.JsonFactory;
import be.rubus.microstream.helidon.demo.service.BookService;
import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class BookHandler implements Service {

    private final BookService bookService;

    public BookHandler() {
        bookService = new BookService();
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::getBookListMessageHandler);

    }

    private void getBookListMessageHandler(ServerRequest request, ServerResponse response) {
        response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asArray(bookService.getAll()));
    }
}
