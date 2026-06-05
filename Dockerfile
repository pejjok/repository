# Stage 1: Build the application
FROM gradle:8.5-jdk17-jammy AS build
WORKDIR /app

# Copy Gradle files for dependency resolution
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies to a separate layer to improve caching
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests
RUN ./gradlew build --no-daemon -x test

# Stage 2: Create the final, smaller image
FROM openjdk:17-jre-slim
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/repository-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the entrypoint to run the application with the prod profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
