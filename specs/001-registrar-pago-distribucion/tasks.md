---

description: "Task list for US-01: Registrar pago y ver distribución"
---

# Tasks: Registrar pago y ver distribución

**Input**: Design documents from `specs/001-registrar-pago-distribucion/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), data-model.md, contracts/pagos-api.yaml

**Tests**: BDD tests using Cucumber with Gherkin (per Constitution requirements)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Java/Spring Boot project**: Standard Maven/Gradle structure
- Paths assume Clean Architecture layers under `src/main/java/com/fondocesantia/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create Maven project structure with pom.xml in root directory
- [X] T002 [P] Configure pom.xml with Spring Boot 3.x, Spring Data JPA, H2, PostgreSQL dependencies
- [X] T003 [P] Add OpenAPI generator plugin to pom.xml
- [ ] T003B [P] Review existing contract contracts/pagos-api.yaml for accuracy
- [X] T004 [P] Configure JaCoCo plugin in pom.xml for coverage reporting
- [X] T005 [P] Add Cucumber dependencies for BDD testing in pom.xml
- [X] T006 Configure application.properties with database settings
- [X] T007 Create main Application class in src/main/java/com/fondocesantia/FondoCesantiaApplication.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T008 [P] Create database schema script in src/main/resources/schema.sql (DDL)
- [X] T008B [P] Create seed data script in src/main/resources/data.sql (DML)
- [X] T008C [P] Configure application.yml to load schema.sql and data.sql on startup
- [X] T009 [P] Setup JPA entity mappings in src/main/java/com/fondocesantia/domain/entity/
- [X] T010 [P] Create repository interfaces in src/main/java/com/fondocesantia/domain/repository/
- [X] T011 [P] Configure application.yml with test profiles
- [X] T012 Setup logging infrastructure with SLF4J
- [X] T013 Create global exception handler in src/main/java/com/fondocesantia/api/config/GlobalExceptionHandler.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Aplicar pago a cuotas vencidas primero (Priority: P1) 🎯 MVP

**Goal**: Registrar un pago y verificar que se aplica primero a cuotas vencidas, luego a pendientes

**Independent Test**: Crear préstamo con cuotas vencidas y pendientes, registrar pago, verificar orden de aplicación

### Tests for User Story 1 (BDD - Required per Constitution)

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T014 [P] [US1] Create BDD feature file tests/functional/pago_cuota_vencida.feature with Gherkin scenarios
- [ ] T015 [P] [US1] Implement step definitions in tests/functional/stepdefinitions/PagoCuotaVencidaSteps.java
- [ ] T016 [US1] Create unit test for distribution algorithm in tests/unit/DistribucionPagoServiceTest.java

### Implementation for User Story 1

- [X] T017 [P] [US1] Create Prestamo entity in src/main/java/com/fondocesantia/domain/entity/Prestamo.java
- [X] T018 [P] [US1] Create Cuota entity in src/main/java/com/fondocesantia/domain/entity/Cuota.java
- [X] T019 [P] [US1] Create CuotaRepository in src/main/java/com/fondocesantia/domain/repository/CuotaRepository.java
- [X] T020 [P] [US1] Create PrestamoRepository in src/main/java/com/fondocesantia/domain/repository/PrestamoRepository.java
- [X] T021 [US1] Implement DistribucionPagoService in src/main/java/com/fondocesantia/domain/service/DistribucionPagoService.java
- [X] T022 [US1] Implement RegistrarPagoUseCase in src/main/java/com/fondocesantia/application/usecase/RegistrarPagoUseCase.java
- [X] T023 [US1] Create PagoController in src/main/java/com/fondocesantia/api/controller/PagoController.java
- [X] T024 [US1] Add validation for loan state (must be ACTIVO) in RegistrarPagoUseCase
- [X] T025 [US1] Add logging for payment registration operations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Registrar pago exacto (Priority: P1)

**Goal**: Cuando el pago es exacto, la cuota se marca como PAGADA

**Independent Test**: Registrar pago igual al valor de una cuota pendiente, verificar estado PAGADA

### Tests for User Story 2 (BDD - Required)

- [ ] T026 [P] [US2] Create BDD feature file tests/functional/pago_exacto.feature
- [ ] T027 [P] [US2] Implement step definitions in tests/functional/stepdefinitions/PagoExactoSteps.java

### Implementation for User Story 2

- [X] T028 [P] [US2] Create Pago entity in src/main/java/com/fondocesantia/domain/entity/Pago.java
- [X] T029 [P] [US2] Create PagoRepository in src/main/java/com/fondocesantia/domain/repository/PagoRepository.java
- [X] T030 [US2] Update DistribucionPagoService to handle exact payment (saldoPagado >= valorCuota)
- [X] T031 [US2] Add state transition logic (PENDIENTE → PAGADA) in Cuota entity

**Checkpoint**: User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Registrar pago parcial (Priority: P1)

**Goal**: Cuando el pago es menor, la cuota queda parcialmente pagada

**Independent Test**: Registrar pago menor al valor de cuota, verificar estado PARCIALMENTE_PAGADA

### Tests for User Story 3 (BDD - Required)

- [ ] T032 [P] [US3] Create BDD feature file tests/functional/pago_parcial.feature
- [ ] T033 [P] [US3] Implement step definitions in tests/functional/stepdefinitions/PagoParcialSteps.java

### Implementation for User Story 3

- [X] T034 [US3] Update DistribucionPagoService to handle partial payment (0 < saldoPagado < valorCuota)
- [X] T035 [US3] Add state transition logic (PENDIENTE → PARCIALMENTE_PAGADA) in Cuota entity

---

## Phase 6: User Story 4 - Aplicar excedente y saldo a favor (Priority: P1)

**Goal**: El excedente del pago se aplica a siguiente cuota o crea saldo a favor

**Independent Test**: Registrar pago mayor al total de cuotas, verificar saldo a favor creado

### Tests for User Story 4 (BDD - Required)

- [ ] T036 [P] [US4] Create BDD feature file tests/functional/pago_excedente.feature
- [ ] T037 [P] [US4] Implement step definitions in tests/functional/stepdefinitions/PagoExcedenteSteps.java

### Implementation for User Story 4

- [X] T038 [P] [US4] Create SaldoAFavor entity in src/main/java/com/fondocesantia/domain/entity/SaldoAFavor.java
- [X] T039 [P] [US4] Create SaldoAFavorRepository in src/main/java/com/fondocesantia/domain/repository/SaldoAFavorRepository.java
- [X] T040 [US4] Implement logic to create SaldoAFavor when payment exceeds total cuotas
- [X] T041 [US4] Implement DistribucionPago entity for payment distribution tracking

**Checkpoint**: All user stories should now be independently functional

---

## Phase 7: User Story 5 - Ver desglose en misma pantalla (Priority: P1)

**Goal**: La respuesta del registro de pago incluye toda la información de distribución

**Independent Test**: Registrar cualquier pago, verificar que la respuesta incluye desglose completo

### Tests for User Story 5 (BDD - Required)

- [ ] T042 [P] [US5] Create BDD feature file tests/functional/desglose_pantalla.feature
- [ ] T043 [P] [US5] Implement step definitions in tests/functional/stepdefinitions/DesglosePantallaSteps.java

### Implementation for User Story 5

- [ ] T044 [P] [US5] Create DTOs in src/main/java/com/fondocesantia/application/dto/
- [ ] T045 [P] [US5] Create response DTOs in src/main/java/com/fondocesantia/interface/dto/
- [ ] T046 [US5] Implement GET /api/pagos/{id}/distribucion endpoint in PagoController
- [ ] T047 [US5] Add response mapping for DistribucionResponse with complete breakdown
- [ ] T048 [US5] Add integration tests in tests/integration/PagoIntegrationTest.java

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T049 [P] Generate API stubs using openapi-generator from contracts/pagos-api.yaml
- [ ] T050 Run JaCoCo coverage report and verify >80% global, >80% per class
- [ ] T051 [P] Update API documentation with Swagger UI
- [ ] T052 Run all BDD tests and verify they pass
- [ ] T053 Polish error messages and validation feedback
- [ ] T054 Run quickstart.md validation scenarios

### Success Criteria Verification

- [ ] T055 Verify SC-001: Operator completes payment registration in less than 2 minutes
- [ ] T056 Verify SC-002: Payment distribution order is correct in 95% of test cases
- [ ] T057 Verify SC-003: Single-view desglose displays all required information (cuotas afectadas, montos, saldo a favor)
- [ ] T058 Verify SC-004: No navigation or page reload required to view payment results

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P1 → P1 → P1 → P1)
- **Polish (Phase 8)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - Builds on US1 entities
- **User Story 3 (P1)**: Can start after Foundational (Phase 2) - Builds on US1 entities
- **User Story 4 (P1)**: Can start after Foundational (Phase 2) - Builds on US1-US3
- **User Story 5 (P1)**: Depends on all previous stories for complete response

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Entities before repositories
- Repositories before services
- Services before use cases
- Use cases before controllers
- Story complete before moving to next

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- All User Story entities marked [P] can run in parallel within that story
- All User Story tests marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Create BDD feature file tests/functional/pago_cuota_vencida.feature"
Task: "Create unit test for distribution algorithm tests/unit/DistribucionPagoServiceTest.java"

# Launch all entities for User Story 1 together:
Task: "Create Prestamo entity in src/main/java/com/fondocesantia/domain/entity/Prestamo.java"
Task: "Create Cuota entity in src/main/java/com/fondocesantia/domain/entity/Cuota.java"

# Launch all repositories for User Story 1 together:
Task: "Create CuotaRepository in src/main/java/com/fondocesantia/domain/repository/CuotaRepository.java"
Task: "Create PrestamoRepository in src/main/java/com/fondocesantia/domain/repository/PrestamoRepository.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Add User Story 5 → Test independently → Deploy/Demo
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 + 2 (shared entities)
   - Developer B: User Story 3 + 4 (payment logic)
   - Developer C: User Story 5 (response formatting)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- **Coverage Requirement**: Per Constitution, maintain >80% global and >80% per class using JaCoCo