# 🧪 GUÍA DE TESTING - CONDUCTOR Y COORDINADOR

## 🎯 Objetivo
Verificar que el conductor y coordinador pueden acceder a sus rutas asignadas, ver información detallada y reportar estados.

---

## 📋 PRE-REQUISITOS

✅ Aplicación iniciada y ejecutándose en `http://localhost:8080`
✅ Base de datos H2 inicializada con SeedData
✅ Swagger UI disponible en `http://localhost:8080/swagger-ui/index.html`

---

## 🔐 STEP 1: AUTENTICACIÓN DEL CONDUCTOR

### Opción A: Usando Swagger UI

1. Navega a `http://localhost:8080/swagger-ui/index.html`
2. Abre la sección **Auth Controller**
3. Click en `POST /api/auth/login`
4. Completa con:
   ```json
   {
     "username": "conductor.juan",
     "password": "conductor123"
   }
   ```
5. Click en **Execute**
6. Copia el token JWT del response

### Opción B: Usando CURL

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "conductor.juan",
    "password": "conductor123"
  }'
```

### Response Esperado
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "conductor.juan"
}
```

---

## 📱 STEP 2: VER RUTAS ASIGNADAS PARA HOY

### Usar el Token JWT

En Swagger UI:
1. Click en el botón **Authorize** (arriba a la derecha)
2. Ingresa: `Bearer {token}`
3. Click en **Authorize**

### Consultar Endpoint

**GET** `/api/rutas/today`

**Respuesta Esperada:**
```json
{
  "id": "ruta-123",
  "nombre": "RECOGIDA MATINAL - Hoy",
  "conductorId": "conductor-001",
  "conductorNombre": "Juan Pérez García",
  "coordinadorId": "coordinador-001",
  "coordinadorNombre": "María López García",
  "busId": "bus-001",
  "busPlaca": "ABC-001",
  "sedeId": "sede-001",
  "sedeName": "Sede Principal",
  "tipoRuta": "RECOGIDA",
  "estado": "ACTIVE",
  "horaInicio": "14:30",
  "horaFin": "15:30",
  "estudiantes": 6,
  "paradas": [...]
}
```

---

## 🚌 STEP 3: VER DETALLES COMPLETOS DE LA RUTA

**GET** `/api/rutas/{rutaId}`

### Respuesta Esperada - Información Conductor
```json
{
  "id": "ruta-123",
  "conductor": {
    "id": "conductor-001",
    "nombre": "Juan Pérez García",
    "cedula": "1088123456",
    "licencia": "LIC-2025-001",
    "estado": "disponible"
  }
}
```

### Respuesta Esperada - Información Coordinador
```json
{
  "coordinador": {
    "id": "coordinador-001",
    "nombre": "María López García",
    "cedula": "1087654321",
    "email": "coord@example.com",
    "estado": "activo"
  }
}
```

### Respuesta Esperada - Bus Asignado
```json
{
  "bus": {
    "id": "bus-001",
    "placa": "ABC-001",
    "capacidad": 40,
    "marca": "Chevrolet",
    "modelo": "Modelo 2023",
    "motorType": "combustible"
  }
}
```

---

## 📍 STEP 4: VER PARADAS Y ESTUDIANTES

**GET** `/api/rutas/{rutaId}/paradas`

### Respuesta Esperada
```json
[
  {
    "orden": 1,
    "nombre": "Sede Principal",
    "direccion": "Carrera 7 # 123, Bogotá",
    "tipo": "INICIO",
    "coordenadas": {
      "latitud": 4.7110,
      "longitud": -74.0721
    }
  },
  {
    "orden": 2,
    "nombre": "Cra 5 #10-25",
    "direccion": "San Alejo",
    "tipo": "RECOGIDA",
    "estudiantes": [
      {
        "id": "est-001",
        "nombre": "Carlos Rodríguez",
        "matricula": "MAT-2026-001",
        "grado": "4to Primaria",
        "padre": "Roberto Rodríguez",
        "estado": "por_recoger"
      }
    ],
    "coordenadas": {
      "latitud": 4.7115,
      "longitud": -74.0725
    }
  },
  {
    "orden": 3,
    "nombre": "Cra 6 #12-30",
    "direccion": "Chapinero",
    "tipo": "RECOGIDA",
    "estudiantes": [
      {
        "id": "est-002",
        "nombre": "Ana Martínez",
        "matricula": "MAT-2026-002",
        "grado": "5to Primaria",
        "padre": "Francisco Martínez",
        "estado": "por_recoger"
      },
      {
        "id": "est-003",
        "nombre": "Pedro González",
        "matricula": "MAT-2026-003",
        "grado": "5to Primaria",
        "padre": "Francisco Martínez",
        "estado": "por_recoger"
      }
    ],
    "coordenadas": {
      "latitud": 4.7120,
      "longitud": -74.0730
    }
  }
]
```

---

## 📤 STEP 5: REPORTAR RECOGIDA DE ESTUDIANTE

**POST** `/api/rutas/{rutaId}/reportar-recogida`

### Request Body
```json
{
  "estudianteId": "est-001",
  "paradaId": "parada-002",
  "timestamp": "2026-02-06T14:35:00",
  "observaciones": "Estudiante recogido puntualmente"
}
```

### Response Esperado
```json
{
  "success": true,
  "message": "Recogida reportada exitosamente",
  "estudiante": {
    "id": "est-001",
    "nombre": "Carlos Rodríguez",
    "estado": "recogido"
  }
}
```

---

## ⚠️ STEP 6: REPORTAR NO ABORDAJE

**POST** `/api/rutas/{rutaId}/reportar-no-abordaje`

### Request Body
```json
{
  "estudianteId": "est-002",
  "paradaId": "parada-003",
  "razon": "Padre cancela viaje",
  "timestamp": "2026-02-06T14:40:00",
  "requiereNotificacion": true
}
```

### Response Esperado
```json
{
  "success": true,
  "message": "No abordaje registrado",
  "estudiante": {
    "id": "est-002",
    "nombre": "Ana Martínez",
    "estado": "no_abordado"
  },
  "notificacion": {
    "enviada": true,
    "padre": "padre.francisco@example.com"
  }
}
```

---

## 📝 STEP 7: REPORTAR NOVEDAD

**POST** `/api/rutas/{rutaId}/novedades`

### Request Body
```json
{
  "titulo": "Tráfico leve en Cra 7",
  "mensaje": "Retraso de 5 minutos esperado",
  "tipo": "info",
  "categoria": "trafico",
  "requiereAprobacion": false
}
```

### Response Esperado
```json
{
  "id": "novedad-001",
  "rutaId": "ruta-123",
  "titulo": "Tráfico leve en Cra 7",
  "createdAt": "2026-02-06T14:42:00",
  "estado": "activa"
}
```

---

## 📊 STEP 8: VER HISTORIAL Y GENERAR REPORTE FINAL

**POST** `/api/rutas/{rutaId}/completar`

### Request Body
```json
{
  "horaFinalizacion": "2026-02-06T15:30:00",
  "kmRecorridos": 12.5,
  "observacionesFinal": "Ruta completada sin inconvenientes",
  "documentosAdjuntos": []
}
```

### Response Esperado
```json
{
  "success": true,
  "rutaId": "ruta-123",
  "estado": "completada",
  "historial": {
    "id": "historial-001",
    "fecha": "2026-02-06",
    "horaInicio": "14:30",
    "horaFin": "15:30",
    "estudiantesRecogidos": 5,
    "estudiantesTotales": 6,
    "kmRecorridos": 12.5,
    "estado": "completada"
  },
  "reporte": {
    "generado": true,
    "url": "/reportes/reporte-ruta-123.pdf"
  }
}
```

---

## 🧪 TESTING DEL COORDINADOR

El coordinador (`coordinador.maria / coordinador123`) puede:

1. ✅ Autenticarse exactamente igual que el conductor
2. ✅ Ver las mismas rutas asignadas
3. ✅ Aprobar/rechazar novedades que requieran aprobación
4. ✅ Ver reportes de no abordaje
5. ✅ Editar información de la ruta en tiempo real

**Pasos idénticos a Conductor:**
- Login con `coordinador.maria / coordinador123`
- GET `/api/rutas/today`
- GET `/api/rutas/{rutaId}`
- POST `/api/rutas/{rutaId}/novedades`
- POST `/api/rutas/{rutaId}/completar`

---

## ✅ CHECKLIST DE VALIDACIÓN

- [ ] Conductor puede loguearse
- [ ] Coordinador puede loguearse
- [ ] Ambos ven la ruta "RECOGIDA MATINAL - Hoy"
- [ ] Pueden ver información del bus asignado
- [ ] Pueden ver estudiantes y direcciones
- [ ] Pueden reportar recogidas
- [ ] Pueden reportar no abordajes
- [ ] Pueden crear novedades
- [ ] Pueden completar la ruta
- [ ] Se genera reporte final

---

## 📞 SOPORTE

Si algún endpoint retorna error:

1. Verificar que el token JWT es válido
2. Verificar que el formato Bearer es correcto: `Bearer {token}`
3. Verificar que la ruta existe con GET `/api/rutas/{rutaId}`
4. Revisar logs de la aplicación para detalles del error

---

## 🎉 ÉXITO

Si todos los pasos se ejecutan correctamente, el conductor y coordinador están completamente operacionales y listos para testing en la aplicación móvil.
