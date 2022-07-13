FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/weather-api-0.0.1-SNAPSHOT-standalone.jar /weather-api/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/weather-api/app.jar"]
