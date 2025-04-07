FROM amazoncorretto:17

WORKDIR /app

COPY target/api-gateway*.jar api-gateway.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]

