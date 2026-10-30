# PLogIt backend - multi-stage Docker build.
# Stage 1: build the jar with Maven + JDK 21.
# Stage 2: run it on a slim JRE.

FROM maven:3.9-eclipse-temurin-21@sha256:99e61abcff91a9b1333463bd8451fb18495d6eba9250ac66a338b518f8278320 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre@sha256:0b99b3376fbb50dbf9c0af8535fdd5b8eb0d3fdfc8b1e4967f383b6919f6901a
RUN useradd -r -u 1001 -g root plogit
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
USER plogit
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
