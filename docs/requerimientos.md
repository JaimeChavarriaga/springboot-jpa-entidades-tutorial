# Requerimientos / Casos de uso

Documento: casos de uso para el software de biblioteca basado en el modelo de clases de `modelo-clases.md`.

Cada caso de uso incluye: propósito, actores, precondición, flujo principal, flujos alternativos (si aplica), postcondición y entidades involucradas.

---

## 1. Registrar Usuario
- Propósito: Crear un nuevo usuario en el sistema de la biblioteca.
- Actor: Bibliotecario (o auto-registro del usuario)
- Precondición: Datos básicos del usuario disponibles (nombre, apellido, email, teléfono).
- Flujo principal:
  1. El actor solicita crear usuario.
  2. El sistema valida los datos.
  3. El sistema crea el registro `Usuario` con `createdAt` y `updatedAt`.
  4. El sistema confirma la creación.
- Flujos alternativos:
  - 2a. Si el email ya existe, el sistema sugiere recuperación o actualización.
- Postcondición: Nuevo `Usuario` persistido.
- Entidades involucradas: `Usuario`.

---

## 2. Registrar Autor
- Propósito: Añadir un autor al catálogo.
- Actor: Bibliotecario
- Precondición: Datos del autor disponibles.
- Flujo principal:
  1. Actor solicita crear Autor.
  2. Sistema valida y persiste `Autor` (createdAt, updatedAt).
  3. Sistema confirma.
- Postcondición: Nuevo `Autor` creado.
- Entidades involucradas: `Autor`.

---

## 3. Registrar Libro
- Propósito: Añadir un libro (metadatos) al catálogo.
- Actor: Bibliotecario
- Precondición: Autor(es) existentes o crear autor simultáneamente.
- Flujo principal:
  1. Actor solicita crear `Libro` con título, ISBN, editorial, año y referencia a `Autor`.
  2. Sistema valida ISBN y autores.
  3. Sistema persiste `Libro` (createdAt, updatedAt).
  4. Sistema confirma.
- Flujos alternativos:
  - 2a. Si el ISBN ya existe, el sistema permite vincular nueva `CopiaLibro` en lugar de crear nuevo `Libro`.
- Postcondición: `Libro` registrado.
- Entidades involucradas: `Libro`, `Autor`.

---

## 4. Registrar Copia de Libro
- Propósito: Añadir una copia física del libro con código de barras.
- Actor: Bibliotecario
- Precondición: `Libro` ya registrado.
- Flujo principal:
  1. Actor solicita crear `CopiaLibro` con `codigoBarras`, ubicación y estado (`disponible`).
  2. Sistema valida unicidad de `codigoBarras`.
  3. Sistema persiste `CopiaLibro` con createdAt/updatedAt y la vincula al `Libro` correspondiente.
  4. Sistema confirma.
- Postcondición: `CopiaLibro` creada y disponible.
- Entidades involucradas: `CopiaLibro`, `Libro`.

---

## 5. Prestar Copia de Libro
- Propósito: Registrar el préstamo de una copia de libro a un usuario.
- Actor: Bibliotecario (o sistema de autoservicio con validación)
- Precondición: `Usuario` registrado; `CopiaLibro` existe y `disponible == true`.
- Flujo principal:
  1. Actor consulta y selecciona `CopiaLibro` por `codigoBarras`.
  2. Sistema verifica que `CopiaLibro.disponible == true`.
  3. Actor selecciona `Usuario` que solicita el préstamo.
  4. Sistema crea `Prestamo` con `fechaPrestamo` = hoy y `fechaVencimiento` calculada, `fechaDevolucion = null`, y createdAt/updatedAt.
  5. Sistema marca `CopiaLibro.disponible = false` y actualiza `updatedAt`.
  6. Sistema confirma el préstamo y entrega el comprobante.
- Flujos alternativos:
  - 2a. Si `disponible == false`, el sistema informa y puede ofrecer reserva o cola.
  - 3a. Si el usuario tiene sanciones o límites de préstamo, el sistema deniega y muestra motivo.
- Postcondición: `Prestamo` creado; `CopiaLibro` no disponible.
- Entidades involucradas: `Prestamo`, `CopiaLibro`, `Usuario`.

---

## 6. Devolver Copia de Libro
- Propósito: Registrar la devolución de una copia y actualizar el estado.
- Actor: Bibliotecario (o punto de devolución automático)
- Precondición: Existe un `Prestamo` activo para la `CopiaLibro` (fechaDevolucion == null).
- Flujo principal:
  1. Actor identifica `CopiaLibro` por `codigoBarras`.
  2. Sistema localiza el `Prestamo` activo asociado (fechaDevolucion == null).
  3. Actor registra la devolución; el sistema establece `Prestamo.fechaDevolucion = hoy` y actualiza `updatedAt`.
  4. Sistema marca `CopiaLibro.disponible = true` y actualiza `updatedAt`.
  5. Sistema calcula sanciones si la devolución es tardía y las registra.
  6. Sistema confirma la devolución.
- Flujos alternativos:
  - 2a. Si no hay `Prestamo` activo, el sistema puede crear una incidencia de devolución no registrada.
- Postcondición: `Prestamo` actualizado con `fechaDevolucion`; `CopiaLibro` disponible.
- Entidades involucradas: `Prestamo`, `CopiaLibro`, `Usuario`.

---

## 7. Consultar historial de préstamos de un Usuario
- Propósito: Mostrar todos los préstamos (histórico y activos) de un usuario.
- Actor: Bibliotecario, Usuario (autenticado)
- Precondición: `Usuario` identificado.
- Flujo principal:
  1. Actor solicita historial del `Usuario`.
  2. Sistema consulta la lista de `Prestamo` vinculados al `Usuario`.
  3. Sistema presenta préstamos con estado (activo/cerrado), fechas y copia asociada.
- Postcondición: Historial mostrado.
- Entidades involucradas: `Usuario`, `Prestamo`, `CopiaLibro`.

---

## 8. Buscar Libros
- Propósito: Localizar libros por título, autor, ISBN o palabra clave.
- Actor: Usuario, Bibliotecario
- Precondición: Catálogo de `Libro` poblado.
- Flujo principal:
  1. Actor introduce criterios de búsqueda.
  2. Sistema devuelve lista de `Libro` que coinciden y para cada libro muestra número de copias y disponibilidad (conteo de `CopiaLibro.disponible == true`).
- Postcondición: Resultados entregados.
- Entidades involucradas: `Libro`, `CopiaLibro`, `Autor`.

---

## 9. Reservar Copia (cola)
- Propósito: Permitir a un usuario reservar una copia cuando todas las copias están prestadas.
- Actor: Usuario
- Precondición: No hay `CopiaLibro` disponible para el `Libro` solicitado.
- Flujo principal (simplificado):
  1. Usuario solicita reserva para un `Libro`.
  2. Sistema añade al usuario a una cola de espera para el `Libro`.
  3. Cuando una `CopiaLibro` se devuelve, el sistema notifica al primer usuario en la cola.
- Entidades involucradas: `Usuario`, `Libro`, `CopiaLibro`, (posible entidad `Reserva` si se modela).

---

## 10. Generar Reportes básicos
- Propósito: Proveer estadísticas: préstamos activos, libros más prestados, usuarios con más multas, copias disponibles.
- Actor: Administrador / Bibliotecario
- Flujo principal:
  1. Actor solicita un reporte (intervalo de fechas opcional).
  2. Sistema calcula y presenta métricas basadas en `Prestamo`, `CopiaLibro`, `Libro`, `Usuario`.
- Entidades involucradas: `Prestamo`, `CopiaLibro`, `Libro`, `Usuario`.

---

## Notas y consideraciones
- Todos los registros deben mantener `createdAt` y `updatedAt`.
- Se recomienda modelar una entidad `Reserva` o `ColaReserva` si se implementan reservas.
- Para auditoría, considerar registrar quién realiza cada acción (actor) y conservar logs separados.
- Políticas de retención de datos y privacidad deben definirse (por ejemplo, anonimizar datos personales si se publican reportes).

---

Si quieres, puedo:
- Añadir diagramas de secuencia para los casos de uso principales (Préstamo, Devolución).
- Añadir plantillas de mensajes/errores y campos obligatorios.
- Crear un archivo de entidad `Reserva` en el modelo y actualizar el diagrama Mermaid.

Terminé la creación; ahora verificaré y marcaré el todo como completado.