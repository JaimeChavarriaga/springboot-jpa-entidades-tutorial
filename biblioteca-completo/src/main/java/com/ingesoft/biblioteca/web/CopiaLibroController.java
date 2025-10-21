package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.service.CopiaLibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/copias")
public class CopiaLibroController {
    private final CopiaLibroService copiaService;

    public CopiaLibroController(CopiaLibroService copiaService) { this.copiaService = copiaService; }

    @PostMapping
    public ResponseEntity<CopiaLibro> create(@RequestBody CopiaLibro c) { CopiaLibro created = copiaService.create(c); return ResponseEntity.created(URI.create("/api/copias/" + created.getId())).body(created); }

    @GetMapping
    public List<CopiaLibro> list() { return copiaService.findAll(); }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CopiaLibro> getByCodigo(@PathVariable String codigo) { return copiaService.findByCodigo(codigo).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }

    @PutMapping("/{id}")
    public ResponseEntity<CopiaLibro> update(@PathVariable Long id, @RequestBody CopiaLibro c) { return ResponseEntity.ok(copiaService.update(id, c)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { copiaService.delete(id); return ResponseEntity.noContent().build(); }
}
