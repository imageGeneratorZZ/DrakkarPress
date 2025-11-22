## Multi-stage Dockerfile for DrakkarPress Backend (Spring Boot 3.5, Java 21)

# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy only pom.xml first for better layer caching
COPY pom.xml .
ARG MAVEN_OPTS="-Xms128m -Xmx512m"
# Compilación más rápida (silenciosa y paralela)
RUN mvn -q dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn -q -T 1C clean package -DskipTests \
	&& cp target/*.jar target/app.jar

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy only the JAR from build stage
COPY --from=build /app/target/app.jar app.jar

# Expose port
EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -Dfile.encoding=UTF-8 -Duser.timezone=UTC"

# Use exec form for better signal handling
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
