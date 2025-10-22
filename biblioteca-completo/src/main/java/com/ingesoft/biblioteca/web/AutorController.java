package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.Autor;
import com.ingesoft.biblioteca.service.AutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {
    private final AutorService autorService;

    public AutorController(AutorService autorService) { this.autorService = autorService; }

    @PostMapping
    public ResponseEntity<Autor> create(@RequestBody Autor a) { Autor c = autorService.create(a); return ResponseEntity.created(URI.create("/api/autores/" + c.getId())).body(c); }

    @GetMapping
    public List<Autor> list() { return autorService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> get(@PathVariable Long id) { return ResponseEntity.ok(autorService.findById(id)); }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> update(@PathVariable Long id, @RequestBody Autor a) {
        Autor updated = autorService.update(id, a);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { autorService.delete(id); return ResponseEntity.noContent().build(); }
}
