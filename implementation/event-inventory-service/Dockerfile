FROM amazoncorretto:17
WORKDIR /app
COPY /target/event-inventory-service*.jar event-inventory-service.jar
ENTRYPOINT ["java","-jar", "event-inventory-service.jar"]
