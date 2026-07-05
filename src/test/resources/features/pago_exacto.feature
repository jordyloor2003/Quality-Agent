# language: es
Funcionalidad: Registrar pago exacto

  Como operador de pagos
  Quiero que cuando el pago sea exacto, la cuota se marque como PAGADA
  Para indicar que la obligación está completamente saldada

  Escenario: Pago exacto a una cuota pendiente
    Dado que existe un préstamo "PR-002" con estado "ACTIVO"
    Y el préstamo tiene una cuota pendiente número "1" con valor "500.00"
    Cuando registro un pago de "500.00" para el préstamo "PR-002"
    Entonces la cuota número "1" debe tener estado "PAGADA"
    Y el saldo a favor debe ser "0.00"

  Escenario: Pago exacto a múltiples cuotas
    Dado que existe un préstamo "PR-002" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas:
      | numero | valor   | estado    |
      | 1      | 500.00  | PENDIENTE |
      | 2      | 500.00  | PENDIENTE |
    Cuando registro un pago de "1000.00" para el préstamo "PR-002"
    Entonces la cuota número "1" debe tener estado "PAGADA"
    Y la cuota número "2" debe tener estado "PAGADA"