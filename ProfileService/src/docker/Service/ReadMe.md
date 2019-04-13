#Overview
This is a template for containerized services running on the Kestrel framework. Each container is a small Java process running on Alpine Linux and connecting to a remote message broker. Everything required to process the requests are contained in this image.

Create an image with 

```$bash
docker build -t profile .
```

The image can be run thusly:
```$bash
docker run -d --name profile profile
``` 

There are several environment variables which are honored. The first is the `transportUri`

```$bash
docker run -d -e transportUri=amqp://guest:guest@172.17.0.2:5672 --name profile profile
``` 
The above will override the `transportUri` environment variable in the container with the proper URI for the environment.

## Multiple Instances
When scaling services in Docker containers, it is a good idea to set the hostname for the instance. This helps troubleshoot errors particularly when intermittent errors are occurring.

```$bash
docker run -d -e transportUri=amqp://guest:guest@172.17.0.2:5672 --name profile8 --hostname profile8 profile
```
Services will send the source unless configured otherwise. It will be the hostname or the IP address if there is no hostname set. In either case, the instance identifier is appended to the source.

## Publishing to a repository

Name it according to the repository. On Docker Hub it is usually your account and your repository delimited with a forward-slash: `sdcote/profile`.

Use a tag for versioning. It’s optional but it is recommended as it helps in maintaining the version(same like ubuntu:16.04 and ubuntu:17.04)
```
docker tag profile sdcote/profile:1.0
```
A common method of setting the latest image is to just tag it with `latest`:
```
docker tag profile sdcote/profile:latest
```
After having logged into the repository (such as docker hub) simply `push` the image you named above:
```
docker push sdcote/profile:latest
```

You can now run the image from the repository by simply referring to the fully-qualified image name:
```
docker run sdcote/profile:latest
``` 
### All Together
Here are the basic build and deliver steps for the service:
```
docker build -t sdcote/profile .
docker push sdcote/profile
docker tag  sdcote/profile sdcote/profile:1.0
docker push sdcote/profile:1.0 
```
Line one builds the latest version of the image. Line two uploads/refreshes the latest image in the repository. Line three tags the latest build with an actual build version, and line four uploads that tagged version to the repository. Since the repository trackes image layers by checksum, only new layers are uploaded. If the layers which make up the image are the same, only the tags are updated in the repository to point to the correct images.
