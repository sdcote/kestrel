# Kestrel Services
This project is a framework for creating message-driven microservices. Kestrel services are highly scalable, fault tolerant and provide automatic fail-over and fall-back as a result of using common messaging patterns.

This library allows a developer to create micro services over message exchanges using popular message brokers such as AMQP in a matter of minutes. All the messaging has been encapsulated in the framework allowing the developer to concentrate on the service implementation.

This is not a generic messaging library. It is a framework which uses messaging to exchange data with the goal of implementing micro services and client API stubs which can be used in any environment and application. The API has been designed for use as a request-reply infrastructure with some eventing for monitoring and management.

## Design Goals
The primary design goal is to implement services in a very rapid manner. Developers should concentrate on business logic more than enabling technologies. The Kestrel Service Framework enables a developer to create and deploy a new service in minutes using the current technology stacks and infrastructures.

## Projects
This project is composed of four sub-projects or modules.

### Kestrel
This is the core library which contains the messaging abstraction, service protocol, and many abstract base classes for simplifying development. Extend the abstract classes and override a few methods to implement your service and clients.

### Profile Service
An example service showing how to use the framework to implement message-driven micro services.

The Profile Service is designed to run in any Java environment, and includes an example Docker container to illustrate containerization principles. This service leverages the Coyote Loader framework to make loading and configuration easy. This allows for the immediate horizontal scaling of the service in any container environment such as any of the popular cloud services.

It should be noted that the service implementations can evolve independently of the clients. If developers are careful, the Service can continue to evolve without every updating existing deployments of the service proxies. Services can remain backward compatible, sensing differences in the request and handling them as appropriate.

### Profile Domain
This is the domain model of the service and the implementation of a client (service proxy). Use this project as a template for your own client and domain model.

Because the service proxy is decoupled from the service implementation, it is possible to achieve significant stability. The service can be evolved in such a manner to allow complete backward compatibility of clients (service proxies).

Any system that need to access the profile service can simply include the Profile Domain and Kestrel packages in their projects along with the packages required for the messaging transport.

### Profile Web Service
This is an implementation of an Adapter pattern to expose the profile service as a HTTP REST service. It is a very thin veneer which simply marshals HTTP requests to API requests using the `ProfileClient` interface. The `ProfileClient` is implemented by the `ProfileProxy` class that contains the logic for marshaling method calls into request messages and return values from response messages.

Just like the service, it is deployable as a container. 

# Design Notes
This framework is designed to support multiple transport technologies. The Default is AMQP, but others are possible including JMS and Tibco Rendezvous. This is why the the underlying class model uses different nomenclature than what some developers may be familiar.

Because this is intended to operate across multiple technologies, this framework may replicate some functionality of the underlying transport so that it can be used with other transports. This abstraction makes it possible to have services running on one technology stack, and clients on another with a very simple bridge between the two. For example, a set of clients may be running on a JMS messaging infrastructure, but services running on an AMQP implementation and a simple bridge that routes messages between the two.

Micro service implementation is the primary purpose of this framework and therefore will only contain qualities of service determined to be useful in that application. This is not designed to be a generic messaging toolkit. The primary use case is to speed the creation of reliable message driven micro services and the consumption thereof.

# Docker Notes
If you have Docker installed and want to have an AMQP broker running all the time for development and testing, use this command:
```
docker run -d --restart unless-stopped --hostname rabbit --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
The above will keep a broker running in the background on the localhost with the management console running on http://localhost:15672. The login of `guest:guest` will allow management of the broker and connection via the API.

# Build Notes
Everything is built with Gradle. This project includes the Gradle 5 wrapper to make it easier for new developers to compile.

The build contains both unit and integration tests. Both run as part of the normal build process.

## Integration Tests
The integration tests expect a RabbitMQ broker running on the localhost bound to port 5672, with a user of guest:guest. Many will find using Docker to run the broker the easiest way to satisfy this requirement. Automated builds should execute a broker before running the tests.

To run just the unit tests, call gradle with `-x itest` argument:
```
gradlew -x itest
```

To run just the integration tests:
```
gradlew itest
```
