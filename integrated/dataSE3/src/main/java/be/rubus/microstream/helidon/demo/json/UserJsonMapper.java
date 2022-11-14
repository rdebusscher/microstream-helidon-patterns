package be.rubus.microstream.helidon.demo.json;

import be.rubus.microstream.helidon.demo.model.User;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class UserJsonMapper extends JsonMapper<User> {

    public UserJsonMapper(JsonBuilderFactory jsonBuilderFactory) {
        super(jsonBuilderFactory);
    }

    @Override
    public JsonObject asJson(Object data) {
        User user = (User) data;
        JsonObjectBuilder result = jsonBuilderFactory.createObjectBuilder();
        result.add("id", user.getId())
                .add("name", user.getName())
                .add("email", user.getEmail());

        return result.build();
    }

    @Override
    public User asJava(JsonObject data) {
        return null;
    }
}
