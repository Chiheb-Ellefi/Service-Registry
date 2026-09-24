ARG BASE_DISTRO=eclipse-temurin
ARG JDK_VERSION=25-jdk
ARG JRE_VERSION=25-jre

#------- Stage 1 : Build --------

FROM ${BASE_DISTRO}:${JDK_VERSION} AS build

LABEL authors="chihebellefi"

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

#------- Stage 2 : Runtime --------

FROM ${BASE_DISTRO}:${JRE_VERSION} AS runtime
ENV SERVER_PORT=8761
WORKDIR /app

RUN addgroup registry && adduser -r -g registry -u 1001 registry
USER registry
COPY --from=build --chown=registry:registry app/target/*.jar app.jar
EXPOSE ${SERVER_PORT}

HEALTHCHECK  --interval=30s --timeout=10s --start-period=5s --retries=3 \
   CMD curl -f 'https://localhost:${SERVER_PORT}/actuator/health' || exit 1
ENTRYPOINT ["java", "-jar","app.jar"]