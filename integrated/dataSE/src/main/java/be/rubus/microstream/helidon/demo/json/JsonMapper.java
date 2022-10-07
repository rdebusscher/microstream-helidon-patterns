package be.rubus.microstream.helidon.demo.json;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

public abstract class JsonMapper<T> {

    protected final JsonBuilderFactory jsonBuilderFactory;

    public JsonMapper(JsonBuilderFactory jsonBuilderFactory) {
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    abstract public JsonObject asJson(Object data);

    abstract public T asJava(JsonObject data);

}
