# Manual de Identidad Básico de AquaPort

## Justificación de la identidad

AquaPort es un sistema de monitoreo hídrico operado por investigadores y técnicos de la ECI en contextos de campo (embalses, canales, lagunas). La identidad debe transmitir tres cosas simultáneamente:

- **Precisión técnica:** el operador toma decisiones críticas sobre asignación de drones.

- **Entorno natural:** el dominio es agua, ambiente, investigación hídrica.

- **Calma operativa:** no es un videojuego; es un panel de control serio.

Por eso la paleta gira en torno a azules profundos (agua, confianza técnica) con acentos cianes (tecnología, monitoreo en tiempo real) y verdes de validación (estado operativo correcto).

## Paleta de colores
**Colores primarios y secundarios**

| Rol | Nombre | Hex | Uso |
|-----|--------|-----|-----|
| Primario           | Azul Profundo  |	#0A2540 | Fondo principal del panel, headers   |
| Primario claro     | Azul Océano    |	#0F3A5F | Cards, superficies elevadas          |
| Secundario         | Cian Técnico   |	#22D3EE | Acentos, iconos activos, links       |
| Secundario oscuro  | Cian Profundo  |	#0891B2 | Hover de elementos interactivos      |
| Neutro claro       | Blanco Hídrico |	#F0F9FF | Texto principal sobre fondos oscuros |
| Neutro medio       | Gris Ribereño  |	#94A3B8 | Texto secundario, bordes suaves      |
| Neutro oscuro	Azul | Noche	      | #04090F | Fondo base de la aplicación          |

**Colores de estado (críticos para el operador)**

| Estado del drone  | Hex | Justificación |
|-------------------|-----|---------------|
| Disponible    |	#34D399 (verde)	  | Color universal de "ok / listo para operar" |
| En misión     |	#38BDF8 (azul claro) | Activo pero no problemático; el dron está trabajando |
| Recargando    |	#FBBF24 (ámbar)	  | Espera temporal, no es error |
| Mantenimiento |	#94A3B8 (gris)	      | Fuera de servicio programado, neutral |
| Fallo         |	#FB7185 (rojo suave) |	Requiere acción inmediata del operador |

- Nota de accesibilidad: todos los colores de estado cumplen contraste WCAG AA (≥ 4.5:1) contra el fondo #0A2540.

## Tipografía

| Uso | Fuente | Pesos | Justificación |
|-----|--------|-------|---------------|
| Interfaz general            | DM Sans | 300, 400, 500, 600, 700 | Sans-serif moderna, alta legibilidad en pantallas de monitoreo, lectura cómoda en sesiones largas |
|IDs, códigos, datos técnicos | DM Mono | 400, 500                |	Monoespaciada para IDs de drones (AR-01), códigos de misión (M-001) y valores numéricos de batería — permite alineación vertical en columnas de datos |

**Jerarquía tipográfica:**

| Nivel | Tamaño | Peso	| Uso |
|-------|--------|------|-----|
| H1          |	2–3 rem       |	700     |	Título de pantalla        |
| H2          |	1.2–1.5 rem   | 600     |	Sección principal         |
| Body        |	0.86–0.91 rem |	400     |	Texto general             |
| Data / Mono |	0.7–0.78 rem  |	400–500 |	IDs, baterías, timestamps |

## Logo (concepto)

- **Símbolo:** gota de agua estilizada con la silueta de un dron sobre ella (líneas geométricas).

- **Wordmark:** "AquaPort" en DM Sans SemiBold, con la "A" en cian técnico #22D3EE.

- **Versión mínima:** solo el símbolo (para favicon y esquina superior izquierda del panel).