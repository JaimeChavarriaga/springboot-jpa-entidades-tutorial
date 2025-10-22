package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.model.Prestamo;
import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.repository.CopiaLibroRepository;
import com.ingesoft.biblioteca.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PrestamoService {

    private final CopiaLibroRepository copiaRepo;
    private final PrestamoRepository prestRepo;

        public PrestamoService(CopiaLibroRepository copiaRepo, PrestamoRepository prestRepo) {
            this.copiaRepo = copiaRepo;
            this.prestRepo = prestRepo;
        }

        @Transactional
        public Prestamo prestar(String codigoBarras, Usuario usuario) {
            CopiaLibro copia = copiaRepo.findByCodigoBarras(codigoBarras)
                    .orElseThrow(() -> new IllegalArgumentException("Copia no encontrada"));
            if (!Boolean.TRUE.equals(copia.getDisponible())) {
                throw new IllegalStateException("Copia no disponible");
            }

            Prestamo p = new Prestamo();
            p.setCopia(copia);
            p.setUsuario(usuario);
            p.setFechaPrestamo(Instant.now());
            p.setFechaVencimiento(Instant.now().plus(14, ChronoUnit.DAYS));
            prestRepo.save(p);

            copia.setDisponible(false);
            copiaRepo.save(copia);

            return p;
        }

        @Transactional
        public Prestamo devolver(String codigoBarras) {
            CopiaLibro copia = copiaRepo.findByCodigoBarras(codigoBarras)
                    .orElseThrow(() -> new IllegalArgumentException("Copia no encontrada"));
            Prestamo p = prestRepo.findByCopiaIdAndFechaDevolucionIsNull(copia.getId())
                    .orElseThrow(() -> new IllegalStateException("No hay préstamo activo para esta copia"));
            p.setFechaDevolucion(Instant.now());
            prestRepo.save(p);

            copia.setDisponible(true);
            copiaRepo.save(copia);

            return p;
        }
}
