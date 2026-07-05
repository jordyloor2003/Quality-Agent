# Quickstart: Registrar pago y ver distribución

**Feature**: US-01 - Registrar pago y ver distribución
**Last Updated**: 2026-07-04

## Prerequisites

- Java 17 installed
- Maven 3.8+ installed
- PostgreSQL 14+ (or H2 for development)
- IDE con soporte para Spring Boot

## Setup

### 1. Build the Project

```bash
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

### 3. Verify Health

```bash
curl http://localhost:8080/actuator/health
```

## Validation Scenarios

### Scenario 1: Registrar pago parcial a cuota vencida

**Objective**: Verificar que el pago se aplica primero a cuota vencida

**Setup**:
1. Crear un préstamo con cuotas vencidas (fecha < hoy)
2. Obtener el ID del préstamo

**Test**:

```bash
# Request
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "prestamoId": 1,
    "montoPago": 500.00,
    "operadorId": 1
  }'

# Expected Response
# - La cuota vencida más antigua recibe el pago parcial
# - Estado de cuota: PARCIALMENTE_PAGADA
# - Estado de pago: REGISTRADO
```

**Verification**:
```bash
# Obtener distribución
curl http://localhost:8080/api/pagos/{pagoId}/distribucion
```

---

### Scenario 2: Registrar pago que cubre cuota exacta

**Objective**: Verificar que cuota se marca como pagada

**Setup**:
1. Préstamo con cuota pendiente
2. Obtener valor exacto de la cuota

**Test**:

```bash
# Request - monto igual al valor de la cuota
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "prestamoId": 1,
    "montoPago": 1000.00,
    "operadorId": 1
  }'
```

**Verification**:
- Cuota estado: PAGADA
- fechaPago registrado
- Saldo del préstamo reducido

---

### Scenario 3: Registrar pago mayor al total de cuotas

**Objective**: Verificar creación de saldo a favor

**Setup**:
1. Préstamo con cuota(s) pendiente(s)
2. Monto de pago > total pendiente

**Test**:

```bash
# Request - monto mayor al total
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "prestamoId": 1,
    "montoPago": 5000.00,
    "operadorId": 1
  }'
```

**Verification**:
- Todas las cuotas pagadas
- SaldoAFavor creado con monto excedente
- Distribución muestra origen: "EXCEDENTE_PAGO"

---

### Scenario 4: Verificar distribución en misma pantalla

**Objective**: Confirmar que la respuesta incluye todo el desglose

**Test**:

```bash
# La respuesta de /pagos incluye:
# - Lista de cuotas afectadas
# - Monto aplicado a cada cuota
# - Saldo a favor (si aplica)
# - Estados anterior y nuevo
```

**Expected Response Structure**:
```json
{
  "pagoId": 1,
  "prestamoId": 1,
  "montoTotal": 1500.00,
  "distribuciones": [
    {
      "cuotaId": 5,
      "numeroCuota": 1,
      "estadoAnterior": "VENCIDA",
      "estadoNuevo": "PAGADA",
      "montoAplicado": 1000.00,
      "saldoRestante": 0
    },
    {
      "cuotaId": 6,
      "numeroCuota": 2,
      "estadoAnterior": "PENDIENTE",
      "estadoNuevo": "PENDIENTE",
      "montoAplicado": 500.00,
      "saldoRestante": 500.00
    }
  ],
  "saldoAFavor": null
}
```

---

## Running Tests

### Unit Tests

```bash
mvn test -Dtest=*UnitTest
```

### Integration Tests

```bash
mvn test -Dtest=*IntegrationTest
```

### BDD Functional Tests

```bash
mvn test -Dtest=*FunctionalTest
```

### All Tests with Coverage

```bash
mvn test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

---

## API Reference

See `contracts/pagos-api.yaml` for complete API specification.

### Endpoints Summary

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/pagos | Registrar pago |
| GET | /api/pagos/{id}/distribucion | Ver distribución |
| GET | /api/prestamos/{id}/cuotas | Listar cuotas |

---

## Troubleshooting

### Error: Préstamo no encontrado
- Verificar que el préstamo existe en la BD
- Verificar que el estado es ACTIVO

### Error: Monto inválido
- El monto debe ser mayor a 0
- El monto no puede exceder el saldo total (sin saldo a favor)

### Error: Cobertura de tests insuficiente
- Ejecutar `mvn jacoco:report` para ver cobertura
- Asegurar >80% global y >80% por clase