FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 17000
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Xss256m", "-Xmx256m", "-jar", "/app.jar"]