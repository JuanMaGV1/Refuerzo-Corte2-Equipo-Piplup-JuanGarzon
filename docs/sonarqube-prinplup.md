# Análisis estático con SonarQube — AquaPort Prinplup v2

Documento de verificación del análisis estático de la v2. Registra el
estado final del dashboard y su consistencia con la cobertura medida por
JaCoCo.

---

## 1. Objetivo y estado final

| Métrica | Objetivo | Estado final |
|---|---|---|
| Bugs | 0 | 0 ✅ |
| Vulnerabilities | 0 | 0 ✅ |
| Deuda técnica (Maintainability) | < 30 min | ✅ |
| Duplicación | < 5% | ✅ |
| Cobertura (SonarQube) | ≥ 80% | ✅ |

---

## 2. Configuración del análisis

### Exclusión de clases de demo

El `pom.xml` excluye la clase `DemoPiplupPrinplup` del análisis porque es
un `main()` de demostración sin lógica de negocio.

```xml
<properties>
    <sonar.exclusions>
        **/demo/**/*.java
    </sonar.exclusions>
    <sonar.coverage.exclusions>
        **/demo/**/*.java
    </sonar.coverage.exclusions>
</properties>
```

### Consumo del reporte JaCoCo

SonarQube consume el reporte XML generado por JaCoCo:

```xml
<sonar.coverage.jacoco.xmlReportPaths>
    ${project.basedir}/target/site/jacoco/jacoco.xml
</sonar.coverage.jacoco.xmlReportPaths>
```

### Comando de análisis

```bash
mvn clean verify
mvn sonar:sonar -Dsonar.token=TU_TOKEN
```

---

## 3. Consistencia SonarQube ↔ JaCoCo

Los reportes de **SonarQube** y **JaCoCo** concuerdan: ambos reflejan el
mismo porcentaje de cobertura sobre el mismo conjunto de clases
(excluyendo `com.eci.aquaport.demo`).

| Métrica | JaCoCo | SonarQube |
|---|---|---|
| Line coverage | 86.6% | 86.6% |
| Branch coverage | 80% | 80% |

**Por qué concuerdan:** SonarQube no recalcula la cobertura, sino que
consume el archivo `target/site/jacoco/jacoco.xml` generado por JaCoCo.
Ambos usan las mismas reglas de exclusión (`**/demo/**`) y por lo tanto
reportan los mismos valores.

---

## 4. Estado del dashboard

### Security

- Vulnerabilities: 0
- Security Hotspots: 0

### Reliability

- Bugs: 0

### Maintainability

- Code Smells: 0
- Deuda técnica: < 30 min
- Duplicación: < 5%

### Coverage

- Line coverage: 86.6%
- Branch coverage: 80%

### Evidencia

![Dashboard SonarQube](sonarqube/dashboard-prinplup.png)

---

## 5. Comandos

```bash
# Análisis completo
mvn clean verify
mvn sonar:sonar -Dsonar.token=TU_TOKEN

# Abrir el dashboard
start http://localhost:9000/dashboard?id=aquaport-mvp
```

---

## 6. Conclusión

El análisis estático de la v2 cierra con los 4 indicadores en verde:

- **0 bugs** y **0 vulnerabilidades**.
- **Deuda técnica** por debajo del umbral de 30 min.
- **Duplicación** por debajo del 5%.
- **Cobertura** del 86.6% en líneas (consistente con JaCoCo).

Ningún issue se suprimió con `@SuppressWarnings`. Las exclusiones se
limitan a la clase de demo (`com/eci/aquaport/demo/**`), que no forma
parte del código de negocio.