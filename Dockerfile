# Use a base image with Java installed
FROM eclipse-temurin:21-jre-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/*.jar /app/app.jar
RUN echo 'spring.datasource.url=jdbc:mysql://shop-db:3306/garden_and_home?createDatabaseIfNotExist=true' > /app/application.properties

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]
