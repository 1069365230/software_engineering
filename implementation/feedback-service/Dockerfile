FROM amazoncorretto:17

WORKDIR /app

COPY target/feedback-service*.jar feedback-service.jar

ENTRYPOINT ["java", "-jar", "feedback-service.jar"]
