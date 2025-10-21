package com.ingesoft.biblioteca.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant fechaPrestamo;
    private Instant fechaVencimiento;
    private Instant fechaDevolucion;
    private String notas;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private CopiaLibro copia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(Instant fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public Instant getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Instant fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Instant getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Instant fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public CopiaLibro getCopia() { return copia; }
    public void setCopia(CopiaLibro copia) { this.copia = copia; }
}
