# Sistema de Diseño y UX — AquaPort Prinplup (v2.0.0)

Documento de diseño UX/UI de la versión Prinplup. Amplía el manual de
identidad de Piplup (`docs/identidad-aquaport.md`) con nuevos componentes
para el flujo de asignación automática.

---

## 1. Tokens de diseño

### 1.1 Colores

Reutilizamos la paleta definida en el manual de identidad de Piplup y
añadimos dos tokens nuevos para v2:

| Token | Hex | Uso |
|---|---|---|
| `--bg` | `#0A2540` | Fondo base (Azul Profundo) |
| `--surface` | `#0F3A5F` | Superficies elevadas (Azul Océano) |
| `--surface-2` | `#163A5F` | Superficies elevadas (segundo nivel) |
| `--accent` | `#22D3EE` | Acento (Cian Técnico) |
| `--accent-hover` | `#0891B2` | Hover del acento |
| `--text` | `#F0F9FF` | Texto principal |
| `--text-muted` | `#94A3B8` | Texto secundario |
| `--border` | `#1E3A5F` | Bordes |
| **NUEVO:** `--danger` | `#FB7185` | Errores y estado FALLO |
| **NUEVO:** `--warning` | `#FBBF24` | Advertencias y estado RECARGANDO |

### 1.2 Colores de estado de drone

| Estado | Color | Hex | Significado |
|---|---|---|---|
| `DISPONIBLE` | Verde | `#34D399` | Listo para recibir misiones |
| `EN_MISION` | Azul claro | `#38BDF8` | Ocupado en una misión activa |
| `RECARGANDO` | Ámbar | `#FBBF24` | En carga; se liberará en breve |
| `MANTENIMIENTO` | Gris | `#94A3B8` | Fuera de servicio programado |
| `FALLO` | Rojo suave | `#FB7185` | Requiere acción inmediata |
| `SUMERGIDO` | Azul oscuro | `#1E40AF` | Bajo la superficie (drone buceador) |

**Nota de accesibilidad:** todos los colores de estado cumplen contraste
WCAG AA (≥ 4.5:1) contra el fondo `#0A2540`. Los usuarios daltónicos
pueden distinguir los estados por el **texto** además del color.

### 1.3 Tipografía

| Uso | Fuente | Pesos |
|---|---|---|
| Interfaz general | DM Sans | 300, 400, 500, 600, 700 |
| IDs y datos técnicos (AR-XX, M-XXX, baterías) | DM Mono | 400, 500 |

### 1.4 Espaciado

Escala base 4px: `4, 8, 12, 16, 24, 32, 48, 64`.

### 1.5 Radios

| Elemento | Radio |
|---|---|
| Botones | `8px` |
| Tarjetas | `12px` |
| Modales | `16px` |
| Pills / badges | `999px` (completamente redondeados) |

---

## 2. Componentes nuevos

### 2.1 Tarjeta de drone acuático (DroneCard)

**Dimensiones:** 280×180 px (grid responsive).

**Anatomía:**

```
┌───────────────────────────────────────────┐
│  [●] AR-06 · BUCEADOR            [⋯]      │  ← header
│                                            │
│  ████████████░░░░░░░  55%                 │  ← batería
│                                            │
│  Embalse Norte                             │  ← zona actual
│                                            │
│  [ Asignar misión ]                        │  ← acción principal
└───────────────────────────────────────────┘
```

**6 estados visuales:**

| Estado | Header (dot) | Borde | Botón principal | Nota |
|---|---|---|---|---|
| `DISPONIBLE` | Verde | Verde suave | Habilitado, "Asignar misión" | Estado ideal |
| `EN_MISION` | Azul claro | Azul suave | Deshabilitado, "En misión" | No asignable |
| `RECARGANDO` | Ámbar | Ámbar suave | Deshabilitado, "Recargando (ETA 12 min)" | ETA visible |
| `MANTENIMIENTO` | Gris | Gris suave | Deshabilitado, "En mantenimiento" | Motivo visible en tooltip |
| `FALLO` | Rojo | Rojo destacado | "Notificar al técnico" (acción alternativa) | Borde pulsante sutil |
| `SUMERGIDO` | Azul oscuro | Azul oscuro | Deshabilitado, "Bajo superficie" | Con profundidad si aplica |

**Estados de interacción del componente:**

- **Default:** sin hover.
- **Hover:** elevación sutil, borde `--accent`.
- **Selected:** borde `--accent` de 2px, fondo `--surface-2`.
- **Focus:** outline `--accent` accesible con teclado.
- **Disabled:** opacidad 0.5, cursor `not-allowed`.
- **Loading:** spinner sobre la tarjeta mientras se procesa la asignación.

### 2.2 Indicador de batería

Componente pequeño, se incrusta dentro de `DroneCard`. Tiene 3 estados de color:

| Rango | Color | Icono | Texto |
|---|---|---|---|
| **≥ 60%** | Verde `#34D399` | Batería llena | `92%` |
| **35 – 59%** | Ámbar `#FBBF24` | Batería media | `45%` |
| **< 35%** | Rojo `#FB7185`| Batería crítica | `18% · crítica` |

**Regla de negocio visual:** cuando la batería es < 35%, el botón
"Asignar misión" de la tarjeta se **deshabilita automáticamente**, incluso
si el estado es DISPONIBLE. Esto previene el error antes de que ocurra
(Nielsen #5 — Prevención de errores).

### 2.3 Botón "Asignar misión"

**5 estados visuales:**

| Estado | Fondo | Texto | Cursor |
|---|---|---|---|
| **Default** | `--accent` | "Asignar misión" | pointer |
| **Hover** | `--accent-hover` | "Asignar misión" | pointer |
| **Procesando** | `--accent` + spinner | "Asignando..." | wait |
| **Éxito** | Verde `#34D399` | "Asignado" | default |
| **Deshabilitado** | `--border` | "No disponible" | not-allowed |

### 2.4 Botón de emergencia (⚠ Nuevo en v2)

**Ubicación:** esquina superior derecha del panel de flota, siempre visible.

**Dimensiones:** 72×72 px (grande, imposible de ignorar).

**Color:** rojo `#FB7185` con icono de alerta.

**Comportamiento:** abre un modal que permite:

- Abortar todas las misiones activas.
- Marcar todos los drones en FALLO.
- Notificar al Centro de Control y al Técnico simultáneamente.

**Justificación con Ley de Fitts (ver sección 3.1).**

---

## 3. Leyes UX aplicadas

### 3.1 Ley de Fitts — Botón de emergencia

> **Ley de Fitts:** el tiempo para alcanzar un objetivo es función de la
> distancia al objetivo y del tamaño del objetivo. Cuanto más grande y
> más cerca, más rápido se alcanza.

**Aplicación en AquaPort v2:**

- **Tamaño:** el botón de emergencia es **72×72 px** (vs. 32×32 px de los
  botones normales). Es 5 veces más grande en área.
- **Ubicación:** esquina superior derecha, siempre visible. Los usuarios
  tienen una expectativa cultural de encontrar acciones críticas en las
  esquinas (efecto "hot corner").
- **Distancia:** siempre en el mismo lugar, no hay que buscarlo. El
  operador desarrolla memoria muscular.
- **Área de click ampliada:** el padding interno (16px) hace que el área
  real de click sea 88×88 px.

**Por qué importa en el dominio:** en una emergencia (drone en fallo
crítico, condición hídrica adversa), cada segundo cuenta. Un botón
pequeño o escondido retrasa la acción. La Ley de Fitts garantiza que el
operador pueda accionarlo en <500 ms desde cualquier punto del panel.

### 3.2 Ley de Hick — Agrupación por tipo en el panel de flota

> **Ley de Hick:** el tiempo para tomar una decisión aumenta
> logarítmicamente con el número de opciones.

**Aplicación en AquaPort v2:**

Con **15 drones** en la flota, presentarlos como una lista plana de 15
opciones es cognitivamente costoso. El panel los organiza en **3 grupos
colapsables por tipo**:

```
▼ SUPERFICIALES (5)            ← click para colapsar
  ├─ AR-01 · 92% · Canal Central
  ├─ AR-02 · 78% · Punto Ribereño Este
  ├─ AR-03 · 45% · Laboratorio Hídrico
  ├─ AR-04 · 60% · Canal Central (EN_MISION)
  └─ AR-05 · 22% · Punto Ribereño Este (MANTENIMIENTO)

▶ SEMISUMERGIDOS (5)           ← colapsado por defecto
▶ BUCEADORES (5)               ← colapsado por defecto
```

**Reducción de la carga cognitiva:**

- **Antes:** 15 opciones simultáneas → decisión lenta.
- **Después:** 3 grupos visibles + 1 grupo expandido con 5 opciones → decisión 3× más rápida.

Cuando el operador busca un drone para una zona específica, ya sabe qué
tipo necesita (por ejemplo, Embalse Norte → solo BUCEADOR). Puede
expandir directamente el grupo relevante.

**Por qué importa en el dominio:** en una asignación en vivo, el operador
no quiere "explorar" 15 drones. Quiere filtrar por tipo y ver solo los
candidatos. La agrupación reduce la decisión de 15 a 3.

### 3.3 Ley de Miller — Presentación por bloques de 3-4

> **Ley de Miller:** la mente humana puede retener entre 5 y 9 elementos
> en memoria de corto plazo. Bloques de 3-4 son más fáciles de escanear
> que listas largas.

**Aplicación en AquaPort v2:**

- **Tarjetas de drone:** grid de 3 columnas máx. Nunca 4+ en una fila.
- **Métricas del panel:** 3 indicadores arriba (Drones activos, Misiones
  en curso, Alertas). No 5 ni 7.
- **Datos de cada tarjeta:** máximo 4 datos visibles (ID, tipo, batería,
  zona). El resto se oculta en el detalle.

**Por qué importa en el dominio:** el operador escanea el panel en
segundos. Una tarjeta con 8 datos tarda más en procesarse que una con 4.

---

## 4. Flujo de asignación automática (3 pantallas)

### Pantalla 1 — Panel de flota

**Objetivo:** el operador ve el estado completo de la flota y elige
"iniciar asignación".

**Elementos:**

- Header con logo, título "Panel de Monitoreo de Flota", contador "3 de 15 disponibles".
- **Botón de emergencia** (arriba a la derecha, 72×72 px, rojo).
- **Panel de 3 grupos colapsables** por tipo (Ley de Hick).
- Cada grupo muestra `DroneCard` de sus miembros.
- Grid responsive de 3 columnas máx (Ley de Miller).

**Acción primaria:** botón grande "Iniciar asignación automática" en la
parte inferior.

### Pantalla 2 — Detalle de la misión y drone recomendado

**Objetivo:** el sistema propone un drone y explica **por qué**.

**Elementos:**

- **Card de la misión** con:
  - ID de misión (M-XXX).
  - Zona origen → zona destino (con flecha).
  - Tipo de carga y peso.
  - Prioridad (con badge de color: CRITICA = rojo).
- **Card del drone recomendado**:
  - `DroneCard` del drone elegido.
  - **Razón de la selección** en texto claro:
    *"Mayor batería entre los BUCEADORES compatibles con Embalse Norte"*.
  - **Estrategia aplicada**: badge "AP-07 · MAYOR BATERÍA" o
    "AP-08 · PRIORIDAD CRÍTICA".
- **Botones:** "Confirmar asignación" (primario) y "Elegir otro drone"
  (secundario, lleva a lista de candidatos).
- **Alerta informativa** si la estrategia aplicada no es la por defecto
  (ej. CRITICA → AP-08).

### Pantalla 3 — Confirmación

**Objetivo:** el operador ve el resultado y puede continuar.

**Elementos:**

- **Toast / banner de éxito:** *"Misión M-001 asignada al drone AR-06."*
- **Resumen de la asignación:**
  - ID de misión.
  - ID y tipo de drone.
  - Hora exacta de asignación.
  - Estrategia aplicada (con enlace a la explicación).
- **Estado actualizado del drone:** ahora EN_MISION.
- **Actualización del contador:** "2 de 15 disponibles".
- **Botón secundario:** "Asignar otra misión".
- **Historial reciente:** lista de las últimas 3 asignaciones (Ley de Miller).

---

## 5. Resumen de aplicación por ley

| Ley | Componente | Aplicación concreta | Por qué importa en AquaPort |
|---|---|---|---|
| **Fitts** | Botón de emergencia | 72×72 px, esquina superior derecha, área de click ampliada | Emergencias requieren acción en <500 ms |
| **Hick** | Panel de flota | 3 grupos colapsables por tipo (Superficial, Semisumergido, Buceador) | 15 drones son demasiadas opciones simultáneas |
| **Miller** | Todas las pantallas | Máx. 3 columnas, máx. 4 datos por tarjeta, máx. 3 métricas arriba | Escaneo visual rápido sin sobrecarga |

---

## 6. Coherencia con el manual de identidad de Piplup

Todos los componentes reutilizan:

- Paleta `#0A2540` / `#22D3EE` / `#F0F9FF` definida en `identidad-aquaport.md`.
- Tipografía DM Sans + DM Mono.
- Tono de voz técnico y preciso (sin emojis ni lenguaje coloquial).
- Accesibilidad WCAG AA en colores de estado.

**Lo nuevo en v2:**

- 3 estados de drone adicionales (FALLO, SUMERGIDO, RECARGANDO con ETA).
- Componente "botón de emergencia" (nuevo).
- Agrupación por tipo en el panel de flota (nuevo).
- Flujo de 3 pantallas para la asignación automática (nuevo).

---

## 7. Anexo — Mock textual del flujo

### Pantalla 1 — Panel de flota

```
╔══════════════════════════════════════════════════════════════════════╗
║   AquaPort · Panel de Monitoreo de Flota         [⚠ EMERGENCIA]      ║
║                                           3 de 15 disponibles        ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                      ║
║  ▼ SUPERFICIALES (5)                                                 ║
║  ┌──────────────┬──────────────┬──────────────┐                      ║
║  │    AR-01     │    AR-02     │    AR-03     │                      ║
║  │ 92% · Canal  │ 78% · Ribera │ 45% · Lab    │                      ║
║  │ [Asignar]    │ [Asignar]    │ [Asignar]    │                      ║
║  └──────────────┴──────────────┴──────────────┘                      ║
║                                                                      ║
║  ▶ SEMISUMERGIDOS (5)                                                ║
║  ▶ BUCEADORES (5)                                                    ║
║                                                                      ║
║  ┌────────────────────────────────────────────────────────────┐      ║
║  │         [ Iniciar asignación automática ]                  │      ║
║  └────────────────────────────────────────────────────────────┘      ║
╚══════════════════════════════════════════════════════════════════════╝
```

### Pantalla 2 — Detalle de misión y recomendación

```
╔══════════════════════════════════════════════════════════════════════╗
║  ← Volver al panel                                                   ║
║                                                                      ║
║  MISIÓN M-001                                                        ║
║  ┌──────────────────────────────────────────────────────────────┐    ║
║  │ Embalse Norte  →  Laboratorio Hídrico                        │    ║
║  │ MUESTRA_AGUA · 200g                                          │    ║
║  │ [CRITICA]                                                    │    ║
║  └──────────────────────────────────────────────────────────────┘    ║
║                                                                      ║
║  DRONE RECOMENDADO POR EL SISTEMA                                    ║
║  ┌──────────────────────────────────────────────────────────────┐    ║
║  │ AR-06 · BUCEADOR                                             │    ║
║  │ ████████████░░░░░░░  55%                                     │    ║
║  │ Embalse Norte                                                │    ║
║  │                                                              │    ║
║  │ Razón: Aptitud técnica máxima entre los BUCEADORES           │    ║
║  │ compatibles con Embalse Norte.                               │    ║
║  │ [AP-08 · PRIORIDAD CRÍTICA]                                  │    ║
║  └──────────────────────────────────────────────────────────────┘    ║
║                                                                      ║
║  ┌────────────────────────┐  ┌────────────────────────┐              ║
║  │  Confirmar asignación  │  │   Elegir otro drone    │              ║
║  └────────────────────────┘  └────────────────────────┘              ║
╚══════════════════════════════════════════════════════════════════════╝
```

### Pantalla 3 — Confirmación

```
╔══════════════════════════════════════════════════════════════════════╗
║  ✓ Misión M-001 asignada al drone AR-06                              ║
║                                                                      ║
║  Resumen:                                                            ║
║  · Misión: M-001 (CRITICA)                                           ║
║  · Drone: AR-06 (BUCEADOR)                                           ║
║  · Asignada: 2026-10-12 18:30:00                                     ║
║  · Estrategia: AP-08 (PRIORIDAD CRÍTICA)                             ║
║                                                                      ║
║  Estado actualizado:                                                 ║
║  · Drone AR-06 → EN_MISION                                           ║
║  · Disponibles: 2 de 15                                              ║
║                                                                      ║
║  Últimas 3 asignaciones:                                             ║
║  · M-001 → AR-06  (ahora)                                            ║
║  · M-099 → AR-12  (hace 5 min)                                       ║
║  · M-098 → AR-04  (hace 12 min)                                      ║
║                                                                      ║
║  ┌────────────────────────┐                                          ║
║  │  Asignar otra misión   │                                          ║
║  └────────────────────────┘                                          ║
╚══════════════════════════════════════════════════════════════════════╝
```

---

## 8. Trazabilidad

| Elemento | Reto origen | Reutilización en Prinplup |
|---|---|---|
| Paleta de colores | Reto 08 Piplup | Reutilizada tal cual |
| Tipografía | Reto 08 Piplup | Reutilizada tal cual |
| Estados de drone | Reto 03 Prinplup | 6 estados vs 5 en MVP |
| Ley de Fitts | Nuevo en v2 | Botón de emergencia |
| Ley de Hick | Nuevo en v2 | Agrupación por tipo |
| Ley de Miller | Nuevo en v2 | Bloques de 3-4 |