# Diagrama de Casos de Uso — AquaPort Prinplup v2

Documento explicativo del diagrama de casos de uso de la v2. Amplía el
diagrama de Piplup con nuevos actores, CU y relaciones encadenadas.

## Actores

### Actor base abstracto

**Usuario AquaPort** es un actor abstracto que agrupa el comportamiento
común a los 3 roles internos del sistema (Operador, Solicitante, Admin).
**No interactúa directamente con ningún CU**; solo define la herencia.

### Actores heredados

| Actor | Hereda de | CU propios |
|---|---|---|
| Operador Hídrico | Usuario AquaPort | Asignar misión, Confirmar asignación, Consultar flota, Cancelar misión, Consultar condiciones hídricas |
| Solicitante | Usuario AquaPort | Registrar solicitud de misión |
| Administrador ECI | Usuario AquaPort | Consultar flota, Consultar condiciones hídricas |

### Actores independientes

| Actor | Tipo | CU propios |
|---|---|---|
| Técnico de Mantenimiento | Interno (NUEVO v2) | Marcar drone en mantenimiento |
| Sistema de Alertas | Externo (NUEVO v2) | Notificar fallo a observadores |
| Centro de Control ECI | Externo (NUEVO v2) | Registrar evento en bitácora legal |

## Casos de uso principales de v2

### CU principal: Asignar misión automáticamente

Es el CU que da sentido a Prinplup. Ejecuta **3 includes obligatorios**:

1. **`<<include>>` Validar condiciones hídricas** — sin validar las
   condiciones de la zona, no se puede decidir la estrategia.
2. **`<<include>>` Filtrar candidatos compatibles** — descarta drones
   incompatibles o con batería insuficiente.
3. **`<<include>>` Aplicar estrategia de selección** — decide qué drone
   elegir entre los candidatos (AP-07 o AP-08 según prioridad).

### CU principal: Confirmar asignación propuesta

El operador **confirma o rechaza** la propuesta del sistema. Este CU
incluye:

- **`<<include>>` Registrar evento en bitácora legal** — toda confirmación
  queda registrada en el Centro de Control ECI.

### CU con extend condicional: Elegir otro drone

`<<extend>>` de "Confirmar asignación propuesta", condicionado a
**[operador rechaza la propuesta]**. Permite al operador seleccionar
manualmente otro drone.

### CU con extend condicional: Notificar fallo

`<<extend>>` de "Asignar misión automáticamente", condicionado a
**[drone entra en FALLO durante la asignación]**. Notifica a los
observadores y delega la reasignación.

## Encadenamiento de relaciones

```
Asignar misión automáticamente
├── <<include>> Validar condiciones hídricas
├── <<include>> Filtrar candidatos compatibles
├── <<include>> Aplicar estrategia de selección
└── <<extend>> [drone en FALLO] Notificar fallo a observadores
                               └── Sistema de Alertas

Confirmar asignación propuesta
├── <<include>> Registrar evento en bitácora legal
│                └── Centro de Control ECI
└── <<extend>> [operador rechaza] Elegir otro drone
```

**Nota:** "Notificar fallo" es un `<<extend>>` de "Asignar misión
automáticamente" y tiene su propio actor externo asociado (Sistema de
Alertas). Es un ejemplo de CU extendido con actor externo — solo se
ejecuta bajo condición, y su actor solo participa en ese flujo.

## Diferencia entre include y extend

| | `<<include>>` | `<<extend>>` |
|---|---|---|
| ¿Cuándo se ejecuta? | Siempre, sin condición | Solo bajo una condición específica |
| Dirección de la flecha | CU base → CU incluido | CU que extiende → CU base |
| Actores asociados | El CU base hereda la asociación | Solo el CU extendido tiene actor propio |
| Ejemplos en AquaPort v2 | Validar condiciones, Filtrar candidatos, Aplicar estrategia, Registrar bitácora | Elegir otro drone, Notificar fallo |

## Generalización de actores

El actor abstracto **Usuario AquaPort** es la base común. Los actores
Operador Hídrico, Solicitante y Administrador ECI heredan su
comportamiento y añaden sus propios CU.

**¿Por qué generalización?** Porque los 3 comparten una precondición
implícita: autenticarse en el sistema. Representar esa herencia evita
repetir la relación de autenticación en cada actor.

**Técnico de Mantenimiento NO hereda de Usuario AquaPort** porque tiene
un flujo propio (marcar en mantenimiento) y no comparte las operaciones
de los otros 3 actores.