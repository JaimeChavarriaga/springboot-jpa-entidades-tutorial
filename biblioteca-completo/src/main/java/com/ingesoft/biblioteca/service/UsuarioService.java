package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.Usuario;
import com.ingesoft.biblioteca.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepo;

    public UsuarioService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public Usuario create(Usuario u) {
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());
        return usuarioRepo.save(u);
    }

    public Optional<Usuario> findById(Long id) { return usuarioRepo.findById(id); }
    public List<Usuario> findAll() { return usuarioRepo.findAll(); }

    public Usuario update(Long id, Usuario u) {
        Usuario existing = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario not found with id: " + id));
        existing.setNombre(u.getNombre());
        existing.setApellido(u.getApellido());
        existing.setEmail(u.getEmail());
        existing.setTelefono(u.getTelefono());
        existing.setUpdatedAt(Instant.now());
        return usuarioRepo.save(existing);
    }

    public void delete(Long id) { usuarioRepo.deleteById(id); }
}
