# Especificación de Seguridad (JWT)

## 1. Entidades y Roles
El sistema contará con una gestión de usuarios basada en la entidad `Usuario` y un enumerado `Rol`.

### Entidad: Usuario
- `id` (Long, Autogenerado)
- `username` (String, Formato Email, Único)
- `password` (String, Encriptada con BCrypt)
- `rol` (Enum Rol)

### Roles Disponibles
- **ROLE_ADMIN**: Acceso total al sistema.
- **ROLE_EMPLEADO**: Operadores logísticos (pueden crear envíos, avanzar estados, etc).
- **ROLE_CLIENTE**: Usuario final (solo puede ver sus propios envíos).

---

## 2. Endpoints de Autenticación (Públicos)

### 2.1. Registro de Usuario
- **Ruta:** `POST /api/auth/register`
- **Descripción:** Permite a un nuevo usuario darse de alta en el sistema.
- **Requiere Autenticación:** NO
- **Body de la Petición:**
  ```json
  {
    "username": "cliente@correo.com",
    "password": "miPasswordSegura123",
    "rol": "ROLE_CLIENTE"
  }
  ```
- **Respuestas Esperadas:**
  - `201 Created`: El usuario fue registrado exitosamente.
  - `400 Bad Request`: Datos inválidos o el username ya existe.

### 2.2. Inicio de Sesión
- **Ruta:** `POST /api/auth/login`
- **Descripción:** Valida las credenciales y devuelve el token JWT.
- **Requiere Autenticación:** NO
- **Body de la Petición:**
  ```json
  {
    "username": "cliente@correo.com",
    "password": "miPasswordSegura123"
  }
  ```
- **Respuestas Esperadas:**
  - `200 OK`: Credenciales válidas. Retorna el Token JWT.
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "cliente@correo.com",
    "rol": "ROLE_CLIENTE"
  }
  ```
  - `401 Unauthorized`: Usuario o contraseña incorrectos.

---

## 3. Matriz de Autorización (Seguridad en Endpoints de Negocio)

Toda petición hacia los endpoints protegidos debe incluir la cabecera:
`Authorization: Bearer <token_jwt>`

### Reglas
| Ruta | Método | Rol Requerido | Descripción |
|------|--------|---------------|-------------|
| `/api/envios` | `POST` | `ADMIN`, `EMPLEADO` | Crear un envío nuevo. |
| `/api/envios/{codigo}/avanzar` | `PUT` | `ADMIN`, `EMPLEADO` | Avanzar el estado de un envío. |
| `/api/envios/remitente/{documento}`| `GET` | `ADMIN`, `EMPLEADO`, `CLIENTE`* | Ver envíos. (*En el futuro, un CLIENTE solo debería ver los suyos). |
| `/api/clientes` | `POST`, `PUT` | `ADMIN`, `EMPLEADO` | Crear o modificar clientes. |
| `/api/auth/**` | `POST` | `(Público)` | Login y registro. |

*Cualquier ruta no especificada requerirá, como mínimo, que el usuario esté autenticado.*
