# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
COPY opt /opt

ARG app_home=/opt/kestrel
ENV app.home=$app_home

ARG brokerUri=amqp://guest:guest@localhost:5672
ENV transportUri=$brokerUri

ENTRYPOINT ["/opt/kestrel/bin/service","app.json"]
