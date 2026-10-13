# Correcciones — Reto 01 Empoleon

Documento de trazabilidad de las 5 correcciones aplicadas al reto 01
Empoleon tras la revisión.

| # | Observación | Corrección | Estado |
|---|---|---|---|
| Bloqueante 1 | Falta justificación de stream paralelo | `docs/streams-paralelo-empoleon.md` | ✅ |
| Obs 1 | `combiner` no cubierto por tests | Test dedicado en `CollectorResumenZonaTest` | ✅ |
| Obs 2 | Desempate por id inconsistente con Reto 06 | Orden inverso consistente + doc Reto 06 actualizado | ✅ |
| Obs 3 | Eficiencia por zona ambigua multi-etapa | Javadoc aclara: destino final | ✅ |
| Obs 4 | Clase supera 150 líneas | Dividida: 4 consultas Empoleon en clase nueva | ✅ |
| Obs 5 | `calcularEficiencia` sin guard | Guard `if (isEmpty()) return 0.0` | ✅ |
| Obs 6 | Mismo drone en varios tramos | `Set<String> yaAsignados` filtra drones usados | ✅ |

## Conteo de líneas

`AnalizadorFlotaEmpoleon`: ~140 líneas de código (sin javadoc).
Dentro del límite de 150 líneas de Empoleon.