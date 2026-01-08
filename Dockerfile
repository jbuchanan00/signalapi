FROM maven:3.9-amazoncorretto-24-alpine AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ARG SIGNALAPI_PORT
ARG HALO_URL
ENV SIGNALAPI_PORT=${SIGNALAPI_PORT}
ENV HALO_URL=${HALO_URL}
RUN mvn -f /usr/src/app/pom.xml clean package -e

FROM eclipse-temurin
COPY --from=build /usr/src/app/target/Signal-0.0.1-SNAPSHOT.jar /usr/app/Signal-0.0.1-SNAPSHOT.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar","/usr/app/Signal-0.0.1-SNAPSHOT.jar"]


