package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.Libro;
import com.ingesoft.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LibroService {
    private final LibroRepository libroRepo;

    public LibroService(LibroRepository libroRepo) { this.libroRepo = libroRepo; }

    public Libro create(Libro l) { l.setCreatedAt(Instant.now()); l.setUpdatedAt(Instant.now()); return libroRepo.save(l); }
    public List<Libro> findAll() { return libroRepo.findAll(); }

    public Libro findById(Long id) { return libroRepo.findById(id).orElseThrow(() -> new RuntimeException("Libro not found")); }

    public Libro update(Long id, Libro l) {
        Libro existing = findById(id);
        existing.setTitulo(l.getTitulo());
        existing.setIsbn(l.getIsbn());
        existing.setEditorial(l.getEditorial());
        existing.setUpdatedAt(Instant.now());
        return libroRepo.save(existing);
    }

    public void delete(Long id) { libroRepo.deleteById(id); }
}
