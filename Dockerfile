# Multi-stage Dockerfile para construir y ejecutar la app Spring Boot
# Usa Java 21 (Eclipse Temurin). Ajusta versiones si tu entorno lo requiere.

# Build stage
FROM maven:3.10.1-eclipse-temurin-21 as builder
WORKDIR /workspace
# Copiamos todo el proyecto (se usa mvnw si está presente)
COPY . /workspace/
# Aseguramos permisos y construimos el jar (omitimos tests para velocidad)
RUN chmod +x mvnw || true
RUN ./mvnw -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
# Argumento para localizar el jar de la build (ruta relativa al context)
ARG JAR_FILE=target/admin-0.0.1-SNAPSHOT.jar
COPY --from=builder /workspace/${JAR_FILE} /app/app.jar
WORKDIR /app
# Cloud Run expone el puerto que le pasen en $PORT; por defecto 8080
ENV PORT=8080
EXPOSE 8080
# Permite pasar opciones de JVM vía JAVA_OPTS
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Arranque: usa valor de PORT si está presente
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT}"]
