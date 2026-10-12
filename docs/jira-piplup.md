# Gestion Agil

## Jerarquía completa en Jira

```
ÉPICA
 └── FEATURE
      ├── HU-01
      ├── HU-02
      └── HU-03
           └── Subtarea 1
           └── Subtarea 2
           └── Subtarea 3
```
## Épica del MVP
| Tipo | Épica |
|------|-------|
| Nombre | Digitalizar el monitoreo y reparto hídrico de la ECI mediante una flota de drones acuáticos supervisada|
|Descripción |La ECI necesita modernizar la gestión de sus zonas hídricas (embalses, canales, lagunas y puntos de recolección) reemplazando los procesos manuales por un sistema centralizado que permita registrar misiones de transporte de muestras y sensores, asignar drones acuáticos y hacer seguimiento del estado de cada operación. Esta épica cubre el MVP: 4 drones, zonas fijas y asignación manual por parte del operador.
|Objetivo de negocio | Reducir el tiempo de asignación manual de misiones y tener trazabilidad de cada transporte hídrico. |
| Criterio de cierre | El operador puede registrar una misión, asignar un drone disponible y confirmar la entrega, todo desde el panel de AquaPort. |
|Prioridad |Must Have (MVP)|

## Feature

| Tipo | Feature | 
|------|---------|
| Épica padre | Digitalizar el monitoreo y reparto hídrico de la ECI
| Nombre | Gestión de la flota de drones acuáticos del campus
| Descripción | Funcionalidad que permite al operador hídrico visualizar la flota, conocer el estado de cada drone y asignar drones disponibles a misiones de transporte de muestras o sensores entre las zonas hídricas del campus.
| Valor para el usuario |El operador pasa de hojas de cálculo y llamadas a un panel único donde ve y decide en segundos.
| Criterio de cierre | Las 3 HU de esta feature están completadas y aceptadas en el sprint.

## Historias de Usuario

### HU-01 — Ver flota disponible

| Tipo | Historia de Usuario |
|------|---------------------|
| Feature padre | Gestión de la flota de drones acuáticos del campus|
| Título | Consultar flota de drones disponibles|
| Descripción | Como operador hídrico, quiero ver la lista de drones disponibles con su nivel de batería y zona actual, para saber rápidamente cuáles puedo asignar a una misión sin revisar planillas aparte. |
| Criterios de aceptación | <ul><li>La lista muestra únicamente drones con disponible = true. <li>Cada drone muestra ID (AR-XX), batería (%), estado y zona actual.</li>  <li> La lista está ordenada de mayor a menor batería.</li></ul> |
| Story Points | 3 |
| Prioridad | Must Have |

### HU-02 — Asignar drone a una misión

| Tipo | Historia de Usuario |
|------|---------------------|
| Feature padre | Gestión de la flota de drones acuáticos del campus |
| Título        | Asignar drone a una misión registrada |
| Descripción |Como operador hídrico, quiero asignar un drone disponible a una misión en estado PENDIENTE, para poner en marcha el transporte de la muestra o sensor sin tener que coordinar manualmente con el piloto. |
| Criterios de aceptación | <ul><li>Solo se pueden asignar drones con disponible = true y batería ≥ 35%.</li> <li>Al asignar, la misión pasa a estado EN_TRANSITO y el drone a disponible = false. </li> <li>Si el drone no cumple los requisitos, el sistema muestra el mensaje: "El drone AR-XX tiene batería insuficiente (YY%). Mínimo requerido: 35%." </li> |
| Story Points | 5 |
| Prioridad | Must Have |

#### Subtareas de HU-02:

| Subtarea | Descripción |
|----------|-------------|
| ST-01 | Implementar validación de disponibilidad y batería en ValidadorMision	Crear el método tieneBateriaSuficiente(DroneAcuatico) que retorna true si la batería ≥ 35%. Crear estaDisponible(DroneAcuatico) que verifica disponible = true. Cubrir con pruebas unitarias TDD.
| ST-02 | Implementar el cambio de estado de la misión a EN_TRANSITO en RegistradorMisiones	Método asignarDrone(Mision, DroneAcuatico) que valida primero con ValidadorMision y luego actualiza el estado de la misión y del drone de forma atómica. |
| ST-03 | Implementar el mensaje de error específico en el panel del operador	Mostrar el mensaje con ID del drone, batería actual y mínimo requerido. Verificar que el botón "Asignar" se deshabilita visualmente para drones con batería < 35% (Nielsen #5). |

### HU-03 — Cancelar misión pendiente

| Tipo | Historia de Usuario |
|------|---------------------|
| Feature padre | Gestión de la flota de drones acuáticos del campus
| Título        |	Cancelar una misión en estado PENDIENTE
| Descripción   |	Como operador hídrico, quiero cancelar una misión que aún no ha iniciado, para corregir un error de asignación o atender un cambio de última hora sin afectar el flujo del sistema.
| Criterios de aceptación | <ul><li>Solo se pueden cancelar misiones en estado PENDIENTE. </li> <li>Al cancelar, la misión pasa a estado FALLIDA con motivo "Cancelada por el operador".</li> <li>El drone asignado, si lo había, vuelve a disponible = true.</li>
| Story Points | 2 |
| Prioridad | Should Have |