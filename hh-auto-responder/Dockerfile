FROM alpine:latest
RUN apk add --no-cache openjdk17-jdk
ENV JAVA_OPTS="-Xms256m -Xmx512m"
WORKDIR /app
EXPOSE 8080
COPY target/hh-auto-responder-0.0.1-SNAPSHOT.jar /app/hh-auto-responder.jar
CMD ["java", "-jar", "hh-auto-responder.jar"]