package com.ingesoft.biblioteca.repository;

import com.ingesoft.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    Optional<Prestamo> findByCopiaIdAndFechaDevolucionIsNull(Long copiaId);
}
