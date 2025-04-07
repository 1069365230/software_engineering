FROM maven:3.9.1 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/end-to-end*.jar ./end-to-end.jar

CMD ["java", "-ea", "-jar", "end-to-end.jar"]
