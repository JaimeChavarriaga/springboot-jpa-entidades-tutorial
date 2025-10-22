package com.ingesoft.biblioteca.init;

import com.ingesoft.biblioteca.model.Autor;
import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.model.Libro;
import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.model.Prestamo;
import com.ingesoft.biblioteca.repository.AutorRepository;
import com.ingesoft.biblioteca.repository.CopiaLibroRepository;
import com.ingesoft.biblioteca.repository.LibroRepository;
import com.ingesoft.biblioteca.repository.UsuarioRepository;
import com.ingesoft.biblioteca.repository.PrestamoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AutorRepository autorRepo;
    private final LibroRepository libroRepo;
    private final CopiaLibroRepository copiaRepo;
    private final UsuarioRepository usuarioRepo;
    private final PrestamoRepository prestamoRepo;

    public DataInitializer(AutorRepository autorRepo, LibroRepository libroRepo,
                           CopiaLibroRepository copiaRepo, UsuarioRepository usuarioRepo,
                           PrestamoRepository prestamoRepo) {
        this.autorRepo = autorRepo;
        this.libroRepo = libroRepo;
        this.copiaRepo = copiaRepo;
        this.usuarioRepo = usuarioRepo;
        this.prestamoRepo = prestamoRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!autorRepo.findAll().isEmpty()) return;

        Autor a1 = new Autor();
        a1.setNombre("Isabel");
        a1.setApellido("Allende");
        autorRepo.save(a1);

        Autor a2 = new Autor();
        a2.setNombre("Pablo");
        a2.setApellido("Neruda");
        autorRepo.save(a2);

        Libro l1 = new Libro();
        l1.setTitulo("La casa de los espíritus");
        l1.setIsbn("978-0140151717");
        l1.setAutor(a1);
        libroRepo.save(l1);

        Libro l2 = new Libro();
        l2.setTitulo("Veinte poemas de amor");
        l2.setIsbn("978-8437604940");
        l2.setAutor(a2);
        libroRepo.save(l2);

        CopiaLibro c1 = new CopiaLibro();
        c1.setCodigoBarras("BI-001");
        c1.setUbicacion("Estante 1");
        c1.setLibro(l1);
        copiaRepo.save(c1);

        CopiaLibro c2 = new CopiaLibro();
        c2.setCodigoBarras("BI-002");
        c2.setUbicacion("Estante 1");
        c2.setLibro(l1);
        copiaRepo.save(c2);

        Usuario u1 = new Usuario();
        u1.setNombre("Ana");
        u1.setApellido("Lopez");
        u1.setEmail("ana.lopez@example.com");
        usuarioRepo.save(u1);

        Prestamo p = new Prestamo();
        p.setCopia(c1);
        p.setUsuario(u1);
        p.setFechaPrestamo(Instant.now());
        p.setFechaVencimiento(Instant.now().plus(14, ChronoUnit.DAYS));
        prestamoRepo.save(p);
        c1.setDisponible(false);
        copiaRepo.save(c1);
    }
}
