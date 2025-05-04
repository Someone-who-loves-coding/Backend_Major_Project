#FROM maven:3.8.6-openjdk-18-slim
#
#WORKDIR /data
#
## Copy the project files into the container
#COPY . .
#
## Run Maven to clean, build, and package the project
#RUN mvn clean package -DskipTests
#
#RUN cd target
#
#RUN ls
#
#EXPOSE 8080
#
## Run the Spring Boot application
#CMD ["java", "-jar", "target/backend_REST-0.0.1-SNAPSHOT.jar"]

# Stage 1: Build the application using Maven
FROM maven:3.8.6-openjdk-18-slim AS builder
WORKDIR /data
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image with a JRE
FROM openjdk:17-jdk-slim
WORKDIR /data
COPY --from=builder /data/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]