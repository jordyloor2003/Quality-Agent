# language: es
Funcionalidad: Aplicar pago a cuotas vencidas primero

  Como operador de pagos
 Quiero que el sistema aplique los pagos primero a las cuotas vencidas
  Para asegurar que los intereses moratorios no se acumulen

  Antecedentes:
    Dado que existe un préstamo "PR-001" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas:
      | numero | valor   | estado    | fecha_vencimiento |
      | 1      | 1000.00 | VENCIDA   | 2025-02-15        |
      | 2      | 1000.00 | VENCIDA   | 2025-03-15        |
      | 3      | 1000.00 | PENDIENTE | 2025-04-15        |
      | 4      | 1000.00 | PENDIENTE | 2025-05-15        |

  Escenario: Registrar pago y verificar distribución a cuotas vencidas primero
    Cuando registro un pago de "500.00" para el préstamo "PR-001"
    Entonces el sistema debe aplicar el pago a la cuota número "1"
    Y la cuota número "1" debe tener estado "PARCIALMENTE_PAGADA"
    Y la cuota número "3" debe mantener estado "PENDIENTE"

  Escenario: Registrar pago mayor a cuota vencida
    Cuando registro un pago de "1200.00" para el préstamo "PR-001"
    Entonces el sistema debe aplicar "1000.00" a la cuota número "1"
    Y aplicar "200.00" a la cuota número "2"
    Y la cuota número "1" debe tener estado "PAGADA"
    Y la cuota número "2" debe tener estado "PARCIALMENTE_PAGADA"

  Escenario: Registrar pago que cubre todas las cuotas vencidas
    Cuando registro un pago de "2500.00" para el préstamo "PR-001"
    Entonces el sistema debe aplicar "1000.00" a la cuota número "1"
    Y aplicar "1000.00" a la cuota número "2"
    Y aplicar "500.00" a la cuota número "3"
    Y debe sobrar "0.00" para siguientes cuotas