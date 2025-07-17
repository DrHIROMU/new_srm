FROM eclipse-temurin:21-jre

WORKDIR /app

COPY srm-api/build/libs/srm-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Xmx2048m", "-Xms512m", "-jar", "app.jar"]
