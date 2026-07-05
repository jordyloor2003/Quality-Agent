# language: es
Funcionalidad: Ver desglose en misma pantalla

  Como operador de pagos
  Quiero ver toda la información de distribución del pago en la misma pantalla
  Para no tener que navegar para ver los detalles

  Escenario: Respuesta incluye completo desglose de distribución
    Dado que existe un préstamo "PR-001" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas:
      | numero | valor   | estado    |
      | 1      | 1000.00 | VENCIDA   |
      | 2      | 1000.00 | PENDIENTE |
    Cuando registro un pago de "1500.00" para el préstamo "PR-001"
    Entonces la respuesta debe incluir el ID del pago
    Y la respuesta debe incluir el número de préstamo
    Y la respuesta debe incluir el monto del pago
    Y la respuesta debe incluir la lista de distribuciones
    Y la respuesta debe incluir el saldo a favor

  Escenario: Distribución muestra todas las cuotas afectadas
    Dado que existe un préstamo "PR-001" con estado "ACTIVO"
    Y el préstamo tiene las siguientes cuotas:
      | numero | valor   | estado    |
      | 1      | 1000.00 | VENCIDA   |
      | 2      | 1000.00 | VENCIDA   |
    Cuando registro un pago de "1800.00" para el préstamo "PR-001"
    Entonces la respuesta debe mostrar "2" distribuciones
    Y cada distribución debe incluir el número de cuota
    Y cada distribución debe incluir el valor de la cuota
    Y cada distribución debe incluir el monto aplicado
    Y cada distribución debe incluir el nuevo estado