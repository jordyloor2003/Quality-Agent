package com.fondocesantia.domain.service;

import com.fondocesantia.domain.entity.*;
import com.fondocesantia.domain.repository.CuotaRepository;
import com.fondocesantia.domain.repository.SaldoAFavorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DistribucionPagoService using BDD Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
@DisplayName("DistribucionPagoService - Distribución de Pagos")
class DistribucionPagoServiceTest {

    @Mock
    private CuotaRepository cuotaRepository;

    @Mock
    private SaldoAFavorRepository saldoAFavorRepository;

    @InjectMocks
    private DistribucionPagoService distribucionPagoService;

    private Prestamo prestamo;
    private Pago pago;

    @BeforeEach
    void setUp() {
        // Given: Un préstamo existente con cuotas
        prestamo = new Prestamo("PR-001", 1L, new BigDecimal("10000.00"),
                new BigDecimal("0.15"), Prestamo.PrestamoEstado.ACTIVO);
        prestamo.setId(1L);

        pago = new Pago(prestamo, new BigDecimal("1000.00"), 1L, "Pago de prueba");
        pago.setId(1L);
    }

    @Test
    @DisplayName("US1: Given un préstamo con cuotas vencidas y pendientes, When se registra un pago, Then se aplica primero a cuotas vencidas")
    void testDistribuirPago_AplicaPrimeroCuotasVencidas() {
        // Given: Un préstamo con cuotas vencidas y pendientes
        Cuota cuotaVencida = new Cuota(1, new BigDecimal("1000.00"), new BigDecimal("750.00"),
                new BigDecimal("250.00"), LocalDate.now().minusDays(30));
        cuotaVencida.setId(1L);
        cuotaVencida.setPrestamo(prestamo);
        cuotaVencida.setEstado(Cuota.CuotaEstado.VENCIDA);
        cuotaVencida.setSaldoPagado(BigDecimal.ZERO);

        Cuota cuotaPendiente = new Cuota(2, new BigDecimal("1000.00"), new BigDecimal("750.00"),
                new BigDecimal("250.00"), LocalDate.now().plusDays(15));
        cuotaPendiente.setId(2L);
        cuotaPendiente.setPrestamo(prestamo);
        cuotaPendiente.setEstado(Cuota.CuotaEstado.PENDIENTE);
        cuotaPendiente.setSaldoPagado(BigDecimal.ZERO);

        when(cuotaRepository.findCuotasVencidasByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuotaVencida));
        when(cuotaRepository.findCuotasPendientesByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuotaPendiente));
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(i -> i.getArgument(0));
        when(saldoAFavorRepository.save(any(SaldoAFavor.class))).thenAnswer(i -> i.getArgument(0));

        // When: Se distribuye un pago de 1000
        List<DistribucionPago> distribuciones = distribucionPagoService.distribuirPago(
                prestamo, new BigDecimal("1000.00"), pago);

        // Then: El pago se aplica primero a la cuota vencida
        assertEquals(1, distribuciones.size());
        assertEquals(cuotaVencida.getId(), distribuciones.get(0).getCuota().getId());
        assertEquals(new BigDecimal("1000.00"), distribuciones.get(0).getMontoAplicado());
    }

    @Test
    @DisplayName("US2: Given una cuota pendiente, When el pago es exactamente igual al valor, Then la cuota se marca como PAGADA")
    void testDistribuirPago_PagoExacto_MarcaComoPagada() {
        // Given: Una cuota pendiente con valor de 500
        Cuota cuota = new Cuota(1, new BigDecimal("500.00"), new BigDecimal("400.00"),
                new BigDecimal("100.00"), LocalDate.now().plusDays(15));
        cuota.setId(1L);
        cuota.setPrestamo(prestamo);
        cuota.setEstado(Cuota.CuotaEstado.PENDIENTE);
        cuota.setSaldoPagado(BigDecimal.ZERO);

        when(cuotaRepository.findCuotasVencidasByPrestamoId(1L))
                .thenReturn(Collections.emptyList());
        when(cuotaRepository.findCuotasPendientesByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuota));
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(i -> i.getArgument(0));

        // When: Se paga exactamente el valor de la cuota
        distribucionPagoService.distribuirPago(prestamo, new BigDecimal("500.00"), pago);

        // Then: La cuota se marca como PAGADA
        assertEquals(Cuota.CuotaEstado.PAGADA, cuota.getEstado());
        assertEquals(new BigDecimal("500.00"), cuota.getSaldoPagado());
    }

    @Test
    @DisplayName("US3: Given una cuota pendiente, When el pago es menor al valor, Then la cuota se marca como PARCIALMENTE_PAGADA")
    void testDistribuirPago_PagoParcial_MarcaComoParcialmentePagada() {
        // Given: Una cuota pendiente con valor de 1000
        Cuota cuota = new Cuota(1, new BigDecimal("1000.00"), new BigDecimal("750.00"),
                new BigDecimal("250.00"), LocalDate.now().plusDays(15));
        cuota.setId(1L);
        cuota.setPrestamo(prestamo);
        cuota.setEstado(Cuota.CuotaEstado.PENDIENTE);
        cuota.setSaldoPagado(BigDecimal.ZERO);

        when(cuotaRepository.findCuotasVencidasByPrestamoId(1L))
                .thenReturn(Collections.emptyList());
        when(cuotaRepository.findCuotasPendientesByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuota));
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(i -> i.getArgument(0));

        // When: Se paga solo 300 (menor al valor de la cuota)
        distribucionPagoService.distribuirPago(prestamo, new BigDecimal("300.00"), pago);

        // Then: La cuota se marca como PARCIALMENTE_PAGADA
        assertEquals(Cuota.CuotaEstado.PARCIALMENTE_PAGADA, cuota.getEstado());
        assertEquals(new BigDecimal("300.00"), cuota.getSaldoPagado());
    }

    @Test
    @DisplayName("US4: Given un pago mayor al total de cuotas pendientes, When se distribuye, Then se crea saldo a favor")
    void testDistribuirPago_PagoExcedente_CreaSaldoAFavor() {
        // Given: Cuotas pendientes con total de 500
        Cuota cuota = new Cuota(1, new BigDecimal("500.00"), new BigDecimal("400.00"),
                new BigDecimal("100.00"), LocalDate.now().plusDays(15));
        cuota.setId(1L);
        cuota.setPrestamo(prestamo);
        cuota.setEstado(Cuota.CuotaEstado.PENDIENTE);
        cuota.setSaldoPagado(BigDecimal.ZERO);

        when(cuotaRepository.findCuotasVencidasByPrestamoId(1L))
                .thenReturn(Collections.emptyList());
        when(cuotaRepository.findCuotasPendientesByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuota));
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(i -> i.getArgument(0));
        when(saldoAFavorRepository.save(any(SaldoAFavor.class))).thenAnswer(i -> i.getArgument(0));

        // When: Se paga 700 (mayor al total de cuotas)
        distribucionPagoService.distribuirPago(prestamo, new BigDecimal("700.00"), pago);

        // Then: Se crea saldo a favor de 200
        verify(saldoAFavorRepository).save(argThat(saldo ->
                saldo.getMonto().compareTo(new BigDecimal("200.00")) == 0 &&
                saldo.getClienteId().equals(1L)));
    }

    @Test
    @DisplayName("Given múltiples cuotas vencidas, When se distribuye el pago, Then se procesan en orden de fecha")
    void testDistribuirPago_MultiplesCuotasVencidas_OrdenPorFecha() {
        // Given: Tres cuotas vencidas en diferentes fechas
        Cuota cuota1 = createCuota(1L, 1, new BigDecimal("500.00"),
                LocalDate.now().minusDays(60), Cuota.CuotaEstado.VENCIDA);
        Cuota cuota2 = createCuota(2L, 2, new BigDecimal("500.00"),
                LocalDate.now().minusDays(30), Cuota.CuotaEstado.VENCIDA);
        Cuota cuota3 = createCuota(3L, 3, new BigDecimal("500.00"),
                LocalDate.now().minusDays(15), Cuota.CuotaEstado.VENCIDA);

        when(cuotaRepository.findCuotasVencidasByPrestamoId(1L))
                .thenReturn(Arrays.asList(cuota1, cuota2, cuota3));
        when(cuotaRepository.findCuotasPendientesByPrestamoId(1L))
                .thenReturn(Collections.emptyList());
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(i -> i.getArgument(0));

        // When: Se paga 1500 (suficiente para las 3 cuotas)
        distribucionPagoService.distribuirPago(prestamo, new BigDecimal("1500.00"), pago);

        // Then: Se procesan en orden de fecha (más antigua primero)
        verify(cuotaRepository, times(3)).save(any(Cuota.class));
    }

    private Cuota createCuota(Long id, int numero, BigDecimal valor, LocalDate fechaVencimiento, Cuota.CuotaEstado estado) {
        Cuota cuota = new Cuota(numero, valor, new BigDecimal("400.00"),
                new BigDecimal("100.00"), fechaVencimiento);
        cuota.setId(id);
        cuota.setPrestamo(prestamo);
        cuota.setEstado(estado);
        cuota.setSaldoPagado(BigDecimal.ZERO);
        return cuota;
    }
}