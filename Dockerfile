FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/catatdulu-users-0.0.1-SNAPSHOT.jar catatdulu-v0.1.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "catatdulu-v0.1.jar"]