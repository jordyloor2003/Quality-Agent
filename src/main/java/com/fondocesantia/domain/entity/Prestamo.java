package com.fondocesantia.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un préstamo del Fondo de Cesantía.
 */
@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_prestamo", unique = true, nullable = false, length = 20)
    private String numeroPrestamo;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "monto_original", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "saldo_actual", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoActual;

    @Column(name = "tasa_interes", nullable = false, precision = 5, scale = 4)
    private BigDecimal tasaInteres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrestamoEstado estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("numeroCuota ASC")
    private List<Cuota> cuotas = new ArrayList<>();

    public Prestamo() {
    }

    public Prestamo(String numeroPrestamo, Long clienteId, BigDecimal montoOriginal,
                    BigDecimal tasaInteres, PrestamoEstado estado) {
        this.numeroPrestamo = numeroPrestamo;
        this.clienteId = clienteId;
        this.montoOriginal = montoOriginal;
        this.saldoActual = montoOriginal;
        this.tasaInteres = tasaInteres;
        this.estado = estado;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPrestamo() {
        return numeroPrestamo;
    }

    public void setNumeroPrestamo(String numeroPrestamo) {
        this.numeroPrestamo = numeroPrestamo;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getMontoOriginal() {
        return montoOriginal;
    }

    public void setMontoOriginal(BigDecimal montoOriginal) {
        this.montoOriginal = montoOriginal;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public BigDecimal getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(BigDecimal tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public PrestamoEstado getEstado() {
        return estado;
    }

    public void setEstado(PrestamoEstado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<Cuota> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<Cuota> cuotas) {
        this.cuotas = cuotas;
    }

    public void addCuota(Cuota cuota) {
        cuotas.add(cuota);
        cuota.setPrestamo(this);
    }

    /**
     * Enum que representa los estados posibles de un préstamo.
     */
    public enum PrestamoEstado {
        ACTIVO,
        INACTIVO,
        MOROSO
    }
}