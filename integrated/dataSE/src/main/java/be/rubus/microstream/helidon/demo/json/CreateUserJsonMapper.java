package be.rubus.microstream.helidon.demo.json;

import be.rubus.microstream.helidon.demo.dto.CreateUser;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

public class CreateUserJsonMapper extends JsonMapper<CreateUser> {

    public CreateUserJsonMapper(JsonBuilderFactory jsonBuilderFactory) {
        super(jsonBuilderFactory);
    }

    @Override
    public JsonObject asJson(Object data) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public CreateUser asJava(JsonObject data) {
        String name = data.getString("name");
        String email = data.getString("email");
        return new CreateUser(name, email);
    }
}
