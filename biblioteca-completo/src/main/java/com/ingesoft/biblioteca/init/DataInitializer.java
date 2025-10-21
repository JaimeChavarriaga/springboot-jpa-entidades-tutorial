package com.ingesoft.biblioteca.init;

import com.ingesoft.biblioteca.model.Autor;
import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.model.Libro;
import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.model.Prestamo;
import com.ingesoft.biblioteca.service.AutorService;
import com.ingesoft.biblioteca.service.CopiaLibroService;
import com.ingesoft.biblioteca.service.LibroService;
import com.ingesoft.biblioteca.service.UsuarioService;
import com.ingesoft.biblioteca.service.PrestamoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AutorService autorService;
    private final LibroService libroService;
    private final CopiaLibroService copiaService;
    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;

    public DataInitializer(AutorService autorService, LibroService libroService,
                           CopiaLibroService copiaService, UsuarioService usuarioService,
                           PrestamoService prestamoService) {
        this.autorService = autorService;
        this.libroService = libroService;
        this.copiaService = copiaService;
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Evitar insertar datos repetidos en ejecuciones múltiples comprobando existencia mínima
        if (!autorService.findAll().isEmpty()) {
            return;
        }

        List<Autor> autores = new ArrayList<>();

        Autor a1 = new Autor();
        a1.setNombre("Gabriel");
        a1.setApellido("García Márquez");
    autorService.create(a1);
        autores.add(a1);

        Autor a2 = new Autor();
        a2.setNombre("Julio");
        a2.setApellido("Cortázar");
    autorService.create(a2);
        autores.add(a2);

        // Libros
        Libro l1 = new Libro();
        l1.setTitulo("Cien años de soledad");
        l1.setIsbn("978-0307474728");
        l1.setAutor(a1);
    libroService.create(l1);

        Libro l2 = new Libro();
        l2.setTitulo("Rayuela");
        l2.setIsbn("978-9500393088");
        l2.setAutor(a2);
    libroService.create(l2);

        // Copias
        CopiaLibro c1 = new CopiaLibro();
        c1.setCodigoBarras("C-1001");
        c1.setUbicacion("Anaquel A1");
        c1.setLibro(l1);
    copiaService.create(c1);

        CopiaLibro c2 = new CopiaLibro();
        c2.setCodigoBarras("C-1002");
        c2.setUbicacion("Anaquel A2");
        c2.setLibro(l1);
    copiaService.create(c2);

        CopiaLibro c3 = new CopiaLibro();
        c3.setCodigoBarras("C-2001");
        c3.setUbicacion("Anaquel B1");
        c3.setLibro(l2);
    copiaService.create(c3);

        // Usuarios
        Usuario u1 = new Usuario();
        u1.setNombre("Juan");
        u1.setApellido("Pérez");
        u1.setEmail("juan.perez@example.com");
    usuarioService.create(u1);

        Usuario u2 = new Usuario();
        u2.setNombre("María");
        u2.setApellido("Gómez");
        u2.setEmail("maria.gomez@example.com");
    usuarioService.create(u2);

        // Préstamos de ejemplo
        try {
            Prestamo p1 = prestamoService.prestar("C-1001", u1);
            // crear otro préstamo
            Prestamo p2 = prestamoService.prestar("C-2001", u2);
        } catch (Exception ex) {
            // No interrumpir el arranque si algo falla; registrar silenciosamente
            System.err.println("No se pudieron crear préstamos de ejemplo: " + ex.getMessage());
        }
    }
}
