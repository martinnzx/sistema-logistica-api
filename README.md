<div align="center">
  <h1>🚚 Sistema de Logística de Envíos API</h1>
  <p><strong>Plataforma integral para la gestión de paquetería, flotas y rutas de distribución.</strong></p>

  <!-- Badges -->
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-Multi--stage-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Security-JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT Security" />
</div>

<br>

## 📖 Sobre el Proyecto

El **Sistema de Logística de Envíos** es una API RESTful diseñada bajo los principios de Arquitectura Limpia y *Spec Driven Development (SDD)*. Permite a las empresas de transporte gestionar el ciclo de vida completo de un envío: desde el registro del paquete (considerando restricciones de fragilidad y refrigeración) hasta la asignación de flotas y el seguimiento histórico de estados.

## ✨ Características Principales

* **Seguridad y Roles (JWT):** Autenticación *Stateless* con Spring Security. Acceso basado en roles (`ADMIN`, `EMPLEADO`, `CLIENTE`).
* **Gestión de Envíos:** Transiciones de estado validadas (`GENERADO` ➔ `EN_ALMACEN` ➔ `EN_RUTA` ➔ `ENTREGADO`).
* **Trazabilidad Absoluta:** Historial automatizado de cambios de estado para cada envío.
* **Restricciones Físicas Inteligentes:** Validación automática de peso, volumen, requerimientos de cadena de frío y niveles de fragilidad al asignar paquetes a vehículos.
* **Migraciones Controladas:** Control de versiones de la base de datos automatizado mediante **Flyway**.
* **Documentación Auto-generada (OpenAPI 3):** Implementación del *Patrón de Interfaces para Controladores*, manteniendo la lógica de negocio impecable y libre de anotaciones de Swagger.

## 🏗️ Arquitectura y Stack Tecnológico

* **Backend Core:** Java 21, Spring Boot (Web, Security, Data JPA, Validation).
* **Base de Datos:** PostgreSQL.
* **Migraciones:** Flyway.
* **Orquestación:** Docker & Docker Compose.
* **Testing:** JUnit 5, Mockito, Spring Boot Test (Integration Testing).
* **Documentación de API:** Springdoc OpenAPI (Swagger) y Postman.

## 🚀 Guía de Inicio Rápido

El proyecto está diseñado para desplegarse fácilmente utilizando contenedores.

### Requisitos Previos
* [Docker](https://www.docker.com/products/docker-desktop/) y Docker Compose.
* *Opcional:* JDK 21 y Maven (si deseas correrlo fuera de Docker).

### Paso 1: Configurar Variables de Entorno
Clona el repositorio y configura las variables de entorno utilizando el archivo de ejemplo proporcionado:
```bash
git clone https://github.com/tu-usuario/sistema-logistica-api.git
cd sistema-logistica-api

# Crear el archivo de entorno copiando el template
cp .env.example .env
```
*(Asegúrate de rellenar tus credenciales de base de datos y SMTP en el archivo `.env` recién creado).*

### Paso 2: Ejecutar el Proyecto
Levanta toda la infraestructura (Base de datos y API) con un solo comando:
```bash
docker-compose up -d --build
```
> **Nota técnica:** El proceso de compilación utiliza un *Multi-stage build*. La primera vez descargará la imagen de Maven y empaquetará el `.jar` automáticamente antes de desplegarlo en una imagen ligera Alpine JRE.

## 🧪 Pruebas y Documentación de la API

Una vez que el contenedor esté corriendo, puedes explorar y probar la API de las siguientes formas:

1. **Swagger UI Interactivo (Recomendado):**
   Abre tu navegador en: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
   *Para endpoints protegidos, crea un usuario, haz login, y pega el token JWT devuelto en el botón verde **Authorize**.*

2. **Colección de Postman:**
   En la carpeta `/postman` del repositorio encontrarás el archivo `sistema-logistica-api.postman_collection.json`. 
   Impórtalo en Postman; incluye scripts automáticos para gestionar el token JWT en las variables de entorno de la colección.

## 📋 Estructura y Metodología (SDD)

Este proyecto aplica la metodología **Spec Driven Development (SDD)**. Cada funcionalidad fue planificada y documentada antes de su desarrollo. 
Puedes consultar las decisiones arquitectónicas (ADRs) y especificaciones en la carpeta `docs/features/`:
* `01-security-jwt-implementation.md`
* `02-database-migrations-flyway.md`
* `03-clean-architecture-swagger.md`
* `04-dockerization.md`

## 🤝 Contribuciones

Si deseas contribuir:
1. Crea una rama a partir de `develop` (`git checkout -b feature/nueva-mejora`).
2. Redacta tu especificación en `docs/features/`.
3. Desarrolla, aplica tests y realiza tus commits en español (con prefijos convencionales en inglés: `feat:`, `fix:`, `refactor:`).
4. Crea un Pull Request contra `develop`.
