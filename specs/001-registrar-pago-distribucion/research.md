# Research: Registrar pago y ver distribución

**Date**: 2026-07-04
**Feature**: US-01 - Registrar pago y ver distribución

## Technology Decisions

### 1. Language & Version

**Decision**: Java 17

**Rationale**: 
- Long Term Support (LTS) version con soporte hasta 2029
- Compatibilidad completa con Spring Boot 3.x
- Mejor rendimiento comparado con Java 11
- Amplio ecosistema de librerías

**Alternatives Considered**:
- Java 21: Más reciente pero menor adopción en proyectos financieros
- Kotlin: Funcional pero requiere curva de aprendizaje adicional

---

### 2. Framework

**Decision**: Spring Boot 3.x

**Rationale**:
- Ecosystem maduro para desarrollo de APIs REST
- Soporte nativo para JPA/Hibernate
- Integración con Spring Security
- Comunidad activa y documentación extensa

**Alternatives Considered**:
- Quarkus: Más liviano y rápido pero menor经验的 equipo
- Micronaut: Similar a Quarkus, buena opción pero Spring tiene más adopción

---

### 3. Database

**Decision**: PostgreSQL

**Rationale**:
- Robustez para datos financieros
- Soporte completo de transacciones ACID
- Tipos de datos especializados para monetaria
- Excelente rendimiento y escalabilidad

**Alternatives Considered**:
- MySQL: Menor costo pero menos features transaccionales
- H2: Solo para desarrollo/pruebas

---

### 4. Testing Framework

**Decision**: JUnit 5 + Cucumber

**Rationale**:
- JUnit 5: Estándar para testing en Java
- Cucumber: Soporte nativo para Gherkin (BDD)
- Mockito: Mocking para tests unitarios
- Consistencia con requisitos de constitución

**Alternatives Considered**:
- JBehave: Alternativa BDD pero menos mantenido
- TestNG: Similar a JUnit pero menor adopción

---

### 5. API Specification

**Decision**: OpenAPI 3.0 + openapi-generator

**Rationale**:
- Cumplimiento con principio API First de la constitución
- Generación automática de stubs y documentación
- Estandar industria para REST APIs
- Swagger UI para documentación interactiva

---

### 6. Clean Architecture Implementation

**Decision**: Robert Martin's Clean Architecture

**Rationale**:
- Requisito de constitución
- Separación clara de responsabilidades
- Testabilidad de reglas de negocio
- Independencia de frameworks

**Capas**:
1. **Domain**: Entidades y reglas de negocio (Préstamo, Cuota, Pago, DistribuciónPago, SaldoAFavor)
2. **Application**: Casos de uso (RegistrarPagoUseCase, CalcularDistribucionUseCase)
3. **Infrastructure**: Implementaciones de repositorios, adaptadores externos
4. **Interface**: Controladores REST, DTOs

---

## Distribution Algorithm Research

### Payment Distribution Logic

**Algorithm**: First-In-First-Out (FIFO) por fecha de vencimiento

**Rules**:
1. Aplicar primero a cuotas vencidas (desde la más antigua)
2. Luego a cuotas pendientes (desde la más antigua)
3. Sobrante → saldo a favor

**States**:
- **VENCIDA**: Fecha actual > fecha límite
- **PENDIENTE**: Fecha actual <= fecha límite
- **PAGADA**: Monto aplicado >= valor cuota
- **PARCIALMENTE_PAGADA**: 0 < Monto aplicado < valor cuota

---

## References

- Clean Architecture: Robert C. Martin
- OpenAPI 3.0 Specification: https://spec.openapis.org/oas/v3.0.3
- Spring Boot Reference: https://docs.spring.io/spring-boot/docs/current/reference/
- Cucumber Documentation: https://cucumber.io/docs/