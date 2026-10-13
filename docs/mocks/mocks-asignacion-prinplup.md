# Mocks del flujo de asignación automática — AquaPort Prinplup

Documento de mocks generados con IA para el flujo completo de asignación
automática de la v2. Incluye 3 pantallas del flujo normal (heredadas del
reto 08) y 1 pantalla adicional para el estado de alerta de una misión
CRÍTICA sin drone disponible.

## Estructura del flujo

```
Flujo normal:
  1. Panel de flota              → el operador inicia la asignación
  2. Detalle de misión           → el sistema propone un drone
  3. Confirmación                → la asignación se registra

Flujo excepcional (alerta):
  4. Sin drone disponible (CRÍTICA) → el sistema no puede asignar
```

---

## Reutilización del reto 08

Los mocks de las pantallas 1, 2 y 3 fueron diseñados y generados en el
Reto 08 Prinplup (sistema de diseño UX v2). Se reutilizan tal cual porque
el flujo no cambia entre ambos retos: la única diferencia es que el reto
11 pide adjuntar los prompts explícitamente.

| Mock | Origen | Archivo |
|---|---|---|
| Pantalla 1 — Panel de flota | Reto 08 | `docs/mocks/panel-flota-prinplup.png` |
| Pantalla 2 — Detalle de misión | Reto 08 | `docs/mocks/detalle-mision-prinplup.png` |
| Pantalla 3 — Confirmación | Reto 08 | `docs/mocks/confirmacion-prinplup.png` |
| **Pantalla 4 — Alerta CRÍTICA** | **Nuevo** | `docs/mocks/alerta-critica-prinplup.png` |

---

## Prompt 1 — Panel de flota (reutilizado del reto 08)

```
Actúa como diseñador UX senior de sistemas de monitoreo de flotas.

SISTEMA: AquaPort · Panel de flota de drones acuáticos (v2.0.0)
PANTALLA: Panel de monitoreo de flota con 15 drones

ESTILO:
- Fondo: #0A2540 (azul profundo)
- Superficies: #0F3A5F
- Acento: #22D3EE (cian)
- Tipografía UI: DM Sans, datos: DM Mono

LAYOUT:
- Header: logo "AquaPort" izquierda, contador "3 de 15 disponibles" derecha
- Botón EMERGENCIA grande (72×72 px) arriba a la derecha en rojo #FB7185
- Panel de 3 grupos colapsables: "▼ SUPERFICIALES (5)",
  "▶ SEMISUMERGIDOS (5)", "▶ BUCEADORES (5)"
- Dentro del grupo expandido: grid de 3 tarjetas máx.
- Cada tarjeta: ID (AR-XX), batería con barra de color, zona actual,
  botón "Asignar misión"

ESTADOS DE DRONE VISIBLES:
- DISPONIBLE (verde #34D399)
- EN_MISION (azul claro #38BDF8)
- MANTENIMIENTO (gris #94A3B8)
- FALLO (rojo #FB7185)

APLICAR:
- Ley de Hick: 3 grupos colapsables en vez de 15 drones en lista plana
- Ley de Fitts: botón de emergencia grande y en esquina
- Ley de Miller: máximo 3 columnas y 4 datos por tarjeta
```

**Mock generado:** `docs/mocks/panel-flota-prinplup.png`

---

## Prompt 2 — Detalle de misión y drone recomendado (reutilizado)

```
Actúa como diseñador UX senior de sistemas de monitoreo ambiental.

SISTEMA: AquaPort · Pantalla de detalle de misión (v2.0.0)
PANTALLA: Detalle de la misión con drone recomendado por el sistema

ESTILO:
- Mismo sistema de diseño que el panel de flota
- Fondo: #0A2540, superficies: #0F3A5F, acento: #22D3EE
- Tipografía: DM Sans + DM Mono

CONTENIDO:
- Card de misión:
  · ID: M-001
  · Zona origen → zona destino: Embalse Norte → Laboratorio Hídrico
  · Tipo de carga: MUESTRA_AGUA · 200g
  · Prioridad: CRITICA (badge rojo #FB7185)

- Card del drone recomendado:
  · ID: AR-06 (DM Mono)
  · Tipo: BUCEADOR
  · Batería: 55% (barra verde)
  · Zona actual: Embalse Norte
  · Razón de selección (texto claro):
    "Aptitud técnica máxima entre los BUCEADORES compatibles
     con Embalse Norte."
  · Badge de estrategia: "AP-08 · PRIORIDAD CRÍTICA" (ámbar #FBBF24)

- Botones al pie:
  · "Confirmar asignación" (primario, cian #22D3EE)
  · "Elegir otro drone" (secundario, borde cian)

APLICAR:
- Ley de Miller: máximo 4 datos en la card del drone
- Jerarquía visual: la razón de selección debe ser legible pero secundaria
```

**Mock generado:** `docs/mocks/detalle-mision-prinplup.png`

---

## Prompt 3 — Confirmación de asignación (reutilizado)

```
Actúa como diseñador UX senior de sistemas de monitoreo ambiental.

SISTEMA: AquaPort · Pantalla de confirmación de asignación (v2.0.0)
PANTALLA: Resultado de la asignación automática

ESTILO:
- Mismo sistema de diseño
- Fondo #0A2540, superficies #0F3A5F

CONTENIDO:
- Toast / banner de éxito en la parte superior:
  "✓ Misión M-001 asignada al drone AR-06"

- Card de resumen:
  · Misión: M-001 (CRITICA)
  · Drone: AR-06 (BUCEADOR)
  · Asignada: 2026-10-12 18:30:00
  · Estrategia: AP-08 (PRIORIDAD CRÍTICA)

- Estado actualizado:
  · Drone AR-06 → EN_MISION (badge azul claro)
  · Disponibles: 2 de 15 (contador actualizado)

- Historial de últimas 3 asignaciones:
  · M-001 → AR-06 (ahora)
  · M-099 → AR-12 (hace 5 min)
  · M-098 → AR-04 (hace 12 min)

- Botón secundario: "Asignar otra misión"

APLICAR:
- Ley de Miller: el historial muestra solo 3 elementos
- Feedback claro: el operador sabe exactamente qué pasó
```

**Mock generado:** `docs/mocks/confirmacion-prinplup.png`

---

## Prompt 4 — Alerta CRÍTICA sin drone disponible (NUEVO)

Este es el mock nuevo que pide el reto 11.

```
Actúa como diseñador UX senior de sistemas de monitoreo ambiental.

SISTEMA: AquaPort · Pantalla de alerta crítica (v2.0.0)
PANTALLA: Estado de error cuando una misión CRÍTICA no tiene drone
disponible

ESTILO:
- Mismo sistema de diseño que las otras pantallas
- Fondo #0A2540, superficies #0F3A5F
- Color de alerta: rojo #FB7185 (--danger)
- Uso de patrón visual de urgencia pero sin histeria

CONTEXTO:
El operador intentó asignar una misión CRÍTICA (M-042) al Embalse Norte.
El sistema filtró candidatos y no encontró ninguno disponible:
todos los BUCEADORES compatibles con esa zona están en FALLO
o en MANTENIMIENTO.

CONTENIDO DE LA PANTALLA:

- Banner de alerta en la parte superior (fondo #FB7185 translúcido):
  ⚠ ICONO + "Sin drone disponible para misión CRÍTICA M-042"
  Subtexto: "Ningún BUCEADOR compatible con Embalse Norte está
  disponible."

- Card de la misión que no pudo asignarse:
  · ID: M-042
  · Zona: Embalse Norte → Laboratorio Hídrico
  · Carga: EQUIPO_MEDICION · 250g
  · Prioridad: CRITICA (badge rojo)
  · Estado: PENDIENTE (no se pudo asignar)

- Card de la razón del fallo:
  · "Se encontraron 3 BUCEADORES en la zona destino, pero:"
  · AR-06: FALLO (desde 18:22)
  · AR-11: MANTENIMIENTO (desde 17:45)
  · AR-15: RECARGANDO (ETA 22 min)
  · Total: 0 disponibles

- Sección de acciones recomendadas (botones):
  · [Reintentar en 20 min] (secundario)
  · [Notificar al técnico] (primario, rojo #FB7185)
  · [Cancelar misión] (terciario, sin fondo)

- Footer con timestamp: "Alerta generada a las 18:30:00"

- Lado derecho: miniatura del panel de flota mostrando el estado
  actual, para que el operador vea el contexto completo

APLICAR:
- Nielsen #1: visibilidad del estado (queda claro QUÉ pasó y POR QUÉ)
- Nielsen #9: mensaje de error específico (no "Error genérico")
- Ley de Miller: máximo 3 acciones ofrecidas
- Contraste WCAG AA: el rojo #FB7185 sobre #0A2540 cumple contraste
- NO usar rojo saturado ni vibraciones; mantener tono profesional

FORMATO: mockup de alta fidelidad 1920×1080, vista horizontal
```

**Mock generado:** `docs/mocks/alerta-critica-prinplup.png`

---

## Justificación de las heurísticas de Nielsen aplicadas

La pantalla de alerta (Mock 4) cumple 4 heurísticas de Nielsen:

| Heurística | Cómo la cumple |
|---|---|
| **#1 Visibilidad del estado** | El banner explica qué pasó (misión CRÍTICA sin drone), el card del fallo lista los 3 drones con su estado actual y la razón por la cual no son candidatos. |
| **#3 Control y libertad** | El operador tiene 3 acciones posibles: reintentar, notificar al técnico o cancelar la misión. No queda bloqueado. |
| **#5 Prevención de errores** | El sistema NO permite asignar un drone en FALLO o MANTENIMIENTO. La alerta aparece antes de que el operador cometa el error. |
| **#9 Mensaje de error claro** | No dice "Error de asignación". Dice: "Ningún BUCEADOR compatible con Embalse Norte está disponible" + lista los 3 drones con su estado. El operador entiende exactamente qué pasó y por qué. |

Y cumple 2 leyes UX:

| Ley | Aplicación |
|---|---|
| **Ley de Miller** | Máximo 3 acciones ofrecidas (reintentar, notificar, cancelar). No 6 opciones. |
| **Ley de Fitts** | El botón "Notificar al técnico" es el más grande (acción primaria en contexto de emergencia). |

---

## Estructura final de archivos

```
docs/
├── mocks/
│   ├── panel-flota-prinplup.png          ← reutilizado del reto 08
│   ├── detalle-mision-prinplup.png       ← reutilizado del reto 08
│   ├── confirmacion-prinplup.png         ← reutilizado del reto 08
│   └── alerta-critica-prinplup.png       ← NUEVO en reto 11
└── mocks-asignacion-prinplup.md          ← este documento
```

---

## Trazabilidad con otros retos

| Pantalla | Reto origen | Justificación |
|---|---|---|
| Panel de flota | Reto 08 | Ley de Hick + botón de emergencia |
| Detalle de misión | Reto 08 | Razón de selección (AP-07 vs AP-08) |
| Confirmación | Reto 08 | Feedback claro al operador |
| **Alerta CRÍTICA** | **Reto 11** | **Cubre el flujo FA-01 de la plantilla DOSW (AP-07)** |

La pantalla de alerta es la representación visual del flujo alterno
**FA-01** (Sin drones disponibles) documentado en
`docs/AP-07-plantilla-prinplup.md`.