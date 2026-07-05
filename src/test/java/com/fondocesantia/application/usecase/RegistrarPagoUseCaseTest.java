package com.fondocesantia.application.usecase;

import com.fondocesantia.application.dto.RegistrarPagoRequest;
import com.fondocesantia.application.dto.RegistrarPagoResponse;
import com.fondocesantia.domain.entity.Cuota;
import com.fondocesantia.domain.entity.Prestamo;
import com.fondocesantia.domain.repository.CuotaRepository;
import com.fondocesantia.domain.repository.PagoRepository;
import com.fondocesantia.domain.repository.PrestamoRepository;
import com.fondocesantia.domain.service.DistribucionPagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for RegistrarPagoUseCase using BDD Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrarPagoUseCase - Tests de Integración")
class RegistrarPagoUseCaseTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private DistribucionPagoService distribucionPagoService;

    @InjectMocks
    private RegistrarPagoUseCase registrarPagoUseCase;

    private Prestamo prestamo;
    private RegistrarPagoRequest request;

    @BeforeEach
    void setUp() {
        // Given: Un préstamo existente
        prestamo = new Prestamo("PR-001", 1L, new BigDecimal("10000.00"),
                new BigDecimal("0.15"), Prestamo.PrestamoEstado.ACTIVO);
        prestamo.setId(1L);

        request = new RegistrarPagoRequest(1L, new BigDecimal("500.00"), 1L, "Pago de prueba");
    }

    @Test
    @DisplayName("Given un préstamo activo, When se registra un pago válido, Then el pago se registra exitosamente")
    void testExecute_PagoExitoso() {
        // Given: Un préstamo activo en la base de datos
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(pagoRepository.save(any())).thenAnswer(i -> {
            var pago = i.getArgument(0);
            ((com.fondocesantia.domain.entity.Pago) pago).setId(1L);
            return pago;
        });
        when(distribucionPagoService.distribuirPago(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(prestamoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When: Se ejecuta el caso de uso
        RegistrarPagoResponse response = registrarPagoUseCase.execute(request);

        // Then: El pago se registra exitosamente
        assertNotNull(response);
        assertEquals("PR-001", response.getNumeroPrestamo());
        assertEquals(new BigDecimal("500.00"), response.getMontoPago());
        verify(pagoRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Given un préstamo inexistente, When se registra un pago, Then lanza excepción")
    void testExecute_PrestamoNoEncontrado() {
        // Given: No existe el préstamo
        when(prestamoRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then: Se lanza excepción
        RegistrarPagoRequest requestInvalido = new RegistrarPagoRequest(
                999L, new BigDecimal("500.00"), 1L, "Pago de prueba");

        assertThrows(IllegalArgumentException.class, () ->
                registrarPagoUseCase.execute(requestInvalido));
    }

    @Test
    @DisplayName("Given un préstamo inactivo, When se registra un pago, Then lanza excepción de estado")
    void testExecute_PrestamoInactivo() {
        // Given: Un préstamo que no está activo
        prestamo.setEstado(Prestamo.PrestamoEstado.INACTIVO);
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        // When/Then: Se lanza excepción de estado
        assertThrows(IllegalStateException.class, () ->
                registrarPagoUseCase.execute(request));
    }

    @Test
    @DisplayName("Given un monto de pago inválido, When se registra, Then lanza excepción")
    void testExecute_MontoInvalido() {
        // Given: Un préstamo activo
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        // When/Then: Se lanza excepción por monto inválido
        RegistrarPagoRequest requestMontoInvalido = new RegistrarPagoRequest(
                1L, BigDecimal.ZERO, 1L, "Pago de prueba");

        assertThrows(IllegalArgumentException.class, () ->
                registrarPagoUseCase.execute(requestMontoInvalido));
    }
}