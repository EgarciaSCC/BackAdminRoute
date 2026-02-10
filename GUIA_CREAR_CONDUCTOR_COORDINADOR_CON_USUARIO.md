# 📚 GUÍA: CREAR CONDUCTOR/COORDINADOR CON USUARIO EN UN PASO

## 🎯 Objetivo
Simplificar el proceso de creación de conductores y coordinadores, incluyendo automáticamente la creación del usuario asociado.

---

## 🚀 CREAR CONDUCTOR CON USUARIO

### Endpoint (Propuesto)
```
POST /api/conductores/con-usuario
Content-Type: application/json
Authorization: Bearer <token_admin>
```

### Request Body
```json
{
  "nombre": "Juan Pérez García",
  "cedula": "1088123456",
  "licencia": "LIC-2025-001",
  "tipoLicencia": "A1",
  "telefono": "+573001234567",
  
  "username": "conductor.juan",
  "password": "conductor123",
  "email": "conductor.juan@example.com",
  
  "estado": "disponible",
  "tenant": "transport-1"
}
```

### Response (Success - 201 Created)
```json
{
  "id": "conductor-001",
  "nombre": "Juan Pérez García",
  "cedula": "1088123456",
  "licencia": "LIC-2025-001",
  "telefono": "+573001234567",
  "estado": "disponible",
  "tenant": "transport-1",
  "mensaje": "✅ Conductor y usuario creados exitosamente"
}
```

### Logs
```
✅ Conductor creado: Juan Pérez García (ID: conductor-001)
✅ Usuario creado: conductor.juan - vinculado a conductor: conductor-001
   📱 Username: conductor.juan | Contraseña: conductor123 (hash guardado)
```

---

## 👩‍✈️ CREAR COORDINADOR CON USUARIO

### Endpoint (Propuesto)
```
POST /api/coordinadores/con-usuario
Content-Type: application/json
Authorization: Bearer <token_admin>
```

### Request Body
```json
{
  "nombre": "María López García",
  "cedula": "1087654321",
  "email": "maria.lopez@example.com",
  "telefono": "+573009876543",
  
  "username": "coordinador.maria",
  "password": "coordinador123",
  
  "estado": "activo",
  "tenant": "transport-1"
}
```

### Response (Success - 201 Created)
```json
{
  "id": "coordinador-001",
  "nombre": "María López García",
  "cedula": "1087654321",
  "email": "maria.lopez@example.com",
  "telefono": "+573009876543",
  "estado": "activo",
  "tenant": "transport-1",
  "mensaje": "✅ Coordinador y usuario creados exitosamente"
}
```

### Logs
```
✅ Coordinador creado: María López García (ID: coordinador-001)
✅ Usuario creado: coordinador.maria - vinculado a coordinador: coordinador-001
   📱 Username: coordinador.maria | Contraseña: coordinador123 (hash guardado)
```

---

## 🔍 VALIDACIONES INCLUIDAS

### Duplicados Validados:
```
✓ Cédula única (conductor/coordinador)
✓ Licencia única (conductor)
✓ Username único (usuario)
✓ Email válido
✓ Contraseña longitud (6-72 caracteres)
```

### Errores Posibles:
```json
{
  "error": "Ya existe conductor con cédula: 1088123456",
  "status": 400
}
```

```json
{
  "error": "Ya existe usuario con username: conductor.juan",
  "status": 400
}
```

```json
{
  "error": "Contraseña debe tener entre 6 y 72 caracteres",
  "status": 400
}
```

---

## 💾 QUÉ SE CREA AUTOMÁTICAMENTE

### Para Conductor:
```
1. Conductor
   ├─ ID: conductor-001
   ├─ nombre: Juan Pérez García
   ├─ cedula: 1088123456
   ├─ licencia: LIC-2025-001
   ├─ estado: disponible
   └─ tenant: transport-1

2. Usuario
   ├─ ID: usuario-xyz
   ├─ username: conductor.juan
   ├─ password: hash(conductor123)
   ├─ email: conductor.juan@example.com
   ├─ role: ROLE_TRANSPORT
   └─ conductorId: conductor-001 ✅ VINCULADO
```

### Para Coordinador:
```
1. Coordinador
   ├─ ID: coordinador-001
   ├─ nombre: María López García
   ├─ cedula: 1087654321
   ├─ email: maria.lopez@example.com
   ├─ estado: activo
   └─ tenant: transport-1

2. Usuario
   ├─ ID: usuario-abc
   ├─ username: coordinador.maria
   ├─ password: hash(coordinador123)
   ├─ email: maria.lopez@example.com
   ├─ role: ROLE_TRANSPORT
   └─ coordinadorId: coordinador-001 ✅ VINCULADO
```

---

## 🔐 SEGURIDAD

### Password Hashing:
```
Algoritmo: BCrypt
Salt: Generado automáticamente
Longitud máxima: 72 caracteres
Validación: Inmediata post-creación
```

### Mitigación CVE-2025-22228:
```
✓ Validación de longitud de contraseña
✓ Prevención de truncado
✓ Hash verificado al crear
```

---

## 📋 COMPARATIVA: ANTES vs DESPUÉS

### ANTES (2 pasos):
```
1. POST /api/conductores
   {
     "nombre": "Juan Pérez",
     "cedula": "1088123456",
     "licencia": "LIC-2025-001",
     ...
   }
   Response: { "id": "conductor-001" }

2. POST /api/usuarios
   {
     "username": "conductor.juan",
     "password": "conductor123",
     "conductorId": "conductor-001"
   }
   Response: { "id": "usuario-001" }
```

### AHORA (1 paso):
```
1. POST /api/conductores/con-usuario
   {
     "nombre": "Juan Pérez",
     "cedula": "1088123456",
     "licencia": "LIC-2025-001",
     "username": "conductor.juan",
     "password": "conductor123",
     ...
   }
   Response: { 
     "id": "conductor-001",
     "mensaje": "✅ Conductor y usuario creados exitosamente"
   }
   
   Dentro de la respuesta se confirma que:
   ✓ Conductor creado
   ✓ Usuario creado
   ✓ Vinculación establecida
```

---

## 🧪 TESTING CON CURL

### Crear Conductor:
```bash
curl -X POST http://localhost:8080/api/conductores/con-usuario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token_admin>" \
  -d '{
    "nombre": "Juan Pérez García",
    "cedula": "1088123456",
    "licencia": "LIC-2025-001",
    "tipoLicencia": "A1",
    "telefono": "+573001234567",
    "username": "conductor.juan",
    "password": "conductor123",
    "email": "conductor.juan@example.com",
    "estado": "disponible",
    "tenant": "transport-1"
  }'
```

### Crear Coordinador:
```bash
curl -X POST http://localhost:8080/api/coordinadores/con-usuario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token_admin>" \
  -d '{
    "nombre": "María López García",
    "cedula": "1087654321",
    "email": "maria.lopez@example.com",
    "telefono": "+573009876543",
    "username": "coordinador.maria",
    "password": "coordinador123",
    "estado": "activo",
    "tenant": "transport-1"
  }'
```

---

## ✅ CHECKLIST POST-CREACIÓN

Después de crear conductor/coordinador con usuario:

- [ ] Conductor/Coordinador aparece en listado
- [ ] Usuario aparece con role ROLE_TRANSPORT
- [ ] Vinculación conductorId/coordinadorId existe
- [ ] Contraseña funciona en login
- [ ] Usuario puede ver rutas asignadas
- [ ] Logs muestran ambas creaciones

---

## 🎯 VENTAJAS

✅ **Eficiencia:** 1 request en lugar de 2
✅ **Atomicidad:** Si falla, falla todo
✅ **Validación:** Valida duplicados en ambas entidades
✅ **Logs:** Registra ambas creaciones
✅ **Seguridad:** Password hasheado automáticamente
✅ **Experiencia:** API más intuitiva

---

## 📞 IMPLEMENTACIÓN CONTROLADOR

Para completar la implementación, agregar estos endpoints:

```java
@RestController
@RequestMapping("/api/conductores")
public class ConductorController {
    
    @PostMapping("/con-usuario")
    public ResponseEntity<?> createWithUser(
        @Valid @RequestBody CreateConductorWithUserRequest request) {
        Conductor conductor = service.createConductorWithUser(request);
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conductor.getId())
                .toUri()
        ).body(conductor);
    }
}

@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {
    
    @PostMapping("/con-usuario")
    public ResponseEntity<?> createWithUser(
        @Valid @RequestBody CreateCoordinadorWithUserRequest request) {
        Coordinador coordinador = service.createCoordinadorWithUser(request);
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(coordinador.getId())
                .toUri()
        ).body(coordinador);
    }
}
```

---

## 🎉 RESULTADO

Con esta mejora:
- ✅ Conductor/Coordinador y usuario se crean juntos
- ✅ Vinculación automática garantizada
- ✅ Validaciones integradas
- ✅ Logs claros del proceso
- ✅ API más simple y eficiente
