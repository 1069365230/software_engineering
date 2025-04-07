FROM amazoncorretto:17

WORKDIR /app

COPY target/login-service*.jar login-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "login-service.jar"]
