# Fix para Error 500 en /v3/api-docs

## Problema Identificado

El endpoint `/v3/api-docs` retornaba error 500 debido a problemas en la generación de la documentación OpenAPI (Swagger). El generador de OpenAPI (springdoc-openapi) tiene dificultades al procesar tipos de retorno complejos sin anotaciones explícitas.

### Causas Raíz

Los siguientes controladores tenían métodos que retornaban tipos genéricos sin documentación:

1. **RealtimeController**:
   - `GET /api/realtime/positions` → `Map<String, Object>`
   - `GET /api/realtime/positions/geojson` → `Map<String, Object>`
   - `POST /api/realtime/positions` → `ResponseEntity<?>`
   - `POST /api/realtime/positions/feature` → `ResponseEntity<?>`

2. **NovedadController**:
   - `PUT /api/novedades/{id}/approve` → `Map<String, Object>`
   - `PUT /api/novedades/{id}/reject` → `Map<String, Object>`

3. **ParadaTemporalController**:
   - `POST /api/rutas/{rutaId}/paradas-temporales` → `Map<String, Object>`
   - `GET /api/rutas/{rutaId}/paradas-temporales` → `Map<String, Object>`
   - `PUT /api/rutas/{rutaId}/paradas-temporales/{paradaId}/approve` → `Map<String, Object>`
   - `PUT /api/rutas/{rutaId}/paradas-temporales/{paradaId}/reject` → `Map<String, Object>`

4. **RutaController**:
   - Faltaban anotaciones OpenAPI en varios métodos

## Solución Implementada

Se agregaron anotaciones OpenAPI de `io.swagger.v3.oas.annotations` a todos los controladores problemáticos:

### 1. Anotaciones Agregadas

#### A Nivel de Clase:
```java
@Tag(name = "...", description = "...")
```

#### A Nivel de Métodos:
```java
@Operation(summary = "...", description = "...")
@ApiResponse(responseCode = "200", description = "...", content = @Content(schema = @Schema(type = "object")))
```

### 2. Archivos Modificados

1. **RealtimeController.java**
   - Agregado `@Tag(name = "Realtime", description = "...")`
   - Anotaciones `@Operation` y `@ApiResponse` en todos los métodos

2. **NovedadController.java**
   - Agregado `@Tag(name = "Novedades", description = "...")`
   - Anotaciones en métodos approve y reject

3. **ParadaTemporalController.java**
   - Agregado `@Tag(name = "Paradas Temporales", description = "...")`
   - Anotaciones en todos los métodos CRUD

4. **RutaController.java**
   - Agregado `@Tag(name = "Rutas", description = "...")`
   - Anotaciones en todos los métodos principales

## Importaciones Requeridas

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
```

## Verificación

Para verificar que el fix funciona:

1. Compilar el proyecto:
   ```bash
   mvn clean compile
   ```

2. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

3. Acceder a la documentación:
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

El error 500 en `/v3/api-docs` debería estar resuelto.

## Nota Adicional

La dependencia `springdoc-openapi-starter-webmvc-ui` (v2.5.0) ya está en el `pom.xml` y es compatible con Spring Boot 3.3.8.

## Configuración OpenAPI Existente

La configuración en `OpenApiConfig.java` ya contiene:

```java
@Bean
public OpenAPI adminOpenAPI() {
    return new OpenAPI()
            .components(new Components()
                    .addSecuritySchemes("bearerAuth", new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")))
            .info(new Info()
                    .title("NCA Admin API")
                    .description("API para gestión de rutas y buses")
                    .version("v0.0.1"))
            // ... más configuración
}
```

Esto proporciona la base para la documentación centralizada de toda la API.
