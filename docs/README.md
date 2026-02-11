Proyecto admin.nca — Módulo Gestión de Rutas (CRUD de Bus)

Descripción

Este microservicio expone endpoints REST para la gestión de autobuses (Bus) que serán consumidos por la consola administrativa de NCA.

Estado actual

- Implementado modelo `Bus` con validaciones básicas.
- Repositorio en memoria (`InMemoryBusRepository`) para desarrollo y pruebas.
- Servicio `BusService` con lógica CRUD.
- Controlador REST `BusController` exponiendo endpoints CRUD en `/api/buses`.
- Manejador global de excepciones para respuestas 404 (recurso no encontrado) y errores de validación 400.
- `application.yaml` configurado para excluir la auto-configuración de DataSource/JPA (la aplicación puede arrancar sin BD por ahora).
- Empaquetado: `target/admin-0.0.1-SNAPSHOT.jar` disponible tras el build.

Entidad Bus (estructura JSON)

{
  "id": "string (generado)",
  "placa": "ABC-123",
  "capacidad": 40,
  "marca": "Marca",
  "modelo": "Modelo X",
  "fechaRevisionTecnica": "2026-01-01",
  "fechaSeguroObligatorio": "2026-06-01",
  "tipoMotor": "combustible|hibrido|electrico|otro",
  "tipoMotorOtro": "opcional si tipoMotor=otro",
  "estado": "activo|mantenimiento|inactivo"
}

Endpoints

- Listar buses
  - GET /api/buses
  - Respuesta: 200 OK, lista de `Bus` (JSON array)

- Obtener bus por id
  - GET /api/buses/{id}
  - Respuesta: 200 OK (Bus) o 404 Not Found

- Crear bus
  - POST /api/buses
  - Body: `Bus` (sin `id` o con `id` null)
  - Respuesta: 201 Created, Location header con `/api/buses/{id}` y body con el `Bus` creado

- Actualizar bus
  - PUT /api/buses/{id}
  - Body: `Bus` (los campos se reemplazan por los del body)
  - Respuesta: 200 OK (Bus actualizado) o 404 Not Found

- Eliminar bus
  - DELETE /api/buses/{id}
  - Respuesta: 204 No Content o 404 Not Found

Ejemplos (curl)

Crear un bus:

```bash
curl -X POST http://localhost:8080/api/buses \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC-123",
    "capacidad": 40,
    "marca": "Mercedes",
    "modelo": "Sprinter",
    "fechaRevisionTecnica": "2026-01-01",
    "fechaSeguroObligatorio": "2026-06-01",
    "tipoMotor": "combustible",
    "estado": "activo"
  }'
```

Listar todos los buses:

```bash
curl http://localhost:8080/api/buses
```

Obtener un bus por id:

```bash
curl http://localhost:8080/api/buses/{id}
```

Actualizar un bus:

```bash
curl -X PUT http://localhost:8080/api/buses/{id} \
  -H "Content-Type: application/json" \
  -d '{ "placa": "ABC-123", "capacidad": 45, "marca": "MarcaX", "estado": "mantenimiento", "tipoMotor": "hibrido" }'
```

Eliminar un bus:

```bash
curl -X DELETE http://localhost:8080/api/buses/{id}
```

Ejecutar la aplicación localmente

1) Compilar y empaquetar (desde la raíz del proyecto):

```bash
# en Windows (cmd.exe)
mvnw.cmd -DskipTests=true package
# o en Unix
./mvnw -DskipTests=true package
```

2) Ejecutar el JAR:

```bash
java -jar target/admin-0.0.1-SNAPSHOT.jar
```

Notas y siguientes pasos recomendados

- Actualmente la persistencia es en memoria; para producción deberías:
  - Sustituir `InMemoryBusRepository` por una implementación basada en JPA (o Repositorio reactivo) y configurar la base de datos en `application.yaml`.
  - Añadir validaciones más estrictas y DTOs de entrada/salida si se requieren campos específicos/transformaciones.
  - Añadir autenticación/autorización (el proyecto ya tiene starter-security en el `pom.xml`, pero no está configurado).
  - Añadir pruebas unitarias y de integración para `BusService` y `BusController`.

Si quieres, puedo:
- Añadir persistencia JPA y la entidad `@Entity` y migraciones iniciales (Flyway/Liquibase).
- Añadir tests unitarios para `BusService` y tests de integración para `BusController`.
- Configurar seguridad básica (JWT/OAuth2) para los endpoints.


