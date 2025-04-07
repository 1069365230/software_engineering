FROM amazoncorretto:17

WORKDIR /app

COPY target/analyticsandreport-service*.jar analyticsandreport-service.jar

ENTRYPOINT ["java", "-jar", "analyticsandreport-service.jar"]
