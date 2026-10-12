# AquaPort — Sistema de drones acuáticos de la ECI

> Proyecto DOSW · Escuela Colombiana de Ingeniería · 2026-2
> Prof. Andrés Martín Cantór Urrego
> Equipo Piplup — Juan Garzón

## Descripción

La Escuela Colombiana de Ingeniería necesita monitorear y gestionar el
reparto de muestras, sensores y equipos entre sus zonas hídricas: embalses
de investigación, canales del campus y puntos de recolección ambiental.
Una flota de drones acuáticos autónomos ejecuta las misiones. Los
operadores asignan las rutas, los drones navegan y el sistema registra
cada evento.

Este repositorio implementa **AquaPort** de forma evolutiva: el mismo
sistema, madurando en tres niveles — **Piplup** (MVP), **Prinplup**
(flota autónoma) y **Empoleon** (red multi-embalse).

## Niveles del proyecto

| Nivel | Alcance | Estado |
|---|---|---|
| **Piplup** | 4 drones, zonas fijas, asignación manual | Completado |
| **Prinplup** | 15 drones, 3 tipos, asignación automática | En progreso |
| **Empoleon** | 60 drones, 4 zonas, rutas multi-etapa | Pendiente |

## Arquitectura

El proyecto sigue una arquitectura por capas, preparada para escalar a
Empoleon sin refactors masivos:
```
src/main/java/com/eci/aquaport/

├── dominio/ Entidades y enums (Java puro, sin dependencias externas)

├── aplicacion/ Servicios, validadores, repositorios (interfaces)

└── infraestructura/ Implementaciones concretas (repositorios, adaptadores)
```
### Regla de dependencias

- `dominio/` **no depende de nada**
- `aplicacion/` depende solo de `dominio/`.
- `infraestructura/` depende de `dominio/` y `aplicacion/`.
- **Nunca** al revés.

## Estructura del repositorio

```
├── docs/ Documentación y evidencias de cada reto
│ ├── mocks/ Mocks del panel del operador
│ ├── jacoco/ Capturas de cobertura
│ ├── sonarqube/ Capturas del análisis estático
│ └── referencias/ Referencias visuales de diseño
├── src/main/java/ Código de producción
├── src/test/java/ Pruebas unitarias
├── pom.xml Configuración Maven (JUnit 5, JaCoCo, Sonar)
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
**Testing y cobertura**

- Framework: JUnit 5 (Jupiter)

- Mocking: Mockito 5

- Cobertura: JaCoCo 0.8.11

- Quality Gate: Line coverage ≥ 80% del bundle

**Reporte de cobertura**

![Reporte Jacoco](/docs/jacoco/reporte-general.png)

**Análisis estático (SonarQube)**

![Reporte SonarQube](/docs/sonarqube/dashboard.png)

**Entregables por reto**

- Nivel Piplup (MVP)

| #	| Reto	| Entregable	|Evidencia|
|---|-------|---------------|---------|
|01	| Streams & Lambdas	     | ConsultorFlota con 5 consultas              | src/main/java/.../aplicacion/ConsultorFlota.java
|02 | GitHub y GitFlow       | Repositorio con ramas main/develop/feature/ | Historial de git
|03 | Patrones de Diseño     | Mision.Builder con validación               | src/main/java/.../dominio/Mision.java
|04 | Principios SOLID       | Separación SRP + DIP                        | RegistradorMisiones, ValidadorMision, NotificadorOperador
|05 | Diagrama C4 (contexto) |	Diagrama nivel 1                           | docs/c4-contexto-piplup.png
|06 | RF y RNF               |	3 RF + 3 RNF + MoSCoW	                   | docs/requisitos-piplup.md
|07 | Plantilla DOSW         |	Plantilla RF AP-01	                       | docs/AP-01-plantilla-piplup.md
|08 | Identidad y UX/UI      |	Manual de identidad + Nielsen	           | docs/identidad-aquaport.md
|09 | Agilismo y Jira        |	Épica + Feature + 3 HU + subtareas	       | docs/jira-piplup.md
|10 | Diagramas de CU        |	Diagrama con include/extend	               | docs/diagrama-cu-piplup.png
|11 | Mocks con IA           |	3 estados del panel del operador	       | docs/mocks/
|12 | TDD                    |	ValidadorMision con TDD estricto	       | src/test/java/.../ValidadorMisionTest.java
|13 | JaCoCo                 |	Cobertura ≥ 80%	                           | docs/jacoco/
|14 | SonarQube              |	0 bugs, 0 vulnerabilidades, 0 smells	   | docs/sonarqube/


**Nivel Prinplup (v2)**

| #	| Reto	| Entregable	|Evidencia|
|---|-------|---------------|---------|
|01 | Streams avanzados                    |    |   |
|02 | GitFlow con release y hotfix         |    |   |
|03 | Strategy + Observer + Factory        |    |   |
|04 | SOLID completo                       |    |   |
|05 | C4 v2 con sistemas externos          |    |   |
|06 | RF y RNF v2                          |    |   |
|07 | Plantilla DOSW asignación automático |    |   |
|08 | Identidad v2 y leyes UX              |    |   |
|09 | Sprint planning                      |    |   |
|10 | Diagrama CU extendido                |    |   |
|11 | Mocks flujo asignación	           |    |   |
|12 | TDD con Mockito	                   |    |   |
|13 | JaCoCo ≥ 80% en dominio	           |    |   |
|14 | SonarQube deuda < 30 min             |    |   |