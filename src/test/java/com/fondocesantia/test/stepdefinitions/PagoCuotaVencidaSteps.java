package com.fondocesantia.test.stepdefinitions;

import com.fondocesantia.application.dto.RegistrarPagoRequest;
import com.fondocesantia.application.dto.RegistrarPagoResponse;
import com.fondocesantia.application.usecase.RegistrarPagoUseCase;
import com.fondocesantia.domain.entity.Cuota;
import com.fondocesantia.domain.entity.Prestamo;
import com.fondocesantia.domain.repository.CuotaRepository;
import com.fondocesantia.domain.repository.PrestamoRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions para pruebas funcionales de distribución de pagos a cuotas vencidas.
 */
public class PagoCuotaVencidaSteps {

    @Autowired
    private RegistrarPagoUseCase registrarPagoUseCase;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private CuotaRepository cuotaRepository;

    private Prestamo prestamo;
    private RegistrarPagoResponse response;
    private String mensajeError;

    @Dado("que existe un préstamo {string} con estado {string}")
    public void queExisteUnPrestamo(String numeroPrestamo, String estado) {
        prestamo = prestamoRepository.findByNumeroPrestamo(numeroPrestamo).orElse(null);
        if (prestamo == null) {
            prestamo = new Prestamo(numeroPrestamo, 1L, new BigDecimal("10000.00"),
                    new BigDecimal("0.15"), Prestamo.PrestamoEstado.valueOf(estado));
            prestamo = prestamoRepository.save(prestamo);
        }
        assertNotNull(prestamo);
    }

    @Dado("el préstamo tiene las siguientes cuotas:")
    public void elPrestamoTieneLasSiguientesCuotas(List<Map<String, String>> cuotasData) {
        for (Map<String, String> cuotaData : cuotasData) {
            Cuota cuota = new Cuota(
                    Integer.parseInt(cuotaData.get("numero")),
                    new BigDecimal(cuotaData.get("valor")),
                    new BigDecimal("750.00"),
                    new BigDecimal("250.00"),
                    LocalDate.parse(cuotaData.get("fecha_vencimiento"))
            );
            cuota.setPrestamo(prestamo);
            cuota.setEstado(Cuota.CuotaEstado.valueOf(cuotaData.get("estado")));
            cuota.setSaldoPagado(BigDecimal.ZERO);
            cuotaRepository.save(cuota);
        }
    }

    @Cuando("registro un pago de {string} para el préstamo {string}")
    public void registroUnPagoParaElPrestamo(String monto, String numeroPrestamo) {
        try {
            RegistrarPagoRequest request = new RegistrarPagoRequest(
                    prestamo.getId(),
                    new BigDecimal(monto),
                    1L,
                    "Pago de prueba BDD"
            );
            response = registrarPagoUseCase.execute(request);
            mensajeError = null;
        } catch (Exception e) {
            mensajeError = e.getMessage();
        }
    }

    @Entonces("el sistema debe aplicar el pago a la cuota número {string}")
    public void elSistemaDebeAplicarElPagoALaCuotaNumero(String numeroCuota) {
        assertNotNull(response, "La respuesta no debe ser null");
        assertFalse(response.getDistribuciones().isEmpty(), "Debe haber distribuciones");

        int numCuota = Integer.parseInt(numeroCuota);
        boolean found = response.getDistribuciones().stream()
                .anyMatch(d -> d.getNumeroCuota().equals(numCuota));
        assertTrue(found, "El pago debe aplicarse a la cuota " + numeroCuota);
    }

    @Y("la cuota número {string} debe tener estado {string}")
    public void laCuotaNumeroDebeTenerEstado(String numeroCuota, String estado) {
        int numCuota = Integer.parseInt(numeroCuota);
        var distribucion = response.getDistribuciones().stream()
                .filter(d -> d.getNumeroCuota().equals(numCuota))
                .findFirst();

        if (distribucion.isPresent()) {
            assertEquals(estado, distribucion.get().getEstadoNuevo());
        }
    }

    @Y("la cuota número {string} debe mantener estado {string}")
    public void laCuotaNumeroDebeMantenerEstado(String numeroCuota, String estado) {
        // Verificar que la cuota no aparece en las distribuciones
        int numCuota = Integer.parseInt(numeroCuota);
        boolean found = response.getDistribuciones().stream()
                .anyMatch(d -> d.getNumeroCuota().equals(numCuota));
        assertFalse(found, "La cuota " + numeroCuota + " no debe haber sido afectada");
    }

    @Entonces("el sistema debe aplicar {string} a la cuota número {string}")
    public void elSistemaDebeAplicarALaCuotaNumero(String monto, String numeroCuota) {
        assertNotNull(response);
        int numCuota = Integer.parseInt(numeroCuota);
        var distribucion = response.getDistribuciones().stream()
                .filter(d -> d.getNumeroCuota().equals(numCuota))
                .findFirst();

        assertTrue(distribucion.isPresent());
        assertEquals(new BigDecimal(monto), distribucion.get().getMontoAplicado());
    }

    @Y("aplicar {string} a la cuota número {string}")
    public void aplicarALaCuotaNumero(String monto, String numeroCuota) {
        // Continúa la verificación
        elSistemaDebeAplicarALaCuotaNumero(monto, numeroCuota);
    }

    @Y("debe sobrar {string} para siguientes cuotas")
    public void debeSobrarParaSiguientesCuotas(String monto) {
        assertNotNull(response);
        assertEquals(new BigDecimal(monto), response.getSaldoAFavor());
    }
}