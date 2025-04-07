FROM amazoncorretto:17

WORKDIR /app

COPY target/notification-service*.jar notification-service.jar

EXPOSE 8092

ENTRYPOINT ["java", "-jar", "notification-service.jar"]
