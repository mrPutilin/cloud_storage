FROM amazoncorretto:17
VOLUME ./my_java_volume
EXPOSE 8080
WORKDIR /app
COPY . .
ENTRYPOINT ["java", "-jar", "build/libs/cloud_storage-0.0.1-SNAPSHOT.jar"]
