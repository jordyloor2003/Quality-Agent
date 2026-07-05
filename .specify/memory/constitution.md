# FondoCesantia Service Constitution

## Core Principles

### I. Clean Architecture (NON-NEGOTIABLE)

All code MUST follow Robert Martin's Clean Architecture principles:

- **Domain Layer**: Business rules and entities - no external dependencies
- **Application Layer**: Use cases and application services - orchestrate domain
- **Infrastructure Layer**: External concerns (DB, APIs, frameworks)
- **Interface Layer**: Controllers, presenters, and API adapters

**Rules**:
- Dependencies MUST point inward - outer layers depend on inner, never vice versa
- Business rules MUST be testable in isolation without frameworks or databases
- Frameworks and tools are plugins, not the core of the application
- Each layer MUST be independently testable

**Rationale**: Ensures maintainability, testability, and independence from specific technologies.

### II. BDD Testing (NON-NEGOTIABLE)

All tests MUST use Behavior-Driven Development with the Gherkin syntax:

- **Unit Tests**: Test individual components in isolation using Given-When-Then
- **Integration Tests**: Test interactions between components with real dependencies
- **Functional Tests**: End-to-end tests verifying complete user journeys

**Rules**:
- All test files MUST use `.feature` files with Gherkin syntax
- Tests MUST be written BEFORE implementation (Test-First)
- Test descriptions MUST clearly state the behavior being verified
- Each user story MUST have corresponding scenario tests

**Rationale**: BDD ensures requirements are clearly documented and verifiable.

### III. SOLID, YAGNI, DRY Principles (NON-NEGOTIABLE)

All code MUST adhere to these programming principles:

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable for base types
- **I**nterface Segregation: Many specific interfaces > one general interface
- **D**ependency Inversion: Depend on abstractions, not concretions

- **YAGNI**: Only implement features when actually needed
- **DRY**: Avoid duplication - extract common patterns into reusable abstractions

**Rules**:
- Code reviews MUST verify SOLID compliance
- Complex classes MUST be refactored to follow SRP
- Duplicate code MUST be extracted before PR approval

**Rationale**: These principles prevent technical debt and ensure maintainable code.

### IV. API First (NON-NEGOTIABLE)

All APIs MUST be designed using the OpenAPI specification first:

- **Contract Requirement**: Every API endpoint MUST have an OpenAPI contract in YAML/JSON
- **Generator Requirement**: Use openapi-generator to generate server stubs and client SDKs
- **Version Control**: OpenAPI contracts are version-controlled alongside code

**Rules**:
- OpenAPI contract MUST be created BEFORE any endpoint implementation
- Contract changes MUST be reviewed separately from implementation
- Generated code MUST be committed but can be regenerated
- API documentation is auto-generated from the contract

**Rationale**: API First ensures clear contracts, reduces misunderstandings, and enables automatic SDK generation.

### V. Coverage Metrics (NON-NEGOTIABLE)

Code coverage MUST meet the following thresholds using JaCoCo:

- **Per-Class Coverage**: MUST be greater than 80%
- **Global Coverage**: MUST be greater than or equal to 80%

**Rules**:
- JaCoCo reports MUST be generated for every build
- Coverage MUST be checked in CI/CD pipelines
- Classes below 80% coverage require justification in PR comments
- Critical business logic classes require 90%+ coverage

**Rationale**: High coverage ensures code quality and reduces regression bugs.

## Technology Standards

### Language & Framework

- **Language**: Java or Kotlin (JVM-based)
- **Build Tool**: Maven or Gradle
- **Framework**: Spring Boot for REST APIs
- **Testing**: JUnit 5, Cucumber for BDD, Mockito

### API Specification

- **Contract Format**: OpenAPI 3.0 YAML
- **Generator**: openapi-generator-maven-plugin
- **Documentation**: Swagger UI auto-generated

### Quality Gates

All PRs MUST pass:
- Unit tests with 80%+ class coverage
- Integration tests for all service interactions
- Functional tests for critical user journeys
- Static analysis (SonarQube or similar)
- OpenAPI contract validation

## Development Workflow

### Feature Implementation Flow

1. Create/OpenAPI contract first
2. Generate API stubs with openapi-generator
3. Write BDD tests (unit, integration, functional)
4. Implement domain layer
5. Implement application layer
6. Implement infrastructure layer
7. Implement interface layer
8. Verify coverage metrics
9. Submit PR with JaCoCo report

### Code Review Requirements

- All PRs require at least one reviewer
- Constitution compliance MUST be verified
- Coverage reports MUST be attached
- SOLID violations MUST be addressed before merge

## Governance

### Constitution Compliance

- All team members MUST follow these principles
- Violations require explicit approval with justification
- Complexity MUST be justified in PR comments
- Simpler alternatives must be considered and documented

### Amendment Procedure

1. Propose changes with rationale
2. Discuss in team review
3. Document migration plan if needed
4. Update version in this document
5. Communicate changes to all team members

### Version Policy

- **MAJOR**: Backward incompatible changes to architecture or principles
- **MINOR**: New principles or materially expanded guidance
- **PATCH**: Clarifications, wording fixes, non-semantic refinements

**Version**: 1.0.0 | **Ratified**: 2026-07-04 | **Last Amended**: 2026-07-04