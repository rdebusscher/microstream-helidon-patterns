package be.rubus.microstream.helidon.demo.health;

import io.helidon.integrations.microstream.cdi.MicrostreamStorage;
import io.helidon.integrations.microstream.health.MicrostreamHealthCheck;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Liveness
public class MicroStreamHealth implements HealthCheck {

    @Inject
    @MicrostreamStorage(configNode = "one.microstream.storage.bookstore")
    private EmbeddedStorageManager storageManager;

    @Override
    public HealthCheckResponse call() {
        return MicrostreamHealthCheck.create(storageManager)
                .call();
    }
}
