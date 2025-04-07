FROM amazoncorretto:17
WORKDIR /app
COPY /target/recommender-service*.jar recommender-service.jar
ENTRYPOINT ["java","-jar", "recommender-service.jar"]
