package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.SaldoAFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para la entidad SaldoAFavor.
 */
@Repository
public interface SaldoAFavorRepository extends JpaRepository<SaldoAFavor, Long> {

    /**
     * Busca todos los saldos a favor de un cliente que no han sido utilizados.
     */
    @Query("SELECT s FROM SaldoAFavor s WHERE s.clienteId = :clienteId AND s.utilizado = false")
    List<SaldoAFavor> findSaldoAFavorNoUtilizadoByClienteId(@Param("clienteId") Long clienteId);

    /**
     * Calcula el total de saldo a favor de un cliente.
     */
    @Query("SELECT COALESCE(SUM(s.monto), 0) FROM SaldoAFavor s WHERE s.clienteId = :clienteId AND s.utilizado = false")
    BigDecimal calculateTotalSaldoAFavor(@Param("clienteId") Long clienteId);
}