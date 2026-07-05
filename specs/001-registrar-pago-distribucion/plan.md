# Implementation Plan: Registrar pago y ver distribución

**Branch**: `001-registrar-pago-distribucion` | **Date**: 2026-07-04 | **Spec**: specs/001-registrar-pago-distribucion/spec.md

**Input**: Feature specification from `specs/001-registrar-pago-distribucion/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

El sistema debe permitir a un operador de pagos registrar un pago contra un préstamo vigente y visualizar en tiempo real el desglose de la distribución del dinero entre cuotas (vencidas, pendientes, parcialmente pagadas) y saldo a favor, sin necesidad de recargar la pantalla.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot 3.x, Spring Data JPA, H2 (desarrollo), PostgreSQL (producción)

**Storage**: PostgreSQL con JPA/Hibernate + Datos de prueba en resources

**Testing**: JUnit 5, Cucumber para BDD, Mockito

**Target Platform**: Linux server (API REST)

**Project Type**: Web service (API REST)

**Performance Goals**: Tiempo de respuesta < 500ms para registro de pagos

**Constraints**: Compatible con arquitectura limpia (Clean Architecture)

**Scale/Scope**: Sistema de gestión de préstamos para Fondo de Cesantía

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gates from Constitution

1. **Clean Architecture**: 
   - ✅ Domain Layer: Entidades Préstamo, Cuota, Pago, DistribuciónPago, SaldoAFavor
   - ✅ Application Layer: Casos de uso para registro y distribución de pagos
   - ✅ Infrastructure Layer: Repositorios, servicios externos
   - ✅ Interface Layer: Controladores REST

2. **API First**:
   - ✅ Contrato OpenAPI 3.0 requerido para endpoints
   - ✅ openapi-generator para generación de stubs

3. **BDD Testing**:
   - ✅ Tests con Gherkin (Cucumber)
   - ✅ Unit, Integration, Functional tests

4. **Coverage**:
   - ✅ JaCoCo configurado
   - ✅ Target: >80% global, >80% por clase

## Project Structure

### Documentation (this feature)

```text
specs/001-registrar-pago-distribucion/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── pagos-api.yaml   # OpenAPI contract
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
# Java/Spring Boot project with Clean Architecture
src/
├── main/java/com/fondocesantia/
│   ├── domain/              # Domain Layer (business rules)
│   │   ├── entity/          # Préstamo, Cuota, Pago, etc.
│   │   ├── repository/     # Repository interfaces
│   │   └── service/        # Domain services
│   ├── application/        # Application Layer (use cases)
│   │   ├── usecase/        # RegistrarPagoUseCase, etc.
│   │   ├── dto/            # Data Transfer Objects
│   │   └── port/           # Port interfaces
│   ├── infrastructure/     # Infrastructure Layer
│   │   ├── repository/    # JPA implementations
│   │   └── adapter/        # External adapters
│   └── interface/          # Interface Layer
│       ├── controller/     # REST controllers
│       ├── dto/            # API DTOs
│       └── config/         # Configuration
└── main/resources/
    ├── application.yml      # Application configuration
    ├── schema.sql          # Database schema (DDL)
    ├── data.sql            # Seed data (DML)
    └── import.sql          # Combined schema + data
```

**Structure Decision**: Java/Spring Boot project con arquitectura limpia de Robert Martin

### Database Setup in Resources

**Archivos en `src/main/resources/`**:

1. **schema.sql** - DDL (Data Definition Language)
   - CREATE TABLE para entidades
   - CREATE INDEX para búsquedas eficiente
   - Constraints y foreign keys

2. **data.sql** - DML (Data Manipulation Language)
   - Datos de prueba pre-cargados
   - Préstamos de ejemplo con cuotas
   - Estados diversos para testing

3. **import.sql** (opcional) - Combina schema + data
   - Útil para H2 en modoembebido

**Configuración en application.yml**:
```yaml
spring:
  sql:
    init:
      mode: always  # Ejecuta scripts al iniciar
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
```

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

## Research Findings (Phase 0)

*See research.md for detailed findings*

### Key Decisions

| Decision | Rationale | Alternatives Considered |
|----------|-----------|------------------------|
| Java 17 | LTS, compatibilidad con Spring Boot 3 | Java 21 (más reciente pero menor adopción) |
| Spring Boot 3.x | Ecosystem maduro, soporte REST | Quarkus (más liviano pero menor经验的团队) |
| PostgreSQL | BD robusta para financiero | MySQL (menos features transaccionales) |
| Cucumber BDD | Soporte Gherkin nativo | JBehave (menos mantenido) |
| H2 + SQL scripts | Desarrollo rápido, testing reproducible | Flyway (sobrecarga para proyectos pequeños) |

*See research.md for detailed analysis*

## Data Model (Phase 1)

*See data-model.md for entity definitions*

### Key Entities

- **Préstamo**: Entidad principal con estado y cuotas asociadas
- **Cuota**: Valores, fechas, estados (vencida, pendiente, pagada, parcialmente pagada)
- **Pago**: Monto, fecha, distribución
- **DistribuciónPago**: Relación pago-cuota con montos
- **SaldoAFavor**: Excedente acumulado

## Contracts (Phase 1)

*See contracts/pagos-api.yaml for API definitions*

### Endpoints

- `POST /api/pagos` - Registrar pago
- `GET /api/pagos/{id}/distribucion` - Ver distribución

## Quickstart (Phase 1)

*See quickstart.md for validation guide*

---

**Plan Status**: Phase 1 Complete | **Last Updated**: 2026-07-04