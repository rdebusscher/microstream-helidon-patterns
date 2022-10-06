# MicroStream Helidon Usage Patterns

Patterns in using Helidon with MicroStream.

MicroStream can be used in 2 different ways with Helidon.

MicroStream is integrated into the Helidon codebase by the Oracle Team.  You can find the examples in the `integrated` folder.

Since Helidon MP makes use of Jakarta EE CDI and MicroProfile Config, our generic integration for these frameworks can also be used with Helidon.  These examples can be found in the `integration` folder.


## Integrated

As of Helidon 2.4, MicroStream is integrated within the framework. This means that you can make use of the Helidon artifacts to make use of the MicroStream functionality using the Helidon-provided code.

The following functionality is provided

- Use the Helidon Config functionality to define the configuration parameter that will be used when instantiating the `EmbeddedStorageManager`.
- Ability to define and inject multiple `EmbeddedStorageManager`'s.
- Ability to configure a MicroStream cache (based on JCache specification) and inject it into CDI beans when using Helidon MP.
- Optional Health check that reports if the _StorageManager_ is active.
- Optional metrics reporting some basic statistics on the _StorageManager_.


Native compilation is not fully supported.

### dataMP

The folder `dataMP` contains a Maven project that makes use of Helidon MP 2.5.2 to implement a small application with REST endpoints storing the data using MicroStream. Highlights:

- Recommended usage of the Helidon MP integrated code
- Use a CDI producer to provide the Root object as a CDI bean.
- Check MicroStream startup and initial data set-up using a `HelidonTest`.
- Using a Docker image with volume mapping to store MicroStream storage outside of the container.
- Using Kubernetes deployment and Service concepts to run the Helidon MP with MicroStream application.
- Use JLink and the Class Data Sharing feature of the JVM to generate a small and fast starting fully contained Java application.

Also, have a look at the `readme.MD` file in that folder to learn about the details.
