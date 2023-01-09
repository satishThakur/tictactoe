FROM sbtscala/scala-sbt:eclipse-temurin-19.0.1_10_1.8.0_3.2.1 AS builder
ADD . .
RUN sbt clean assembly

FROM openjdk:19-alpine3.16
COPY --from=builder /root/target/scala-3.2.1/tictactoe-assembly-0.1.0-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]