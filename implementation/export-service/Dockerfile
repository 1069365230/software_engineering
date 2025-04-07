FROM amazoncorretto:17

WORKDIR /app

COPY target/export-service*.jar export-service.jar

ENTRYPOINT ["java", "-jar", "export-service.jar"]