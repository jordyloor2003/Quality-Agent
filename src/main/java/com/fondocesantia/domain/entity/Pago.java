package com.fondocesantia.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un pago realizado a un préstamo.
 */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(name = "monto_pago", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoPago;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "operador_id", nullable = false)
    private Long operadorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PagoEstado estado;

    @Column(length = 500)
    private String observacion;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DistribucionPago> distribuciones = new ArrayList<>();

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SaldoAFavor saldoAFavor;

    public Pago() {
    }

    public Pago(Prestamo prestamo, BigDecimal montoPago, Long operadorId, String observacion) {
        this.prestamo = prestamo;
        this.montoPago = montoPago;
        this.operadorId = operadorId;
        this.observacion = observacion;
        this.fechaPago = LocalDateTime.now();
        this.estado = PagoEstado.REGISTRADO;
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

    public Long getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Long operadorId) {
        this.operadorId = operadorId;
    }

    public PagoEstado getEstado() {
        return estado;
    }

    public void setEstado(PagoEstado estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<DistribucionPago> getDistribuciones() {
        return distribuciones;
    }

    public void setDistribuciones(List<DistribucionPago> distribuciones) {
        this.distribuciones = distribuciones;
    }

    public SaldoAFavor getSaldoAFavor() {
        return saldoAFavor;
    }

    public void setSaldoAFavor(SaldoAFavor saldoAFavor) {
        this.saldoAFavor = saldoAFavor;
    }

    public void addDistribucion(DistribucionPago distribucion) {
        distribuciones.add(distribucion);
        distribucion.setPago(this);
    }

    /**
     * Enum que representa los estados posibles de un pago.
     */
    public enum PagoEstado {
        REGISTRADO,
        CONFIRMADO,
        RECHAZADO
    }
}