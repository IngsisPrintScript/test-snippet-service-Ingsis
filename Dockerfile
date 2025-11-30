# --- Stage 1: build the application ---
FROM gradle:8.4-jdk21-alpine AS builder

WORKDIR /home/gradle/project

# ============================
# Credenciales para GitHub Packages
# ============================
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

ENV GITHUB_USERNAME=${GITHUB_USERNAME}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Copiar sólo los archivos necesarios para cachear dependencias
COPY build.gradle settings.gradle gradlew gradle /home/gradle/project/

# Descargar dependencias
RUN gradle --no-daemon build -x test || return 0

# Copiar el resto del código
COPY . /home/gradle/project

# Build real
RUN gradle --no-daemon clean bootJar


# --- Stage 2: run the application ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8084

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]