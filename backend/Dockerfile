## Multi-stage Dockerfile for DrakkarPress Backend (Spring Boot 3.5, Java 21)

# -------- Build stage --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom first for better layer caching
COPY pom.xml .

# Download dependencies (cached if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build (skip tests for faster image builds; enable in CI if needed)
RUN mvn -B -DskipTests clean package

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Add non-root user
RUN useradd -r -u 1001 appuser

# Copy fat jar from build
COPY --from=build /workspace/target/*.jar /app/app.jar

# Expose application port
EXPOSE 8080

# Healthcheck for Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

USER appuser

# Run with memory-optimized JVM flags for Railway free tier (512MB)
ENTRYPOINT ["java",\
  "-XX:+UseContainerSupport",\
  "-XX:MaxRAMPercentage=75.0",\
  "-XX:InitialRAMPercentage=50.0",\
  "-XX:MinRAMPercentage=50.0",\
  "-XX:+UseSerialGC",\
  "-XX:MaxMetaspaceSize=128m",\
  "-XX:CompressedClassSpaceSize=32m",\
  "-Xss256k",\
  "-XX:+TieredCompilation",\
  "-XX:TieredStopAtLevel=1",\
  "-Djava.security.egd=file:/dev/./urandom",\
  "-Dspring.backgroundpreinitializer.ignore=true",\
  "-jar","/app/app.jar"]
