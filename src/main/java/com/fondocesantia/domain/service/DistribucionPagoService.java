package com.fondocesantia.domain.service;

import com.fondocesantia.domain.entity.*;
import com.fondocesantia.domain.repository.CuotaRepository;
import com.fondocesantia.domain.repository.SaldoAFavorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio que maneja la lógica de distribución de pagos.
 */
@Service
public class DistribucionPagoService {

    private static final Logger logger = LoggerFactory.getLogger(DistribucionPagoService.class);

    private final CuotaRepository cuotaRepository;
    private final SaldoAFavorRepository saldoAFavorRepository;

    public DistribucionPagoService(CuotaRepository cuotaRepository,
                                   SaldoAFavorRepository saldoAFavorRepository) {
        this.cuotaRepository = cuotaRepository;
        this.saldoAFavorRepository = saldoAFavorRepository;
    }

    /**
     * Distribuye un pago entre las cuotas de un préstamo.
     * El algoritmo sigue el orden:
     * 1. Cuotas vencidas (orden ascendente por fecha)
     * 2. Cuotas pendientes (orden ascendente por fecha)
     *
     * @param prestamo El préstamo al que se aplica el pago
     * @param montoPago El monto total del pago
     * @param pago La entidad Pago asociada
     * @return Lista de distribuciones realizadas
     */
    @Transactional
    public List<DistribucionPago> distribuirPago(Prestamo prestamo, BigDecimal montoPago, Pago pago) {
        logger.info("Iniciando distribución de pago para préstamo {} con monto {}",
                    prestamo.getNumeroPrestamo(), montoPago);

        List<DistribucionPago> distribuciones = new ArrayList<>();
        BigDecimal montoRestante = montoPago;

        // Obtener cuotas vencidas primero
        List<Cuota> cuotasVencidas = cuotaRepository.findCuotasVencidasByPrestamoId(prestamo.getId());
        logger.debug("Cuotas vencidas encontradas: {}", cuotasVencidas.size());

        // Procesar cuotas vencidas
        montoRestante = procesarCuotas(distribuciones, cuotasVencidas, montoRestante, pago);

        // Obtener cuotas pendientes
        List<Cuota> cuotasPendientes = cuotaRepository.findCuotasPendientesByPrestamoId(prestamo.getId());
        logger.debug("Cuotas pendientes encontradas: {}", cuotasPendientes.size());

        // Procesar cuotas pendientes
        montoRestante = procesarCuotas(distribuciones, cuotasPendientes, montoRestante, pago);

        // Si hay excedente, crear saldo a favor
        if (montoRestante.compareTo(BigDecimal.ZERO) > 0) {
            logger.info("Creando saldo a favor por monto restante: {}", montoRestante);
            SaldoAFavor saldoAFavor = new SaldoAFavor(
                pago,
                prestamo.getClienteId(),
                montoRestante,
                "EXCEDENTE_PAGO"
            );
            saldoAFavorRepository.save(saldoAFavor);
            pago.setSaldoAFavor(saldoAFavor);
        }

        logger.info("Distribución completada. Distribuciones: {}, Saldo a favor: {}",
                    distribuciones.size(),
                    montoRestante.compareTo(BigDecimal.ZERO) > 0 ? montoRestante : BigDecimal.ZERO);

        return distribuciones;
    }

    /**
     * Procesa una lista de cuotas aplicando el pago restante.
     */
    private BigDecimal procesarCuotas(List<DistribucionPago> distribuciones,
                                       List<Cuota> cuotas,
                                       BigDecimal montoRestante,
                                       Pago pago) {
        for (Cuota cuota : cuotas) {
            if (montoRestante.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal saldoPendiente = cuota.getValorCuota().subtract(cuota.getSaldoPagado());
            BigDecimal montoAAplicar = montoRestante.min(saldoPendiente);

            // Aplicar el pago a la cuota
            cuota.aplicarPago(montoAAplicar);
            cuotaRepository.save(cuota);

            // Crear la distribución
            DistribucionPago distribucion = new DistribucionPago(pago, cuota, montoAAplicar);
            distribuciones.add(distribucion);

            logger.debug("Aplicado {} a cuota {} (estado: {})",
                        montoAAplicar, cuota.getNumeroCuota(), cuota.getEstado());

            montoRestante = montoRestante.subtract(montoAAplicar);
        }

        return montoRestante;
    }

    /**
     * Obtiene el total de cuotas impagas de un préstamo.
     */
    public BigDecimal getTotalCuotasImpagas(Prestamo prestamo) {
        List<Cuota> cuotasImpagas = cuotaRepository.findCuotasImpagasByPrestamoId(prestamo.getId());
        return cuotasImpagas.stream()
                .map(cuota -> cuota.getValorCuota().subtract(cuota.getSaldoPagado()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}