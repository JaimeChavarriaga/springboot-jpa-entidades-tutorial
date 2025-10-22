package com.ingesoft.biblioteca.service;

import com.ingesoft.biblioteca.model.CopiaLibro;
import com.ingesoft.biblioteca.repository.CopiaLibroRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CopiaLibroService {
    private final CopiaLibroRepository copiaRepo;

    public CopiaLibroService(CopiaLibroRepository copiaRepo) { this.copiaRepo = copiaRepo; }

    public CopiaLibro create(CopiaLibro c) { c.setCreatedAt(Instant.now()); c.setUpdatedAt(Instant.now()); return copiaRepo.save(c); }
    public Optional<CopiaLibro> findByCodigo(String codigo) { return copiaRepo.findByCodigoBarras(codigo); }
    public List<CopiaLibro> findAll() { return copiaRepo.findAll(); }
}
