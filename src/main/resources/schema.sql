-- =====================================================
-- SCHEMA: FondoCesantia Payment System (H2 Compatibility)
-- =====================================================

-- Table: Prestamo (Loan)
CREATE TABLE prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_prestamo VARCHAR(20) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    monto_original DECIMAL(15,2) NOT NULL,
    saldo_actual DECIMAL(15,2) NOT NULL,
    tasa_interes DECIMAL(5,4) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: Cuota (Installment)
CREATE TABLE cuota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id BIGINT NOT NULL,
    numero_cuota INTEGER NOT NULL,
    valor_cuota DECIMAL(15,2) NOT NULL,
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
    monto_pago DECIMAL(15,2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operador_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO',
    observacion VARCHAR(500),
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
    monto DECIMAL(15,2) NOT NULL,
    origen VARCHAR(50) NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saldo_pago FOREIGN KEY (pago_id) REFERENCES pago(id)
);

-- Indexes
CREATE INDEX idx_cuota_fecha_vencimiento ON cuota(fecha_vencimiento);
CREATE INDEX idx_cuota_prestamo ON cuota(prestamo_id);
CREATE INDEX idx_distribucion_pago ON distribucion_pago(pago_id);
CREATE INDEX idx_saldo_cliente ON saldo_a_favor(cliente_id);