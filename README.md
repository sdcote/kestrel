# Kestrel Services
This project is a framework for creating event-based microservices. Kestrel services are highly scalable, fault tolerant and provide automatic fail-over and fall-back as a result of using common messaging patterns.

This library allows a developer to create micro services over message exchanges using popular message brokers such as AMQP in a matter of minutes. All the messaging has been encapsulated in the framework allowing the developer to concentrate on the service implementation.

This is not a generic messaging library. It is a framework which uses messaging to exchange data with the goal of implementing micro services and client API stubs which can be used in any environment an application. The API has been designed for use as a request-reply infrastructure with some eventing for monitoring and management. 

# Docker Notes

If you have Docker installed and want to have an AMQP broker running all the time for development and testing, use this command:
```
docker run -d --restart unless-stopped --hostname rabbit --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
The above will keep a broker running in the background on the localhost with the management console running on http://localhost:15672. The login of `guest:guest` will allow management of the broker and connection via the API.

