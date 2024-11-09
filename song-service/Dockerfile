FROM eclipse-temurin:17.0.13_11-jdk-focal
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} song-service.jar
ENTRYPOINT ["java","-jar","/song-service.jar"]