# Justificación del uso (o no) de Streams Paralelos — Empoleon

Documento que justifica cuándo usar `parallelStream()` en AquaPort
Empoleon y por qué en el resto del código NO se aplica.

---

## 1. Cuándo el paralelismo ayuda

Un stream paralelo mejora el rendimiento cuando se cumplen **todas**:

1. **Volumen alto.** Miles o millones de elementos. El overhead de dividir
   y combinar se amortiza solo con volumen grande.
2. **Operación costosa por elemento.** Si cada elemento tarda
   microsegundos, el overhead domina. Si tarda milisegundos o más,
   paralelismo paga.
3. **Sin estado compartido mutable.** Los streams paralelos requieren
   operaciones puras o acumuladores thread-safe.
4. **Sin operaciones de E/S.** Llamadas a red o disco bloquean los
   hilos del ForkJoinPool.
5. **CPUs multi-core disponibles.** El `commonPool` tiene
   `availableProcessors() - 1` hilos.
6. **Orden no relevante o UNORDERED declarado.**

---

## 2. Por qué NO aplica a la flota de 60 drones

| Criterio | Estado en AquaPort | Veredicto |
|---|---|---|
| Volumen | 60 drones | ❌ Insuficiente |
| Costo por elemento | Nanosegundos | ❌ Muy barato |
| Estado mutable | Acumulador del Collector | ⚠️ Requiere combiner |
| E/S | Ninguna | ✅ |
| Núcleos | No controlados | ⚠️ Incierto |
| Orden | Algunas consultas lo requieren | ❌ No aplica |

**Cálculo:** recorrer 60 elementos secuencialmente tarda ~50 μs.
Dividir entre hilos añade ~200-500 μs de coordinación. Es **4-10× más lento** en paralelo.

---

## 3. Dónde SÍ tendría sentido

### 3.1 Batch nocturno sobre historial completo

Si AquaPort registra millones de eventos al año, un job nocturno que
calcule eficiencia histórica sobre 10 millones de misiones sí se
beneficia del paralelismo.

### 3.2 Simulaciones de Monte Carlo

Si en Empoleon se simulan 10.000 escenarios de planificación de rutas,
cada simulación es independiente y costosa. Paralelismo da 4-8× de mejora.

### 3.3 Telemetría en tiempo real

Si cada drone envía 1000 eventos/segundo, procesar un lote de 100.000
eventos en paralelo con un ForkJoinPool dedicado sí acelera.

---

## 4. Por qué el `combiner` está implementado

El `CollectorResumenZona.combiner()` está implementado porque:

1. Es parte del contrato de `Collector` (método obligatorio).
2. Deja el collector listo para streams paralelos futuros.
3. Tiene un test dedicado que lo ejercita directamente
   (`CollectorResumenZonaTest.combinaAcumuladoresCorrectamente`),
   sin necesidad de usar `parallelStream()`. Así JaCoCo lo marca
   como cubierto.

---

## 5. Regla de decisión

| Volumen | Operación | ¿Paralelizar? |
|---|---|---|
| < 1.000 | Barata | ❌ No |
| < 1.000 | Costosa (>1ms) | ⚠️ Medir |
| 1.000 - 100.000 | Barata | ⚠️ Medir |
| 1.000 - 100.000 | Costosa | ✅ Sí |
| > 100.000 | Cualquiera | ✅ Sí |

En AquaPort Empoleon estamos en la primera fila: **60 elementos,
operaciones baratas → secuencial**.