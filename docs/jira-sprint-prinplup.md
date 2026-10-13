# Sprint Planning — AquaPort Prinplup v2.0.0

Documento de planificación ágil del sprint que cierra el nivel Prinplup.
Incluye épica, features, historias de usuario, subtareas, estimaciones
Fibonacci, DoD y retrospectiva.

---

## 1. Contexto del sprint

| Campo | Valor |
|---|---|
| **Proyecto** | AquaPort |
| **Nivel** | Prinplup (v2.0.0) |
| **Sprint** | Sprint 2 — Asignación automática y observadores |
| **Duración** | 2 semanas (10 días hábiles) |
| **Equipo** | 1 developer (Juan Garzón) |
| **Capacidad estimada** | 40 story points (basado en velocity del Sprint 1) |

### Velocity histórico

| Sprint | Velocity | Notas |
|---|---|---|
| Sprint 1 (Piplup) | 34 SP | MVP con 76 tests, 85% cobertura, 0 issues SonarQube |
| **Sprint 2 (Prinplup)** | **40 SP** (estimado) | Se espera +15% por curva de aprendizaje |

**Nota metodológica:** la velocity inicial se calibra después del Sprint 1.
El +15% se justifica porque el equipo (1 dev) ya conoce el dominio y el
stack, y tiene menos curvas de aprendizaje en este sprint.

---

## 2. Sprint Goal

> **"Completar el asignador automático con Strategy y las alertas con
> Observer, dejando la v2 funcionalmente cerrada y con cobertura ≥80%."**

**Criterio de éxito:** una misión puede asignarse automáticamente según
su prioridad (AP-07 o AP-08), y ante un fallo de drone se notifica a
todos los observadores.

---

## 3. Épica del sprint

### Épica: Digitalizar el monitoreo y reparto hídrico de la ECI con flota autónoma

| Campo | Valor |
|---|---|
| **Tipo** | Épica |
| **Descripción** | Extender el MVP de AquaPort con asignación automática de drones, alertas de fallo y gestión descentralizada por técnico. La épica agrupa los 3 patrones de diseño de Prinplup (Strategy, Observer, Factory Method) y las integraciones externas. |
| **Criterio de cierre** | El sistema asigna drones sin intervención manual, notifica fallos en <1s y permite marcar drones en mantenimiento desde el panel del técnico. |
| **Prioridad** | Must Have |
| **Estado** | In Progress |

---

## 4. Features del sprint

### Feature 1: Asignación automática de drones

| Campo | Valor |
|---|---|
| **Épica padre** | Digitalizar el monitoreo con flota autónoma |
| **Descripción** | El sistema selecciona automáticamente el drone óptimo para cada misión según su prioridad (AP-07: mayor batería; AP-08: aptitud técnica para críticas). |
| **Valor para el usuario** | El operador deja de asignar manualmente y solo supervisa. |
| **Story Points totales** | 21 SP |

### Feature 2: Alertas de fallo y observadores

| Campo | Valor |
|---|---|
| **Épica padre** | Digitalizar el monitoreo con flota autónoma |
| **Descripción** | Cuando un drone entra en FALLO, el sistema notifica automáticamente al Centro de Control y al Técnico de Mantenimiento. |
| **Valor para el usuario** | Los fallos no quedan abandonados. |
| **Story Points totales** | 13 SP |

### Feature 3: Factory Method y gestión por técnico

| Campo | Valor |
|---|---|
| **Épica padre** | Digitalizar el monitoreo con flota autónoma |
| **Descripción** | La fábrica de drones permite crear los 3 tipos sin switch; el técnico puede marcar drones en mantenimiento desde su panel. |
| **Valor para el usuario** | Extensibilidad del sistema sin tocar código existente. |
| **Story Points totales** | 6 SP |

---

## 5. Historias de Usuario

### Feature 1 — Asignación automática

#### HU-01 — Asignación por mayor batería (AP-07)

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Asignación automática |
| **Título** | Asignar drone por mayor batería a misiones no críticas |
| **Descripción** | **Como** operador hídrico, **quiero** que el sistema asigne automáticamente el drone de mayor batería disponible cuando la misión no sea crítica, **para** reducir el tiempo de asignación y no tener que elegir manualmente entre 15 drones. |
| **Criterios de aceptación** | 1. Para misiones con prioridad ALTA, NORMAL o BAJA, elige el drone disponible con mayor batería.<br>2. El drone debe ser compatible con la zona destino.<br>3. El drone debe tener batería ≥ 35%.<br>4. El drone debe tener capacidad de carga ≥ peso de la misión.<br>5. Se notifica al Centro de Control con el ID del drone asignado. |
| **Story Points** | **5 SP** |
| **Prioridad** | Must Have |

#### HU-02 — Asignación por aptitud técnica para críticas (AP-08)

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Asignación automática |
| **Título** | Asignar drone técnicamente más apto a misiones CRÍTICAS |
| **Descripción** | **Como** operador hídrico, **quiero** que el sistema asigne a las misiones críticas el drone técnicamente más apto (mayor capacidad de carga, tipo compatible), **independientemente de la batería**, **para** garantizar que las misiones de alta relevancia no fallen por falta de aptitud técnica. |
| **Criterios de aceptación** | 1. Para misiones con prioridad CRITICA, elige el drone con mayor capacidad de carga.<br>2. La batería **NO** se usa como desempate (se desempata por ID).<br>3. Aun así, el drone debe tener batería ≥ 35% (mínimo operativo).<br>4. Se notifica al Centro de Control con la razón de selección. |
| **Story Points** | **5 SP** |
| **Prioridad** | Must Have |

#### HU-03 — Filtro de candidatos por compatibilidad y capacidad

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Asignación automática |
| **Título** | Filtrar drones candidatos por compatibilidad y capacidad |
| **Descripción** | **Como** operador hídrico, **quiero** que el sistema descarte drones no compatibles con la zona o con capacidad insuficiente, **para** que solo se consideren candidatos aptos para la misión. |
| **Criterios de aceptación** | 1. Solo drones con `tipo().esCompatibleCon(zonaDestino)`.<br>2. Solo drones con batería ≥ 35%.<br>3. Solo drones con capacidad ≥ peso de la carga.<br>4. Solo drones en estado DISPONIBLE. |
| **Story Points** | **3 SP** |
| **Prioridad** | Must Have |

#### HU-04 — Razón de selección visible

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Asignación automática |
| **Título** | Explicar al operador por qué se eligió el drone |
| **Descripción** | **Como** operador hídrico, **quiero** ver en la pantalla la razón por la que el sistema eligió ese drone (mayor batería, aptitud técnica, etc.), **para** confiar en la decisión automática. |
| **Criterios de aceptación** | 1. La pantalla de detalle muestra la razón en texto claro.<br>2. Se indica qué estrategia se aplicó (MAYOR_BATERIA o PRIORIDAD_CRITICA).<br>3. Si la misión es CRITICA, la razón menciona "aptitud técnica". |
| **Story Points** | **5 SP** |
| **Prioridad** | Should Have |

**Subtotal Feature 1: 21 SP**

### Feature 2 — Alertas de fallo

#### HU-05 — Notificar fallo a todos los observadores

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Alertas de fallo |
| **Título** | Notificar fallo de drone a todos los observadores registrados |
| **Descripción** | **Como** técnico de mantenimiento, **quiero** recibir una notificación cuando un drone entra en FALLO, **para** revisar el equipo antes de que afecte a más misiones. |
| **Criterios de aceptación** | 1. Cuando un drone cambia a FALLO, se notifica a todos los observadores.<br>2. La notificación se emite en <1 segundo.<br>3. Cada observador recibe la notificación exactamente **una vez**.<br>4. La misión asociada se marca como ABORTADA. |
| **Story Points** | **5 SP** |
| **Prioridad** | Must Have |

#### HU-06 — Observador del Centro de Control

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Alertas de fallo |
| **Título** | Registrar eventos de misión en el Centro de Control |
| **Descripción** | **Como** administrador ECI, **quiero** que el Centro de Control registre cada asignación y cada fallo en su bitácora, **para** cumplir con la auditoría legal. |
| **Criterios de aceptación** | 1. `ObservadorCentroControl` implementa `ObservadorMision`.<br>2. Loggea asignaciones exitosas con nivel INFO.<br>3. Loggea fallos con nivel WARNING.<br>4. El log incluye ID de misión, ID de drone y razón. |
| **Story Points** | **3 SP** |
| **Prioridad** | Must Have |

#### HU-07 — Observador del Técnico de Mantenimiento

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Alertas de fallo |
| **Título** | Notificar al técnico cuando hay fallos |
| **Descripción** | **Como** técnico de mantenimiento, **quiero** recibir alertas cuando hay fallos, **para** priorizar la revisión de equipos comprometidos. |
| **Criterios de aceptación** | 1. `ObservadorTecnicoMantenimiento` implementa `ObservadorMision`.<br>2. Solo implementa `notificarFalloAsignacion` (ISP).<br>3. Loggea con nivel WARNING la razón del fallo. |
| **Story Points** | **2 SP** |
| **Prioridad** | Must Have |

**Subtotal Feature 2: 13 SP**

### Feature 3 — Factory Method y gestión por técnico

#### HU-08 — Marcar drone en mantenimiento desde panel del técnico

| Campo | Valor |
|---|---|
| **Tipo** | Historia de Usuario |
| **Feature padre** | Gestión por técnico |
| **Título** | Marcar drone en mantenimiento |
| **Descripción** | **Como** técnico de mantenimiento, **quiero** marcar drones como MANTENIMIENTO desde mi panel, **para** evitar que sean asignados a misiones mientras los reviso. |
| **Criterios de aceptación** | 1. El técnico selecciona un drone y lo marca MANTENIMIENTO.<br>2. Un drone con misión activa no puede marcarse.<br>3. El cambio se registra con timestamp y razón. |
| **Story Points** | **3 SP** |
| **Prioridad** | Should Have |

**Subtotal Feature 3: 6 SP**

---

## 6. Resumen del sprint

| Feature | HU | SP | Prioridad |
|---|---|---|---|
| Asignación automática | HU-01 | 5 | Must Have |
| | HU-02 | 5 | Must Have |
| | HU-03 | 3 | Must Have |
| | HU-04 | 3 | Must Have |
| | HU-05 | 5 | Should Have |
| Alertas de fallo | HU-06 | 5 | Must Have |
| | HU-07 | 3 | Must Have |
| | HU-08 | 3 | Must Have |
| | HU-09 | 2 | Must Have |
| Factory Method | HU-10 | 3 | Must Have |
| Gestión por técnico | HU-11 | 3 | Should Have |
| **Total** | **11 HU** | **40 SP** | |

**Capacidad estimada del sprint:** 40 SP.
**Comprometido:** 40 SP.
**Velocity esperada:** 40 SP (basada en Sprint 1 con +15% de mejora).

---

## 7. Definition of Done (DoD)

Una HU está terminada cuando **todos** estos criterios se cumplen:

- [ ] **Código compila** sin warnings (`mvn clean compile` → BUILD SUCCESS).
- [ ] **Tests unitarios** pasan (JUnit 5, todos en verde).
- [ ] **Cobertura JaCoCo ≥ 80%** para el bundle completo.
- [ ] **Sin code smells críticos** en SonarQube (0 bugs, 0 vulnerabilities, deuda < 30 min).
- [ ] **PR revisado y aprobado** por al menos otro miembro del equipo (o el líder técnico).
- [ ] **Commit atómico** con mensaje siguiendo Conventional Commits.
- [ ] **Javadoc actualizado** en las clases modificadas.
- [ ] **Sin `System.out.println`** en código de producción (usar Logger).
- [ ] **Diagrama C4 o de CU** actualizado si la HU introduce nuevos flujos.
- [ ] **README actualizado** si la HU introduce funcionalidad visible.

---

## 8. Retrospectiva del Sprint 1 (Piplup)

### Qué funcionó bien ✅

- **TDD estricto** en `ValidadorMision`: los tests se escribieron primero, el código pasó a verde a la primera.
- **Refactor de paquetes** antes de añadir Prinplup: evitó deuda técnica en el Sprint 2.
- **Exclusión de la clase demo** en JaCoCo y SonarQube: sin ella, el bundle no llegaba al 80%.

### Qué NO funcionó ❌

- **Commits agrupados** al principio del Sprint 1: mezclar refactor + feature + test en un solo commit dificultó el review.
- **Tests con lambdas compuestas** en `assertThrows`: Sonar detectó `java:S5778` y hubo que reescribirlos.
- **Uso de `System.out.println`** inicial: hubo que migrar a Logger después, generando deuda técnica temprana.

### Qué cambiar en el Sprint 2 🔄

1. **Commits atómicos desde el inicio:** un commit por cambio lógico (feat, test, docs, refactor).
2. **`assertThrows` con lambdas de una sola invocación** desde el principio (extraer argumentos).
3. **Logger desde el día 1** en clases nuevas (no `System.out.println`).
4. **Ejecutar SonarQube en cada feature branch** antes del PR, no después del merge.
5. **Documentar los flujos alternos** en la plantilla DOSW antes de implementar.

---

## 9. Riesgos e impedimentos

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| **Migración de `DroneAcuatico` de record a clase** puede romper tests existentes | Alta | Alto | Actualizar tests en el mismo commit; usar `TestFixtures` helpers |
| **Cambio de firma de `ObservadorMision`** (agregar parámetro) puede romper tests | Media | Medio | Actualizar tests con `verify(obs, times(1)).notificarAsignacion(eq(m), any())` |
| **Curva de aprendizaje de ArchUnit** si se decide implementarlo | Baja | Bajo | Documentar sin implementar en este sprint |
| **Tiempo de espera por PR review** si el equipo crece | Media | Medio | Definir un SLA de 24 h para PRs en el DoD |

---

## 10. Captura del sprint activo (Jira)

![](/docs/jira/jira-tablero.png)

---

## 10. Trazabilidad con los requisitos

| HU | RF cubierto | RNF relacionado |
|---|---|---|
| HU-01 | AP-07 | AP-RNF-01 |
| HU-02 | AP-08 | AP-RNF-01 |
| HU-03 | (arquitectura) | — |
| HU-04 | AP-07, AP-08 | — |
| HU-05 | AP-07, AP-08 | AP-RNF-04 |
| HU-06 | AP-11 | AP-RNF-02 |
| HU-07 | AP-11 | — |
| HU-08 | AP-11 | AP-RNF-04 |
| HU-09 | AP-11 | — |
| HU-10 | (arquitectura) | — |
| HU-11 | AP-10 | — |