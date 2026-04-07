# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Gradle para aprovechar el caché
COPY gradle gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .

# Dar permisos de ejecución al wrapper
RUN chmod +x gradlew

# Descargar dependencias (offline)
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y construir el JAR
COPY src src
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de construcción
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto del servidor
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
