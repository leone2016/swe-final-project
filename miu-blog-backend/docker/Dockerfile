FROM openjdk:24-jdk-slim as build

WORKDIR /app

COPY ../target/TODO.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]