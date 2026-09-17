# Documentación de Implementación: Limpieza de Arquitectura (Swagger / OpenAPI)

## 1. Resumen de la Característica
Se desacoplaron las anotaciones de documentación OpenAPI/Swagger de los controladores de negocio (`Controller`) mediante la implementación del **Patrón de Interfaces de Documentación** (`dev.logistica.api.controller.doc.*Api`). De esta forma, las clases controladoras se mantienen 100% enfocadas en la lógica HTTP y orquestación, mientras que las interfaces concentran las anotaciones de Swagger y los contratos web.

## 2. Decisiones Arquitectónicas (ADR)
- **Patrón de Interfaces para Documentación:** En lugar de saturar las clases controladoras con metadatos de Swagger (`@Tag`, `@Operation`, `@ApiResponse`, etc.), se definieron interfaces Java dedicadas en el paquete `controller.doc` (ej. `ClienteApi`, `VehiculoApi`, `EnvioApi`). Los controladores implementan estas interfaces (`implements ClienteApi`), permitiendo a `springdoc-openapi` heredar y construir la documentación enriquecida sin ensuciar la implementación real.
- **Configuración Global de Seguridad en OpenAPI:** Se centralizó el esquema `Bearer JWT` en `OpenApiConfig.java` para que Swagger UI exponga el botón global `Authorize` para autenticación con token.
- **Colección de Postman Externa:** Se agregó la colección en `/postman` con extracción automática de JWT mediante scripts en las peticiones de autenticación, ofreciendo una alternativa estandarizada para testing fuera del navegador.

## 3. Componentes Modificados y Nuevos
- **Nuevas Interfaces (`dev.logistica.api.controller.doc`):**
  - `AuthApi.java`
  - `ClienteApi.java`
  - `EnvioApi.java`
  - `PaqueteApi.java`
  - `ReporteEnviosApi.java`
  - `RutaApi.java`
  - `VehiculoApi.java`
- **Controladores Limpios:**
  - `AuthController.java`
  - `ClienteController.java`
  - `EnvioController.java`
  - `PaqueteController.java`
  - `ReporteEnviosController.java`
  - `RutaController.java`
  - `VehiculoController.java`
- **Configuración OpenAPI:** `OpenApiConfig.java`
- **Artefacto Postman:** `postman/sistema-logistica-api.postman_collection.json`

## 4. Pruebas y Validación
- Validación de compilación sin errores (`mvn clean compile`).
- Validación de suite de pruebas de integración (`mvn test`).
- Verificación en Swagger UI (`/swagger-ui/index.html`) confirmando presencia de contratos, esquemas y botón `Authorize`.
