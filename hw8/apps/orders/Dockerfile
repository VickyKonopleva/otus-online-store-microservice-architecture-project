FROM alpine:3.17

RUN apk add openjdk17

COPY target/orders-1.0.0-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=dev"]