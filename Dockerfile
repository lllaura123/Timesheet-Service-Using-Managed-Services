ARG DOCKER_PULL_URI=docker.lej.eis.network:443

FROM $DOCKER_PULL_URI/library/openjdk:8-jdk

ARG JAR_FILE

COPY $JAR_FILE app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080/tcp
