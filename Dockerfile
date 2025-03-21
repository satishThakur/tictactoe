FROM eclipse-temurin:21-jdk AS builder

# Install SBT 1.10.11
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && \
    apt-get install -y sbt

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the project
RUN sbt clean assembly

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the assembled JAR from the builder stage
COPY --from=builder /app/target/scala-3.6.4/tictactoe-assembly-0.1.0-SNAPSHOT.jar /app/app.jar

# Run the application
CMD ["java", "-jar", "/app/app.jar"]