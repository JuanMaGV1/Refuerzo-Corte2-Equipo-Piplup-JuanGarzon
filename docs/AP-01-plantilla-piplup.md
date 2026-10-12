# Plantilla DOSW — AP-01

|Código| AP-01|
|------|------|
| Nombre |	Registrar misión de transporte de muestra |
| Actor  |	Operador Hídrico |
| Descripción |	El operador registra una solicitud de transporte de muestra de agua entre dos zonas hídricas del campus. El sistema valida los datos, genera un código de misión único y la deja en estado PENDIENTE lista para asignación. |
| Prioridad|Must Have |

## Precondiciones

- Debe existir al menos un drone disponible con batería ≥ 35%.

- Las zonas de partida y llegada deben pertenecer al catálogo de zonas válidas: Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico.

- El operador debe estar autenticado en el sistema.

## Datos de Entrada
| Nombre | Tipo de campo | Reglas | Obligatorio |
|--------|---------------|--------|-------------|
| puntoPartida             | Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO) | Debe ser distinto a puntoLlegada | Sí |
| puntoLlegada             | Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO) |	Debe ser distinto a puntoPartida | Sí |
| tipoCarga                | Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO)	                              | | Sí|
| droneAsignado.id         | String	Formato AR-XX (ej. AR-01)                                         | | Sí |
| droneAsignado.bateria    | Integer                                                                  | Entre 0 y 100 | Sí |
| droneAsignado.disponible | Boolean	                                                              | Debe ser true | Sí |
| droneAsignado.zona       | String	Debe coincidir con puntoPartida	                                  | | Sí |

## Datos de Salida
| Nombre | Tipo de campo | Descripción |
|--------|---------------|-------------|
| codigoMision  | String                                           | Código único generado por el sistema, formato M-XXX (ej. M-001) |
| estadoMision  | Enum(PENDIENTE, EN_TRANSITO, ENTREGADA, FALLIDA) | Estado inicial: PENDIENTE |
| fechaRegistro | LocalDateTime	                                   | Timestamp del momento en que se registró la misión |
| mensaje	    | String	                                       | Confirmación: "Misión M-001 registrada correctamente en estado PENDIENTE." |

## Flujo Básico (5 pasos)

El operador accede al módulo "Registrar misión" y selecciona puntoPartida y puntoLlegada del catálogo de zonas válidas.

El operador selecciona el tipoCarga de entre las opciones disponibles.

El sistema consulta la flota y muestra los drones disponibles con batería ≥ 35% en la zona de partida.

El operador selecciona el drone a asignar de la lista mostrada.

El sistema valida los datos, genera el codigoMision único, crea la misión en estado PENDIENTE y muestra el mensaje de confirmación al operador.

## Flujos Alternos

FA-01 — Drone sin batería suficiente
En el paso 4, si el drone seleccionado tiene batería < 35%:

El sistema rechaza la asignación.

Muestra el mensaje: "El drone AR-03 tiene batería insuficiente (18%). Mínimo requerido: 35%."

El operador regresa al paso 3 y selecciona otro drone.

FA-02 — Zona de destino inválida
En el paso 1, si el operador ingresa una zona que no pertenece al catálogo de zonas válidas:

El sistema rechaza el registro.

Muestra el mensaje: "La zona 'X' no es válida. Zonas permitidas: Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico."

El operador regresa al paso 1.

## Reglas de Negocio
| Código | Regla | Tipo |
|--------|-------|------|
| RN-01 |	Un drone no puede tener más de 1 misión activa simultáneamente. Antes de asignarlo, el sistema debe verificar que el drone no tenga otra misión en estado EN_TRANSITO. | Restricción de dominio — aplica DURANTE la ejecución |
| RN-02 | El puntoPartida y el puntoLlegada no pueden ser la misma zona. El sistema debe validar que sean distintos antes de generar la misión.	| Restricción de dominio  aplica DURANTE la ejecución