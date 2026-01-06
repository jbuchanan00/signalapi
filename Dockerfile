FROM eclipse-temurin
CMD ["mvn", "clean", "install"]
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV HALO_URL="localhost:8081"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]


