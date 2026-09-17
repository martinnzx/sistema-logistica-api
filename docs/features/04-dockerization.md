# Documentación de Implementación: Dockerización

## 1. Resumen de la Característica
Se implementó un entorno contenedorizado para la API y la Base de Datos mediante Docker y Docker Compose, permitiendo levantar y probar toda la infraestructura del Sistema de Logística con un solo comando.

## 2. Decisiones Arquitectónicas (ADR)
- **Multi-stage Build (Dockerfile):** 
  - La imagen se construye en dos fases: una para compilar el código (usando `maven:3.9.9-eclipse-temurin-21`) y otra para ejecutarlo (`eclipse-temurin:21-jre-alpine`). Esto reduce el tamaño de la imagen final de ~800MB a ~200MB, excluyendo código fuente y dependencias locales de Maven.
- **Inyección de Secretos por `.env`:** 
  - No se queman credenciales en los archivos de Docker. El archivo `docker-compose.yml` lee directamente del `.env` local (`env_file: - .env`) para inyectar credenciales tanto a PostgreSQL como a Spring Boot.
- **Healthchecks de Base de Datos:**
  - El contenedor de la API espera explícitamente a que el contenedor de la Base de Datos (`postgres:17`) reporte estado "healthy" (sano) mediante la utilidad `pg_isready`, evitando cuelgues o crasheos de Spring Boot al inicio.

## 3. Componentes Modificados y Nuevos
- **Nuevos Archivos:**
  - `Dockerfile`: Orquestación del build de la aplicación Java.
  - `docker-compose.yml`: Definición de infraestructura multicontenedor.
  - `.dockerignore`: Optimización del contexto de construcción (ignora `target/`, `.git/`, `.env`, etc.).

## 4. Pruebas y Validación
- Validación de que `docker-compose up --build -d` levanta correctamente ambos servicios.
- Verificación del inicio secuencial (API espera a que DB esté lista).
- Migraciones ejecutadas exitosamente a través del contenedor API contra la DB contenedorizada.
