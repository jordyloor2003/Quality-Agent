package com.fondocesantia.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una cuota de un préstamo.
 */
@Entity
@Table(name = "cuota")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "valor_cuota", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorCuota;

    @Column(name = "valor_capital", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorCapital;

    @Column(name = "valor_interes", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorInteres;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "saldo_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CuotaEstado estado;

    @OneToMany(mappedBy = "cuota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DistribucionPago> distribuciones = new ArrayList<>();

    public Cuota() {
    }

    public Cuota(Integer numeroCuota, BigDecimal valorCuota, BigDecimal valorCapital,
                BigDecimal valorInteres, LocalDate fechaVencimiento) {
        this.numeroCuota = numeroCuota;
        this.valorCuota = valorCuota;
        this.valorCapital = valorCapital;
        this.valorInteres = valorInteres;
        this.fechaVencimiento = fechaVencimiento;
        this.saldoPagado = BigDecimal.ZERO;
        this.estado = CuotaEstado.PENDIENTE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
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

    public BigDecimal getValorCapital() {
        return valorCapital;
    }

    public void setValorCapital(BigDecimal valorCapital) {
        this.valorCapital = valorCapital;
    }

    public BigDecimal getValorInteres() {
        return valorInteres;
    }

    public void setValorInteres(BigDecimal valorInteres) {
        this.valorInteres = valorInteres;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getSaldoPagado() {
        return saldoPagado;
    }

    public void setSaldoPagado(BigDecimal saldoPagado) {
        this.saldoPagado = saldoPagado;
    }

    public CuotaEstado getEstado() {
        return estado;
    }

    public void setEstado(CuotaEstado estado) {
        this.estado = estado;
    }

    public List<DistribucionPago> getDistribuciones() {
        return distribuciones;
    }

    public void setDistribuciones(List<DistribucionPago> distribuciones) {
        this.distribuciones = distribuciones;
    }

    /**
     * Actualiza el saldo pagado y el estado de la cuota según el monto aplicado.
     */
    public void aplicarPago(BigDecimal montoAplicado) {
        this.saldoPagado = this.saldoPagado.add(montoAplicado);
        this.fechaPago = LocalDateTime.now();

        if (this.saldoPagado.compareTo(this.valorCuota) >= 0) {
            this.estado = CuotaEstado.PAGADA;
        } else if (this.saldoPagado.compareTo(BigDecimal.ZERO) > 0) {
            this.estado = CuotaEstado.PARCIALMENTE_PAGADA;
        }
    }

    /**
     * Enum que representa los estados posibles de una cuota.
     */
    public enum CuotaEstado {
        VENCIDA,
        PENDIENTE,
        PAGADA,
        PARCIALMENTE_PAGADA
    }
}