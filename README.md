# Sistema de Logística API 🚚

API RESTful desarrollada en Java y Spring Boot para la gestión integral de una empresa de logística y envíos.

## 🛠️ Tecnologías Utilizadas
* **Java 21**
* **Spring Boot 3.5** (Web, Data JPA, Validation)
* **PostgreSQL**
* **Hibernate**
* **Swagger / OpenAPI** para documentación
* **Lombok**

## ⚙️ Configuración y Ejecución

1. Clona el repositorio.
2. Crea una base de datos en PostgreSQL llamada `logistica_envios`.
3. Crea un archivo `.env` en la raíz del proyecto tomando como base `.env.example`:
   ```env
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_contraseña
   MAIL_USERNAME=tu_email
   MAIL_PASSWORD=tu_token
   ```
4. Ejecuta el proyecto desde tu IDE o usa Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

## 📖 Documentación de la API
Una vez que la aplicación esté corriendo, puedes explorar e interactuar con todos los endpoints a través de Swagger UI ingresando a:
`http://localhost:8080/swagger-ui/index.html`
