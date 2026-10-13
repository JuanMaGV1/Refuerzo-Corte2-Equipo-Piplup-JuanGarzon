markdown
# Auditoría SOLID — AquaPort Prinplup (v2.0.0)

Documento de verificación de los 5 principios SOLID sobre la arquitectura
de la versión Prinplup. Cada sección apunta a clases concretas del
repositorio y explica la decisión de diseño en el contexto del dominio.

---

## 1. S — Single Responsibility Principle

> Una clase debe tener una sola razón para cambiar.

### Aplicación 1: separación de consultas de flota y misiones

| Clase | Responsabilidad única | Razón para cambiar |
|---|---|---|
| `ConsultorFlota` | Consultas simples sobre `List<DroneAcuatico>` (filtrar, ordenar, contar) | Cambia si cambia el modelo de `DroneAcuatico` |
| `AnalizadorFlota` | Consultas agregadas sobre flota (groupingBy, averagingInt) | Cambia si cambia el modelo de `DroneAcuatico` |
| `AnalizadorMisiones` | Consultas agregadas sobre misiones (partitioningBy, flatMap) | Cambia si cambia el modelo de `Mision` |

**Por qué importa en AquaPort:** en Prinplup, la flota y las misiones son
dos agregados del dominio. Si mezcláramos sus consultas en una sola clase,
un cambio en `Mision` obligaría a recompilar y re-testear toda la lógica
de flota. La separación evita esa fricción.

**Violación detectada y corregida:** en el primer borrador,
`separarMisionesCriticas` estaba dentro de `ConsultorFlotaAvanzado`
(que solo consultaba flota). Se movió a `AnalizadorMisiones`. Ver commit
`refactor: mover consultas de misiones a AnalizadorMisiones`.

### Aplicación 2: separación de validación de estructura vs negocio

| Componente | Responsabilidad | No hace |
|---|---|---|
| `Mision.Builder` | Valida **estructura** (campos presentes, no vacíos) | No valida reglas de negocio |
| `ValidadorMision` | Valida **reglas de negocio** (disponibilidad, batería, estado) | No construye misiones |

**Por qué importa:** en el MVP de Piplup se confundían ambas. Un cambio
en la regla de disponibilidad obligaba a tocar el Builder, que también
tenía que validar estructura. Ahora el Builder solo rechaza objetos
sintácticamente inválidos; el Validador rechaza objetos semánticamente
inválidos para operar.

---

## 2. O — Open/Closed Principle

> Las entidades deben estar abiertas a extensión pero cerradas a modificación.

### Aplicación: Factory Method polimórfico en `TipoDrone`

**Clase:** `TipoDrone`

```java
public enum TipoDrone {
    SUPERFICIAL(...) {
        @Override
        public DroneAcuatico crear(...) {
            return new DroneSuperficial(...);
        }
    },
    SEMISUMERGIDO(...) { /* ... */ },
    BUCEADOR(...) { /* ... */ };

    public abstract DroneAcuatico crear(String id, ...);
}
```
**Qué se evita:** un switch (tipo) en FabricaDrones que obligaría a
modificar la fábrica cada vez que se añade un tipo de drone.

**Extensión sin modificación:** añadir SUMERGIBLE_PROFUNDO (previsto
para Empoleon) requiere:

1. Crear DroneSumergibleProfundo.java (nueva subclase).

2. Añadir una constante al enum TipoDrone con su implementación de crear.

**No se toca** ni FabricaDrones, ni AsignadorAutomatico, ni ninguna
estrategia. Esa es la prueba de que OCP se respeta.

**Violación detectada y corregida:** la primera versión de FabricaDrones
tenía un switch (tipo) con todos los casos. Se refactorizó moviendo la
decisión al enum. Ver commit refactor: mover creación de drones al enum TipoDrone (OCP real).


## 3. L — Liskov Substitution Principle

### Aplicación: jerarquía `DroneAcuatico` → `DroneSuperficial`, `DroneSemisumergido`, `DroneBuceador`

Clase base: `DroneAcuatico` (abstracta)

```java
public abstract class DroneAcuatico {
    // Comportamiento común (final no, pero sin override destructivo)
    public boolean estaDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }
    public int bateria() { return bateria; }
    // ...
    // Contratos que las subclases DEBEN cumplir
    public abstract TipoDrone tipo();
    public abstract int capacidadCargaGramos();
}
```
**Verificación de LSP:** donde el sistema acepta DroneAcuatico, funciona
igual con cualquiera de las 3 subclases:

| Uso |Clase que lo usa | Comportamiento |
|-----|-----------------|----------------|
| filtrarCandidatos | AsignadorAutomatico | Filtra por isDisponible, tipo().esCompatibleCon, bateria, capacidadCargaGramos
| droneOptimoParaZona | AnalizadorFlota | Usa tipo().esCompatibleCon y bateria
| agruparDisponiblesPorTipo |AnalizadorFlota | Usa tipo()

Ninguno de esos métodos hace `instanceof DroneSuperficial` ni rompe con
una subclase específica. Todas las subclases sustituyen a la base sin
alterar el comportamiento.

**Restricción de LSP documentada:**

- DroneBuceador.capacidadCargaGramos() retorna 300.

- DroneSemisumergido.capacidadCargaGramos() retorna 1500.

- DroneSuperficial.capacidadCargaGramos() retorna 500.

Los contratos **no prometen un valor específico**, solo prometen devolver
un entero positivo. Las subclases pueden devolver valores distintos sin
violar LSP, porque el contrato es "devuelve la capacidad del drone", no
"devuelve 500".

Violación evitada: si en algún momento una subclase lanzara una
excepción en estaDisponible() (heredado de la base), violaría LSP. Las
3 subclases lo heredan tal cual, sin override.

## 4. I — Interface Segregation Principle

### Aplicación: ObservadorMision con métodos por defecto
Interfaz: ObservadorMision

```java
public interface ObservadorMision {
    default void notificarAsignacion(Mision mision, DroneAcuatico drone) { }
    default void notificarFalloAsignacion(Mision mision, String razon) { }
}
```
Clientes:

| Implementación | Implementa | Ignora |
|----------------|------------|--------|
| ObservadorCentroControl | Ambos métodos |	— |
| ObservadorTecnicoMantenimiento | Solo notificarFalloAsignacion | notificarAsignacion |

**Por qué importa:** sin default, ObservadorTecnicoMantenimiento
estaría obligado a implementar notificarAsignacion con un cuerpo vacío.
Eso es exactamente lo que ISP busca evitar: un cliente no debería
depender de métodos que no usa.

Violación detectada y corregida: la primera versión tenía la firma
void notificarAsignacion(Mision) sin default, y
ObservadorTecnicoMantenimiento la implementaba con un comentario
// El técnico no necesita saber.... Se refactorizó a métodos default.

### Aplicación: interfaces de estrategia

EstrategiaSeleccion tiene un solo método:

```java
public interface EstrategiaSeleccion {
    Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision);
}
```
Cada implementación concreta solo implementa ese método. No hay métodos
que alguna estrategia ignore.

## 5. D — Dependency Inversion Principle
Depende de abstracciones, no de concreciones.

### Aplicación 1: AsignadorAutomatico depende de interfaces
Clase: `AsignadorAutomatico`

```java
public class AsignadorAutomatico {
    private final List<DroneAcuatico> flota;
    private final EstrategiaSeleccion estrategia;         // ← interfaz
    private final List<ObservadorMision> observadores = new ArrayList<>(); // ← interfaz

    public AsignadorAutomatico(List<DroneAcuatico> flota,
                                EstrategiaSeleccion estrategia) {
        this.flota = List.copyOf(flota);
        this.estrategia = estrategia;
    }
}
```
**Abstracciones de las que depende:**

- EstrategiaSeleccion (interfaz) → no conoce EstrategiaMayorBateria, EstrategiaPrioridadCritica ni EstrategiaTipoCarga.

- ObservadorMision (interfaz) → no conoce ObservadorCentroControl ni
ObservadorTecnicoMantenimiento.

**Prueba:** los tests del reto 03 usan @Mock EstrategiaSeleccion y
@Mock ObservadorMision. Se puede testear el asignador sin ninguna
implementación concreta real.

**Violación detectada y corregida:** la primera versión de
AsignadorAutomatico inyectaba la flota con setFlota(...) mutable.
Se cambió a constructor injection.

### Aplicación 2: estrategias condicionales con fallback inyectado
Clase: `EstrategiaPrioridadCritica`

```java
public class EstrategiaPrioridadCritica implements EstrategiaSeleccion {
    private final EstrategiaSeleccion fallback;   // ← interfaz, no concreción

    public EstrategiaPrioridadCritica(EstrategiaSeleccion fallback) {
        this.fallback = fallback;
    }
}
```
**Antes (violación)**: private final EstrategiaSeleccion fallback = new EstrategiaMayorBateria();

**Por qué importaba:** instanciar una concreción dentro de otra creaba un
acoplamiento oculto. Si mañana el fallback de EstrategiaPrioridadCritica
debe ser otro, hay que tocar la clase. Con constructor injection, el
fallback se configura desde fuera.

**Violación detectada y corregida:** ver commit
refactor: inyectar fallback por constructor en estrategias condicionales.