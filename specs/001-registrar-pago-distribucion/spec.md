# Feature Specification: Registrar pago y ver distribución

**Feature Branch**: `001-registrar-pago-distribucion`

**Created**: 2026-07-04

**Status**: Draft

**Input**: User description: "Como Operador de pagos, quiero registrar un pago contra un préstamo vigente y ver de inmediato, en la misma pantalla y sin recargar la vista, el desglose exacto de la distribución del dinero (cuotas cubiertas, cuotas con saldo parcial, monto aplicado a cada cuota y saldo a favor acreditado si lo hubiera), aplicando el pago primero a las cuotas vencidas y, dentro de las pendientes, desde la más antigua a la más reciente, para validar la operación a la primera vista sin tener que revisar manualmente el historial del préstamo."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Aplicar pago a cuotas vencidas primero (Priority: P1)

**Como** Operador de pagos, **quiero** que al registrar un pago se aplique primero a las cuotas vencidas más antiguas y luego a las pendientes, **para** asegurar que el riesgo de mora se reduzca primero.

**Why this priority**: Las cuotas vencidas representan mayor riesgo financiero, por lo que deben atenderse primero.

**Independent Test**: Se puede probar creando un préstamo con cuotas vencidas y pendientes, registrando un pago parcial, y verificando que el dinero se aplica en orden correcto.

**Acceptance Scenarios**:

1. **Given** un préstamo activo con al menos una cuota vencida y una cuota por vencer, **When** registro un pago parcial, **Then** el dinero se aplica primero a la cuota vencida más antigua y, si sobra, continúa con la siguiente cuota pendiente en orden cronológico ascendente.

2. **Given** un préstamo activo con varias cuotas pendientes sin vencer, **When** registro un pago, **Then** se aplica desde la más antigua hacia la más reciente.

---

### User Story 2 - Registrar pago exacto (Priority: P1)

**Como** Operador de pagos, **quiero** que al registrar un pago por el valor exacto de una cuota, esta quede marcada como pagada, **para** reflejar correctamente el estado del préstamo.

**Why this priority**: El registro exacto es el caso más común y debe funcionar correctamente.

**Independent Test**: Registrar pago igual al valor de una cuota pendiente y verificar que se marca como pagada.

**Acceptance Scenarios**:

1. **Given** un préstamo activo con una cuota pendiente, **When** registro un pago por el valor exacto de esa cuota, **Then** esa cuota queda marcada como pagada, las demás mantienen su estado y el sistema muestra el desglose en la misma pantalla.

---

### User Story 3 - Registrar pago parcial (Priority: P1)

**Como** Operador de pagos, **quiero** que al registrar un pago menor al valor de la cuota, esta quede parcialmente pagada, **para** reflejar el saldo pendiente correctamente.

**Why this priority**: Los pagos parciales son frecuentes y deben actualizar el estado de la cuota correctamente.

**Independent Test**: Registrar pago menor al valor de una cuota y verificar el saldo pendiente.

**Acceptance Scenarios**:

1. **Given** un préstamo activo con una cuota pendiente, **When** registro un pago que no alcanza para cubrirla completa, **Then** esa cuota queda parcialmente pagada (no se marca como pagada) y se muestra el saldo pendiente de la cuota.

---

### User Story 4 - Aplicar excedente y saldo a favor (Priority: P1)

**Como** Operador de pagos, **quiero** que cuando un pago supera el valor de las cuotas, el excedente se aplique a la siguiente cuota o quede como saldo a favor, **para** evitar pérdida de dinero.

**Why this priority**: Asegura que todo el dinero del pago se aplica correctamente.

**Independent Test**: Registrar pago mayor al total de cuotas y verificar el destino del excedente.

**Acceptance Scenarios**:

1. **Given** un pago cuyo monto supera el valor de la cuota más antigua, **When** confirmo el registro, **Then** el excedente se aplica automáticamente a la siguiente cuota pendiente; si aún sobra, se acumula como saldo a favor con su monto y origen visible en el desglose.

---

### User Story 5 - Ver desglose en misma pantalla (Priority: P1)

**Como** Operador de pagos, **quiero** ver el desglose completo de la distribución del pago en la misma pantalla de confirmación, **para** validar la operación sin navegar.

**Why this priority**: Este es el objetivo principal de la historia de usuario - evitar navegación adicional.

**Independent Test**: Registrar cualquier pago y verificar que el desglose aparece en la misma vista.

**Acceptance Scenarios**:

1. **Given** que acabo de registrar un pago, **When** se muestra la confirmación, **Then** la lista de cuotas afectadas, los montos aplicados y el saldo a favor resultante aparecen en la misma vista, sin necesidad de navegar a otra pantalla ni recargar.

---

### Edge Cases

- ¿Qué sucede cuando el préstamo tiene todas las cuotas pagadas?
- ¿Qué sucede cuando el monto del pago es mayor al total pendiente incluyendo intereses?
- ¿Qué sucede cuando hay múltiples préstamos activos y se selecciona el incorrecto?
- ¿Qué sucede cuando se intenta registrar un pago para un préstamo inactivo o moroso?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir seleccionar un préstamo activo para registrar un pago
- **FR-002**: El sistema DEBE mostrar las cuotas del préstamo con su estado (vencida, pendiente, pagada)
- **FR-003**: El sistema DEBE permitir ingresar el monto del pago
- **FR-004**: El sistema DEBE aplicar el pago primero a las cuotas vencidas desde la más antigua (menor fechaVencimiento)
- **FR-005**: El sistema DEBE aplicar el excedente a cuotas pendientes desde la más antigua (menor fechaVencimiento)
- **FR-006**: El sistema DEBE crear saldo a favor cuando el pago excede el total de cuotas
- **FR-007**: El sistema DEBE marcar como pagada una cuota cuando el monto cubre su valor total
- **FR-008**: El sistema DEBE dejar parcialmente pagada una cuota cuando el monto no cubre su valor total
- **FR-009**: El sistema DEBE mostrar el desglose completo en la pantalla de confirmación sin recargar

### Key Entities *(include if feature involves data)*

- **Préstamo**: Crédito activo con cuotas definidas, tiene estado (activo, inactivo, moroso)
- **Cuota**: Pago programado del préstamo, tiene número, fecha límite, valor, estado (vencida, pendiente, pagada, parcialmente pagada)
- **Pago**: Registro monetario tiene monto, fecha, préstamo asociado, distribución de cuotas
- **DistribuciónPago**: Detalle de cómo se aplicó el pago a cada cuota (cuota, monto aplicado, saldo restante)
- **SaldoAFavor**: Monto excedente que queda a favor del cliente, tiene origen y monto

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El operador puede completar el registro de un pago en menos de 2 minutos
- **SC-002**: El sistema aplica correctamente el pago en orden (vencidas primero, luego pendientes) en el 95% de los casos probados
- **SC-003**: El desglose muestra toda la información necesaria (cuotas afectadas, montos, saldo a favor) en una sola vista
- **SC-004**: No se requiere navegación a otra pantalla ni recarga para ver el resultado del pago

## Assumptions

- El sistema tiene acceso a la información de cuotas de cada préstamo
- Las cuotas tienen fechas de vencimiento definidas
- El sistema conoce qué cuotas están vencidas vs pendientes
- El operador puede identificar el préstamo correcto para el pago
- El backend puede procesar la lógica de distribución de pagos