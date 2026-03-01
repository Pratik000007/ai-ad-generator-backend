# Step 1: Build the JAR using Maven
# Using eclipse-temurin for the build stage as well
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the JAR using a production-ready JRE
# eclipse-temurin is the official replacement for the old openjdk images
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]