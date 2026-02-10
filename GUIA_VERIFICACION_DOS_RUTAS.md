# 🧪 GUÍA PASO A PASO: VERIFICACIÓN DE DOS RUTAS

## 📋 PRE-REQUISITOS

- ✅ Proyecto compilado
- ✅ Base de datos H2 funcional
- ✅ Puerto 8080 disponible

---

## 🚀 PASO 1: COMPILAR Y EMPAQUETAR

```bash
# Navegar al directorio del proyecto
cd C:\Users\soporte\Documents\NCA\Rutas\NCABackend\admin

# Compilar y crear JAR
mvn clean package -DskipTests
```

**Esperado:**
```
BUILD SUCCESS
Time: XX seconds
```

---

## 🎯 PASO 2: INICIAR LA APLICACIÓN

```bash
# Ejecutar el JAR
java -jar target/admin-0.0.1-SNAPSHOT.jar
```

**Esperado en logs:**

```
✅ Sede San José Barranquilla creada: [ID]
✅ 4 estudiantes San José creados
✅ Ruta SAN JOSÉ Programada creada: [ID] (Estado: PROGRAMMED)
   📍 Parada 1 (Inicio): Colegio San José - Carrera 45 #72-15
   📍 Parada 2: Prado - Carrera 42 #71-20 (1 estudiante: Miguel)
   📍 Parada 3: El Prado - Carrera 48 #73-40 (1 estudiante: Isabella)
   📍 Parada 4: San Alejo - Carrera 51 #75-30 (1 estudiante: Andrés)
   📍 Parada 5: Murillo - Carrera 55 #77-50 (1 estudiante: Valentina)
   📍 Parada 6 (Final): Retorno a Colegio San José
   👨‍✈️  Conductor: Juan Pérez García
   👩‍✈️  Coordinadora: María López García
```

**Y al final:**

```
╔════════════════════════════════════════════════════════════════════════════════════╗
║                    ✅ SEED DATA COMPLETADO - 2 RUTAS CREADAS                      ║
║                                                                                    ║
║  📍 RUTA 1: RECOGIDA MATINAL (Estado: ACTIVE)                                     ║
║  ════════════════════════════════════════════════════════════════════════════     ║
║  ID: [ruta-id-1]                                              ║
║  Sede: Sede Principal (Bogotá)  |  Estudiantes: 6                                ║
║  Horario: 14:30 a 15:30                                                   ║
║  Paradas: 5 (1 inicio + 3 intermedias + 1 final)                                 ║
║                                                                                    ║
║  📍 RUTA 2: RUTA BARRANQUILLA - RECOGIDA TARDE (Estado: PROGRAMMED)              ║
║  ════════════════════════════════════════════════════════════════════════════     ║
║  ID: [ruta-id-2]                                        ║
║  Sede: Colegio San José (Barranquilla)  |  Estudiantes: 4                        ║
║  Horario: 14:30 a 16:30                                                   ║
║  Paradas: 6 (1 inicio + 4 intermedias + 1 final) ← TESTING CON 5 PARADAS         ║
│...más información...
╚════════════════════════════════════════════════════════════════════════════════════╝
```

---

## 🔐 PASO 3: AUTENTICACIÓN DEL CONDUCTOR

### Opción A: Usando Postman/Insomnia

**Request:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "conductor.juan",
  "password": "conductor123"
}
```

**Response Esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "conductor.juan"
}
```

### Opción B: Usando cURL

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "conductor.juan",
    "password": "conductor123"
  }'
```

**Guardar el token para los siguientes pasos:**
```
TOKEN="<token_recibido>"
```

---

## 📱 PASO 4: VER RUTAS DEL DÍA

### Verificar que ambas rutas aparecen

**Request:**
```http
GET http://localhost:8080/api/driver/routes/today
Authorization: Bearer eyJhbGciOi...
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/driver/routes/today \
  -H "Authorization: Bearer $TOKEN"
```

**Response Esperada:**
```json
{
  "driverId": "conductor-juan-id",
  "driverName": "Juan Pérez García",
  "date": "2026-02-06",
  "activeRoute": {
    "id": "ruta-1-id",
    "nombre": "RECOGIDA MATINAL - Hoy",
    "horaInicio": "14:30",
    "horaFin": "15:30",
    "estadoHistorial": "en_progreso"
  },
  "scheduledRoutes": [
    {
      "id": "ruta-2-id",
      "nombre": "RUTA BARRANQUILLA - RECOGIDA TARDE",
      "horaInicio": "14:30",
      "horaFin": "16:30",
      "estadoHistorial": "programada"
    }
  ],
  "completedRoutes": []
}
```

**✅ Validación:**
- [ ] `activeRoute` contiene Ruta 1 (RECOGIDA MATINAL)
- [ ] `scheduledRoutes` contiene Ruta 2 (RUTA BARRANQUILLA)
- [ ] Ambas tienen conductor y coordinadora asignados

---

## 🗺️ PASO 5: VER DETALLES DE RUTA BARRANQUILLA (5 PARADAS)

**Request:**
```http
GET http://localhost:8080/api/rutas/{ruta-id-2}
Authorization: Bearer $TOKEN
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/rutas/ruta-id-2 \
  -H "Authorization: Bearer $TOKEN"
```

**Response Esperada (Verificar paradas):**
```json
{
  "id": "ruta-id-2",
  "nombre": "RUTA BARRANQUILLA - RECOGIDA TARDE",
  "conductorId": "conductor-juan-id",
  "coordinadorId": "coordinador-maria-id",
  "sedeId": "sede-san-jose-id",
  "horaInicio": "14:30",
  "horaFin": "16:30",
  "paradas": [
    {
      "orden": 1,
      "nombre": "Colegio San José",
      "direccion": "Carrera 45 #72-15, Barranquilla",
      "tipo": "INICIO",
      "coordenadas": {"lat": 10.9905, "lon": -74.7975}
    },
    {
      "orden": 2,
      "nombre": "Prado",
      "direccion": "Carrera 42 #71-20, Barranquilla",
      "tipo": "RECOGIDA",
      "estudiantes": [{"nombre": "Miguel Ángel Vélez", ...}],
      "coordenadas": {"lat": 10.9915, "lon": -74.7985}
    },
    {
      "orden": 3,
      "nombre": "El Prado",
      "direccion": "Carrera 48 #73-40, Barranquilla",
      "tipo": "RECOGIDA",
      "estudiantes": [{"nombre": "Isabella Martínez", ...}],
      "coordenadas": {"lat": 10.9925, "lon": -74.7965}
    },
    {
      "orden": 4,
      "nombre": "San Alejo",
      "direccion": "Carrera 51 #75-30, Barranquilla",
      "tipo": "RECOGIDA",
      "estudiantes": [{"nombre": "Andrés Felipe López", ...}],
      "coordenadas": {"lat": 10.9935, "lon": -74.7955}
    },
    {
      "orden": 5,
      "nombre": "Murillo",
      "direccion": "Carrera 55 #77-50, Barranquilla",
      "tipo": "RECOGIDA",
      "estudiantes": [{"nombre": "Valentina Rodríguez", ...}],
      "coordenadas": {"lat": 10.9945, "lon": -74.7945}
    },
    {
      "orden": 6,
      "nombre": "Colegio San José",
      "direccion": "Carrera 45 #72-15, Barranquilla",
      "tipo": "FINAL",
      "coordenadas": {"lat": 10.9905, "lon": -74.7975}
    }
  ]
}
```

**✅ Validación:**
- [ ] 6 paradas totales (1 inicio + 4 intermedias + 1 final)
- [ ] Cada parada tiene coordenadas GPS
- [ ] Estudiantes vinculados a paradas 2-5
- [ ] Parada 1 y 6 son el mismo lugar (Colegio San José)

---

## 📍 PASO 6: VERIFICAR PARADAS EN SWAGGER

1. Navega a `http://localhost:8080/swagger-ui/index.html`
2. Busca `Ruta Controller`
3. Click en `GET /api/rutas/{id}/paradas`
4. Ingresa el ID de Ruta 2
5. Ejecuta y verifica que aparecen 6 paradas

---

## 📊 PASO 7: REPORTAR RECOGIDA EN PARADA 2

**Request:**
```http
POST http://localhost:8080/api/rutas/ruta-id-2/reportar-recogida
Authorization: Bearer $TOKEN
Content-Type: application/json

{
  "estudianteId": "miguel-angel-id",
  "paradaId": "parada-2-id",
  "timestamp": "2026-02-06T14:35:00",
  "observaciones": "Estudiante recogido"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/rutas/ruta-id-2/reportar-recogida \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "estudianteId": "miguel-angel-id",
    "paradaId": "parada-2-id",
    "timestamp": "2026-02-06T14:35:00"
  }'
```

**✅ Validación:**
- [ ] Response code: 200 OK
- [ ] Estudiante está marcado como recogido
- [ ] Se guarda timestamp

---

## 📝 PASO 8: REPORTAR NOVEDAD

**Request:**
```http
POST http://localhost:8080/api/rutas/ruta-id-2/reportar-novedad
Authorization: Bearer $TOKEN
Content-Type: application/json

{
  "titulo": "Tráfico en Cra 45",
  "mensaje": "Retraso de 10 minutos por congestión",
  "tipo": "info",
  "categoria": "trafico",
  "requiereAprobacion": false
}
```

**✅ Validación:**
- [ ] Novedad creada exitosamente
- [ ] Se puede consultar en listado de novedades

---

## 🏁 PASO 9: COMPLETAR RUTA Y GENERAR REPORTE

**Request:**
```http
POST http://localhost:8080/api/rutas/ruta-id-2/completar
Authorization: Bearer $TOKEN
Content-Type: application/json

{
  "horaFinalizacion": "2026-02-06T16:30:00",
  "kmRecorridos": 25.3,
  "observacionesFinal": "Ruta completada exitosamente",
  "estudiantesRecogidos": 4,
  "estudiantesTotales": 4
}
```

**✅ Validación:**
- [ ] Ruta cambia a estado COMPLETED
- [ ] Historial se actualiza
- [ ] Reporte final se genera

---

## ✅ CHECKLIST FINAL DE VALIDACIÓN

### Rutas Creadas
- [ ] Ruta 1 (RECOGIDA MATINAL) - Estado: ACTIVE
- [ ] Ruta 2 (RUTA BARRANQUILLA) - Estado: PROGRAMMED
- [ ] Ambas con conductor.juan asignado
- [ ] Ambas con coordinador.maria asignado

### Paradas
- [ ] Ruta 1: 5 paradas (1+3+1)
- [ ] Ruta 2: 6 paradas (1+4+1)
- [ ] Todas con coordenadas GPS válidas
- [ ] Estudiantes vinculados correctamente

### Horarios
- [ ] Inicio: Hora Actual + 30 minutos
- [ ] Fin: Según duración de ruta
- [ ] Formatos HH:mm correctos

### Funcionalidades
- [ ] Ver rutas en /api/driver/routes/today
- [ ] Ver detalles de ruta
- [ ] Ver paradas por ruta
- [ ] Reportar recogida
- [ ] Reportar novedad
- [ ] Completar ruta

### Datos
- [ ] 10 estudiantes en BD (6 + 4)
- [ ] 2 sedes (Bogotá + Barranquilla)
- [ ] 1 conductor + 1 coordinadora
- [ ] Bus asignado a ambas rutas

---

## 🐛 TROUBLESHOOTING

### Puerto 8080 en uso
```bash
# Cambiar puerto en application.yaml
server:
  port: 8081
```

### Base de datos vacía
- Verificar que el perfil es "default"
- Revisar logs de SeedData
- Limpiar BD: eliminar archivo H2

### Token inválido
- Verificar que usuario existe
- Confirmar contraseña: `conductor123`
- Revisar logs de autenticación

### Paradas no aparecen
- Verificar que Ruta 2 se creó en SeedData
- Revisar logs de creación de paradas
- Confirmar que estudiantes están vinculados

---

## 📞 CONCLUSIÓN

Si todos los pasos se ejecutan correctamente, significa que:

✅ Las 2 rutas se crean automáticamente en cada startup
✅ Se pueden ver en `/api/driver/routes/today`
✅ Tienen 5 paradas para testing avanzado
✅ Conductor y coordinadora pueden acceder
✅ Todas las funcionalidades están disponibles

**¡Sistema listo para testing completo!** 🎉
