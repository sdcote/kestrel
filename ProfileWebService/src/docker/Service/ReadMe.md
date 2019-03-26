This is a template for containerized services running on the Kestrel framework. Each container is a small Java process running on Alpine Linux and connecting to a remote message broker. Everything required to process the requests are contained in this image.

Create an image with 

```
docker build -t profile .
```

The image can be run thusly:
```$bash
docker run -d --name profile profile
``` 

There are several environment variables which are honored. The first is the `transportUri`

```$bash
docker run -d -e transportUri='amqp://guest:guest@172.17.0.2:5672' --name profile profile
``` 
The above will override the `transportUri` environment variable in the container with the proper URI for the environment.


