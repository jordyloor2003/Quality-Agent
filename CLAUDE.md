<!-- SPECKIT START -->
# FondoCesantia Service - Contexto del Proyecto

## Feature Actual

**US-01**: Registrar pago y ver distribución en la misma pantalla

**Plan**: `specs/001-registrar-pago-distribucion/plan.md`

## Tecnologías

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.x
- **Arquitectura**: Clean Architecture (Robert Martin)
- **Testing**: JUnit 5, Cucumber (BDD), Mockito
- **API**: OpenAPI 3.0 + openapi-generator
- **Cobertura**: JaCoCo (target >80% global, >80% por clase)

## Estructura del Proyecto

```
src/
├── domain/          # Entidades y reglas de negocio
├── application/     # Casos de uso
├── infrastructure/ # Repositorios, adaptadores
└── interface/       # Controladores REST
```

## Próximo Paso

Ejecutar `/speckit-tasks` para generar las tareas de implementación
<!-- SPECKIT END -->