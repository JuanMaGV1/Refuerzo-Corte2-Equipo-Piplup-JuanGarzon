# Plantilla DOSW — AP-07 Asignar automáticamente drone a misión hídrica

Documento de especificación del requisito funcional AP-07 de AquaPort
Prinplup, siguiendo el formato DOSW.

---

## Identificación

| Campo | Valor |
|---|---|
| **Código** | AP-07 |
| **Nombre** | Asignar automáticamente drone a misión hídrica |
| **Actor primario** | Operador Hídrico (supervisa), Sistema (decide) |
| **Prioridad** | Must Have |
| **Nivel** | Prinplup (v2.0.0) |
| **RF relacionado** | AP-08 (prioridad absoluta a misiones CRÍTICAS) |

---

## Descripción

El sistema recibe una solicitud de misión hídrica y selecciona
automáticamente el drone más adecuado según el tipo de carga, la zona
destino, las condiciones hídricas y la prioridad de la misión. El
operador no elige el drone: solo confirma o cancela la asignación
propuesta.

Para misiones no críticas, aplica **AP-07 (mayor batería)**. Para misiones
críticas, delega en **AP-08 (aptitud técnica sin importar batería)**.

---

## Precondiciones

1. Debe existir al menos un drone **disponible** (estado = DISPONIBLE)
   con **batería ≥ 35%**.
2. El catálogo de zonas hídricas debe estar configurado: Embalse Norte,
   Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico.
3. La **API de Condiciones Hídricas** debe estar disponible o existir
   un valor en caché del último conocido (con marca `desactualizado`).
4. El actor debe estar autenticado en el sistema.

---

## Datos de Entrada

### Objeto principal: `carga` (sub-objeto anidado)

| Nombre | Tipo de campo | Reglas | Obligatorio |
|---|---|---|---|
| `carga` | — | — | Sí |
| `carga.peso` | `Integer` | Entre 1 y 1500 gramos | Sí |
| `carga.tipo` | `Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO, EQUIPO_MEDICION)` | — | Sí |
| `carga.prioridad` | `Enum(CRITICA, ALTA, NORMAL, BAJA)` | — | Sí |

### Objeto: `origen`

| Nombre | Tipo de campo | Reglas | Obligatorio |
|---|---|---|---|
| `zonaOrigen` | `Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO)` | — | Sí |
| `zonaDestino` | `Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO)` | Debe ser distinta a `zonaOrigen` | Sí |

---

## Datos de Salida

### Objeto principal: `asignacion`

| Nombre | Tipo de campo | Descripción |
|---|---|---|
| `droneAsignado` | — | Sub-objeto con los datos del drone elegido |
| `droneAsignado.id` | `String` | Formato `AR-XX` |
| `droneAsignado.tipo` | `Enum(SUPERFICIAL, SEMISUMERGIDO, BUCEADOR)` | Tipo del drone |
| `droneAsignado.bateria` | `Integer` | Batería al momento de la asignación (0-100) |
| `droneAsignado.capacidadCarga` | `Integer` | Capacidad máxima en gramos |
| `droneAsignado.zonaActual` | `String` | Zona en la que se encontraba |
| `razonSeleccion` | `String` | Texto explicativo de por qué se eligió ese drone |
| `estrategiaAplicada` | `Enum(MAYOR_BATERIA, PRIORIDAD_CRITICA)` | Qué estrategia se usó |
| `timestamp` | `LocalDateTime` | Momento de la asignación |
| `mensaje` | `String` | Confirmación para el operador |

### Ejemplo de salida

```json
{
  "droneAsignado": {
    "id": "AR-06",
    "tipo": "BUCEADOR",
    "bateria": 55,
    "capacidadCarga": 300,
    "zonaActual": "Embalse Norte"
  },
  "razonSeleccion": "Mayor batería entre los BUCEADORES compatibles con Embalse Norte",
  "estrategiaAplicada": "MAYOR_BATERIA",
  "timestamp": "2026-10-12T18:30:00",
  "mensaje": "Misión M-001 asignada al drone AR-06 (BUCEADOR, 55%)."
}
```

---

## Flujo Básico (7 pasos)

1. **Recibir la solicitud.** El sistema recibe `carga`, `zonaOrigen`,
   `zonaDestino` del operador. Valida que `zonaOrigen ≠ zonaDestino`.
2. **Consultar condiciones hídricas.** El sistema consulta la API de
   Condiciones Hídricas para `zonaDestino`. Si la API no responde en
   3 s, usa el último valor conocido (`desactualizado = true`).
3. **Filtrar drones aptos para la zona.** El sistema filtra la flota:
   - Estado = `DISPONIBLE`.
   - Tipo del drone compatible con `zonaDestino` (según
     `TipoDrone.esCompatibleCon(zona)`).
   - Batería ≥ 35%.
   - Capacidad de carga ≥ `carga.peso`.
4. **Aplicar estrategia de selección.**
   - Si `carga.prioridad == CRITICA` → `EstrategiaPrioridadCritica` (AP-08).
   - En otro caso → `EstrategiaMayorBateria` (AP-07).
5. **Validar el drone elegido.** El sistema verifica una última vez que
   el drone sigue disponible (no fue asignado a otra misión en paralelo)
   y que cumple todas las reglas de negocio.
6. **Asignar el drone.** El sistema cambia el estado del drone a
   `EN_MISION`, actualiza la misión a `EN_TRANSITO` y persiste el cambio.
7. **Notificar a los observadores.** El sistema notifica al Centro de
   Control y al Técnico de Mantenimiento con el ID del drone asignado y
   la razón de la selección.

---

## Flujos Alternos

### FA-01 — Sin drones disponibles

**Cuándo se activa:** en el paso 3, tras filtrar, la lista de candidatos
queda vacía.

**Qué hace el sistema:**
1. Distingue dos sub-casos:
   - **FA-01a:** existe al menos un drone en la zona destino pero en
     estado `FALLO` → notifica con razón *"Drone(s) en FALLO en la zona
     destino"*.
   - **FA-01b:** no hay ningún drone compatible con la zona → notifica
     con razón *"Sin drones compatibles con la zona destino"*.
2. Notifica a todos los observadores con `notificarFalloAsignacion(mision, razon)`.
3. Retorna `Optional.empty()` al operador.
4. La misión permanece en estado `PENDIENTE` para reintento manual.

**Salida:** mensaje *"No se pudo asignar drone: [razón]. La misión M-XXX
permanece pendiente."*

### FA-02 — Condiciones hídricas adversas

**Cuándo se activa:** en el paso 2, cuando la API retorna
`nivelAgitacion = AGITADA` para la zona destino.

**Qué hace el sistema:**
1. Filtra los tipos de drone aceptados para esa zona en condiciones
   agitadas: **excluye `SUPERFICIAL`** (no puede operar en agua agitada).
2. Si tras el filtro hay candidatos, continúa con el flujo básico paso 4.
3. Si no hay candidatos, delega en **FA-01**.

**Salida:** si hay candidatos, la asignación se completa normalmente pero
con `razonSeleccion` incluyendo *"condiciones agitadas en zona destino"*.
Si no hay, aplica FA-01.

### FA-03 — Carga supera la capacidad del tipo de drone

**Cuándo se activa:** en el paso 3, cuando `carga.peso` excede la
capacidad máxima de todos los drones del tipo compatible con la zona.

Ejemplo típico: `carga.peso = 500g` hacia Embalse Norte. Solo el
`BUCEADOR` es compatible con Embalse Norte, pero su capacidad máxima es
300g. Ningún drone puede llevar esa carga.

**Qué hace el sistema:**
1. Notifica con razón *"Carga de [peso]g supera la capacidad de los
   drones compatibles con [zona]. Máximo disponible: [capacidad]g."*
2. Notifica a todos los observadores.
3. Retorna `Optional.empty()`.
4. La misión permanece `PENDIENTE`.

**Salida:** mensaje *"No se pudo asignar: carga excede capacidad de los
drones disponibles para la zona destino."*

---

## Reglas de Negocio

| Código | Regla | Tipo |
|---|---|---|
| **RN-01** | El drone asignado debe tener batería ≥ 35%. Drones con batería inferior no son candidatos, sin importar su tipo. | Restricción del dominio |
| **RN-02** | El `DroneBuceador` no se asigna para cargas superiores a 300g. Su capacidad máxima es 300g; cargas mayores deben ir a `DroneSemisumergido` (1500g) o `DroneSuperficial` (500g). | Restricción del dominio |
| **RN-03** | El tipo del drone debe ser compatible con la zona destino. La compatibilidad está definida en `TipoDrone.esCompatibleCon(zona)`. | Restricción del dominio |
| **RN-04** | `zonaOrigen` y `zonaDestino` no pueden ser la misma zona. | Restricción del dominio |
| **RN-05** | Un drone no puede tener más de 1 misión activa simultáneamente. Antes de asignarlo, el sistema verifica que el drone no tenga otra misión en `EN_TRANSITO`. | Restricción del dominio |
| **RN-06** | Para misiones `CRITICA`, se aplica AP-08: aptitud técnica primero, sin usar batería como desempate. Para las demás, AP-07: mayor batería. | Restricción del dominio |
| **RN-07** | Si la API de Condiciones Hídricas no responde en 3 s, se usa el último valor conocido con `desactualizado = true`. No se bloquea la asignación. | Restricción de fiabilidad |

---

## Trazabilidad

| Elemento | Referencia |
|---|---|
| **RF** | AP-07 en `docs/requisitos-prinplup.md` |
| **RNF** | AP-RNF-01 (asignación ≤ 400 ms), AP-RNF-03 (timeout API 3 s), AP-RNF-04 (trazabilidad de eventos) |
| **Patrón de diseño** | Strategy (Reto 03 Prinplup) |
| **Clase orquestadora** | `AsignadorAutomatico` |
| **Estrategias** | `EstrategiaMayorBateria` (AP-07), `EstrategiaPrioridadCritica` (AP-08) |
| **Observadores** | `ObservadorCentroControl`, `ObservadorTecnicoMantenimiento` |

---

## Casos de prueba asociados

| # | Caso | Cubre |
|---|---|---|
| 1 | Misión no crítica con múltiples candidatos → elige el de mayor batería | Flujo básico + RN-06 |
| 2 | Misión CRITICA con múltiples candidatos → elige el de mayor capacidad (sin importar batería) | RN-06 + AP-08 |
| 3 | Sin drones disponibles en la zona → FA-01b | FA-01 |
| 4 | Todos los drones de la zona en FALLO → FA-01a | FA-01 |
| 5 | Carga de 500g hacia Embalse Norte (solo BUCEADOR compatible, capacidad 300g) → FA-03 | FA-03 + RN-02 |
| 6 | Condiciones agitadas en la zona destino → excluye SUPERFICIAL | FA-02 |
| 7 | Carga de 100g hacia Embalse Norte con BUCEADOR y SEMISUMERGIDO disponibles → elige por estrategia | Flujo básico |
| 8 | Drone con batería 34% → no es candidato | RN-01 |
| 9 | API hídrica lenta (>3 s) → usa caché con `desactualizado = true` | RN-07 |
| 10 | `zonaOrigen == zonaDestino` → rechaza la solicitud | RN-04 |