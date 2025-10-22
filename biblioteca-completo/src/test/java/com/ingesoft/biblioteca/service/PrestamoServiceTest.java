package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.model.Prestamo;
import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.repository.CopiaLibroRepository;
import com.ingesoft.biblioteca.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrestamoServiceTest {
    private CopiaLibroRepository copiaRepo;
    private PrestamoRepository prestRepo;
    private PrestamoService service;

    @BeforeEach
    void setUp() {
        copiaRepo = Mockito.mock(CopiaLibroRepository.class);
        prestRepo = Mockito.mock(PrestamoRepository.class);
        service = new PrestamoService(copiaRepo, prestRepo);
    }

    @Test
    void prestarExitoso() {
        CopiaLibro copia = new CopiaLibro();
        copia.setId(1L);
        copia.setCodigoBarras("ABC123");
        copia.setDisponible(true);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(copiaRepo.findByCodigoBarras("ABC123")).thenReturn(Optional.of(copia));
        when(prestRepo.save(any(Prestamo.class))).thenAnswer(i -> i.getArgument(0));
        when(copiaRepo.save(any(CopiaLibro.class))).thenAnswer(i -> i.getArgument(0));

        Prestamo p = service.prestar("ABC123", usuario);

        assertNotNull(p);
        verify(prestRepo, times(1)).save(any(Prestamo.class));
        verify(copiaRepo, times(1)).save(any(CopiaLibro.class));
        assertFalse(copia.getDisponible());
    }

    @Test
    void prestarNoExistente() {
        when(copiaRepo.findByCodigoBarras("NOEX"))
                .thenReturn(Optional.empty());
        Usuario u = new Usuario();
        assertThrows(IllegalArgumentException.class, () -> service.prestar("NOEX", u));
    }

    @Test
    void devolverExitoso() {
        CopiaLibro copia = new CopiaLibro();
        copia.setId(1L);
        copia.setCodigoBarras("ABC123");
        copia.setDisponible(false);

        Prestamo p = new Prestamo();
        p.setId(1L);
        p.setCopia(copia);
        p.setFechaPrestamo(Instant.now());

        when(copiaRepo.findByCodigoBarras("ABC123")).thenReturn(Optional.of(copia));
        when(prestRepo.findByCopiaIdAndFechaDevolucionIsNull(1L)).thenReturn(Optional.of(p));
        when(prestRepo.save(any(Prestamo.class))).thenAnswer(i -> i.getArgument(0));
        when(copiaRepo.save(any(CopiaLibro.class))).thenAnswer(i -> i.getArgument(0));

        Prestamo res = service.devolver("ABC123");

        assertNotNull(res.getFechaDevolucion());
        assertTrue(copia.getDisponible());
        verify(prestRepo, times(1)).save(any(Prestamo.class));
    }

    @Test
    void devolverNoActiva() {
        CopiaLibro copia = new CopiaLibro();
        copia.setId(2L);
        copia.setCodigoBarras("NOACT");
        copia.setDisponible(false);

        when(copiaRepo.findByCodigoBarras("NOACT")).thenReturn(Optional.of(copia));
        when(prestRepo.findByCopiaIdAndFechaDevolucionIsNull(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.devolver("NOACT"));
    }
}
