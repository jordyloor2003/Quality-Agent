# Data Model: Registrar pago y ver distribución

**Date**: 2026-07-04
**Feature**: US-01 - Registrar pago y ver distribución

## Entities

### 1. Préstamo (Loan)

Entidad principal que representa un crédito activo.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, Auto-increment | Identificador único |
| numeroPrestamo | String | Unique, Not Null | Número de préstamo |
| clienteId | Long | Not Null | ID del cliente |
| montoOriginal | BigDecimal | Not Null, > 0 | Monto del préstamo |
| saldoActual | BigDecimal | Not Null, >= 0 | Saldo pendiente |
| tasaInteres | BigDecimal | Not Null | Tasa de interés anual |
| estado | Enum | Not Null | ACTIVO, INACTIVO, MOROSO |
| fechaCreacion | LocalDateTime | Not Null | Fecha de creación |
| fechaActualizacion | LocalDateTime | Not Null | Última actualización |

**Relationships**:
- One-to-Many: Cuotas (ordenadas por número de cuota)

---

### 2. Cuota (Installment)

Pago programado del préstamo.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, Auto-increment | Identificador único |
| prestamoId | Long | FK, Not Null | Referencia al préstamo |
| numeroCuota | Integer | Not Null | Número de cuota |
| valorCuota | BigDecimal | Not Null, > 0 | Valor total de la cuota |
| valorCapital | BigDecimal | Not Null, >= 0 | Porción capital |
| valorInteres | BigDecimal | Not Null, >= 0 | Porción intereses |
| fechaVencimiento | LocalDate | Not Null | Fecha límite de pago |
| fechaPago | LocalDateTime | Nullable | Fecha de pago efectivo |
| saldoPagado | BigDecimal | Not Null, >= 0 | Monto pagado hasta ahora |
| estado | Enum | Not Null | VENCIDA, PENDIENTE, PAGADA, PARCIALMENTE_PAGADA |

**Relationships**:
- Many-to-One: Préstamo
- One-to-Many: DistribucionesPago

---

### 3. Pago (Payment)

Registro monetario del pago realizado.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, Auto-increment | Identificador único |
| prestamoId | Long | FK, Not Null | Referencia al préstamo |
| montoPago | BigDecimal | Not Null, > 0 | Monto total del pago |
| fechaPago | LocalDateTime | Not Null | Fecha de registro |
| operadorId | Long | Not Null | ID del operador |
| estado | Enum | Not Null | REGISTRADO, CONFIRMADO, RECHAZADO |
| observacion | String | Nullable | Observaciones |

**Relationships**:
- Many-to-One: Préstamo
- One-to-Many: DistribucionesPago
- One-to-One: SaldoAFavor (opcional)

---

### 4. DistribucionPago (PaymentDistribution)

Detalle de cómo se aplicó el pago a cada cuota.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, Auto-increment | Identificador único |
| pagoId | Long | FK, Not Null | Referencia al pago |
| cuotaId | Long | FK, Not Null | Referencia a la cuota |
| montoAplicado | BigDecimal | Not Null, >= 0 | Monto aplicado a esta cuota |
| fechaAplicacion | LocalDateTime | Not Null | Fecha de aplicación |

**Relationships**:
- Many-to-One: Pago
- Many-to-One: Cuota

---

### 5. SaldoAFavor (CreditBalance)

Monto excedente que queda a favor del cliente.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, Auto-increment | Identificador único |
| pagoId | Long | FK, Not Null | Pago que generó el saldo |
| clienteId | Long | Not Null | Cliente beneficiario |
| monto | BigDecimal | Not Null, > 0 | Monto del saldo a favor |
| origen | String | Not Null | Origen del excedente |
| utilizado | Boolean | Not Null, Default false | Si ya fue utilizado |
| fechaCreacion | LocalDateTime | Not Null | Fecha de creación |

**Relationships**:
- Many-to-One: Pago

---

## State Transitions

### Cuota State Machine

```
PENDIENTE ──(pago >= valor)──► PAGADA
    │                             
    └──(0 < pago < valor)──► PARCIALMENTE_PAGADA
    │
    └──(fecha < hoy)──► VENCIDA
```

### Pago State Machine

```
REGISTRADO ──(confirmar)──► CONFIRMADO
     │
     └──(rechazar)──► RECHAZADO
```

---

## Validation Rules

### Registro de Pago

1. El préstamo debe estar en estado ACTIVO
2. El monto del pago debe ser mayor a 0
3. El monto no puede exceder el saldo total del préstamo (sin saldo a favor existente)
4. Solo un operador autorizado puede registrar pagos

### Distribución de Pago

1. Las cuotas vencidas se procesan primero (orden ascendente por fecha)
2. Las cuotas pendientes se procesan después (orden ascendente por fecha)
3. Si el pago supera el total de cuotas → crear SaldoAFavor
4. Una cuota se marca PAGADA cuando saldoPagado >= valorCuota
5. Una cuota se marca PARCIALMENTE_PAGADA cuando 0 < saldoPagado < valorCuota

---

## SQL Schema (schema.sql)

```sql
-- =====================================================
-- SCHEMA: FondoCesantia Payment System
-- =====================================================

-- Table: Prestamo (Loan)
CREATE TABLE prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_prestamo VARCHAR(20) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    monto_original DECIMAL(15,2) NOT NULL CHECK (monto_original > 0),
    saldo_actual DECIMAL(15,2) NOT NULL CHECK (saldo_actual >= 0),
    tasa_interes DECIMAL(5,4) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: Cuota (Installment)
CREATE TABLE cuota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id BIGINT NOT NULL,
    numero_cuota INTEGER NOT NULL,
    valor_cuota DECIMAL(15,2) NOT NULL CHECK (valor_cuota > 0),
    valor_capital DECIMAL(15,2) NOT NULL,
    valor_interes DECIMAL(15,2) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    fecha_pago TIMESTAMP,
    saldo_pagado DECIMAL(15,2) NOT NULL DEFAULT 0,
    estado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_cuota_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamo(id)
);

-- Table: Pago (Payment)
CREATE TABLE pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id BIGINT NOT NULL,
    monto_pago DECIMAL(15,2) NOT NULL CHECK (monto_pago > 0),
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operador_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO',
    observacion TEXT,
    CONSTRAINT fk_pago_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamo(id)
);

-- Table: DistribucionPago (PaymentDistribution)
CREATE TABLE distribucion_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pago_id BIGINT NOT NULL,
    cuota_id BIGINT NOT NULL,
    monto_aplicado DECIMAL(15,2) NOT NULL,
    fecha_aplicacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_distribucion_pago FOREIGN KEY (pago_id) REFERENCES pago(id),
    CONSTRAINT fk_distribucion_cuota FOREIGN KEY (cuota_id) REFERENCES cuota(id)
);

-- Table: SaldoAFavor (CreditBalance)
CREATE TABLE saldo_a_favor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pago_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    monto DECIMAL(15,2) NOT NULL CHECK (monto > 0),
    origen VARCHAR(50) NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saldo_pago FOREIGN KEY (pago_id) REFERENCES pago(id)
);

-- Indexes
CREATE INDEX idx_cuota_fecha_vencimiento ON cuota(fechaVencimiento);
CREATE INDEX idx_cuota_prestamo ON cuota(prestamoId);
CREATE INDEX idx_distribucion_pago ON distribucion_pago(pagoId);
CREATE INDEX idx_saldo_cliente ON saldo_a_favor(clienteId);
```

---

## Seed Data (data.sql)

```sql
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
```

---

## Index Recommendations

```sql
-- Index for finding overdue installments
CREATE INDEX idx_cuota_fecha_vencimiento ON Cuota(fechaVencimiento);

-- Index for finding installments by loan
CREATE INDEX idx_cuota_prestamo ON Cuota(prestamoId);

-- Index for payment distribution queries
CREATE INDEX idx_distribucion_pago ON DistribucionPago(pagoId);

-- Index for credit balance queries
CREATE INDEX idx_saldo_cliente ON SaldoAFavor(clienteId);
```