# PLogIt backend - multi-stage Docker build.
# Stage 1: build the jar with Maven + JDK 21.
# Stage 2: run it on a slim JRE.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
RUN useradd -r -u 1001 -g root plogit
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
USER plogit
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
