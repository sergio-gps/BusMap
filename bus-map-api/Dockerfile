# Multi-stage build for Spring Boot (Java 21)
# 1) Build stage: compile and package with Maven
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Define artifact coordinates (override with --build-arg if version changes)
ARG APP_NAME=bus-map-api
ARG APP_VERSION=0.0.1-SNAPSHOT

# Copy sources and build
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

# 2) Runtime stage: tiny JRE image
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Accept build args for jar name
ARG APP_NAME=bus-map-api
ARG APP_VERSION=0.0.1-SNAPSHOT

# Copy only the fat jar (avoid copying any original-* artifacts)
COPY --from=build /app/target/${APP_NAME}-${APP_VERSION}.jar /app/app.jar

# App port (can be overridden by Spring config)
EXPOSE 8080

# Optional JVM flags
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
