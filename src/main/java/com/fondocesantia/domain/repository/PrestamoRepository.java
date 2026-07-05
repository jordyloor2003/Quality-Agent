package com.fondocesantia.domain.repository;

import com.fondocesantia.domain.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Prestamo.
 */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    /**
     * Busca un préstamo por su número.
     */
    Optional<Prestamo> findByNumeroPrestamo(String numeroPrestamo);

    /**
     * Verifica si existe un préstamo con el número dado.
     */
    boolean existsByNumeroPrestamo(String numeroPrestamo);
}