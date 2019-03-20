This shows how to expose a Kestrel service as a web service.

This is a small web server which exposes the ProfileProxy client as a web service.

Using the Coyote Loader project and its contributed HTTP server, this project defines a `Responnder` the server uses to respond to requests at a particular URL. In an application, a single project of this type would have responders for many different service proxies.

## Operation
When a request comes in, it is mapped to a `Responder`. That responder retrieves a cached Proxy from the web server through the `ClientRegistry` then makes a service call through that proxy. The responses are marshaled into JSON and returned across the HTTP connection.