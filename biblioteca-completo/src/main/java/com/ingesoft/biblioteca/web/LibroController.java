package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.Libro;
import com.ingesoft.biblioteca.service.LibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) { this.libroService = libroService; }

    @PostMapping
    public ResponseEntity<Libro> create(@RequestBody Libro l) { Libro c = libroService.create(l); return ResponseEntity.created(URI.create("/api/libros/" + c.getId())).body(c); }

    @GetMapping
    public List<Libro> list() { return libroService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> get(@PathVariable Long id) { return ResponseEntity.ok(libroService.findById(id)); }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> update(@PathVariable Long id, @RequestBody Libro l) { return ResponseEntity.ok(libroService.update(id, l)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { libroService.delete(id); return ResponseEntity.noContent().build(); }
}
