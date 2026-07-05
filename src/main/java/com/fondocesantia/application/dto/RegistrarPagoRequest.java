package com.fondocesantia.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para solicitar el registro de un pago.
 */
public class RegistrarPagoRequest {

    @NotNull(message = "El ID del préstamo es requerido")
    private Long prestamoId;

    @NotNull(message = "El monto del pago es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoPago;

    @NotNull(message = "El ID del operador es requerido")
    private Long operadorId;

    private String observacion;

    public RegistrarPagoRequest() {
    }

    public RegistrarPagoRequest(Long prestamoId, BigDecimal montoPago, Long operadorId, String observacion) {
        this.prestamoId = prestamoId;
        this.montoPago = montoPago;
        this.operadorId = operadorId;
        this.observacion = observacion;
    }

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public Long getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Long operadorId) {
        this.operadorId = operadorId;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}