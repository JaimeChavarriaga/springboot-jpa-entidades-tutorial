package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario u) {
        Usuario created = usuarioService.create(u);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Usuario> list() { return usuarioService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> get(@PathVariable Long id) {
        return usuarioService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario u) {
        return ResponseEntity.ok(usuarioService.update(id, u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
