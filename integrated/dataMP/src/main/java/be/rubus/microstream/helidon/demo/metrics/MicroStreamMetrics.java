package be.rubus.microstream.helidon.demo.metrics;

import io.helidon.integrations.microstream.cdi.MicrostreamStorage;
import io.helidon.integrations.microstream.metrics.MicrostreamMetricsSupport;
import io.helidon.metrics.api.RegistryFactory;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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
