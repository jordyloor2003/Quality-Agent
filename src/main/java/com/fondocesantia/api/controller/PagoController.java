package com.fondocesantia.api.controller;

import com.fondocesantia.application.dto.RegistrarPagoRequest;
import com.fondocesantia.application.dto.RegistrarPagoResponse;
import com.fondocesantia.application.usecase.RegistrarPagoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de pagos.
 */
@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "API para registro y consulta de pagos")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    private final RegistrarPagoUseCase registrarPagoUseCase;

    public PagoController(RegistrarPagoUseCase registrarPagoUseCase) {
        this.registrarPagoUseCase = registrarPagoUseCase;
    }

    /**
     * Registra un pago y retorna la distribución aplicada.
     */
    @PostMapping
    @Operation(summary = "Registrar pago", description = "Registra un pago y distribuye su monto entre las cuotas del préstamo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o préstamo no encontrado"),
        @ApiResponse(responseCode = "409", description = "El préstamo no está en estado activo")
    })
    public ResponseEntity<RegistrarPagoResponse> registrarPago(
            @Valid @RequestBody RegistrarPagoRequest request) {
        logger.info("Recibida solicitud de registro de pago para préstamo ID: {}", request.getPrestamoId());

        try {
            RegistrarPagoResponse response = registrarPagoUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            logger.warn("Error de estado: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene la distribución de un pago registrado.
     */
    @GetMapping("/{id}/distribucion")
    @Operation(summary = "Ver distribución", description = "Obtiene el detalle de la distribución de un pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Distribución encontrada"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<RegistrarPagoResponse> verDistribucion(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        logger.info("Solicitando distribución del pago ID: {}", id);
        // TODO: Implementar consulta de distribución por ID
        throw new UnsupportedOperationException("Implementación pendiente");
    }
}