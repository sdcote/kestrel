# Profile Service

This is a Docker image that implements a service using Kestrel.

The `ProfileService` extends the framework `AbstractService` class and inherits all of its service logic.

## Processing Requests

The entry point for all Kestrel services is the `process(Message message) ` method that handles request messages from the framework.

### Acknowledging Messages

All messages should be acknowledged, either positively or negatively.

Calling the `sendAck(Message requestMessage, DataFrame responsePayload)` is how services send positive responses to requests.

When the request could not be processed as might be the case for bad requests, the `sendNak(Message requestMessage, String message)` method is used. This sends a negative acknowledgement to the given request message.

## Specifying the Request Channel

The name of the request channel is determined by a call to this classes `getGroupName()` method.

