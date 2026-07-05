package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Cuota.
 */
@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    /**
     * Busca todas las cuotas de un préstamo ordenadas por número de cuota.
     */
    List<Cuota> findByPrestamoIdOrderByNumeroCuotaAsc(Long prestamoId);

    /**
     * Busca cuotas por estado y préstamo, ordenadas por fecha de vencimiento.
     */
    @Query("SELECT c FROM Cuota c WHERE c.prestamo.id = :prestamoId AND c.estado = :estado ORDER BY c.fechaVencimiento ASC")
    List<Cuota> findByPrestamoIdAndEstadoOrderByFechaVencimientoAsc(
            @Param("prestamoId") Long prestamoId,
            @Param("estado") Cuota.CuotaEstado estado);

    /**
     * Busca cuotas impagas (VENCIDA, PENDIENTE, PARCIALMENTE_PAGADA) de un préstamo.
     */
    @Query("SELECT c FROM Cuota c WHERE c.prestamo.id = :prestamoId AND c.estado IN ('VENCIDA', 'PENDIENTE', 'PARCIALMENTE_PAGADA') ORDER BY c.fechaVencimiento ASC")
    List<Cuota> findCuotasImpagasByPrestamoId(@Param("prestamoId") Long prestamoId);

    /**
     * Busca cuotas vencidas de un préstamo ordenadas por fecha de vencimiento.
     */
    @Query("SELECT c FROM Cuota c WHERE c.prestamo.id = :prestamoId AND c.estado = 'VENCIDA' ORDER BY c.fechaVencimiento ASC")
    List<Cuota> findCuotasVencidasByPrestamoId(@Param("prestamoId") Long prestamoId);

    /**
     * Busca cuotas pendientes de un préstamo ordenadas por fecha de vencimiento.
     */
    @Query("SELECT c FROM Cuota c WHERE c.prestamo.id = :prestamoId AND c.estado = 'PENDIENTE' ORDER BY c.fechaVencimiento ASC")
    List<Cuota> findCuotasPendientesByPrestamoId(@Param("prestamoId") Long prestamoId);
}