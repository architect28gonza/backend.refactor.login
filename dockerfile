FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY /target/masiva-0.0.1-SNAPSHOT.jar login-app-refactor.jar
CMD ["java", "-jar", "login-app-refactor.jar"]