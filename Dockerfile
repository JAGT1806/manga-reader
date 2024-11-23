FROM tomcat:10.1-jdk21-openjdk-slim

ARG JAR_FILE=target/manga-reader-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_manga-reader.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_manga-reader.jar"]