FROM amazoncorretto:17
WORKDIR /app
COPY /target/attendance-service*.jar attendance-service.jar
ENTRYPOINT ["java","-jar", "attendance-service.jar"]
