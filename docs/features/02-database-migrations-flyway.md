# Documentación de Implementación: Control de Versiones de BD con Flyway

## 1. Resumen de la Característica
Se reemplazó el sistema automático de generación de esquemas de Hibernate por un control de versiones estricto y predecible utilizando **Flyway**.

## 2. Referencia a la Especificación (SDD)
Esta implementación satisface el contrato definido en: `specs/database-migrations-spec.md`

## 3. Decisiones Arquitectónicas (ADR)
- **Eliminación de `ddl-auto=create-drop/update`:** Se cambió a `validate` en producción/desarrollo para prevenir pérdida de datos o alteraciones inesperadas por parte del ORM. Hibernate ahora actúa como un mecanismo de solo validación contra el SQL nativo.
- **Migración de `data.sql`:** Se descubrió un archivo legado de inicialización (`data.sql`). Para mantener un control unificado y evitar conflictos (Condiciones de carrera en el arranque), se integró dentro de Flyway como la versión `V3`.
- **Estrategia en Testing:** Para no comprometer la velocidad de los tests ni lidiar con scripts SQL que contienen sintaxis específica de PostgreSQL en bases de datos H2 en memoria, Flyway fue deshabilitado exclusivamente en el perfil de `test` (`application-test.properties`).

## 4. Componentes Implementados
1. **Dependencias:** `flyway-core` y `flyway-database-postgresql`.
2. **Scripts SQL (`src/main/resources/db/migration/`):**
   - `V1__init_schema.sql`: DDL base del dominio de negocio (Clientes, Vehículos, Paquetes, etc).
   - `V2__add_security_tables.sql`: DDL de la entidad Usuario (Seguridad).
   - `V3__seed_initial_data.sql`: Refactor del script de prueba heredado.

## 5. Pruebas y Validación
El arranque de la aplicación fue validado. Flyway crea y mantiene exitosamente la tabla `flyway_schema_history`, validando los checksums en el arranque y ejecutando solo los scripts faltantes de manera inmutable.
