package be.rubus.microstream.helidon.demo;


import be.rubus.microstream.helidon.demo.database.DB;
import be.rubus.microstream.helidon.demo.handler.BookHandler;
import be.rubus.microstream.helidon.demo.handler.UserHandler;
import io.helidon.integrations.microstream.health.MicrostreamHealthCheck;
import io.helidon.integrations.microstream.metrics.MicrostreamMetricsSupport;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.metrics.api.RegistryFactory;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     */
    static Single<WebServer> startServer() {

        // load logging configuration
        LogConfig.configureRuntime();

        // By default this will pick up application.yaml from the classpath
        Config config = Config.create();

        // Create and initialise the storage manager
        EmbeddedStorageManager storageManager = DB.createStorageManager(config);

        WebServer server = WebServer.builder(createRouting(storageManager))
                .config(config.get("server"))
                .addMediaSupport(JsonbSupport.create())
                .build();

        Single<WebServer> webserver = server.start();

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        webserver.thenAccept(ws -> {
            System.out.println("WEB server is up! http://localhost:" + ws.port());
            ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
        })
        .exceptionallyAccept(t -> {
            System.err.println("Startup failed: " + t.getMessage());
            t.printStackTrace(System.err);
        });

        return webserver;
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     */
    private static Routing createRouting(EmbeddedStorageManager storageManager) {
        MetricsSupport metrics = defineMetricsSupport(storageManager);

        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .addLiveness(MicrostreamHealthCheck.create(storageManager))
                .build();

        UserHandler userHandler = new UserHandler();
        BookHandler bookHandler = new BookHandler();

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/user", userHandler)
                .register("/book", bookHandler)
                .build();
    }

    private static MetricsSupport defineMetricsSupport(EmbeddedStorageManager storageManager) {
        RegistryFactory metricsRegistry = RegistryFactory.getInstance();

        MicrostreamMetricsSupport microstreamMetrics = MicrostreamMetricsSupport
                .builder(storageManager)
                .registryFactory(metricsRegistry)
                .build();

        microstreamMetrics.registerMetrics();

        return MetricsSupport.create();
    }
}
