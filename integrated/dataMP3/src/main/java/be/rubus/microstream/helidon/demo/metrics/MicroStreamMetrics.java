package be.rubus.microstream.helidon.demo.metrics;

import io.helidon.integrations.microstream.cdi.MicrostreamStorage;
import io.helidon.integrations.microstream.metrics.MicrostreamMetricsSupport;
import io.helidon.metrics.api.RegistryFactory;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class MicroStreamMetrics {

    @Inject
    @MicrostreamStorage(configNode = "one.microstream.storage.bookstore")
    private EmbeddedStorageManager storageManager;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        RegistryFactory metricsRegistry = RegistryFactory.getInstance();

        MicrostreamMetricsSupport microstreamMetrics = MicrostreamMetricsSupport
                .builder(storageManager)
                .registryFactory(metricsRegistry)
                .build();

        microstreamMetrics.registerMetrics();
    }
}
