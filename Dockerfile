# Use an official JDK runtime as a parent image
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
# Note: The JAR name reflects your new artifactId 'VKAcademy'
COPY target/VKAcademy-1.0.jar app.jar

# Expose the port the app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
