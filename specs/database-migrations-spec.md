# Especificación de Migraciones de Base de Datos (Flyway)

## 1. Objetivo
Migrar la gestión del esquema de base de datos de la generación automática de Hibernate (`ddl-auto=create-drop`/`update`) hacia un sistema de versionado controlado utilizando **Flyway**. Esto garantizará la integridad, reproducibilidad y evolución segura de la base de datos en todos los entornos (Desarrollo, Testing, Producción).

## 2. Configuración Core
- **Herramienta:** Flyway
- **Ubicación de Scripts:** `src/main/resources/db/migration/`
- **Nomenclatura Estándar:** `V<Versión>__<Descripción>.sql` (Ej: `V1__init_schema.sql`)
- **Política de Hibernate:** `spring.jpa.hibernate.ddl-auto=validate`

## 3. Estructura de Versiones Iniciales

### V1__init_schema.sql
Script base que contiene el DDL original del sistema de logística.
- Tabla `clientes`
- Tabla `vehiculo`
- Tabla `paquete` (y tablas hijas de herencia: `paquete_fragil`, `paquete_refrigerado`)
- Tabla `ruta`
- Tabla `envios`
- Tabla `envios_paquetes` (relación)
- Tabla `ruta_envios` (relación)
- Tabla `historial_estado_envio`

### V2__add_security_tables.sql
Script que contiene el esquema de las tablas necesarias para la seguridad JWT introducida anteriormente.
- Tabla `usuarios`

### V3__seed_initial_data.sql (Opcional)
Si es necesario contar con datos básicos de configuración o prueba, se incluirán en este script para no depender de la ejecución mágica del archivo `data.sql` tradicional de Spring, dejándolo versionado explícitamente en Flyway.

## 4. Reglas de Desarrollo
1. **Inmutabilidad:** Una vez que un script de migración ha sido commiteado y ejecutado, **NUNCA** debe ser modificado. Flyway rechazará el arranque de la aplicación si el checksum del archivo cambia.
2. **Evolución:** Cualquier alteración al modelo de datos (añadir una columna, cambiar un tipo de dato) requerirá la creación de un nuevo script (ej. `V4__add_columna_telefono.sql`).
3. **Hibernate Validará:** El código Java (`@Entity`) siempre debe mapear exactamente lo que Flyway construye en SQL.
