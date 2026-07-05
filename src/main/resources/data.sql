-- =====================================================
-- SEED DATA: Préstamos de prueba para desarrollo
-- =====================================================

-- Insertar préstamos de ejemplo
INSERT INTO prestamo (numero_prestamo, cliente_id, monto_original, saldo_actual, tasa_interes, estado, fecha_creacion, fecha_actualizacion) VALUES
('PR-001', 1, 10000.00, 8000.00, 0.15, 'ACTIVO', '2025-01-15 10:00:00', '2025-01-15 10:00:00'),
('PR-002', 2, 5000.00, 3500.00, 0.12, 'ACTIVO', '2025-02-20 09:30:00', '2025-02-20 09:30:00'),
('PR-003', 3, 15000.00, 15000.00, 0.18, 'ACTIVO', '2025-06-01 14:00:00', '2025-06-01 14:00:00');

-- Insertar cuotas para PR-001 (con cuotas vencidas y pendientes)
INSERT INTO cuota (prestamo_id, numero_cuota, valor_cuota, valor_capital, valor_interes, fecha_vencimiento, saldo_pagado, estado) VALUES
(1, 1, 1000.00, 750.00, 250.00, '2025-02-15', 0, 'VENCIDA'),
(1, 2, 1000.00, 750.00, 250.00, '2025-03-15', 0, 'VENCIDA'),
(1, 3, 1000.00, 750.00, 250.00, '2025-04-15', 0, 'PENDIENTE'),
(1, 4, 1000.00, 750.00, 250.00, '2025-05-15', 0, 'PENDIENTE'),
(1, 5, 1000.00, 750.00, 250.00, '2025-06-15', 0, 'PENDIENTE'),
(1, 6, 1000.00, 750.00, 250.00, '2025-07-15', 0, 'PENDIENTE'),
(1, 7, 1000.00, 750.00, 250.00, '2025-08-15', 0, 'PENDIENTE'),
(1, 8, 1000.00, 750.00, 250.00, '2025-09-15', 0, 'PENDIENTE');

-- Insertar cuotas para PR-002 (todas pendientes)
INSERT INTO cuota (prestamo_id, numero_cuota, valor_cuota, valor_capital, valor_interes, fecha_vencimiento, saldo_pagado, estado) VALUES
(2, 1, 500.00, 400.00, 100.00, '2025-06-20', 0, 'PENDIENTE'),
(2, 2, 500.00, 400.00, 100.00, '2025-07-20', 0, 'PENDIENTE'),
(2, 3, 500.00, 400.00, 100.00, '2025-08-20', 0, 'PENDIENTE'),
(2, 4, 500.00, 400.00, 100.00, '2025-09-20', 0, 'PENDIENTE'),
(2, 5, 500.00, 400.00, 100.00, '2025-10-20', 0, 'PENDIENTE'),
(2, 6, 500.00, 400.00, 100.00, '2025-11-20', 0, 'PENDIENTE'),
(2, 7, 500.00, 400.00, 100.00, '2025-12-20', 0, 'PENDIENTE');

-- Insertar cuotas para PR-003 (recién creado, todas pendientes)
INSERT INTO cuota (prestamo_id, numero_cuota, valor_cuota, valor_capital, valor_interes, fecha_vencimiento, saldo_pagado, estado) VALUES
(3, 1, 1500.00, 1200.00, 300.00, '2025-07-15', 0, 'PENDIENTE'),
(3, 2, 1500.00, 1200.00, 300.00, '2025-08-15', 0, 'PENDIENTE'),
(3, 3, 1500.00, 1200.00, 300.00, '2025-09-15', 0, 'PENDIENTE'),
(3, 4, 1500.00, 1200.00, 300.00, '2025-10-15', 0, 'PENDIENTE'),
(3, 5, 1500.00, 1200.00, 300.00, '2025-11-15', 0, 'PENDIENTE'),
(3, 6, 1500.00, 1200.00, 300.00, '2025-12-15', 0, 'PENDIENTE'),
(3, 7, 1500.00, 1200.00, 300.00, '2026-01-15', 0, 'PENDIENTE'),
(3, 8, 1500.00, 1200.00, 300.00, '2026-02-15', 0, 'PENDIENTE'),
(3, 9, 1500.00, 1200.00, 300.00, '2026-03-15', 0, 'PENDIENTE'),
(3, 10, 1500.00, 1200.00, 300.00, '2026-04-15', 0, 'PENDIENTE');

-- Operadores de prueba
INSERT INTO pago (prestamo_id, monto_pago, fecha_pago, operador_id, estado, observacion) VALUES
(1, 500.00, '2025-03-01 11:00:00', 1, 'CONFIRMADO', 'Pago de prueba para cuota vencida');

-- Distribuciones de prueba
INSERT INTO distribucion_pago (pago_id, cuota_id, monto_aplicado, fecha_aplicacion) VALUES
(1, 1, 500.00, '2025-03-01 11:00:00');

-- Actualizar cuota parcialmente pagada
UPDATE cuota SET saldo_pagado = 500.00, estado = 'PARCIALMENTE_PAGADA' WHERE id = 1;