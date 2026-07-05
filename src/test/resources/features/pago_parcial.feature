# language: es
Funcionalidad: Registrar pago parcial

  Como operador de pagos
  Quiero que cuando el pago sea menor al valor de la cuota, quede parcialmente pagada
  Para permitir pagos en cuotas del cliente

  Escenario: Pago menor al valor de una cuota
    Dado que existe un préstamo "PR-001" con estado "ACTIVO"
    Y el préstamo tiene una cuota pendiente número "3" con valor "1000.00"
    Cuando registro un pago de "300.00" para el préstamo "PR-001"
    Entonces la cuota número "3" debe tener estado "PARCIALMENTE_PAGADA"
    Y el saldo pagado de la cuota debe ser "300.00"

  Escenario: Pago parcial que no cubre ninguna cuota completa
    Dado que existe un préstamo "PR-001" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas:
      | numero | valor   | estado    |
      | 1      | 1000.00 | VENCIDA   |
      | 2      | 1000.00 | PENDIENTE |
    Cuando registro un pago de "800.00" para el préstamo "PR-001"
    Entonces la cuota número "1" debe tener estado "PARCIALMENTE_PAGADA"
    Y la cuota número "2" debe mantener estado "PENDIENTE"