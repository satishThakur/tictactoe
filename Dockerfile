FROM openjdk:19-alpine3.16
COPY target/scala-3.2.1/tictactoe-assembly-0.1.0-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]