# Diagramas de secuencia

Este documento contiene diagramas de secuencia (Mermaid) para los casos de uso "Préstamo" y "Devolución" basados en el modelo de clases del sistema de biblioteca.

---

## Préstamo de copia de libro

```mermaid
sequenceDiagram
    autonumber
    participant Usuario
    participant Bibliotecario
    participant Sistema
    participant Copia as CopiaLibro
    participant Prest as Prestamo

    Usuario->>Bibliotecario: Solicita préstamo (código de barras)
    Bibliotecario->>Sistema: Buscar CopiaLibro por códigoBarras
    Sistema->>Copia: consultar disponibilidad
    Copia-->>Sistema: disponible = true
    Sistema->>Bibliotecario: mostrar información de copia
    Bibliotecario->>Sistema: Seleccionar Usuario y confirmar préstamo
    Sistema->>Sistema: validar límites y sanciones del Usuario
    alt Usuario permitido
        Sistema->>Prest: crear Prestamo (fechaPrestamo, fechaVencimiento)
        Sistema->>Copia: actualizar disponible = false
        Sistema-->>Bibliotecario: confirmar préstamo
        Bibliotecario-->>Usuario: entregar copia y comprobante
    else Usuario no permitido
        Sistema-->>Bibliotecario: denegar préstamo (motivo)
        Bibliotecario-->>Usuario: informar denegación
    end
```

---

## Devolución de copia de libro

```mermaid
sequenceDiagram
    autonumber
    participant Usuario
    participant Bibliotecario
    participant Sistema
    participant Copia as CopiaLibro
    participant Prest as Prestamo

    Usuario->>Bibliotecario: Entrega Copia (código de barras)
    Bibliotecario->>Sistema: Buscar CopiaLibro por códigoBarras
    Sistema->>Prest: localizar Prestamo activo (fechaDevolucion == null)
    alt Prestamo encontrado
        Bibliotecario->>Sistema: registrar devolución
        Sistema->>Prest: set fechaDevolucion = hoy
        Sistema->>Copia: set disponible = true
        Sistema->>Sistema: calcular sanciones si fechaDevolucion > fechaVencimiento
        Sistema-->>Bibliotecario: confirmar devolución y mostrar sanciones
        Bibliotecario-->>Usuario: entrega comprobante de devolución
    else No hay préstamo activo
        Sistema-->>Bibliotecario: generar incidencia (devolución no registrada)
        Bibliotecario-->>Usuario: solicitar más información / crear incidencia
    end
```

---

Notas:
- Los diagramas usan participantes simplificados (`Sistema` agrupa la lógica de negocio y persistencia).
- Si quieres, puedo separar `Sistema` en `ServicioPrestamo`, `RepositorioCopia`, y `ServicioNotificaciones` para mayor granularidad.
