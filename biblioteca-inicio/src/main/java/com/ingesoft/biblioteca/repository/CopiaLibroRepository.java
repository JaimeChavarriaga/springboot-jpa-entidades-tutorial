package com.ingesoft.biblioteca.repository;

import com.ingesoft.biblioteca.model.CopiaLibro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CopiaLibroRepository extends JpaRepository<CopiaLibro, Long> {
    Optional<CopiaLibro> findByCodigoBarras(String codigoBarras);
}
