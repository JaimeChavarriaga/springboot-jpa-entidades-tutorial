package com.ingesoft.biblioteca.web;

import com.ingesoft.biblioteca.model.Prestamo;
import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.service.PrestamoService;
import com.ingesoft.biblioteca.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {
    private final PrestamoService prestamoService;
    private final UsuarioService usuarioService;

    public PrestamoController(PrestamoService prestamoService, UsuarioService usuarioService) {
        this.prestamoService = prestamoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/prestar/{codigo}")
    public ResponseEntity<Prestamo> prestar(@PathVariable String codigo, @RequestParam Long usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario not found"));
        Prestamo p = prestamoService.prestar(codigo, usuario);
        return ResponseEntity.ok(p);
    }

    @PostMapping("/devolver/{codigo}")
    public ResponseEntity<Prestamo> devolver(@PathVariable String codigo) {
        Prestamo p = prestamoService.devolver(codigo);
        return ResponseEntity.ok(p);
    }
}
