# Helidon SE with MicroStream

Sample Helidon SE project that make use of MicroStream.

## Creating the project

You can start from the Helidon Starter webpage https://helidon.io/starter/2.5.2 or from the Helidon CLI with `helidon init` to create a basic project structure using Helidon SE.

> **_NOTE:_** Helidon SE is mainly created with reactive programming in mind. Since you are using a single shared data structure (your data in memory) this must be protected properly for concurrent (modification) access. Also, the Lazy loading option of MicroStream is a blocking operation.

Add the following Helidon integration extension dependencies to make use of MicroStream.

```
        <dependency>
            <groupId>io.helidon.integrations.microstream</groupId>
            <artifactId>helidon-integrations-microstream</artifactId>
        </dependency>
```

Define the MicroStream configuration parameters in any source that is supported by the Helidon MicroProfile Config implementations.  In this example, we put the following values in the `application.yaml` file.

```
one.microstream:
  storage-directory: microstream2-data
  channel-count: 2
```
The key can be chosen freely as you will refer to it in the `Main` method of your application. 

The _StorageManager_ is created in the `be.rubus.microstream.helidon.demo.database.DB.createStorageManager()` method by using the `EmbeddedStorageManagerBuilder` from the integration.

```
StorageManager result = EmbeddedStorageManagerBuilder.create(config.get("one.microstream")).start();
```

Within this method, we also initialize the root when the database is created for the first time.

## Build and run

With JDK11+

```bash
mvn package
java -jar target/demo.jar
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
curl  'http://localhost:8080/user/008ba677-2fc9-4dc2-a1b4-30be69f4bce1/book'
```

Add book to user (use id of added user)
```
curl -X POST 'http://localhost:8080/user/008ba677-2fc9-4dc2-a1b4-30be69f4bce1/book/9780141321097'
```

List again
```
curl  'http://localhost:8080/user/008ba677-2fc9-4dc2-a1b4-30be69f4bce1/book'
```

Update user
```
curl -X PATCH 'http://localhost:8080/user/008ba677-2fc9-4dc2-a1b4-30be69f4bce1' \
--header 'Content-Type: application/json' \
--data-raw '{"email": "r.debusscher@microstream.one"}'
```

Stop and start Spring boot application
Are data stored?

```
curl  'http://localhost:8080/user/'
curl  'http://localhost:8080/user/008ba677-2fc9-4dc2-a1b4-30be69f4bce1/book'
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

And configure the healthcheck

```
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .addLiveness(MicrostreamHealthCheck.create(storageManager))
                .build();
```

The health can be checked through the health endpoint of Helidon.

```
curl -s -X GET http://localhost:8080/health
```

To add MicroStream metrics to the endpoint of Helidon, add the following dependency to your project.

```
    <dependency>
        <groupId>io.helidon.integrations.microstream</groupId>
        <artifactId>helidon-integrations-microstream-metrics</artifactId>
    </dependency>
```

The following method is an example of adapting the service for the metrics to include the MicroStream metrics.  These metrics are

- The number of datafiles (fixed number)
- The size of data within the storage maintained by MicroStream (live and total size)

```
    private static MetricsSupport defineMetricsSupport(EmbeddedStorageManager storageManager) {
        RegistryFactory metricsRegistry = RegistryFactory.getInstance();

        MicrostreamMetricsSupport microstreamMetrics = MicrostreamMetricsSupport
                .builder(storageManager)
                .registryFactory(metricsRegistry)
                .build();

        microstreamMetrics.registerMetrics();

        return MetricsSupport.create();
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

## Build the Docker Image

```
docker build -t demo .
```

## Start the application with Docker

The MicroStream storage directory is mapped into the container so that data is not lost.

```
docker run --rm -v microstream-data:/helidon/microstream-data -p 8080:8080 demo:latest
```

Exercise the application as described above

## Deploy the application to Kubernetes

```
kubectl cluster-info                        # Verify which cluster
kubectl get pods                            # Verify connectivity to cluster
kubectl create -f app.yaml                  # Deploy application
kubectl get pods                            # Wait for quickstart pod to be RUNNING
kubectl get service helidon-quickstart-se   # Get service info
```

Note the PORTs. You can now exercise the application as you did before but use the second
port number (the NodePort) instead of 8080.

After you’re done, cleanup.

```
kubectl delete -f app.yaml
```

## Build a Java Runtime Image using jlink

You can build a custom Java Runtime Image (JRI) containing the application jars and the JDK modules
on which they depend. This image also:

* Enables Class Data Sharing by default to reduce startup time.
* Contains a customized `start` script to simplify CDS usage and support debug and test modes.

You can build a custom JRI in two different ways:
* Local
* Using Docker


### Local build

```
# build the JRI
mvn package -Pjlink-image
```

See https://github.com/oracle/helidon-build-tools/tree/master/helidon-maven-plugin#goal-jlink-image
 for more information.

Start the application:

```
./target/demo-jri/bin/start
```

### Multi-stage Docker build

Build the JRI as a Docker Image

```
docker build -t demo-jri -f Dockerfile.jlink .
```

Start the application:

```
docker run --rm -p 8080:8080 demo-jri:latest
```

See the start script help:

```
docker run --rm demo-jri:latest --help
```
