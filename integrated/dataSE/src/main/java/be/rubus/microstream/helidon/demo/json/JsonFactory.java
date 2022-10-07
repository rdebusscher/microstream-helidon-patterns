package be.rubus.microstream.helidon.demo.json;

import be.rubus.microstream.helidon.demo.dto.CreateUser;
import be.rubus.microstream.helidon.demo.model.Book;
import be.rubus.microstream.helidon.demo.model.User;

import javax.json.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonFactory {

    private static final JsonFactory INSTANCE = new JsonFactory();

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private static final Map<Class<?>, JsonMapper<?>> mappers;

    static {
        mappers = new HashMap<>();
        mappers.put(User.class, new UserJsonMapper(JSON));
        mappers.put(CreateUser.class, new CreateUserJsonMapper(JSON));
        mappers.put(Book.class, new BookJsonMapper(JSON));

    }

    private JsonFactory() {
    }

    public JsonObjectBuilder createObjectBuilder() {
        return JSON.createObjectBuilder();
    }

    public JsonObject asJson(Object data) {
        JsonMapper<?> mapper = mappers.get(data.getClass());
        if (mapper != null) {
            return mapper.asJson(data);
        } else {
            return JSON.createObjectBuilder().build();
        }
    }

    public JsonArray asArray(Collection<?> data) {
        JsonArrayBuilder arrayBuilder = JSON.createArrayBuilder();
        data.stream().map(this::asJson).forEach(arrayBuilder::add);
        return arrayBuilder.build();

    }

    public <T> T asJava(JsonObject data, Class<T> classType) {
        JsonMapper<?> mapper = mappers.get(classType);
        return (T) mapper.asJava(data);
    }

    public static JsonFactory getInstance() {
        return INSTANCE;
    }
}
