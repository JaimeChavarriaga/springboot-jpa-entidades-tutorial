Title: Codelab — Biblioteca con Spring Boot
Summary: Tutorial paso a paso para crear una aplicación de biblioteca con Spring Boot usando JPA, basado en un modelo de clases y casos de uso (préstamo y devolución).
Author: 
Tags: java, spring, jpa, tutorial, biblioteca

# Objetivo

En este codelab aprenderás a crear una aplicación básica de biblioteca con Spring Boot y Spring Data JPA. Cubriremos:

- Definición del modelo de dominio (Autor, Libro, CopiaLibro, Usuario, Prestamo).
- Implementación de las entidades JPA.
- Repositorios Spring Data JPA.
- Servicios que implementan los casos de uso (prestar y devolver).
- Pruebas unitarias para servicios.

Requisitos previos:
- Java 17+ instalado
- Maven o Gradle
- IDE (IntelliJ, VS Code)

---

# 1. Entidades y modelo de clases

El modelo de clases está en `docs/modelo_clases.mmd`. Resume las entidades y relaciones: Autor (1..* Libro), Libro (1..* CopiaLibro), CopiaLibro, Usuario, Prestamo. Cada entidad mantiene `createdAt` y `updatedAt`.

## 1.1 Explicación de cada clase

A continuación se presenta una tabla resumen con las responsabilidades principales, campos clave y relaciones de cada entidad del dominio:

| Clase | Responsabilidad | Campos clave | Relaciones |
|---|---|---|---|
| Autor | Representa la persona que escribió libros; contiene metadatos del autor. | `id`, `nombre`, `apellido`, `fechaNacimiento`, `createdAt`, `updatedAt` | 1 — * `Libro` (un autor puede escribir muchos libros) |
| Libro | Metadatos bibliográficos del libro. | `id`, `titulo`, `isbn`, `anioPublicacion`, `editorial`, `createdAt`, `updatedAt` | * — 1 `Autor`; 1 — * `CopiaLibro` (un libro tiene varias copias físicas) |
| CopiaLibro | Representa una copia física identificada por código de barras y su estado. | `id`, `codigoBarras`, `ubicacion`, `disponible`, `createdAt`, `updatedAt` | * — 1 `Libro`; 1 — 0..1 `Prestamo` (una copia puede estar asociada a un préstamo activo) |
| Usuario | Representa la persona registrada en la biblioteca que puede solicitar préstamos. | `id`, `nombre`, `apellido`, `email`, `telefono`, `fechaRegistro`, `createdAt`, `updatedAt` | 1 — * `Prestamo` (un usuario puede tener varios préstamos) |
| Prestamo | Registra el acto de prestar una copia a un usuario y sus fechas (préstamo, vencimiento, devolución). | `id`, `fechaPrestamo`, `fechaVencimiento`, `fechaDevolucion`, `notas`, `createdAt`, `updatedAt` | * — 1 `Usuario`; * — 1 `CopiaLibro` |


---

# 2. Código de las entidades (ejemplos)

A continuación se muestran implementaciones Java simplificadas para cada entidad usando JPA y Lombok (opcional). Ajusta paquetes según tu proyecto.

## 2.1 `Autor` (Java)

```java
package com.example.biblioteca.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Autor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private Instant fechaNacimiento;
    private Instant createdAt;
    private Instant updatedAt;

    // getters/setters, constructores
}
```

## 2.2 `Libro` (Java)

```java
package com.example.biblioteca.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
public class Libro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
    private String editorial;
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne
    private Autor autor;

    @OneToMany(mappedBy = "libro")
    private List<CopiaLibro> copias;

    // getters/setters
}
```

## 2.3 `CopiaLibro` (Java)

```java
package com.example.biblioteca.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class CopiaLibro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String codigoBarras;
    private String ubicacion;
    private Boolean disponible = true;
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne
    private Libro libro;

    // getters/setters
}
```

## 2.4 `Usuario` (Java)

```java
package com.example.biblioteca.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Instant fechaRegistro;
    private Instant createdAt;
    private Instant updatedAt;

    // getters/setters
}
```

## 2.5 `Prestamo` (Java)

```java
package com.example.biblioteca.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Prestamo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant fechaPrestamo;
    private Instant fechaVencimiento;
    private Instant fechaDevolucion;
    private String notas;
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private CopiaLibro copia;

    // getters/setters
}
```

---

# 3. Repositorios

Usaremos Spring Data JPA para los repositorios. Ejemplos:

```java
package com.example.biblioteca.repository;

import com.example.biblioteca.model.CopiaLibro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CopiaLibroRepository extends JpaRepository<CopiaLibro, Long> {
    Optional<CopiaLibro> findByCodigoBarras(String codigoBarras);
}
```

```java
package com.example.biblioteca.repository;

import com.example.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    Optional<Prestamo> findByCopiaIdAndFechaDevolucionIsNull(Long copiaId);
}
```

Repositorios para `Libro`, `Autor`, `Usuario` serían similares (extender `JpaRepository<T, Long>`).

---

# 4. Casos de uso (listado)

Listado principal de casos de uso (resumido):
- Registrar Usuario
- Registrar Autor
- Registrar Libro
- Registrar Copia de Libro
- Prestar Copia de Libro (detallado abajo)
- Devolver Copia de Libro (detallado abajo)
- Consultar historial de préstamos
- Buscar libros
- Reservar copia (cola)
- Generar reportes

## 4.1 Prestar (explicación)

El flujo principal para prestar:
1. Identificar `CopiaLibro` por `codigoBarras`.
2. Verificar `CopiaLibro.disponible == true`.
3. Validar que `Usuario` puede tomar prestado (sanciones, límite).
4. Crear `Prestamo` con `fechaPrestamo` y `fechaVencimiento` y `fechaDevolucion = null`.
5. Marcar `CopiaLibro.disponible = false`.
6. Persistir cambios y devolver el comprobante.

Entidades clave: `CopiaLibro`, `Prestamo`, `Usuario`.

## 4.2 Devolver (explicación)

Flujo principal para devolver:
1. Identificar `CopiaLibro` por `codigoBarras`.
2. Buscar `Prestamo` activo para la copia (fechaDevolucion == null).
3. Establecer `fechaDevolucion = hoy`.
4. Marcar `CopiaLibro.disponible = true`.
5. Calcular e imputar sanciones si corresponde.
6. Persistir cambios y generar comprobante.

Entidades clave: `Prestamo`, `CopiaLibro`, `Usuario`.

---

# 5. Código de servicios (ejemplos)

Servicio simplificado para préstamos:

```java
package com.example.biblioteca.service;

import com.example.biblioteca.model.CopiaLibro;
import com.example.biblioteca.model.Prestamo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.CopiaLibroRepository;
import com.example.biblioteca.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class PrestamoService {

    private final CopiaLibroRepository copiaRepo;
    private final PrestamoRepository prestRepo;

    public PrestamoService(CopiaLibroRepository copiaRepo, PrestamoRepository prestRepo) {
        this.copiaRepo = copiaRepo;
        this.prestRepo = prestRepo;
    }

    @Transactional
    public Prestamo prestar(String codigoBarras, Usuario usuario) {
        CopiaLibro copia = copiaRepo.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new IllegalArgumentException("Copia no encontrada"));
        if (!Boolean.TRUE.equals(copia.getDisponible())) {
            throw new IllegalStateException("Copia no disponible");
        }
        // validar sanciones/limites del usuario (omitir detalle)

        Prestamo p = new Prestamo();
        p.setCopia(copia);
        p.setUsuario(usuario);
        p.setFechaPrestamo(Instant.now());
        p.setFechaVencimiento(Instant.now().plus(14, ChronoUnit.DAYS));
        prestRepo.save(p);

        copia.setDisponible(false);
        copiaRepo.save(copia);

        return p;
    }

    @Transactional
    public Prestamo devolver(String codigoBarras) {
        CopiaLibro copia = copiaRepo.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new IllegalArgumentException("Copia no encontrada"));
        Prestamo p = prestRepo.findByCopiaIdAndFechaDevolucionIsNull(copia.getId())
                .orElseThrow(() -> new IllegalStateException("No hay préstamo activo para esta copia"));
        p.setFechaDevolucion(Instant.now());
        prestRepo.save(p);

        copia.setDisponible(true);
        copiaRepo.save(copia);

        // calcular sanciones si corresponde (omitir detalle)
        return p;
    }
}
```

---

# 6. Pruebas unitarias (ejemplo)

A continuación un ejemplo con JUnit y Mockito para `PrestamoService`.

```java
package com.example.biblioteca.service;

import com.example.biblioteca.model.CopiaLibro;
import com.example.biblioteca.model.Prestamo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.CopiaLibroRepository;
import com.example.biblioteca.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrestamoServiceTest {
    private CopiaLibroRepository copiaRepo;
    private PrestamoRepository prestRepo;
    private PrestamoService service;

    @BeforeEach
    void setUp() {
        copiaRepo = Mockito.mock(CopiaLibroRepository.class);
        prestRepo = Mockito.mock(PrestamoRepository.class);
        service = new PrestamoService(copiaRepo, prestRepo);
    }

    @Test
    void prestarExitoso() {
        CopiaLibro copia = new CopiaLibro();
        copia.setId(1L);
        copia.setCodigoBarras("ABC123");
        copia.setDisponible(true);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(copiaRepo.findByCodigoBarras("ABC123")).thenReturn(Optional.of(copia));
        when(prestRepo.save(any(Prestamo.class))).thenAnswer(i -> i.getArgument(0));
        when(copiaRepo.save(any(CopiaLibro.class))).thenAnswer(i -> i.getArgument(0));

        Prestamo p = service.prestar("ABC123", usuario);

        assertNotNull(p);
        verify(prestRepo, times(1)).save(any(Prestamo.class));
        verify(copiaRepo, times(1)).save(any(CopiaLibro.class));
        assertFalse(copia.getDisponible());
    }

    @Test
    void devolverExitoso() {
        CopiaLibro copia = new CopiaLibro();
        copia.setId(1L);
        copia.setCodigoBarras("ABC123");
        copia.setDisponible(false);

        Prestamo p = new Prestamo();
        p.setId(1L);
        p.setCopia(copia);
        p.setFechaPrestamo(Instant.now());

        when(copiaRepo.findByCodigoBarras("ABC123")).thenReturn(Optional.of(copia));
        when(prestRepo.findByCopiaIdAndFechaDevolucionIsNull(1L)).thenReturn(Optional.of(p));
        when(prestRepo.save(any(Prestamo.class))).thenAnswer(i -> i.getArgument(0));
        when(copiaRepo.save(any(CopiaLibro.class))).thenAnswer(i -> i.getArgument(0));

        Prestamo res = service.devolver("ABC123");

        assertNotNull(res.getFechaDevolucion());
        assertTrue(copia.getDisponible());
        verify(prestRepo, times(1)).save(any(Prestamo.class));
    }
}
```

---

# 7. Resumen y siguientes pasos

En este codelab vimos:

- Modelado del dominio para una biblioteca (Autor, Libro, CopiaLibro, Usuario, Prestamo).
- Implementación de entidades JPA y repositorios Spring Data JPA.
- Servicios con lógica de negocio para prestar y devolver copias.
- Pruebas unitarias con Mockito/JUnit.

Siguientes pasos recomendados:
- Añadir seguridad (Spring Security) y autenticación de usuarios.
- Implementar control de transacciones y manejo de excepciones robusto.
- Implementar gestión de reservas (entidad `Reserva`).
- Añadir documentación de API (OpenAPI/Swagger) y endpoints REST/GraphQL.

---

# Notas para publicar con claat

- `claat` acepta Markdown con algunos metadatos al inicio. El archivo creado incluye `Title`, `Summary` y `Tags`.
- Para convertir a codelab con `claat`, instala claat y ejecuta:

```bash
# instalar claat (si no lo tienes)
go install github.com/googlecodelabs/tools/claat@latest

# generar el codelab
claat export docs/codelab-biblioteca.md
```

Ajusta el frontmatter y las secciones según prefieras para la publicación.
