# language: es
Funcionalidad: Aplicar excedente y saldo a favor

  Como operador de pagos
  Quiero que el excedente del pago se maneje como saldo a favor
  Para que el cliente pueda usarlo en próximos pagos

  Escenario: Pago mayor al total de cuotas pendientes
    Dado que existe un préstamo "PR-002" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas pendientes:
      | numero | valor   |
      | 1      | 500.00  |
      | 2      | 500.00  |
    Cuando registro un pago de "1500.00" para el préstamo "PR-002"
    Entonces ambas cuotas deben estar pagadas
    Y el saldo a favor debe ser "500.00"

  Escenario: Pago que excede todas las cuotas
    Dado que existe un préstamo "PR-003" con estado "ACTIVO"
    Y el préstamo tiene cuotas pendientes por total de "5000.00"
    Cuando registro un pago de "6000.00" para el préstamo "PR-003"
    Entonces todas las cuotas deben estar pagadas
    Y el saldo a favor debe ser "1000.00"
    Y el origen del saldo a favor debe ser "EXCEDENTE_PAGO"