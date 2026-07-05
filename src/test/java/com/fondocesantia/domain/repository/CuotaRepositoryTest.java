package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.Cuota;
import com.fondocesantia.domain.entity.Prestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CuotaRepository using BDD Given-When-Then pattern.
 */
@DataJpaTest
@DisplayName("CuotaRepository - Tests de Integración")
class CuotaRepositoryTest {

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        // Given: Un préstamo en la base de datos
        prestamo = new Prestamo("PR-TEST", 1L, new BigDecimal("10000.00"),
                new BigDecimal("0.15"), Prestamo.PrestamoEstado.ACTIVO);
        prestamo = prestamoRepository.save(prestamo);
    }

    @Test
    @DisplayName("Given cuotas en diferentes estados, When se buscan cuotas vencidas, Then retorna solo las vencidas")
    void testFindCuotasVencidas() {
        // Given: Cuotas en diferentes estados
        Cuota cuotaVencida = createCuota(1, new BigDecimal("500.00"),
                LocalDate.now().minusDays(30), Cuota.CuotaEstado.VENCIDA);
        Cuota cuotaPendiente = createCuota(2, new BigDecimal("500.00"),
                LocalDate.now().plusDays(15), Cuota.CuotaEstado.PENDIENTE);

        cuotaRepository.save(cuotaVencida);
        cuotaRepository.save(cuotaPendiente);

        // When: Se buscan cuotas vencidas
        List<Cuota> cuotasVencidas = cuotaRepository.findCuotasVencidasByPrestamoId(prestamo.getId());

        // Then: Solo se retornan las cuotas vencidas
        assertEquals(1, cuotasVencidas.size());
        assertEquals(Cuota.CuotaEstado.VENCIDA, cuotasVencidas.get(0).getEstado());
    }

    @Test
    @DisplayName("Given cuotas en diferentes estados, When se buscan cuotas pendientes, Then retorna solo las pendientes")
    void testFindCuotasPendientes() {
        // Given: Cuotas en diferentes estados
        Cuota cuotaVencida = createCuota(1, new BigDecimal("500.00"),
                LocalDate.now().minusDays(30), Cuota.CuotaEstado.VENCIDA);
        Cuota cuotaPendiente = createCuota(2, new BigDecimal("500.00"),
                LocalDate.now().plusDays(15), Cuota.CuotaEstado.PENDIENTE);

        cuotaRepository.save(cuotaVencida);
        cuotaRepository.save(cuotaPendiente);

        // When: Se buscan cuotas pendientes
        List<Cuota> cuotasPendientes = cuotaRepository.findCuotasPendientesByPrestamoId(prestamo.getId());

        // Then: Solo se retornan las cuotas pendientes
        assertEquals(1, cuotasPendientes.size());
        assertEquals(Cuota.CuotaEstado.PENDIENTE, cuotasPendientes.get(0).getEstado());
    }

    @Test
    @DisplayName("Given cuotas con diferentes estados, When se buscan cuotas impagas, Then retorna todas las no pagadas")
    void testFindCuotasImpagas() {
        // Given: Cuotas en diferentes estados
        Cuota cuotaVencida = createCuota(1, new BigDecimal("500.00"),
                LocalDate.now().minusDays(30), Cuota.CuotaEstado.VENCIDA);
        Cuota cuotaParcial = createCuota(2, new BigDecimal("500.00"),
                LocalDate.now().plusDays(15), Cuota.CuotaEstado.PARCIALMENTE_PAGADA);
        Cuota cuotaPagada = createCuota(3, new BigDecimal("500.00"),
                LocalDate.now().plusDays(30), Cuota.CuotaEstado.PAGADA);

        cuotaRepository.save(cuotaVencida);
        cuotaRepository.save(cuotaParcial);
        cuotaRepository.save(cuotaPagada);

        // When: Se buscan cuotas impagas
        List<Cuota> cuotasImpagas = cuotaRepository.findCuotasImpagasByPrestamoId(prestamo.getId());

        // Then: Se retornan cuotas vencidas y parcialmente pagadas (no pagadas completas)
        assertEquals(2, cuotasImpagas.size());
    }

    @Test
    @DisplayName("Given cuotas de un préstamo, When se ordenan por número de cuota, Then mantiene el orden ascendente")
    void testFindByPrestamoIdOrderByNumeroCuota() {
        // Given: Múltiples cuotas para un préstamo
        for (int i = 5; i >= 1; i--) {
            Cuota cuota = createCuota(i, new BigDecimal("500.00"),
                    LocalDate.now().plusDays(i * 15L), Cuota.CuotaEstado.PENDIENTE);
            cuotaRepository.save(cuota);
        }

        // When: Se buscan cuotas ordenadas por número
        List<Cuota> cuotas = cuotaRepository.findByPrestamoIdOrderByNumeroCuotaAsc(prestamo.getId());

        // Then: Las cuotas están en orden ascendente
        assertEquals(5, cuotas.size());
        for (int i = 0; i < cuotas.size(); i++) {
            assertEquals(i + 1, cuotas.get(i).getNumeroCuota());
        }
    }

    private Cuota createCuota(int numero, BigDecimal valor, LocalDate fechaVencimiento, Cuota.CuotaEstado estado) {
        Cuota cuota = new Cuota(numero, valor, new BigDecimal("400.00"),
                new BigDecimal("100.00"), fechaVencimiento);
        cuota.setPrestamo(prestamo);
        cuota.setEstado(estado);
        cuota.setSaldoPagado(BigDecimal.ZERO);
        return cuota;
    }
}