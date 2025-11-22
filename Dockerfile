# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml ./backend/pom.xml
RUN mvn -f backend/pom.xml -q -B -DskipTests dependency:go-offline
COPY backend ./backend
RUN mvn -f backend/pom.xml -q -B -DskipTests -T1C package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/*.jar /app/app.jar
# Puerto interno de la app (ahora 12000)
EXPOSE 12000
ENTRYPOINT ["/bin/sh","-c","exec java -XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Dserver.port=${PORT:-12000} -jar /app/app.jar"]
