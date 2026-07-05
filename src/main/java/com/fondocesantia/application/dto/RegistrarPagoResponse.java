package com.fondocesantia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la respuesta del registro de un pago.
 */
public class RegistrarPagoResponse {

    private Long pagoId;
    private String numeroPrestamo;
    private BigDecimal montoPago;
    private LocalDateTime fechaPago;
    private String estado;
    private List<DistribucionDetalle> distribuciones;
    private BigDecimal saldoAFavor;

    public RegistrarPagoResponse() {
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public String getNumeroPrestamo() {
        return numeroPrestamo;
    }

    public void setNumeroPrestamo(String numeroPrestamo) {
        this.numeroPrestamo = numeroPrestamo;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DistribucionDetalle> getDistribuciones() {
        return distribuciones;
    }

    public void setDistribuciones(List<DistribucionDetalle> distribuciones) {
        this.distribuciones = distribuciones;
    }

    public BigDecimal getSaldoAFavor() {
        return saldoAFavor;
    }

    public void setSaldoAFavor(BigDecimal saldoAFavor) {
        this.saldoAFavor = saldoAFavor;
    }

    /**
     * DTO para el detalle de cada distribución.
     */
    public static class DistribucionDetalle {
        private Integer numeroCuota;
        private BigDecimal valorCuota;
        private BigDecimal montoAplicado;
        private String estadoAnterior;
        private String estadoNuevo;

        public DistribucionDetalle() {
        }

        public Integer getNumeroCuota() {
            return numeroCuota;
        }

        public void setNumeroCuota(Integer numeroCuota) {
            this.numeroCuota = numeroCuota;
        }

        public BigDecimal getValorCuota() {
            return valorCuota;
        }

        public void setValorCuota(BigDecimal valorCuota) {
            this.valorCuota = valorCuota;
        }

        public BigDecimal getMontoAplicado() {
            return montoAplicado;
        }

        public void setMontoAplicado(BigDecimal montoAplicado) {
            this.montoAplicado = montoAplicado;
        }

        public String getEstadoAnterior() {
            return estadoAnterior;
        }

        public void setEstadoAnterior(String estadoAnterior) {
            this.estadoAnterior = estadoAnterior;
        }

        public String getEstadoNuevo() {
            return estadoNuevo;
        }

        public void setEstadoNuevo(String estadoNuevo) {
            this.estadoNuevo = estadoNuevo;
        }
    }
}