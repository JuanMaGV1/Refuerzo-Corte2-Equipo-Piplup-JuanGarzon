# Requisitos Funcionales y No Funcionales — AquaPort Prinplup (v2.0.0)

Documento de requisitos de la versión Prinplup. Amplía los requisitos del
MVP (Piplup) con las funcionalidades de asignación automática, alertas y
gestión por técnico.

**Nota sobre códigos AP:** los códigos AP-07 y AP-08 se toman
literalmente del enunciado del curso. Los códigos AP-09 en adelante son
nuevos requisitos introducidos por la v2 (condiciones hídricas, gestión
por técnico, notificación de fallo y reasignación).

---

## Requisitos Funcionales

### AP-07 — Asignación automática por mayor batería (caso por defecto)

| Campo | Contenido |
|---|---|
| Código | AP-07 |
| Nombre | Asignar automáticamente el drone de mayor batería disponible |
| Actor | Operador Hídrico (supervisa), Sistema (decide) |
| Descripción | Para **cualquier misión hídrica no crítica**, el sistema selecciona automáticamente el drone disponible con mayor batería, siempre que sea compatible con la zona destino. El operador no elige el drone; solo confirma o cancela la asignación propuesta. |
| Precondiciones | Existe al menos un drone disponible con batería ≥ 35% y tipo compatible con la zona destino. |
| Entrada | `zonaDestino: Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO)`, `tipoCarga: Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO, EQUIPO_MEDICION)`, `pesoGramos: Integer` |
| Salida | `droneAsignado.id: String`, `droneAsignado.bateria: Integer`, `razonSeleccion: String` |
| Regla de negocio | El drone elegido debe ser compatible con la zona (según `TipoDrone.esCompatibleCon(zona)`). |
| Flujo básico | 1. Recibir la solicitud. 2. Filtrar drones disponibles, compatibles con la zona, con batería ≥ 35% y capacidad ≥ peso. 3. Aplicar la estrategia `EstrategiaMayorBateria`. 4. Asignar el drone y notificar a los observadores. 5. Retornar confirmación con la razón de selección. |
| Flujo alterno | Sin drones compatibles → notificar a los observadores y retornar error. |
| **Alcance** | **Misiones no críticas** (prioridad `ALTA`, `NORMAL`, `BAJA`). Para misiones críticas aplica AP-08. |

### AP-08 — Prioridad absoluta para misiones CRÍTICAS (independiente de la batería)

| Campo | Contenido |
|---|---|
| Código | AP-08 |
| Nombre | Prioridad absoluta a misiones CRÍTICAS: drone técnicamente más apto |
| Actor | Operador Hídrico (supervisa), Sistema (decide) |
| Descripción | Las misiones con prioridad `CRITICA` tienen **prioridad absoluta**. El sistema debe asignarles el **drone técnicamente más apto para la zona**, **independientemente de su batería** (siempre que cumpla el mínimo operativo de 35%). El criterio de selección **no usa batería** como desempate. |
| Precondiciones | Existe al menos un drone disponible con batería ≥ 35% y tipo compatible con la zona destino. |
| Entrada | Igual que AP-07, con `prioridad = CRITICA`. |
| Salida | Igual que AP-07. |
| Regla de negocio 1 | La selección se basa exclusivamente en **aptitud técnica**: el drone debe ser del tipo compatible con la zona y tener capacidad de carga suficiente para el peso de la misión. |
| Regla de negocio 2 | La batería **NO** se usa como desempate. Si hay empate en aptitud técnica, se desempata por **ID del drone** (orden lexicográfico) para garantizar determinismo. |
| Regla de negocio 3 | Aun así, el drone elegido debe tener batería ≥ 35%. "Independientemente de la batería" significa que no se usa como criterio de selección, no que se ignoren los mínimos operativos. |
| Flujo básico | 1. Recibir la solicitud con prioridad CRITICA. 2. Filtrar drones disponibles, compatibles con la zona, con batería ≥ 35% y capacidad ≥ peso. 3. Aplicar la estrategia `EstrategiaPrioridadCritica`. 4. Asignar y notificar observadores. 5. Retornar confirmación con razón de selección. |
| Flujo alterno | Sin drones compatibles → notificar observadores y retornar error. |
| **Alcance** | **Solo misiones `CRITICA`**. |

### AP-09 — Consultar condiciones hídricas de una zona

| Campo | Contenido |
|---|---|
| Código | AP-09 |
| Nombre | Consultar condiciones hídricas de una zona |
| Actor | Operador Hídrico, Administrador ECI |
| Descripción | El sistema consulta la API externa de Condiciones Hídricas y muestra al actor el nivel de agitación, profundidad y temperatura de una zona específica. |
| Precondiciones | La API está disponible o existe un valor en caché del último conocido. |
| Entrada | `zona: Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO)` |
| Salida | `nivelAgitacion: Enum(CALMA, MODERADA, AGITADA)`, `profundidadMetros: Double`, `temperaturaCelsius: Double`, `fechaConsulta: LocalDateTime`, `desactualizado: Boolean` |
| Regla de negocio | Si la API no responde en 3 s, retornar el último valor conocido con `desactualizado = true`. |
| Flujo básico | 1. Recibir la zona. 2. Consultar la API externa. 3. Retornar los datos al actor. |

### AP-10 — Marcar drone en mantenimiento desde el panel del técnico

| Campo | Contenido |
|---|---|
| Código | AP-10 |
| Nombre | Marcar drone en mantenimiento |
| Actor | Técnico de Mantenimiento |
| Descripción | El técnico marca un drone como `MANTENIMIENTO` desde su panel. El drone no será asignado a ninguna misión hasta que el técnico lo vuelva a marcar como `DISPONIBLE`. |
| Precondiciones | El drone no tiene misiones activas. |
| Entrada | `droneId: String`, `razon: String` (opcional) |
| Salida | `estadoDrone: MANTENIMIENTO`, `fechaInicioMantenimiento: LocalDateTime` |
| Regla de negocio | Un drone en `MANTENIMIENTO` no puede recibir nuevas misiones. |
| Flujo básico | 1. Recibir el ID del drone. 2. Verificar que no esté en misión. 3. Actualizar estado a `MANTENIMIENTO`. 4. Registrar fecha y razón. |

### AP-11 — Notificar fallo de drone en misión

| Campo | Contenido |
|---|---|
| Código | AP-11 |
| Nombre | Notificar fallo de drone en misión |
| Actor | Sistema (automático) → notifica a Técnico de Mantenimiento y Centro de Control |
| Descripción | Cuando un drone entra en estado `FALLO` durante una misión en curso, el sistema notifica automáticamente a todos los observadores registrados (Centro de Control, Técnico de Mantenimiento) y marca la misión como `ABORTADA` para que pueda reasignarse (ver AP-12). |
| Precondiciones | Existe una misión en estado `EN_TRANSITO` con un drone asignado. |
| Entrada | `droneId: String`, `misionId: String`, `razonFallo: String` |
| Salida | Notificación enviada a los observadores, estado del drone = `FALLO`, estado de la misión = `ABORTADA`. |
| Regla de negocio | La notificación debe emitirse en menos de 1 segundo desde la detección del fallo. |
| Flujo básico | 1. Detectar el cambio de estado del drone a `FALLO`. 2. Notificar a todos los observadores registrados. 3. Marcar la misión como `ABORTADA`. 4. Registrar el evento en el log con los 5 campos de trazabilidad. |

### AP-12 — Reasignar misión tras fallo de drone

| Campo | Contenido |
|---|---|
| Código | AP-12 |
| Nombre | Reasignar misión tras fallo de drone |
| Actor | Operador Hídrico, Sistema (sugerencia) |
| Descripción | Cuando una misión queda en estado `ABORTADA` por fallo del drone, el sistema propone automáticamente un nuevo drone candidato (aplicando la misma estrategia que correspondía según la prioridad de la misión) y se lo ofrece al operador para que confirme la reasignación. |
| Precondiciones | Existe una misión en estado `ABORTADA` con motivo "fallo de drone". |
| Entrada | `misionId: String` |
| Salida | `nuevoDroneAsignado.id: String` o `sinCandidatos: Boolean` |
| Regla de negocio | La nueva asignación respeta la prioridad original de la misión: si era CRITICA, aplica AP-08; si no, aplica AP-07. |
| Flujo básico | 1. Recibir la misión ABORTADA. 2. Filtrar candidatos disponibles. 3. Aplicar la estrategia correspondiente a la prioridad. 4. Proponer al operador. 5. Confirmar y actualizar la misión. |
| Flujo alterno | Sin candidatos → notificar al operador y dejar la misión en `PENDIENTE` para reintento manual. |

---

## Requisitos No Funcionales

### AP-RNF-01 — Rendimiento del algoritmo de asignación

**Descripción:** La asignación automática (AP-07 o AP-08) debe seleccionar
el drone óptimo en menos de **400 ms** para una flota de hasta **30 drones**.

**Métrica:** ≤ 400 ms · flota ≤ 30 drones · medido con JUnit 5
`assertTimeout(Duration.ofMillis(400), ...)`.

### AP-RNF-02 — Latencia de notificación de fallo

**Descripción:** Cuando un drone entra en `FALLO` (AP-11), todos los
observadores registrados deben recibir la notificación en menos de
**1 segundo**.

**Métrica:** ≤ 1 s · verificado con `assertTimeout` sobre el flujo
completo de notificación a 2+ observadores.

### AP-RNF-03 — Disponibilidad de la API Hídrica

**Descripción:** Si la API externa de Condiciones Hídricas (AP-09) no
responde en **3 segundos**, el sistema debe retornar el **último valor
conocido** marcado como `desactualizado = true`. No debe bloquear la
operación.

**Métrica:** 100% de consultas retornan en ≤ 3 s · verificado con Mockito
simulando timeout.

### AP-RNF-04 — Trazabilidad completa de eventos

**Descripción:** Cada evento de misión (asignación, fallo, aborto,
entrega) debe quedar registrado con: **timestamp**, **ID de misión**,
**ID del drone**, **actor** (sistema / operador / técnico) y **razón**
cuando aplique. El log debe ser consultable por rango de fecha y
exportable para auditoría legal del Centro de Control ECI.

**Métrica:** 100% de eventos con los 5 campos · verificado con prueba
de integración que simula el ciclo completo de una misión.

---

## MoSCoW aplicado a los 8 requisitos

| Código | Requisito | MoSCoW | Justificación |
|---|---|---|---|
| **AP-07** | Asignación por mayor batería (por defecto) | **Must Have** | Es el comportamiento por defecto del asignador automático. Núcleo de la v2. |
| **AP-08** | Prioridad absoluta a misiones CRÍTICAS | **Must Have** | Las misiones críticas no pueden fallar por falta de aptitud técnica. Sin esta prioridad, el sistema asignaría drones inadecuados a misiones de alta relevancia. |
| **AP-09** | Consulta de condiciones hídricas | **Should Have** | Mejora la calidad de la asignación, pero el sistema puede operar sin ella usando reglas estáticas de compatibilidad tipo↔zona. |
| **AP-10** | Marcar drone en mantenimiento | **Should Have** | El operador puede hacerlo manualmente si no hay técnico, pero centralizarlo mejora la operación. |
| **AP-11** | Notificar fallo de drone | **Must Have** | Sin esto, un drone en fallo puede quedar abandonado en una misión activa sin que nadie se entere. El Centro de Control no se entera, el Técnico no actúa, la misión no se reasigna. |
| **AP-12** | Reasignar misión tras fallo | **Should Have** | El operador puede reasignar manualmente si no está automatizado, pero la v2 promete reasignación automática según el HTML. |
| **AP-RNF-01** | Asignación ≤ 400 ms | **Must Have** | El operador está esperando la confirmación; más de 400 ms afecta la usabilidad. |
| **AP-RNF-02** | Notificación ≤ 1 s | **Must Have** | Un fallo no notificado rápido puede derivar en pérdida de equipo. |
| **AP-RNF-03** | Timeout API 3 s | **Should Have** | El sistema puede operar con datos "stale" por un tiempo, pero un bloqueo total no es aceptable. |
| **AP-RNF-04** | Trazabilidad completa | **Should Have** | La trazabilidad es requisito para auditoría legal del Centro de Control ECI. No bloquea la operación del día a día, pero sin ella no se puede cumplir el contrato con el Centro de Control. |

**Nota sobre RNF-04:** en la primera versión del documento se clasificó
como `Could Have`. Se reclasifica a `Should Have` por coherencia con el
dominio: el Centro de Control ECI registra misiones en bitácora legal,
y sin trazabilidad completa del lado de AquaPort, la auditoría legal se
rompe.

---

## Resolución de la tensión AP-07 ↔ AP-08

### Naturaleza de la tensión

El enunciado del curso introduce dos requisitos que **a primera vista
parecen contradictorios**:

- **AP-07** dice: *"El sistema asigna automáticamente el drone de mayor
  batería disponible para cualquier misión hídrica."*
- **AP-08** dice: *"Las misiones CRÍTICAS tienen prioridad absoluta: deben
  recibir el drone técnicamente más apto para la zona, **independientemente
  de la batería**."*

Si se aplica AP-07 literalmente ("para cualquier misión"), incluso las
misiones críticas reciben el drone de mayor batería. Eso contradice AP-08,
que exige priorizar la aptitud técnica sobre la batería.

### Punto clave: "cualquier misión hídrica" en AP-07

La lectura correcta de AP-07 es que describe el comportamiento **por
defecto**. La frase "para cualquier misión hídrica" se interpreta como
"para cualquier misión que no tenga una regla específica que la
sobreescriba". AP-08 es precisamente esa regla específica para misiones
CRÍTICAS.

**Los dos RF no se contradicen: describen el comportamiento del sistema
en dos contextos distintos, discriminados por la prioridad de la misión.**

| Prioridad de misión | RF aplicable | Criterio de selección |
|---|---|---|
| `CRITICA` | AP-08 | Aptitud técnica (tipo compatible + capacidad de carga). **No se usa batería como desempate.** |
| `ALTA`, `NORMAL`, `BAJA` | AP-07 | Mayor batería disponible. |

### Cómo se resuelve en la arquitectura (Reto 03 Prinplup)

Con el patrón **Strategy**:

- `EstrategiaMayorBateria` → implementa AP-07 para misiones no críticas.
- `EstrategiaPrioridadCritica` → implementa AP-08 para misiones críticas.
- `AsignadorAutomatico` **selecciona la estrategia según la prioridad** de
  la misión antes de delegar la selección.

### Consistencia con "independientemente de la batería"

AP-08 dice "independientemente de la batería". Por lo tanto, la
implementación de `EstrategiaPrioridadCritica` **NO debe usar la batería
como desempate**. Si dos drones tienen la misma aptitud técnica (mismo
tipo y misma capacidad de carga), el desempate se hace por **ID del drone**
en orden lexicográfico, para garantizar determinismo sin recurrir a la
batería.

**Nota importante:** "independientemente de la batería" **no** significa
que se ignoren los mínimos operativos. El drone elegido debe cumplir
batería ≥ 35% (regla de negocio general). La frase significa que la
batería **no se usa como criterio de selección** ni como desempate; no
que se acepten drones con batería insuficiente.

### Implementación de referencia (Reto 03)

```java
public class EstrategiaPrioridadCritica implements EstrategiaSeleccion {

    private final EstrategiaSeleccion fallback;

    public EstrategiaPrioridadCritica(EstrategiaSeleccion fallback) {
        this.fallback = fallback;
    }

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        if (mision.prioridad() != Prioridad.CRITICA) {
            return fallback.seleccionar(candidatos, mision);
        }
        // AP-08: aptitud técnica exclusivamente. Sin batería como desempate.
        return candidatos.stream()
                .max(Comparator.comparingInt(DroneAcuatico::capacidadCargaGramos)
                        .thenComparing(DroneAcuatico::id));  // ← desempate por ID, no por batería
    }
}
```

### Justificación de dominio

Una misión crítica (por ejemplo, una muestra médica urgente o un equipo
de medición pesado) **no puede fallar por falta de aptitud técnica**. La
batería es un criterio secundario en misiones críticas porque:

1. El drone sale con batería ≥ 35% por la regla de negocio general.
2. La misión tiene un destino específico donde puede recargar.
3. La aptitud técnica (tipo compatible + capacidad suficiente) determina
   si el drone **puede completar** la misión; la batería solo determina
   cuánto tarda en necesitar recarga.

### Trazabilidad de la decisión

- La **regla de selección** vive en `EstrategiaPrioridadCritica` (retos 03-04 Prinplup).
- La **selección de la estrategia según prioridad** vive en `AsignadorAutomatico`.
- La **auditoría** de que la estrategia respeta AP-08 se verifica con test
  unitario que construye candidatos con distinta capacidad y batería, y
  confirma que el elegido es el de mayor capacidad sin importar la batería.