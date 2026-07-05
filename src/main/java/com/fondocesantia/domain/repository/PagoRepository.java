package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Pago.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Busca todos los pagos de un préstamo ordenados por fecha descendente.
     */
    List<Pago> findByPrestamoIdOrderByFechaPagoDesc(Long prestamoId);
}