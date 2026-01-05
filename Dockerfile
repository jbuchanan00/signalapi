FROM eclipse-temurin:25
RUN mkdir /opt/app
COPY japp.jar /opt/app
ENV HALO_URL="localhost:8081"
CMD ["java", "-jar", "/opt/app/japp.jar"]


