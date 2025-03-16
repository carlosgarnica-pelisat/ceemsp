FROM openjdk:8-jdk-alpine
ENV TZ="America/Mexico_City"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 17000
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Xss8192m", "-Xmx8192m", "-jar", "/app.jar"]