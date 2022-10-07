package be.rubus.microstream.helidon.demo.json;

import be.rubus.microstream.helidon.demo.model.Book;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class BookJsonMapper extends JsonMapper<Book> {

    public BookJsonMapper(JsonBuilderFactory jsonBuilderFactory) {
        super(jsonBuilderFactory);
    }

    @Override
    public JsonObject asJson(Object data) {
        Book book = (Book) data;
        JsonObjectBuilder result = jsonBuilderFactory.createObjectBuilder();
        result.add("isbn", book.getIsbn())
                .add("name", book.getName())
                .add("author", book.getAuthor())
                .add("year", book.getYear());

        return result.build();
    }

    @Override
    public Book asJava(JsonObject data) {
        throw new RuntimeException("Not implemented yet");
    }
}
