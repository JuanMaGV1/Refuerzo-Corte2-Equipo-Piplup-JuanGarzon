package com.eci.aquaport.dominio;

/**
 * Misión de transporte de AquaPort.
 *
 * Una misión puede ser de un solo tramo (comportamiento de Prinplup,
 * {@code ruta == null}) o multi-etapa (Empoleon, con una {@link Ruta}
 * compuesta por waypoints intermedios).
 *
 * El record es inmutable. La construcción segura se hace con el Builder
 * anidado, que valida la estructura obligatoria: id, drone, puntos de
 * partida y llegada, tipo de carga, estado y prioridad. La ruta es
 * opcional.
 */
public record Mision(
        String id,
        DroneAcuatico drone,
        String puntoPartida,
        String puntoLlegada,
        TipoCarga tipoCarga,
        int cargaGramos,
        EstadoMision estadoMision,
        Prioridad prioridad,
        Ruta ruta
) {

    public Mision {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la misión no puede ser nulo ni vacío");
        }
        if (drone == null) {
            throw new IllegalArgumentException("El drone no puede ser nulo");
        }
        if (puntoPartida == null || puntoPartida.isBlank()) {
            throw new IllegalArgumentException("El punto de partida no puede ser nulo ni vacío");
        }
        if (puntoLlegada == null || puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo ni vacío");
        }
        if (tipoCarga == null) {
            throw new IllegalArgumentException("El tipo de carga no puede ser nulo");
        }
        if (cargaGramos <= 0) {
            throw new IllegalArgumentException("La carga debe ser mayor a 0");
        }
        if (estadoMision == null) {
            throw new IllegalArgumentException("El estado de misión no puede ser nulo");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
    }

    /**
     * Indica si la misión es multi-etapa (tiene una ruta con más de 1 tramo).
     * Las misiones de un solo tramo tienen {@code ruta == null}.
     */
    public boolean esMultiEtapa() {
        return ruta != null && ruta.numeroTramos() > 1;
    }

    /**
     * Número de tramos de la misión. Retorna 1 si es de un solo tramo
     * (comportamiento por defecto de Prinplup).
     */
    public int numeroTramos() {
        return ruta == null ? 1 : ruta.numeroTramos();
    }

    // ═══════════════════════════════════════════════════════════════
    // Builder
    // ═══════════════════════════════════════════════════════════════

    /**
     * Builder de Mision.
     *
     * Valida estructura (campos obligatorios presentes). Las reglas de
     * negocio se validan en ValidadorMision.
     *
     * El campo {@code ruta} es opcional: si no se llama a {@link #ruta(Ruta)},
     * la misión se construye como un solo tramo (ruta = null).
     */
    public static class Builder {
        private String id;
        private DroneAcuatico drone;
        private String puntoPartida;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private int cargaGramos = 1;  // default: 1g para retrocompatibilidad
        private EstadoMision estadoMision;
        private Prioridad prioridad;
        private Ruta ruta;            // opcional

        public Builder id(String id) { this.id = id; return this; }
        public Builder drone(DroneAcuatico drone) { this.drone = drone; return this; }
        public Builder puntoPartida(String puntoPartida) { this.puntoPartida = puntoPartida; return this; }
        public Builder puntoLlegada(String puntoLlegada) { this.puntoLlegada = puntoLlegada; return this; }
        public Builder tipoCarga(TipoCarga tipoCarga) { this.tipoCarga = tipoCarga; return this; }

        public Builder cargaGramos(int gramos) {
            if (gramos <= 0) {
                throw new IllegalArgumentException("La carga debe ser mayor a 0");
            }
            this.cargaGramos = gramos;
            return this;
        }

        public Builder estadoMision(EstadoMision estadoMision) { this.estadoMision = estadoMision; return this; }
        public Builder prioridad(Prioridad prioridad) { this.prioridad = prioridad; return this; }

        /** Campo opcional: ruta multi-etapa. */
        public Builder ruta(Ruta ruta) { this.ruta = ruta; return this; }

        public Mision build() {
            if (id == null || id.isBlank()) {
                throw new IllegalStateException("El id de la misión no puede ser nulo ni vacío");
            }
            if (drone == null) {
                throw new IllegalStateException("El drone no puede ser nulo");
            }
            if (puntoPartida == null || puntoPartida.isBlank()) {
                throw new IllegalStateException("El punto de partida no puede ser nulo ni vacío");
            }
            if (puntoLlegada == null || puntoLlegada.isBlank()) {
                throw new IllegalStateException("El punto de llegada no puede ser nulo ni vacío");
            }
            if (tipoCarga == null) {
                throw new IllegalStateException("El tipo de carga no puede ser nulo");
            }
            if (estadoMision == null) {
                throw new IllegalStateException("El estado de misión no puede ser nulo");
            }
            if (prioridad == null) {
                throw new IllegalStateException("La prioridad no puede ser nula");
            }
            return new Mision(id, drone, puntoPartida, puntoLlegada,
                    tipoCarga, cargaGramos, estadoMision, prioridad, ruta);
        }
    }
}