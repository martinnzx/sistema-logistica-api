# Etapa 1: Build (Construccion)
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos el archivo pom.xml y descargamos dependencias primero para aprovechar cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el codigo fuente y empaquetamos la aplicacion
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Ejecucion)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creamos un usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiamos unicamente el JAR ejecutable desde la etapa de Build
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]
