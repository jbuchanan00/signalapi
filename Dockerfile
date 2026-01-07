#FROM eclipse-temurin
#CMD ["mvn", "clean", "install"]
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENV HALO_URL="localhost:8081"
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app.jar"]
#-DHALO_URL=${HALO_URL}
FROM maven:3.9-amazoncorretto-24-alpine AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ENV HALO_URL="http://localhost:8081"
RUN mvn -f /usr/src/app/pom.xml clean package -e

FROM eclipse-temurin
COPY --from=build /usr/src/app/target/Signal-0.0.1-SNAPSHOT.jar /usr/app/Signal-0.0.1-SNAPSHOT.jar
ENV HALO_URL="http://localhost:8081"
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/usr/app/Signal-0.0.1-SNAPSHOT.jar"]


