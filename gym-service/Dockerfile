# Start with a base image containing Java runtime
FROM openjdk:17-jdk-alpine

LABEL authors="ricardo_patino"

# The application's jar file
ARG JAR_FILE=target/gym-service-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} gym-service.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gym-service.jar"]