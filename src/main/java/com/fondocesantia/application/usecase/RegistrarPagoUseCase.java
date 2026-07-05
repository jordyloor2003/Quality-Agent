package com.fondocesantia.application.usecase;

import com.fondocesantia.application.dto.RegistrarPagoRequest;
import com.fondocesantia.application.dto.RegistrarPagoResponse;
import com.fondocesantia.domain.entity.DistribucionPago;
import com.fondocesantia.domain.entity.Pago;
import com.fondocesantia.domain.entity.Prestamo;
import com.fondocesantia.domain.repository.PagoRepository;
import com.fondocesantia.domain.repository.PrestamoRepository;
import com.fondocesantia.domain.service.DistribucionPagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para registrar un pago y distribuirlo entre las cuotas.
 */
@Service
public class RegistrarPagoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarPagoUseCase.class);

    private final PrestamoRepository prestamoRepository;
    private final PagoRepository pagoRepository;
    private final DistribucionPagoService distribucionPagoService;

    public RegistrarPagoUseCase(PrestamoRepository prestamoRepository,
                                PagoRepository pagoRepository,
                                DistribucionPagoService distribucionPagoService) {
        this.prestamoRepository = prestamoRepository;
        this.pagoRepository = pagoRepository;
        this.distribucionPagoService = distribucionPagoService;
    }

    /**
     * Registra un pago y lo distribuye entre las cuotas del préstamo.
     *
     * @param request La solicitud con los datos del pago
     * @return La respuesta con los detalles de la distribución
     */
    @Transactional
    public RegistrarPagoResponse execute(RegistrarPagoRequest request) {
        logger.info("Registrando pago para préstamo ID: {}, monto: {}",
                    request.getPrestamoId(), request.getMontoPago());

        // Validar que el préstamo existe
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(request.getPrestamoId());
        if (prestamoOpt.isEmpty()) {
            throw new IllegalArgumentException("Préstamo no encontrado con ID: " + request.getPrestamoId());
        }

        Prestamo prestamo = prestamoOpt.get();

        // Validar estado del préstamo
        if (prestamo.getEstado() != Prestamo.PrestamoEstado.ACTIVO) {
            throw new IllegalStateException("El préstamo debe estar en estado ACTIVO para registrar pagos");
        }

        // Validar monto del pago
        if (request.getMontoPago().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
        }

        // Crear el pago
        Pago pago = new Pago(
            prestamo,
            request.getMontoPago(),
            request.getOperadorId(),
            request.getObservacion()
        );
        pago = pagoRepository.save(pago);

        // Distribuir el pago entre las cuotas
        List<DistribucionPago> distribuciones = distribucionPagoService.distribuirPago(
            prestamo,
            request.getMontoPago(),
            pago
        );

        // Actualizar el saldo del préstamo
        BigDecimal totalAplicado = distribuciones.stream()
            .map(DistribucionPago::getMontoAplicado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        prestamo.setSaldoActual(prestamo.getSaldoActual().subtract(totalAplicado));
        prestamoRepository.save(prestamo);

        // Marcar el pago como confirmado
        pago.setEstado(Pago.PagoEstado.CONFIRMADO);
        pagoRepository.save(pago);

        logger.info("Pago registrado exitosamente. ID: {}, distribuciones: {}",
                    pago.getId(), distribuciones.size());

        // Crear respuesta
        return buildResponse(pago, prestamo, distribuciones);
    }

    private RegistrarPagoResponse buildResponse(Pago pago, Prestamo prestamo,
                                                 List<DistribucionPago> distribuciones) {
        RegistrarPagoResponse response = new RegistrarPagoResponse();
        response.setPagoId(pago.getId());
        response.setNumeroPrestamo(prestamo.getNumeroPrestamo());
        response.setMontoPago(pago.getMontoPago());
        response.setFechaPago(pago.getFechaPago());
        response.setEstado(pago.getEstado().name());

        // Mapear distribuciones
        List<RegistrarPagoResponse.DistribucionDetalle> detalleList = new ArrayList<>();
        for (DistribucionPago dist : distribuciones) {
            RegistrarPagoResponse.DistribucionDetalle detalle = new RegistrarPagoResponse.DistribucionDetalle();
            detalle.setNumeroCuota(dist.getCuota().getNumeroCuota());
            detalle.setValorCuota(dist.getCuota().getValorCuota());
            detalle.setMontoAplicado(dist.getMontoAplicado());
            // Nota: Para obtener el estado anterior, necesitaríamos guardarlo antes de aplicar el pago
            detalle.setEstadoNuevo(dist.getCuota().getEstado().name());
            detalleList.add(detalle);
        }
        response.setDistribuciones(detalleList);

        // Establecer saldo a favor si existe
        if (pago.getSaldoAFavor() != null) {
            response.setSaldoAFavor(pago.getSaldoAFavor().getMonto());
        } else {
            response.setSaldoAFavor(BigDecimal.ZERO);
        }

        return response;
    }
}