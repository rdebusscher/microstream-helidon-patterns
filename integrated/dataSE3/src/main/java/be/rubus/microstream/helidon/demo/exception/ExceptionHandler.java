package be.rubus.microstream.helidon.demo.exception;

import be.rubus.microstream.helidon.demo.json.JsonFactory;
import io.helidon.common.http.Http;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());

    private ExceptionHandler() {
    }

    public static <T> T processErrors(Throwable ex, ServerRequest request, ServerResponse response) {

        if (ex.getCause() instanceof JsonException) {

            LOGGER.log(Level.FINE, "Invalid JSON", ex);
            JsonObject jsonErrorObject = JsonFactory.getInstance().createObjectBuilder()
                    .add("error", "Invalid JSON")
                    .build();
            response.status(Http.Status.BAD_REQUEST_400).send(jsonErrorObject);

        } else {
            if (ex.getCause() instanceof BusinessException) {
                LOGGER.log(Level.FINE, "BusinessException", ex);
                JsonObject jsonErrorObject = JsonFactory.getInstance().createObjectBuilder()
                        .add("error", ex.getCause().getMessage())
                        .build();
                response.status(Http.Status.PRECONDITION_FAILED_412).send(jsonErrorObject);

            } else {

                LOGGER.log(Level.FINE, "Internal error", ex);
                JsonObject jsonErrorObject = JsonFactory.getInstance().createObjectBuilder()
                        .add("error", "Internal error")
                        .build();
                response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(jsonErrorObject);
            }
        }

        return null;
    }
}
