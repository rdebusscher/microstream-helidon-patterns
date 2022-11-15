# Helidon MP With MicroStream

Sample Helidon MP project that make use of MicroStream.

## Creating the project

You can start from the Helidon Starter webpage https://helidon.io/starter and select the version (2.5.x or 3.0.x) or from the Helidon CLI with `helidon init` to create a basic project structure using Helidon MP.

> **_NOTE:_** This example uses the Helidon provided code and not the CDI integration of MicroStream. this integration is provided by Helidon itself.

Add the following Helidon integration extension dependencies to make use of MicroStream using CDI.

```
        <dependency>
            <groupId>io.helidon.integrations.microstream</groupId>
            <artifactId>helidon-integrations-microstream-cdi</artifactId>
        </dependency>
```

Define the MicroStream configuration parameters in any source that is supported by the Helidon MicroProfile Config implementations.  In this example, we put the following values in the `META-INF/microprofile-config.properties` file.

```
one.microstream.bookstore.storage-directory=microstream-data
```

Other configuration parameters are also possible but this one defines the location of the MicroStream storage for a StorageManager that we call _bookstore_.

Access to the `EmbeddedStorageManager` (only this type can be injected) can be done by using the `@MicrostreamStorage` qualifier.

```
@Inject
@MicrostreamStorage(configNode = "one.microstream.storage.bookstore")
protected EmbeddedStorageManager storageManager;
```

In this demo, the root class of the Object graph is also made injectable by means of creating a CDI producer.  This allows us also to customize that root like initializing it with some initial data when the MicroStream storage has no data yet.  See the class `RootProducer` how this can be done.


## Build and run


With JDK17+
```bash
mvn package
java -jar target/demo.jar
```

When starting in debug mode

```bash
java '-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005' -jar target/demo.jar
```

## Exercise the application

List of books
```
curl http://localhost:8080/book
```

List all users
```
curl 'http://localhost:8080/user/'
```

List non-existing  user -> status 404
```
curl -v 'http://localhost:8080/user/abc'
```

List user by email.
```
curl  'http://localhost:8080/user/by/jane.doe@acme.org'
```

Add user
```
curl -X POST 'http://localhost:8080/user/' \
--header 'Content-Type: application/json' \
--data-raw '{"name": "Rudy", "email": "rudy@microstream.one"}'
```
-> store the id of this added user.

List books of user (use id of added user)
```
curl  'http://localhost:8080/user/e7c07c8e-df40-462d-9d08-6a935d39806d/book'
```

Add book to user (use id of added user)
```
curl -X POST 'http://localhost:8080/user/e7c07c8e-df40-462d-9d08-6a935d39806d/book/9780141321097'
```

List again
```
curl  'http://localhost:8080/user/e7c07c8e-df40-462d-9d08-6a935d39806d/book'
```

Update user
```
curl -X PATCH 'http://localhost:8080/user/e7c07c8e-df40-462d-9d08-6a935d39806d' \
--header 'Content-Type: application/json' \
--data-raw '{"email": "r.debusscher@microstream.one"}'
```

Stop and start Helidon application
Are data stored?

```
curl  'http://localhost:8080/user/'
curl  'http://localhost:8080/user/e7c07c8e-df40-462d-9d08-6a935d39806d/book'
```

## Try health and metrics

There is also a HealthCheck available for the MicroStream StorageManager. It checks if it is still running or not.  You can add it to the Helidon checks by following the following steps.

Add the Maven dependency with the code that creates the healthcheck.

```
    <dependency>
        <groupId>io.helidon.integrations.microstream</groupId>
        <artifactId>helidon-integrations-microstream-health</artifactId>
    </dependency>
```

And configure the healthcheck by defining a CDI bean Liveness check that forwards to the `MicrostreamHealthCheck``

```
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
```

The health can be checked through the health endpoint of Helidon.

```
curl -s -X GET http://localhost:8080/health
```

To add MicroStream metrics to the endpoint of Helidon, you need to add the MicroStream metrics to the registry. This can be done when the application starts. First, add the dependency:

```
    <dependency>
        <groupId>io.helidon.integrations.microstream</groupId>
        <artifactId>helidon-integrations-microstream-metrics</artifactId>
    </dependency>
```

And the following class register the metrics. These metrics are

- The number of datafiles (fixed number)
- The size of data within the storage maintained by MicroStream (live and total size)

```
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
```

By default, the metric are returned in the Prometheus format.

```
curl -s -X GET http://localhost:8080/metrics
```

JSON Format is also supported

```
curl -H 'Accept: application/json' -X GET http://localhost:8080/metrics
```

## Building the Docker Image
```
docker build -t demo .
```

## Start the application with Docker

The MicroStream storage directory is mapped into the container so that data is not lost.

```
docker run --rm -v microstream-data:/helidon/microstream-data -p 8080:8080 demo:latest
```

Exercise the application as described above.                                
