# 🔍 AUDITORÍA DE ESTRUCTURA DE ROLES Y USUARIOS

## Estado: ✅ CUMPLE PARCIALMENTE (CON MEJORAS REQUERIDAS)

---

## 📋 REQUISITOS VALIDADOS

### 1️⃣ ESTRUCTURA DE ROLES
```
✅ ROLE_ADMIN      - Acceso completo según tenant
✅ ROLE_SCHOOL     - Visualizar rutas de estudiantes vinculados
✅ ROLE_TRANSPORT  - Conductor y Coordinador
```

### 2️⃣ USUARIO ADMIN
**Estado:** ✅ IMPLEMENTADO
- ✅ Acceso a tenants asignados
- ✅ Acceso a todos los servicios según tenant
- ✅ Usuario: `admin` / Contraseña: `admin123`
- ✅ Usuario: `admin.transport` (Transport role pero se usa para gestión)
- ✅ Usuario: `admin.colegio` (School role para gestión de sede)

### 3️⃣ USUARIO SCHOOL (Padres y Personal Escuela)
**Estado:** ✅ IMPLEMENTADO
- ✅ Pueden visualizar rutas donde están vinculados sus estudiantes
- ✅ Pueden reportar excusas para cancelar recogida
- ✅ Acceso mediante ROLE_SCHOOL
- ✅ Ejemplos: `padre_roberto`, `padre_francisco`, etc.

### 4️⃣ USUARIO TRANSPORT (Conductor/Coordinador)
**Estado:** ⚠️ IMPLEMENTADO PERO CON OBSERVACIONES

---

## 🔴 PROBLEMAS IDENTIFICADOS

### ❌ PROBLEMA 1: Usuario Coordinador sin campo en entidad Usuario
**Ubicación:** `Usuario.java`
**Detalle:** La entidad Usuario tiene `conductorId` pero NO tiene `coordinadorId`
```java
private String conductorId;  // ✅ Existe
// ❌ FALTA: private String coordinadorId;
```
**Impacto:** Los coordinadores no pueden ser relacionados con un usuario en la BD

**Solución:** Agregar campo `coordinadorId` en Usuario.java

---

### ❌ PROBLEMA 2: SeedData crea usuario coordinador con ROLE_TRANSPORT pero sin vinculación
**Ubicación:** `SeedData.java` línea ~410
**Detalle:** 
```java
usuarioCoordinador.setCoordinadorId(savedCoordinador.getId());  // ✅ Se intenta asignar
// Pero la entidad Usuario no tiene este atributo, genera error
```

**Impacto:** El coordinador no puede identificarse como tal en las rutas

**Solución:** Implementar campo `coordinadorId` en Usuario

---

### ❌ PROBLEMA 3: DriverService solo filtra por Conductor, no por Coordinador
**Ubicación:** `DriverService.java` línea ~50
**Detalle:**
```java
public Conductor resolveDriverFromAuth() {
    // Solo busca conductor
    return conductorRepository.findById(conductorId)
        .orElseThrow(...);
    // ❌ No hay método para resolver Coordinador
}
```

**Impacto:** Los coordinadores no pueden usar `/api/driver/routes/today`

**Solución:** Crear `CoordinadorService` con lógica similar o generalizar `DriverService`

---

### ❌ PROBLEMA 4: No existe endpoint específico para Coordinador
**Ubicación:** No existe `CoordinadorController` con endpoints similares a DriverController
**Detalle:** 
- ✅ Existe `GET /api/coordinadores` (listado general)
- ❌ NO existe `GET /api/coordinador/routes/today` (para coordinador autenticado)
- ❌ NO existe `GET /api/coordinador/routes/history`

**Impacto:** Coordinador no puede ver sus rutas asignadas

**Solución:** Crear CoordinadorService y CoordinadorAuthController

---

### ⚠️ PROBLEMA 5: SeedData asigna ruta a conductor pero no a coordinador correctamente
**Ubicación:** `SeedData.java` línea ~360
**Detalle:**
```java
ruta.conductorId(savedConductor.getId());      // ✅ OK
ruta.coordinadorId(savedCoordinador.getId());  // ✅ OK en ruta
// Pero el coordinador no tiene usuario creado correctamente
```

**Impacto:** Coordinador no puede acceder a su ruta

---

## 🟡 OBSERVACIONES DE IMPLEMENTACIÓN

### Comportamiento Actual (Parcialmente Correcto)

#### ✅ Conducto r (CORRECTO)
1. Se crea Conductor en BD
2. Se crea Usuario con ROLE_TRANSPORT vinculado a Conductor
3. Usuario puede autenticarse con `conductor.juan / conductor123`
4. `DriverService.resolveDriverFromAuth()` obtiene el Conductor
5. `/api/driver/routes/today` retorna rutas asignadas al conductor

#### ⚠️ Coordinador (INCORRECTO)
1. Se crea Coordinador en BD
2. Se intenta crear Usuario con ROLE_TRANSPORT + coordinadorId (PERO EL CAMPO NO EXISTE)
3. Usuario NO tiene forma de saber que es coordinador
4. NO existe endpoint `/api/coordinador/routes/today`
5. Coordinador NO puede ver sus rutas

---

## 📊 TABLA COMPARATIVA

| Aspecto | Conductor | Coordinador |
|---------|-----------|-------------|
| **Entidad en BD** | ✅ Creada | ✅ Creada |
| **Usuario ROLE_TRANSPORT** | ✅ Vinculado | ❌ Vinculado pero incompleto |
| **Campo en Usuario** | ✅ `conductorId` | ❌ Falta `coordinadorId` |
| **Endpoint Autenticado** | ✅ `/api/driver/routes/today` | ❌ No existe |
| **Service para resolver** | ✅ `DriverService` | ❌ `CoordinadorService` incompleto |
| **Ver rutas hoy** | ✅ Funciona | ❌ No funciona |
| **Ver rutas programadas** | ✅ Funciona | ❌ No funciona |
| **Ver rutas completadas** | ✅ Funciona | ❌ No funciona |
| **Reportar recogida** | ✅ Podría | ❌ No puede |
| **Reportar novedades** | ✅ Podría | ❌ No puede |

---

## 🛠️ SOLUCIONES REQUERIDAS

### 1. Actualizar entidad Usuario (CRÍTICA)
```java
// Agregar en Usuario.java
private String coordinadorId;  // Nuevo campo

// Agregar getters y setters
public String getCoordinadorId() { return coordinadorId; }
public void setCoordinadorId(String coordinadorId) { this.coordinadorId = coordinadorId; }
```

### 2. Crear CoordinadorService (CRÍTICA)
- Implementar `resolveCoordinatorFromAuth()`
- Implementar `getRoutesToday()`
- Implementar `getRoutesHistory()`
- Implementar `reportarRecogida()`
- Implementar `reportarNoAbordaje()`
- Implementar `reportarNovedad()`

### 3. Crear CoordinadorAuthController (CRÍTICA)
```
GET  /api/coordinator/routes/today
GET  /api/coordinator/routes/scheduled
GET  /api/coordinator/routes/completed
GET  /api/coordinator/routes/{id}
POST /api/coordinator/routes/{id}/reportar-recogida
POST /api/coordinator/routes/{id}/reportar-no-abordaje
POST /api/coordinator/routes/{id}/reportar-novedad
```

### 4. Validar SeedData (IMPORTANTE)
- Confirmar que coordinador tiene usuario correcto
- Confirmar vinculación en BD

### 5. Crear CoordinadorResponseDTOs (IMPORTANTE)
- `CoordinatorRoutesTodayResponse`
- `CoordinatorRoutePreview`
- `CoordinatorRouteHistoryResponse`

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

- [ ] Agregar `coordinadorId` en Usuario.java
- [ ] Crear migraciones o cambios en esquema BD
- [ ] Implementar `CoordinadorService` con métodos especializados
- [ ] Crear `CoordinatorAuthController` con endpoints
- [ ] Crear DTOs para respuestas de coordinador
- [ ] Validar SeedData crea coordinador correctamente
- [ ] Agregar tests para coordinador
- [ ] Documentar endpoints coordinador en Swagger

---

## 🎯 RESUMEN EJECUTIVO

**Estado Actual:**
- ✅ Estructura de roles es correcta
- ✅ Conductores tienen implementación completa
- ❌ Coordinadores tienen implementación incompleta

**Acción Requerida:**
- Implementar endpoints específicos para coordinadores
- Agregar campo `coordinadorId` en Usuario
- Crear CoordinadorService similar a DriverService

**Impacto:**
- Sin estas soluciones, coordinadores NO pueden acceder a sus rutas
- Los coordinadores quedan sin funcionalidad asignada

---

## 🔗 REFERENCIAS

- Entidad Usuario: `src/main/java/nca/scc/com/admin/rutas/auth/entity/Usuario.java`
- DriverService: `src/main/java/nca/scc/com/admin/rutas/driver/DriverService.java`
- DriverController: `src/main/java/nca/scc/com/admin/rutas/driver/DriverController.java`
- SeedData: `src/main/java/nca/scc/com/admin/rutas/SeedData.java`
