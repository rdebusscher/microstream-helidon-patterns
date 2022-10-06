
package be.rubus.microstream.helidon.demo;

import io.helidon.microprofile.tests.junit5.HelidonTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.client.WebTarget;

@HelidonTest
class MainTest {

    @Inject
    private WebTarget target;

    @Test
    void testMicroStream() {
        JsonArray books = target
                .path("book")
                .request()
                .get(JsonArray.class);

        Assertions.assertEquals(13, books.size(),
                "number of books");

    }
}