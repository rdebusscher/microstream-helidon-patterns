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

With JDK11+

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
curl  'http://localhost:8080/user/ffed3d8e-e64f-4437-9cff-9ee050e61bec/book'
```

Add book to user (use id of added user)
```
curl -X POST 'http://localhost:8080/user/ffed3d8e-e64f-4437-9cff-9ee050e61bec/book/9780141321097'
```

List again
```
curl  'http://localhost:8080/user/ffed3d8e-e64f-4437-9cff-9ee050e61bec/book'
```

Update user
```
curl -X PATCH 'http://localhost:8080/user/ffed3d8e-e64f-4437-9cff-9ee050e61bec' \
--header 'Content-Type: application/json' \
--data-raw '{"email": "r.debusscher@microstream.one"}'
```

Stop and start Helidon application
Are data stored?

```
curl  'http://localhost:8080/user/'
curl  'http://localhost:8080/user/ffed3d8e-e64f-4437-9cff-9ee050e61bec/book'
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
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f app.yaml                   # Deploy application
kubectl get pods                             # Wait for demo pod to be RUNNING
kubectl get service demo                     # Verify deployed service
```

Note the PORTs. You can now exercise the application as you did before but use the second
port number (the NodePort) instead of 8080.

Kill pod does not preserve data

31177

curl  'http://localhost:31177/user/'

curl -X POST 'http://localhost:31177/user/' \
--header 'Content-Type: application/json' \
--data-raw '{"name": "Rudy", "email": "rudy@microstream.one"}'


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
docker run --rm -v microstream-data:/helidon/microstream-data -p 8080:8080 demo-jri:latest
```

See the start script help:

```
docker run --rm demo-jri:latest --help
```
