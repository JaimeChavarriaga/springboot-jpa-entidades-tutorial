package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.Autor;
import com.ingesoft.biblioteca.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepo;

    public AutorService(AutorRepository autorRepo) { this.autorRepo = autorRepo; }

    public Autor create(Autor a) { a.setCreatedAt(Instant.now()); a.setUpdatedAt(Instant.now()); return autorRepo.save(a); }
    public List<Autor> findAll() { return autorRepo.findAll(); }
    public Autor findById(Long id) { return autorRepo.findById(id).orElseThrow(() -> new RuntimeException("Autor not found")); }

    public Autor update(Long id, Autor a) {
        Autor existing = findById(id);
        // keep id and createdAt, update other properties
        existing.setNombre(a.getNombre());
        existing.setApellido(a.getApellido());
        existing.setFechaNacimiento(a.getFechaNacimiento());
        existing.setUpdatedAt(Instant.now());
        return autorRepo.save(existing);
    }

    public void delete(Long id) {
        autorRepo.deleteById(id);
    }
}
