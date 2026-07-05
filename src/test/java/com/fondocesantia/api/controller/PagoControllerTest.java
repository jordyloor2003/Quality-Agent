package com.fondocesantia.api.controller;

import com.fondocesantia.application.dto.RegistrarPagoRequest;
import com.fondocesantia.application.dto.RegistrarPagoResponse;
import com.fondocesantia.application.usecase.RegistrarPagoUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PagoController using BDD Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoController - Tests de Controlador")
class PagoControllerTest {

    @Mock
    private RegistrarPagoUseCase registrarPagoUseCase;

    @InjectMocks
    private PagoController pagoController;

    @Test
    @DisplayName("Given una solicitud válida, When se registra pago, Then retorna código 201")
    void testRegistrarPago_Exitoso() {
        // Given: Una solicitud de pago válida
        RegistrarPagoRequest request = new RegistrarPagoRequest(
                1L, new BigDecimal("500.00"), 1L, "Pago de prueba");

        RegistrarPagoResponse mockResponse = createMockResponse();
        when(registrarPagoUseCase.execute(any())).thenReturn(mockResponse);

        // When: Se registra el pago
        ResponseEntity<RegistrarPagoResponse> response = pagoController.registrarPago(request);

        // Then: Retorna código 201
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PR-001", response.getBody().getNumeroPrestamo());
        verify(registrarPagoUseCase).execute(any());
    }

    @Test
    @DisplayName("Given un préstamo no encontrado, When se registra pago, Then lanza excepción")
    void testRegistrarPago_PrestamoNoEncontrado() {
        // Given: Una solicitud con préstamo inexistente
        RegistrarPagoRequest request = new RegistrarPagoRequest(
                999L, new BigDecimal("500.00"), 1L, "Pago de prueba");

        when(registrarPagoUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("Préstamo no encontrado"));

        // When/Then: Se lanza excepción
        assertThrows(IllegalArgumentException.class, () -> pagoController.registrarPago(request));
    }

    @Test
    @DisplayName("Given un préstamo inactivo, When se registra pago, Then lanza excepción de estado")
    void testRegistrarPago_PrestamoInactivo() {
        // Given: Una solicitud con préstamo inactivo
        RegistrarPagoRequest request = new RegistrarPagoRequest(
                1L, new BigDecimal("500.00"), 1L, "Pago de prueba");

        when(registrarPagoUseCase.execute(any()))
                .thenThrow(new IllegalStateException("Préstamo debe estar ACTIVO"));

        // When/Then: Se lanza excepción de estado
        assertThrows(IllegalStateException.class, () -> pagoController.registrarPago(request));
    }

    @Test
    @DisplayName("Given una solicitud con monto inválido, When se registra pago, Then lanza excepción")
    void testRegistrarPago_MontoInvalido() {
        // Given: Una solicitud con monto cero
        RegistrarPagoRequest request = new RegistrarPagoRequest(
                1L, BigDecimal.ZERO, 1L, "Pago de prueba");

        when(registrarPagoUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("El monto debe ser mayor a 0"));

        // When/Then: Se lanza excepción
        assertThrows(IllegalArgumentException.class, () -> pagoController.registrarPago(request));
    }

    @Test
    @DisplayName("Given una solicitud, When se solicita distribución, Then lanza excepción no implementada")
    void testVerDistribucion_NoImplementado() {
        // Given: Un ID de pago
        Long pagoId = 1L;

        // When/Then: Se lanza excepción
        assertThrows(UnsupportedOperationException.class, () -> pagoController.verDistribucion(pagoId));
    }

    private RegistrarPagoResponse createMockResponse() {
        RegistrarPagoResponse response = new RegistrarPagoResponse();
        response.setPagoId(1L);
        response.setNumeroPrestamo("PR-001");
        response.setMontoPago(new BigDecimal("500.00"));
        response.setEstado("CONFIRMADO");
        response.setDistribuciones(new ArrayList<>());
        response.setSaldoAFavor(BigDecimal.ZERO);
        return response;
    }
}