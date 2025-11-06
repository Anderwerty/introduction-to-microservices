FROM --platform=linux/amd64 maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
RUN apk add --no-cache curl
ARG OTEL_AGENT_VERSION=2.21.0
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar \
    -o opentelemetry-javaagent.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084

CMD ["java", "-javaagent:opentelemetry-javaagent.jar", "-Dotel.java.logs.numCapturedBeforeInstall=1000", "-jar", "app.jar"]

