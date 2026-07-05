package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.DistribucionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DistribucionPago.
 */
@Repository
public interface DistribucionPagoRepository extends JpaRepository<DistribucionPago, Long> {

    /**
     * Busca todas las distribuciones de un pago.
     */
    List<DistribucionPago> findByPagoId(Long pagoId);

    /**
     * Busca todas las distribuciones de una cuota.
     */
    List<DistribucionPago> findByCuotaId(Long cuotaId);
}