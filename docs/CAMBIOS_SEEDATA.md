# 📝 Cambios Realizados en SeedData.java

## Resumen
Se han agregado usuarios y contraseñas para el conductor y coordinador, permitiendo que puedan acceder a la aplicación y visualizar/gestionar las rutas asignadas.

## Cambios Específicos

### 1. ✅ Conductor Principal
- **Nombre:** Juan Pérez García
- **Entidad:** Conductor
- **Usuario:** `conductor.juan`
- **Contraseña:** `conductor123`
- **Rol:** ROLE_TRANSPORT
- **Email:** conductor.juan@example.com
- **Ruta Asignada:** RECOGIDA MATINAL - Hoy

### 2. ✅ Coordinador
- **Nombre:** María López García
- **Entidad:** Coordinador
- **Usuario:** `coordinador.maria`
- **Contraseña:** `coordinador123`
- **Rol:** ROLE_TRANSPORT
- **Email:** coordinador.maria@example.com
- **Ruta Asignada:** RECOGIDA MATINAL - Hoy

### 3. ✅ Usuarios Admin Existentes
- **admin** / `admin123` (ROLE_ADMIN)
- **admin.transport** / `admin123` (ROLE_TRANSPORT)
- **admin.colegio** / `admin123` (ROLE_SCHOOL)

### 4. ✅ Usuarios Padres Existentes
- **padre_roberto** / `padre123` (padre de Carlos)
- **padre_francisco** / `padre123` (padre de Ana y Pedro)
- **padre_patricia** / `padre123` (padre de Lucía y Diego)
- **padre_gustavo** / `padre123` (padre de Sofía)

## Ruta Creada para Pruebas

### Información Principal
- **ID Ruta:** Generado automáticamente
- **Nombre:** RECOGIDA MATINAL - Hoy
- **Conductor:** Juan Pérez García (conductor.juan)
- **Coordinador:** María López García (coordinador.maria)
- **Bus:** ABC-001 (40 estudiantes)
- **Sede:** Sede Principal
- **Tipo:** RECOGIDA
- **Estado:** ACTIVE

### Horario
- **Inicio:** 30 minutos desde ahora (formato HH:MM)
- **Fin:** 1 hora después del inicio

### Estudiantes (6 total)
1. **Carlos Rodríguez** (MAT-2026-001) - 4to Primaria
   - Dirección: Cra 5 #10-25, San Alejo
   - Padre: padre_roberto

2. **Ana Martínez** (MAT-2026-002) - 5to Primaria
   - Dirección: Cra 6 #12-30, Chapinero
   - Padre: padre_francisco

3. **Pedro González** (MAT-2026-003) - 5to Primaria
   - Dirección: Cra 6 #12-35, Chapinero
   - Padre: padre_francisco

4. **Lucía Fernández** (MAT-2026-004) - 3ro Primaria
   - Dirección: Cra 8 #15-40, Usaquén
   - Padre: padre_patricia

5. **Diego Torres** (MAT-2026-005) - 4to Primaria
   - Dirección: Cra 9 #16-50, Usaquén
   - Padre: padre_patricia

6. **Sofía Ramírez** (MAT-2026-006) - 6to Primaria
   - Dirección: Cra 10 #18-60, Usaquén
   - Padre: padre_gustavo

### Paradas de Ruta
1. **Sede Principal** (Partida)
   - Ubicación: Carrera 7 # 123, Bogotá
   - Coordenadas: 4.7110, -74.0721

2. **Cra 5 #10-25, San Alejo**
   - Coordenadas: 4.7115, -74.0725
   - Estudiante: Carlos (1 estudiante)

3. **Cra 6 #12-30, Chapinero**
   - Coordenadas: 4.7120, -74.0730
   - Estudiantes: Ana, Pedro (2 estudiantes)

4. **Cra 8 #15-40, Usaquén**
   - Coordenadas: 4.7130, -74.0735
   - Estudiantes: Lucía, Diego, Sofía (3 estudiantes)

5. **Retorno a Sede Principal**
   - Ubicación: Carrera 7 # 123, Bogotá

## Funcionalidades Disponibles para Conductor/Coordinador

✅ Ver rutas asignadas para hoy: `GET /api/rutas/today`
✅ Ver rutas programadas
✅ Ver rutas completadas
✅ Ver información completa de ruta
✅ Ver bus asignado
✅ Ver coordinador/conductor asignado
✅ Ver paradas y estudiantes a recoger/dejar
✅ Reportar recogida/no abordaje durante ruta en movimiento
✅ Reportar novedades durante la ruta
✅ Generar reporte final post-completar ruta

## Logs de Inicialización

El archivo SeedData.java genera los siguientes logs al inicializar:

```
✅ Conductor creado: [ID]
✅ Coordinador creado: [ID]
✅ [6] estudiantes creados
✅ Ruta creada: [ID] (Asignada a conductor: Juan Pérez García y coordinador: María López)
✅ Historial creado para hoy: [ID]
✅ Usuario Conductor creado - Username: conductor.juan - Password: conductor123
✅ Usuario Coordinador creado - Username: coordinador.maria - Password: coordinador123
✅ Admin Transporte creado (vinculado a conductor admin)
✅ Admin Colegio creado
✅ Admin Sistema creado
✅ Novedad creada
```

## Seguridad (CVE-2025-22228)

Todas las contraseñas se validan y hashean usando BCrypt con validación de longitud máxima (72 caracteres) para mitigar CVE-2025-22228.

## Notas de Implementación

1. Los conductores y coordinadores pueden loguearse con sus propias credenciales
2. Ambos tienen acceso a las rutas asignadas a través del endpoint `/api/rutas/today`
3. La ruta se genera con horario dinámico (+30 minutos desde la hora actual)
4. Se incluye historial pre-generado para la ruta de hoy
5. El seed data es compatible con el perfil "default" de Spring Boot
