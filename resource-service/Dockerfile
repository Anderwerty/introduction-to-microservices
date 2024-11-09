FROM openjdk:17-jdk-alpine
EXPOSE 8080
EXPOSE 8081
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} resource-service.jar.jar
ENTRYPOINT ["java","-jar","/resource-service.jar"]