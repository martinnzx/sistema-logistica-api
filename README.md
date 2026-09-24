<div align="center">
  <h1>🚚 Sistema de Logística de Envíos API</h1>
  <p><strong>Plataforma integral para la gestión de paquetería, flotas y rutas de distribución.</strong></p>

  <!-- Badges -->
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Security-JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT Security" />
</div>

<br>

API REST desarrollada con Java y Spring Boot para gestionar clientes, paquetes, vehículos, envíos y rutas de distribución.

Este proyecto fue realizado en el ámbito académico por un equipo de dos integrantes. Su objetivo fue aplicar modelado orientado a objetos, persistencia de datos, reglas de negocio, seguridad con JWT, patrones de diseño y pruebas automatizadas en una API backend.

## Funcionalidades

- Registro e inicio de sesión mediante JWT.
- Autorización por roles: `ADMIN`, `EMPLEADO` y `CLIENTE`.
- Gestión de clientes, paquetes, vehículos, envíos y rutas.
- Modelado de paquetes frágiles y refrigerados.
- Búsqueda de vehículos por capacidad de peso, volumen y refrigeración.
- Validación de la capacidad del vehículo antes de asignar una ruta.
- Validación de compatibilidad de temperatura para paquetes refrigerados.
- Consulta de envíos por remitente, destinatario, estado y código de seguimiento.
- Historial de cambios de estado de cada envío.
- Generación de reportes y comprobantes en PDF y Excel.
- Notificaciones por correo al registrar y entregar un envío.
- Documentación interactiva de la API con OpenAPI y Swagger UI.
- Colección de Postman incluida en el repositorio.

## Ciclo de vida de un envío

El cambio de estado de los envíos se implementó mediante el patrón State.

```text
GENERADO -> EN_ALMACEN -> EN_RUTA -> ENTREGADO
```

- Un envío puede pasar a `CANCELADO` desde `GENERADO` o `EN_ALMACEN`.
- Un envío puede pasar a `DEVUELTO` desde `EN_RUTA`.

Las transiciones inválidas son rechazadas por la lógica de negocio. Para marcar un envío como entregado, primero debe tener un comprobante de entrega asociado.

## Reglas de negocio destacadas

- Un paquete debe tener peso y volumen válidos.
- Un paquete refrigerado debe indicar una temperatura objetivo dentro de su rango permitido.
- Una ruta solo puede incluir envíos que se encuentren en estado `EN_ALMACEN`.
- El peso y el volumen totales de una ruta no pueden superar la capacidad del vehículo.
- Los paquetes refrigerados requieren un vehículo con refrigeración y un rango de temperatura compatible.
- Un paquete no puede estar asociado a más de un envío.
- Cada envío recibe un código único y conserva el historial de sus cambios de estado.

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security y JWT
- PostgreSQL 17
- Bean Validation
- Springdoc OpenAPI / Swagger UI
- Maven
- JUnit 5
- Docker y Docker Compose
- Postman
- Spring Mail
- Apache POI e iText

## Organización del proyecto

El código está organizado por responsabilidades:

```text
src/main/java/dev/logistica/api/
|-- controller/    # Endpoints REST
|-- dto/           # Objetos de entrada y salida
|-- mapper/        # Conversión entre entidades y DTOs
|-- model/         # Entidades del dominio
|-- repository/    # Acceso a datos
|-- security/      # Autenticación y autorización con JWT
|-- service/       # Lógica de negocio
`-- state/         # Implementación del patrón State
```

La documentación de los endpoints se mantiene separada de los controladores mediante interfaces ubicadas en `controller/doc`.

## Requisitos

Para ejecutar la aplicación con contenedores necesitás:

- Docker Desktop o Docker Engine
- Docker Compose

También podés ejecutarla sin Docker si tenés Java 21 y Maven, pero deberás configurar PostgreSQL y las variables de entorno manualmente.

## Configuración

1. Cloná el repositorio:

```bash
git clone https://github.com/martinnzx/sistema-logistica-api.git
cd sistema-logistica-api
```

2. Creá el archivo `.env` a partir del ejemplo incluido:

```bash
cp .env.example .env
```

En Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

3. Completá las variables del archivo `.env`:

```dotenv
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
MAIL_USERNAME=tu_correo
MAIL_PASSWORD=tu_contraseña_de_aplicación
```

No publiques credenciales reales en el repositorio.

## Ejecución con Docker Compose

Construí la imagen e iniciá la API y PostgreSQL:

```bash
docker compose up --build
```

La API quedará disponible en:

```text
http://localhost:8080
```

Para detener los contenedores:

```bash
docker compose down
```

## Documentación y uso de la API

Con la aplicación en ejecución, Swagger UI está disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

Los endpoints de registro e inicio de sesión son públicos. Para utilizar los demás endpoints:

1. Registrá un usuario en `POST /api/auth/register`.
2. Iniciá sesión en `POST /api/auth/login`.
3. Copiá el token devuelto.
4. En Swagger UI, seleccioná **Authorize** e ingresá el token JWT.

También podés importar en Postman la colección disponible en:

```text
postman/sistema-logistica-api.postman_collection.json
```

## Recursos principales

| Recurso | Ruta base |
|---|---|
| Autenticación | `/api/auth` |
| Clientes | `/api/clientes` |
| Paquetes | `/api/paquetes` |
| Vehículos | `/api/vehiculos` |
| Envíos | `/api/envios` |
| Rutas | `/api/rutas` |
| Reportes de envíos | `/api/reportes/envios` |

La descripción completa de las operaciones, parámetros y respuestas se encuentra en Swagger UI.

## Pruebas

El repositorio incluye pruebas automatizadas sobre la lógica de servicios y la seguridad de la API. Si tenés Maven instalado, podés ejecutar la suite con:

```bash
mvn test
```

## Trabajo en equipo

Proyecto académico desarrollado por dos integrantes:

- [Martin Mamani](https://github.com/martinnzx)
- [MendezM09](https://github.com/MendezM09)

El desarrollo se realizó de manera colaborativa. Entre mis principales aportes se encuentran el modelo de clases, la lógica de servicios y las reglas de negocio, la seguridad con Spring Security y JWT, la implementación del patrón State y las pruebas de la lógica de servicios.
