-- ========================================================================
-- 1. CLIENTES
-- ========================================================================
INSERT INTO clientes (id, nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal) VALUES (10001, 'Marcos Ramires', '20123456789', '3884123456', 'marcos@gmail.com', 'Av. Belgrano 100', '4600');
INSERT INTO clientes (id, nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal) VALUES (10002, 'Luciano Soriano', '27987654321', '3884654321', 'luciano@test.com', 'Los Lapachos 50', '4601');
INSERT INTO clientes (id, nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal) VALUES (10003, 'Empresa Logistica', '30112233445', '3884000000', 'contacto@logistica.com', 'Ruta 9 Km 5', '4600');
INSERT INTO clientes (id, nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal) VALUES (10004, 'Antigua Librería', '30999888777', '3884112233', 'libros@antigua.com', 'Lavalle 300', '4600');
INSERT INTO clientes (id, nombre_razon_social, documento_cuit, telefono, email, direccion_principal, codigo_postal) VALUES (10005, 'Supermercado Dia', '30555666777', '3884998877', 'compras@dia.com', 'Gorriti 100', '4600');

-- ========================================================================
-- 2. VEHÍCULOS
-- ========================================================================
INSERT INTO vehiculo (id, patente, capacidad_max_peso_kg, capacidad_max_vol_dm3, refrigerado, rango_temperatura_min, rango_temperatura_max) VALUES (10101, 'AAA001', 3500.0, 10000.0, FALSE, NULL, NULL);
INSERT INTO vehiculo (id, patente, capacidad_max_peso_kg, capacidad_max_vol_dm3, refrigerado, rango_temperatura_min, rango_temperatura_max) VALUES (10102, 'REF456', 1500.0, 4000.0, TRUE, 0.0, 5.0);
INSERT INTO vehiculo (id, patente, capacidad_max_peso_kg, capacidad_max_vol_dm3, refrigerado, rango_temperatura_min, rango_temperatura_max) VALUES (10103, 'VIE-999', 5000.0, 12000.0, FALSE, NULL, NULL);

-- ========================================================================
-- 3. PAQUETES
-- ========================================================================
-- Paquetes viejos
INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10201, 'PKG-OLD-1', 1.0, 2.0, 'FRAGIL');
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES (10201, 'BAJA', FALSE);

INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10202, 'PKG-OLD-2', 5.0, 10.0, 'FRAGIL');
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES (10202, 'BAJA', FALSE);

-- Paquetes semana pasada
INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10203, 'PKG-WEEK-1', 20.0, 30.0, 'REFRIGERADO');
INSERT INTO paquete_refrigerado (id, temperatura_objetivo, rango_min, rango_max, horas_max_fuera_de_frio) VALUES (10203, -5.0, -10.0, 0.0, 2);

INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10204, 'PKG-WEEK-2', 2.5, 5.0, 'FRAGIL');
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES (10204, 'ALTA', TRUE);

-- Paquetes recientes
INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10205, 'PKG-NEW-1', 1.0, 1.0, 'FRAGIL');
INSERT INTO paquete_fragil (id, nivel_fragilidad, seguro_adicional) VALUES (10205, 'MEDIA', FALSE);

INSERT INTO paquete (id, codigo, peso_kg, volumen_dm3, tipo_paquete) VALUES (10206, 'PKG-NEW-2', 50.0, 100.0, 'REFRIGERADO');
INSERT INTO paquete_refrigerado (id, temperatura_objetivo, rango_min, rango_max, horas_max_fuera_de_frio) VALUES (10206, 2.0, 0.0, 5.0, 4);

-- ========================================================================
-- 4. ENVÍOS
-- ========================================================================
-- CASO 1: Envío COMPLETADO hace 1 MES
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10401, 'ENV-MES-PASADO', 'ENTREGADO', FALSE, 'Calle Vieja 1', '4600', 10001, 10004, 'http://url/comprobante1.pdf');
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10401, 10201);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10401, NULL, 'GENERADO', NOW() - INTERVAL '35 DAY', 'Pedido antiguo');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10401, 'GENERADO', 'EN_ALMACEN', NOW() - INTERVAL '34 DAY', 'Procesado mes pasado');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10401, 'EN_ALMACEN', 'EN_RUTA', NOW() - INTERVAL '32 DAY', 'En viaje larga distancia');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10401, 'EN_RUTA', 'ENTREGADO', NOW() - INTERVAL '30 DAY', 'Entregado correctamente');

-- CASO 2: Envío COMPLETADO la SEMANA PASADA
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10402, 'ENV-SEMANA-PASADA', 'ENTREGADO', TRUE, 'Gorriti 100', '4600', 10003, 10005, 'http://url/comprobante2.pdf');
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10402, 10203);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10402, NULL, 'GENERADO', NOW() - INTERVAL '10 DAY', 'Reposición semanal');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10402, 'GENERADO', 'EN_ALMACEN', NOW() - INTERVAL '9 DAY', 'Control de frío');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10402, 'EN_ALMACEN', 'EN_RUTA', NOW() - INTERVAL '8 DAY', 'Salida reparto');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10402, 'EN_RUTA', 'ENTREGADO', NOW() - INTERVAL '7 DAY', 'Recibido por gerente');

-- CASO 3: Envío CANCELADO hace 2 SEMANAS
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10403, 'ENV-CANCEL-OLD', 'CANCELADO', FALSE, 'Ruta 9', '4600', 10001, 10002, NULL);
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10403, 10202);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10403, NULL, 'GENERADO', NOW() - INTERVAL '15 DAY', 'Intento de compra');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10403, 'GENERADO', 'CANCELADO', NOW() - INTERVAL '14 DAY', 'Cancelado por falta de pago');

-- CASO 4: Envío DEVUELTO AYER
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10404, 'ENV-DEVUELTO-AYER', 'DEVUELTO', TRUE, 'Calle Inexistente 500', '4600', 10005, 10001, NULL);
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10404, 10204);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10404, NULL, 'GENERADO', NOW() - INTERVAL '3 DAY', 'Envío express');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10404, 'GENERADO', 'EN_ALMACEN', NOW() - INTERVAL '3 DAY', 'Rápido en depósito');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10404, 'EN_ALMACEN', 'EN_RUTA', NOW() - INTERVAL '2 DAY', 'Salió a destino');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10404, 'EN_RUTA', 'DEVUELTO', NOW() - INTERVAL '1 DAY', 'Dirección no encontrada');

-- CASO 5: Envío EN RUTA (Salió HOY)
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10405, 'ENV-HOY-RUTA', 'EN_RUTA', FALSE, 'Belgrano 100', '4600', 10004, 10003, NULL);
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10405, 10205);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10405, NULL, 'GENERADO', NOW() - INTERVAL '5 HOUR', 'Generado hoy temprano');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10405, 'GENERADO', 'EN_ALMACEN', NOW() - INTERVAL '4 HOUR', 'Procesado');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10405, 'EN_ALMACEN', 'EN_RUTA', NOW() - INTERVAL '1 HOUR', 'En camino ahora mismo');

-- CASO 6: Envío EN ALMACEN (Estancado hace 3 días)
INSERT INTO envios (id, codigo_unico, estado, requiere_frio, direccion_entrega, codigo_postal, remitente_id, destinatario_id, comprobante_entrega) VALUES (10406, 'ENV-STUCK-ALM', 'EN_ALMACEN', TRUE, 'Lavalle 300', '4600', 10005, 10004, NULL);
INSERT INTO envios_paquetes (envio_id, paquetes_id) VALUES (10406, 10206);
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10406, NULL, 'GENERADO', NOW() - INTERVAL '3 DAY', 'Llegó el lunes');
INSERT INTO historial_estado_envio (envio_id, estado_anterior, estado_nuevo, fecha_hora, observacion) VALUES (10406, 'GENERADO', 'EN_ALMACEN', NOW() - INTERVAL '3 DAY', 'Esperando camión especial');

-- ========================================================================
-- 5. RUTAS
-- ========================================================================
INSERT INTO ruta (id, fecha, vehiculo_id) VALUES (10301, NOW() - INTERVAL '32 DAY', 10103);
INSERT INTO ruta_envios (ruta_id, envios_id) VALUES (10301, 10401);

INSERT INTO ruta (id, fecha, vehiculo_id) VALUES (10302, NOW() - INTERVAL '8 DAY', 10102);
INSERT INTO ruta_envios (ruta_id, envios_id) VALUES (10302, 10402);

INSERT INTO ruta (id, fecha, vehiculo_id) VALUES (10303, NOW() - INTERVAL '2 DAY', 10101);
INSERT INTO ruta_envios (ruta_id, envios_id) VALUES (10303, 10404);

INSERT INTO ruta (id, fecha, vehiculo_id) VALUES (10304, NOW(), 10101);
INSERT INTO ruta_envios (ruta_id, envios_id) VALUES (10304, 10405);