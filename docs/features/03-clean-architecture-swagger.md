# Documentación de Implementación: Limpieza de Arquitectura (Swagger / OpenAPI)

## 1. Resumen de la Característica
Se eliminaron todas las anotaciones manuales de Swagger (`@Tag`, `@Operation`, `@ApiResponse`, etc.) del código fuente para preservar la limpieza de los Controladores y DTOs, manteniendo la funcionalidad de la interfaz web auto-generada y añadiendo una alternativa profesional de pruebas con Postman.

## 2. Decisiones Arquitectónicas (ADR)
- **Eliminación de Anotaciones Swagger:** Las anotaciones de documentación en el código rompen con el principio de Responsabilidad Única, ya que el código de infraestructura/documentación ensucia la lógica de control. Se confía en la capacidad nativa de `springdoc-openapi` para interpretar `@GetMapping`, `@PostMapping` y la estructura de los DTOs para auto-generar la documentación.
- **Colección de Postman Externa:** Se agregó la carpeta `/postman` al repositorio con una colección exportada en formato JSON. Esta estrategia está alineada con estándares de la industria, permitiendo que reclutadores, QAs y otros desarrolladores importen las pruebas con variables de entorno listas para usar (`{{baseUrl}}`, `{{jwt_token}}`).

## 3. Componentes Modificados
- Múltiples Controladores y DTOs (ej. `VehiculoController`, `AuthController`, `ClienteDTO`): Se eliminaron bloques masivos de metadatos Swagger. En promedio se redujeron entre 30 y 80 líneas por controlador sin perder un solo endpoint en la UI de `/swagger-ui/index.html`.
- **Nuevo Artefacto:** `postman/sistema-logistica-api.postman_collection.json`.

## 4. Pruebas y Validación
Se verificó mediante scripts de limpieza que ninguna anotación del paquete `io.swagger.v3.oas.annotations` permaneciera activa en los archivos Java.
