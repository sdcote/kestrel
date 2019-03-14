# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
COPY ../../../build/install/ProfileService /opt/kestrel/ProfileService
WORKDIR /opt/kestrel/ProfileService
RUN javac Main.java
CMD ["java", "Main"]