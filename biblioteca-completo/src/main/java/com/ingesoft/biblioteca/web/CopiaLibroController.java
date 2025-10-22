package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.service.CopiaLibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/copias")
public class CopiaLibroController {
    private final CopiaLibroService copiaService;

    public CopiaLibroController(CopiaLibroService copiaService) { this.copiaService = copiaService; }

    @PostMapping
    public ResponseEntity<CopiaLibro> create(@RequestBody CopiaLibro c) { return ResponseEntity.ok(copiaService.create(c)); }

    @GetMapping
    public ResponseEntity<List<CopiaLibro>> list() { return ResponseEntity.ok(copiaService.findAll()); }
}
