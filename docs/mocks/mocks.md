Actúa como diseñador UX/UI senior de sistemas de monitoreo ambiental.

SISTEMA: AquaPort — Panel de control de flota de drones acuáticos de la ECI
PANTALLA: Panel de monitoreo de la flota (vista principal del operador hídrico)
ESTILO:
  - Paleta: fondo #0A2540, superficies #0F3A5F, acento cian #22D3EE
  - Colores de estado: disponible #34D399, en misión #38BDF8,
    recargando #FBBF24, mantenimiento #94A3B8, fallo #FB7185
  - Tipografía: DM Sans para interfaz, DM Mono para IDs y batería
  - Estilo dashboard técnico oscuro, minimalista, tipo centro de control

ACTOR: Operador Hídrico — necesita tomar decisiones rápidas sobre la flota.

DATOS A MOSTRAR POR DRONE:
  - ID (formato AR-XX)
  - Batería en % con indicador visual (barra)
  - Estado (DISPONIBLE / EN_MISION / RECARGANDO / MANTENIMIENTO / FALLO)
  - Zona actual (Embalse Norte, Canal Central, Laguna Sur, Ribera Este, Lab Hídrico)

ACCIONES DEL OPERADOR:
  - Seleccionar drone
  - Asignar a misión
  - Ver detalle del drone

LAYOUT:
  - Header superior: logo AquaPort + título "Panel de Monitoreo de Flota"
  - Contador global: "3 drones disponibles de 4"
  - Grid de 4 tarjetas (una por drone) con los datos arriba
  - Panel lateral derecho: "Misiones activas" con lista de códigos M-XXX

Nielsen aplicadas:
  - #1 Visibilidad del estado: estado visible sin clics
  - #5 Prevención de errores: drones con batería <35% visualmente deshabilitados
  - #8 Minimalismo: solo ID, batería, estado y zona por tarjeta