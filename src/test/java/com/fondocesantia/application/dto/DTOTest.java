package com.fondocesantia.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DTOs using BDD Given-When-Then pattern.
 */
@DisplayName("DTOs - Tests de Data Transfer Objects")
class DTOTest {

    @Test
    @DisplayName("Given un RegistrarPagoRequest, When se crea con parámetros, Then tiene valores correctos")
    void testRegistrarPagoRequest_Constructor() {
        // Given: Parámetros para crear request

        // When: Se crea el request
        RegistrarPagoRequest request = new RegistrarPagoRequest(
                1L, new BigDecimal("500.00"), 1L, "Observación");

        // Then: Tiene valores correctos
        assertEquals(1L, request.getPrestamoId());
        assertEquals(new BigDecimal("500.00"), request.getMontoPago());
        assertEquals(1L, request.getOperadorId());
        assertEquals("Observación", request.getObservacion());
    }

    @Test
    @DisplayName("Given un RegistrarPagoRequest, When se modifican valores, Then los cambios se reflejan")
    void testRegistrarPagoRequest_Setters() {
        // Given: Un request vacío
        RegistrarPagoRequest request = new RegistrarPagoRequest();

        // When: Se modifican los valores
        request.setPrestamoId(2L);
        request.setMontoPago(new BigDecimal("1000.00"));
        request.setOperadorId(2L);
        request.setObservacion("Nueva observacion");

        // Then: Los cambios se reflejan
        assertEquals(2L, request.getPrestamoId());
        assertEquals(new BigDecimal("1000.00"), request.getMontoPago());
        assertEquals(2L, request.getOperadorId());
        assertEquals("Nueva observacion", request.getObservacion());
    }

    @Test
    @DisplayName("Given un RegistrarPagoResponse, When se crea con parámetros, Then tiene valores correctos")
    void testRegistrarPagoResponse_Constructor() {
        // Given: Parámetros para crear response

        // When: Se crea el response
        RegistrarPagoResponse response = new RegistrarPagoResponse();
        response.setPagoId(1L);
        response.setNumeroPrestamo("PR-001");
        response.setMontoPago(new BigDecimal("500.00"));
        response.setFechaPago(LocalDateTime.now());
        response.setEstado("CONFIRMADO");
        response.setSaldoAFavor(BigDecimal.ZERO);

        // Then: Tiene valores correctos
        assertEquals(1L, response.getPagoId());
        assertEquals("PR-001", response.getNumeroPrestamo());
        assertEquals(new BigDecimal("500.00"), response.getMontoPago());
        assertEquals("CONFIRMADO", response.getEstado());
    }

    @Test
    @DisplayName("Given un RegistrarPagoResponse, When se agregan distribuciones, Then se reflejan correctamente")
    void testRegistrarPagoResponse_Distribuciones() {
        // Given: Un response vacío
        RegistrarPagoResponse response = new RegistrarPagoResponse();

        // When: Se agregan distribuciones
        List<RegistrarPagoResponse.DistribucionDetalle> distribuciones = new ArrayList<>();
        RegistrarPagoResponse.DistribucionDetalle detalle = new RegistrarPagoResponse.DistribucionDetalle();
        detalle.setNumeroCuota(1);
        detalle.setValorCuota(new BigDecimal("1000.00"));
        detalle.setMontoAplicado(new BigDecimal("500.00"));
        detalle.setEstadoNuevo("PARCIALMENTE_PAGADA");
        distribuciones.add(detalle);

        response.setDistribuciones(distribuciones);

        // Then: Las distribuciones están correctas
        assertEquals(1, response.getDistribuciones().size());
        assertEquals(1, response.getDistribuciones().get(0).getNumeroCuota());
    }

    @Test
    @DisplayName("Given un DistribucionDetalle, When se modifican valores, Then los cambios se reflejan")
    void testDistribucionDetalle_Setters() {
        // Given: Un detalle vacío
        RegistrarPagoResponse.DistribucionDetalle detalle = new RegistrarPagoResponse.DistribucionDetalle();

        // When: Se modifican los valores
        detalle.setNumeroCuota(2);
        detalle.setValorCuota(new BigDecimal("1000.00"));
        detalle.setMontoAplicado(new BigDecimal("750.00"));
        detalle.setEstadoAnterior("PENDIENTE");
        detalle.setEstadoNuevo("PAGADA");

        // Then: Los cambios se reflejan
        assertEquals(2, detalle.getNumeroCuota());
        assertEquals(new BigDecimal("1000.00"), detalle.getValorCuota());
        assertEquals(new BigDecimal("750.00"), detalle.getMontoAplicado());
        assertEquals("PENDIENTE", detalle.getEstadoAnterior());
        assertEquals("PAGADA", detalle.getEstadoNuevo());
    }

    @Test
    @DisplayName("Given un response con saldo a favor, When se consulta, Then retorna el valor correcto")
    void testRegistrarPagoResponse_SaldoAFavor() {
        // Given: Un response con saldo a favor
        RegistrarPagoResponse response = new RegistrarPagoResponse();
        response.setSaldoAFavor(new BigDecimal("200.00"));

        // When: Se consulta el saldo
        BigDecimal saldo = response.getSaldoAFavor();

        // Then: Retorna el valor correcto
        assertEquals(new BigDecimal("200.00"), saldo);
    }
}