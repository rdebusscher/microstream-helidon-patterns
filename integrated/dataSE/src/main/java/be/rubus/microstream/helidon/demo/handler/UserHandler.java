package be.rubus.microstream.helidon.demo.handler;

import be.rubus.microstream.helidon.demo.dto.CreateUser;
import be.rubus.microstream.helidon.demo.exception.ExceptionHandler;
import be.rubus.microstream.helidon.demo.exception.MissingInformationException;
import be.rubus.microstream.helidon.demo.json.JsonFactory;
import be.rubus.microstream.helidon.demo.model.User;
import be.rubus.microstream.helidon.demo.service.BookService;
import be.rubus.microstream.helidon.demo.service.UserBookService;
import be.rubus.microstream.helidon.demo.service.UserService;
import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import javax.json.JsonObject;
import java.util.Optional;

public class UserHandler implements Service {

    public static final String EMAIL_PROPERTY = "email";
    private final UserService userService;

    private final UserBookService userBookService;

    public UserHandler() {
        userService = new UserService();
        userBookService = new UserBookService(userService, new BookService());
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/by/{email}", this::getUserByEmailMessageHandler)
                .post("/{id}/book/{isbn}", this::addBookToUserMessageHandler)
                .get("/{id}/book", this::getUserBooksMessageHandler)
                .get("/{id}", this::getUserByIdMessageHandler)
                .delete("/{id}", this::deleteUserByIdMessageHandler)
                .patch("/{id}", this::updateUserMessageHandler)
                .get("/", this::getUserListMessageHandler)
                .post("/", this::addUserMessageHandler);
    }

    private void getUserListMessageHandler(ServerRequest request, ServerResponse response) {
        // GET /
        response.send(JsonFactory.getInstance().asArray(userService.getAll()));
    }

    private void getUserByEmailMessageHandler(ServerRequest request, ServerResponse response) {
        // GET /by/{email}
        Optional<User> byEmail = userService.findByEmail(request.path().param(EMAIL_PROPERTY));
        if (byEmail.isEmpty()) {
            response.status(Http.Status.NOT_FOUND_404).send();
        } else {
            response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asJson(byEmail.get()));
        }
    }

    private void getUserByIdMessageHandler(ServerRequest request, ServerResponse response) {
        // GET /{id}
        Optional<User> byId = userService.getById(request.path().param("id"));
        if (byId.isEmpty()) {
            response.status(Http.Status.NOT_FOUND_404).send();
        } else {
            response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asJson(byId.get()));
        }
    }

    private void getUserBooksMessageHandler(ServerRequest request, ServerResponse response) {
        // GET /{id}/book

        Optional<User> byId = userService.getById(request.path().param("id"));
        if (byId.isEmpty()) {
            response.status(Http.Status.NOT_FOUND_404).send();
        } else {
            response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asArray(byId.get().getBooks()));
        }
    }

    private void addBookToUserMessageHandler(ServerRequest request, ServerResponse response) {
        // GET /{id}/book/{isbn}

        String userId = request.path().param("id");
        String isbn = request.path().param("isbn");

        userBookService.addBookToUser(userId, isbn);
        response.status(Http.Status.NO_CONTENT_204).send();
    }

    private void deleteUserByIdMessageHandler(ServerRequest request, ServerResponse response) {
        // DELETE /{id}
        userService.removeById(request.path().param("id"));
        response.status(Http.Status.NO_CONTENT_204).send();

    }

    private void updateUserMessageHandler(ServerRequest request, ServerResponse response) {
        // PATCH /{id}  body {"email" : "xyz"}
        String userId = request.path().param("id");

        request.content().as(JsonObject.class)
                .thenAccept(jo -> updateUser(userId, jo, response))
                .exceptionally(ex -> ExceptionHandler.processErrors(ex, request, response));
    }

    private void updateUser(String userId, JsonObject jsonObject, ServerResponse response) {
        if (!jsonObject.containsKey(EMAIL_PROPERTY)) {
            throw new MissingInformationException(EMAIL_PROPERTY);
        }
        User user = userService.updateEmail(userId, jsonObject.getString(EMAIL_PROPERTY));
        response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asJson(user));

    }

    private void addUserMessageHandler(ServerRequest request, ServerResponse response) {
        // POST /  body {"name" : "abc", "email" : "xyz"}
        request.content().as(JsonObject.class)
                .thenAccept(jo -> addUser(jo, response))
                .exceptionally(ex -> ExceptionHandler.processErrors(ex, request, response));
    }

    private void addUser(JsonObject jsonObject, ServerResponse response) {
        CreateUser createUser = JsonFactory.getInstance().asJava(jsonObject, CreateUser.class);
        User user = userService.add(createUser);
        response.status(Http.Status.OK_200).send(JsonFactory.getInstance().asJson(user));
    }

}
