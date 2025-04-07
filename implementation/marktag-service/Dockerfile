FROM amazoncorretto:17

WORKDIR /app

COPY target/marktag-service*.jar marktag-service.jar

ENTRYPOINT ["java", "-jar", "marktag-service.jar"]