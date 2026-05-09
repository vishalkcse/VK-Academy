# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the built JAR from the first stage
COPY --from=build /app/target/VKAcademy-1.0.jar app.jar

# Expose the port
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
