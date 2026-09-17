# Documentación de Implementación: Seguridad con JWT

## 1. Resumen de la Característica
Se implementó un sistema de seguridad y autenticación "Stateless" basado en **JSON Web Tokens (JWT)** y **Spring Security**. El objetivo fue proteger los endpoints de la API y establecer roles jerárquicos (ADMIN, EMPLEADO, CLIENTE).

## 2. Referencia a la Especificación (SDD)
Esta implementación satisface el contrato definido en: `specs/security-spec.md`

## 3. Decisiones Arquitectónicas (ADR)
- **State vs Stateless:** Se optó por un modelo "Stateless" (`SessionCreationPolicy.STATELESS`). El servidor no mantiene estado de sesión en memoria, mejorando la escalabilidad horizontal.
- **Filtro Personalizado:** Se implementó `JwtAuthenticationFilter` (extendiendo `OncePerRequestFilter`) para interceptar todas las peticiones, extraer el token del header `Authorization: Bearer <token>` y delegar la validación.
- **Manejo de Errores:** Se integraron las excepciones de autenticación (`BadCredentialsException`) en el `GlobalExceptionHandler` para retornar un formato de error estandarizado (401 Unauthorized) en vez de trazas de error nativas de Spring.

## 4. Componentes Implementados
1. **Modelos:** `Usuario` (Entidad JPA) y `Rol` (Enum).
2. **DTOs:** `RegistroRequest`, `LoginRequest` y `JwtResponse`.
3. **Core Seguridad:**
   - `SecurityConfig`: Configuración principal del filter chain y permisos.
   - `JwtUtils`: Lógica criptográfica (Firma con algoritmo HS256).
   - `CustomUserDetailsService`: Puente entre la base de datos y Spring Security.
4. **Controladores:** `AuthController` que expone `/api/auth/login` y `/api/auth/register`.

## 5. Pruebas y Validación
Se implementó la clase `AuthIntegrationTest` validando flujos correctos (200 OK y 201 Created) y flujos de credenciales inválidas (401 Unauthorized), confirmando que el entorno cumple con la especificación original.
