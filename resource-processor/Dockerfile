FROM --platform=linux/amd64 maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083

CMD ["java", "-jar", "app.jar"]
