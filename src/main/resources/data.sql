-- Archivo: src/main/resources/data.sql

-- ------------------------------------------------------------------------
-- 1. CONFIGURACIÓN INICIAL: Asegura que los IDs empiecen alto
-- ------------------------------------------------------------------------
ALTER TABLE clientes AUTO_INCREMENT = 10001;
ALTER TABLE vehiculo AUTO_INCREMENT = 10101;
ALTER TABLE paquete AUTO_INCREMENT = 10201;


-- ----------------------------------------------------
-- 2. CLIENTES (Clientes/Remitentes/Destinatarios)
-- Recordatorio: La columna se llama 'documento_cuit'
-- ----------------------------------------------------
INSERT INTO clientes
(nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal)
VALUES
    ('Marcos Ramires (Remitente)', '20-12345678-9', '3884123456', 'marcos@gmail.com', 'Av. Gral. Belgrano 100', '4600'),
    ('Luciano Soriano (Destinatario)', '27-98765432-1', '3884654321', 'lucianoS.test@gmail.com', 'Calle Los Lapachos 50', '4601'),
    ('Cliente Mayorista ABC', '30-55511122-3', '3884990011', 'mayorista@abc.com', 'Ruta 9, Km 3', '4602');


-- ----------------------------------------------------
-- 3. VEHÍCULOS
-- ----------------------------------------------------
INSERT INTO vehiculo
(patente, capacidad_max_peso_kg, capacidad_max_vol_dm3, refrigerado, rango_temperatura_min, rango_temperatura_max)
VALUES
    ('AAA-001', 3500.0, 10000.0, FALSE, NULL, NULL), -- Furgoneta Estándar
    ('REF-456', 1500.0, 4000.0, TRUE, 0.0, 5.0);   -- Furgoneta Refrigerada


-- ----------------------------------------------------
-- 4. PAQUETES (SOLO FRAGIL O REFRIGERADO)
-- ----------------------------------------------------

-- **4.1 Paquete FRÁGIL (Copa de Cristal)**
INSERT INTO paquete (codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES
    ('PAQ-FRG-001', 1.2, 3.0, 'FRAGIL');
-- Usamos el ID generado para la tabla hija
SET @id_fragil_1 = LAST_INSERT_ID();
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES
    (@id_fragil_1, 'ALTA', TRUE);


-- **4.2 Paquete REFRIGERADO (Carne)**
INSERT INTO paquete (codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES
    ('PAQ-REF-002', 8.0, 20.0, 'REFRIGERADO');
SET @id_refri_1 = LAST_INSERT_ID();
INSERT INTO paquete_refrigerado (id, temperatura_objetivo, rango_min, rango_max, horas_max_fuera_de_frio) VALUES
    (@id_refri_1, 3.0, 1.0, 5.0, 4);


-- **4.3 Paquete FRÁGIL (Electrónica)**
INSERT INTO paquete (codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES
    ('PAQ-FRG-003', 2.5, 5.0, 'FRAGIL');
SET @id_fragil_2 = LAST_INSERT_ID();
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES
    (@id_fragil_2, 'MEDIA', FALSE);


-- **4.4 Paquete REFRIGERADO (Medicamentos)**
INSERT INTO paquete (codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES
    ('PAQ-REF-004', 0.5, 1.0, 'REFRIGERADO');
SET @id_refri_2 = LAST_INSERT_ID();
INSERT INTO paquete_refrigerado (id, temperatura_objetivo, rango_min, rango_max, horas_max_fuera_de_frio) VALUES
    (@id_refri_2, 5.0, 2.0, 8.0, 2);