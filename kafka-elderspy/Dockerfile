# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-focal as builder

# Set working directory
WORKDIR /app

# Copy Maven files first (for better caching)
COPY pom.xml .
COPY kafka-common/pom.xml kafka-common/
COPY kafka-producer/pom.xml kafka-producer/
COPY kafka-consumer/pom.xml kafka-consumer/

# Copy source code
COPY kafka-common/src kafka-common/src
COPY kafka-producer/src kafka-producer/src
COPY kafka-consumer/src kafka-consumer/src

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Final stage with just the JRE
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy built artifacts from builder stage
COPY --from=builder /app/kafka-producer/target/kafka-producer-*.jar kafka-producer/target/
COPY --from=builder /app/kafka-consumer/target/kafka-consumer-*.jar kafka-consumer/target/
