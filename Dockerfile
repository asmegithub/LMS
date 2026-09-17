# ─────────────────────────────────────────────
# Stage 1 – Build
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Cache Maven dependencies separately from source code.
# Re-downloads only when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy source and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B --no-transfer-progress

# ─────────────────────────────────────────────
# Stage 2 – Runtime (minimal JRE image)
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the fat JAR; exclude the *-original.jar Spring Boot generates
COPY --from=build /app/target/LMS-0.0.1-SNAPSHOT.jar app.jar

# Uploads directory (Render Disk or volume mount goes here)
RUN mkdir -p uploads && chown -R appuser:appgroup /app

USER appuser

# Render injects PORT at runtime; fall back to 8080 locally
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
