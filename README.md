# AquaPort — Sistema de drones acuáticos de la ECI

## Descripción

La Escuela Colombiana de Ingeniería necesita monitorear y gestionar el
reparto de muestras, sensores y equipos entre sus zonas hídricas:
embalses de investigación, canales del campus y puntos de recolección
ambiental. Una flota de drones acuáticos autónomos ejecuta las misiones.
Los operadores asignan las rutas, los drones navegan y el sistema
registra cada evento.

Este repositorio implementa **AquaPort** de forma evolutiva: el mismo
sistema, madurando en tres niveles — **Piplup** (MVP), **Prinplup**
(flota autónoma) y **Empoleon** (red multi-embalse).

## Niveles del proyecto

| Nivel | Alcance | Estado |
|---|---|---|
| **Piplup** | 4 drones, zonas fijas, asignación manual | Completado |
| **Prinplup** | 15 drones, 3 tipos, asignación automática | Completado |
| **Empoleon** | 60 drones, 4 zonas, rutas multi-etapa | En progreso |

## Arquitectura

El proyecto sigue una arquitectura por capas, preparada para escalar a
Empoleon sin refactors masivos:

```
src/main/java/com/eci/aquaport/
├── dominio/          Entidades y enums (Java puro, sin dependencias externas)
├── aplicacion/       Servicios, validadores, repositorios (interfaces),
│                     estrategias, observadores, fábricas
└── infraestructura/  Implementaciones concretas (repositorios, adaptadores)
```

### Regla de dependencias

- `dominio/` **no depende de nada** (solo de `java.*`).
- `aplicacion/` depende solo de `dominio/`.
- `infraestructura/` depende de `dominio/` y `aplicacion/`.
- **Nunca** al revés.

### Patrones de diseño aplicados

| Patrón | Componente | Reto |
|---|---|---|
| **Builder** | `Mision.Builder` | Piplup 03 |
| **Factory Method** | `TipoDrone.crear(...)` + `FabricaDrones` | Prinplup 03 |
| **Strategy** | `EstrategiaSeleccion` + 3 implementaciones | Prinplup 03 |
| **Observer** | `ObservadorMision` + 2 implementaciones | Prinplup 03 |

## Estructura del repositorio

```
.
├── docs/                     Documentación y evidencias de cada reto
│   ├── mocks/                Mocks del panel del operador
│   ├── jacoco/               Capturas de cobertura
│   ├── sonarqube/            Capturas del análisis estático
│   ├── referencias/          Referencias visuales de diseño
│   └── jira/                 Capturas del sprint board
├── src/main/java/            Código de producción
├── src/test/java/            Pruebas unitarias
├── pom.xml                   Configuración Maven (JUnit 5, Mockito, JaCoCo, Sonar)
└── README.md
```

## Ejecución

### Requisitos

- **JDK 21+**
- **Maven 3.8+**

### Comandos principales

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar pruebas unitarias
mvn clean test

# Ejecutar pruebas + verificar cobertura (Quality Gate ≥ 80%)
mvn clean verify

# Ejecutar análisis con SonarQube (requiere servidor corriendo)
mvn sonar:sonar -Dsonar.token=TU_TOKEN

# Abrir el reporte HTML de JaCoCo
start target/site/jacoco/index.html       # Windows
open  target/site/jacoco/index.html       # Linux/macOS
```

### Ejecutar la demo de Prinplup

```bash
mvn clean compile
java -cp target/classes com.eci.aquaport.demo.DemoPiplupPrinplup
```

Muestra las 5 consultas del reto 01 Prinplup sobre una flota de 15 drones:
agrupación por tipo, drone óptimo por zona, promedio de batería por tipo,
separación de misiones críticas y zonas únicas de la flota activa.

## Testing y cobertura

- **Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito 5
- **Cobertura:** JaCoCo 0.8.11
- **Quality Gate:** Line coverage ≥ 80% del bundle

### Reporte de cobertura

![Reporte JaCoCo](docs/jacoco/reporte-general-prinplup.png)

| Métrica | Valor |
|---|---|
| Line coverage (bundle) | 92% |
| Branch coverage (bundle) | 80% |
| Clases cubiertas | 29 / 29 |

Documentación completa: [`docs/jacoco-prinplup.md`](docs/jacoco-prinplup.md).

## Análisis estático (SonarQube)

![Dashboard SonarQube](docs/sonarqube/dashboard-prinplup.png)

| Métrica | Valor |
|---|---|
| Bugs | 0 |
| Vulnerabilities | 0 |
| Code Smells | 0 |
| Deuda técnica | < 30 min |
| Duplicación | < 5% |
| Coverage | 92% |

Documentación completa: [`docs/sonarqube-prinplup.md`](docs/sonarqube-prinplup.md).

---

## Entregables por reto

### Nivel Piplup (MVP)

| # | Reto | Entregable | Evidencia |
|---|---|---|---|
| 01 | Streams & Lambdas | `ConsultorFlota` con 5 consultas | `src/main/java/.../aplicacion/ConsultorFlota.java` |
| 02 | GitHub y GitFlow | Repositorio con ramas `main`/`develop`/`feature/` | Historial de git |
| 03 | Patrones de Diseño | `Mision.Builder` con validación | `src/main/java/.../dominio/Mision.java` |
| 04 | Principios SOLID | Separación SRP + DIP | `RegistradorMisiones`, `ValidadorMision`, `NotificadorOperador` |
| 05 | Diagrama C4 (contexto) | Diagrama nivel 1 | [`docs/c4-contexto-piplup.png`](docs/c4-contexto-piplup.png) |
| 06 | RF y RNF | 3 RF + 3 RNF + MoSCoW | [`docs/requisitos-piplup.md`](docs/requisitos-piplup.md) |
| 07 | Plantilla DOSW | Plantilla RF AP-01 | [`docs/AP-01-plantilla-piplup.md`](docs/AP-01-plantilla-piplup.md) |
| 08 | Identidad y UX/UI | Manual de identidad + Nielsen | [`docs/identidad-aquaport.md`](docs/identidad-aquaport.md) |
| 09 | Agilismo y Jira | Épica + Feature + 3 HU + subtareas | [`docs/jira-piplup.md`](docs/jira-piplup.md) |
| 10 | Diagramas de CU | Diagrama con include/extend | [`docs/diagrama-cu-piplup.png`](docs/diagrama-cu-piplup.png) |
| 11 | Mocks con IA | 3 estados del panel del operador | [`docs/mocks/`](docs/mocks/) |
| 12 | TDD | `ValidadorMision` con TDD estricto | `src/test/java/.../ValidadorMisionTest.java` |
| 13 | JaCoCo | Cobertura ≥ 80% | [`docs/jacoco/`](docs/jacoco/) |
| 14 | SonarQube | 0 bugs, 0 vulnerabilidades, 0 smells | [`docs/sonarqube/`](docs/sonarqube/) |

### Nivel Prinplup (v2.0.0)

Ampliación del MVP con:
- Modelo de dominio extendido: `EstadoDrone`, `TipoDrone`, `Prioridad`.
- Compatibilidad tipo ↔ zona en `TipoDrone` con Factory Method polimórfico.
- Consultas agregadas: `AnalizadorFlota`, `AnalizadorMisiones`.
- Asignación automática con Strategy + Observer.
- Demo ejecutable: `DemoPiplupPrinplup`.

| # | Reto | Entregable | Evidencia |
|---|---|---|---|
| 01 | Streams avanzados | `AnalizadorFlota` y `AnalizadorMisiones` con groupingBy, partitioningBy, flatMap, reduce | `src/main/java/.../aplicacion/AnalizadorFlota.java`, `AnalizadorMisiones.java` |
| 02 | GitFlow con release y hotfix | Ramas `release/v2.0` y `hotfix/fix-zona-nula` con tags `v2.0.0` y `v2.0.1` | [`docs/gitflow-log.txt`](docs/gitflow-log.txt), [`docs/gitflow-tags.txt`](docs/gitflow-tags.txt) |
| 03 | Strategy + Observer + Factory | `EstrategiaSeleccion` (3 impl.), `ObservadorMision` (2 impl.), `TipoDrone.crear(...)` | `src/main/java/.../aplicacion/estrategias/`, `.../observadores/`, `.../dominio/TipoDrone.java` |
| 04 | SOLID completo | Auditoría documentada de los 5 principios | [`docs/solid-audit-prinplup.md`](docs/solid-audit-prinplup.md) |
| 05 | C4 v2 con sistemas externos | Diagrama de contexto con 4 actores + 3 sistemas externos | [`docs/c4-contexto-prinplup.png`](docs/c4-contexto-prinplup.png), [`docs/c4-contexto-comparacion.md`](docs/c4-contexto-comparacion.md) |
| 06 | RF y RNF v2 | 6 RF (AP-07 a AP-12) + 4 RNF + tensión AP-07 ↔ AP-08 resuelta | [`docs/requisitos-prinplup.md`](docs/requisitos-prinplup.md) |
| 07 | Plantilla DOSW asignación automática | Plantilla RF AP-07 con sub-objetos anidados | [`docs/AP-07-plantilla-prinplup.md`](docs/AP-07-plantilla-prinplup.md) |
| 08 | Identidad v2 y leyes UX | Sistema de diseño + Fitts + Hick + Miller | [`docs/diseno-ux-prinplup.md`](docs/diseno-ux-prinplup.md) |
| 09 | Sprint planning | Épica + 3 features + 11 HU + DoD + retrospectiva | [`docs/jira-sprint-prinplup.md`](docs/jira-sprint-prinplup.md) |
| 10 | Diagrama CU extendido | CU con include/extend encadenados y generalización de actores | [`docs/diagrama-cu-prinplup.png`](docs/diagrama-cu-prinplup.png), [`docs/diagrama-cu-prinplup.md`](docs/diagrama-cu-prinplup.md) |
| 11 | Mocks flujo asignación | 4 pantallas (panel → detalle → confirmación → alerta) | [`docs/mocks/`](docs/mocks/), [`docs/mocks-asignacion-prinplup.md`](docs/mocks-asignacion-prinplup.md) |
| 12 | TDD con Mockito | `AsignadorAutomaticoTest` con 5 casos obligatorios | [`docs/tdd-mockito-prinplup.md`](docs/tdd-mockito-prinplup.md), `src/test/java/.../AsignadorAutomaticoTest.java` |
| 13 | JaCoCo ≥ 80% en dominio | Bundle 92% line / 80% branch | [`docs/jacoco-prinplup.md`](docs/jacoco-prinplup.md), [`docs/jacoco/`](docs/jacoco/) |
| 14 | SonarQube deuda < 30 min | 0 bugs, 0 vulnerabilidades, deuda < 30 min | [`docs/sonarqube-prinplup.md`](docs/sonarqube-prinplup.md), [`docs/sonarqube/`](docs/sonarqube/) |

### Nivel Empoleon (Enterprise)

| # | Reto | Estado |
|---|---|---|
| 01–14 | Ver enunciado Empoleon | 🚧 Pendiente |

---

## Flujo de trabajo GitFlow

El proyecto sigue GitFlow:

- `main`: rama de producción. Solo recibe merges de `release/*` y `hotfix/*` vía PR.
- `develop`: integración continua. Recibe merges de `feature/*` vía PR.
- `feature/*`: una rama por funcionalidad. Sale de `develop`, vuelve a `develop`.
- `release/*`: preparación de release. Sale de `develop`, va a `main` y `develop`.
- `hotfix/*`: correcciones urgentes. Sale de `main`, va a `main` y `develop`.

### Tags del proyecto

| Tag | Contenido |
|---|---|
| `v1.0.0` | AquaPort MVP (Piplup) |
| `v2.0.0` | AquaPort Prinplup (15 drones, 3 tipos, asignación automática) |
| `v2.0.1` | Hotfix: `TipoDrone.esCompatibleCon` rechaza zonas en blanco |

### Convenciones de commits

El proyecto sigue **Conventional Commits**:

```
feat:     nueva funcionalidad
fix:      corrección de bug
refactor: cambio de estructura sin alterar comportamiento
test:     agregar o modificar pruebas
docs:     documentación
build:    cambios en pom.xml o configuración de build
chore:    tareas de mantenimiento
```

---

## Stack tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Build | Maven 3.8+ |
| Testing | JUnit 5, Mockito 5 |
| Cobertura | JaCoCo 0.8.11 |
| Análisis estático | SonarQube 10.4 Community |
| Control de versiones | Git + GitHub (GitFlow) |
| Documentación | Markdown, Draw.io, PlantUML |
| Gestión ágil | Jira |