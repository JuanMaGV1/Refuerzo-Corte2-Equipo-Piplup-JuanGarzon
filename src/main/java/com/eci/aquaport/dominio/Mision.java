package com.eci.aquaport.dominio;

public record Mision(
    String id,
    DroneAcuatico drone,
    String puntoPartida,
    String puntoLlegada,
    TipoCarga tipoCarga,
    int cargaGramos,
    EstadoMision estadoMision,
    Prioridad prioridad
) {

    /**
     * Builder para construcción segura de Mision.
     *
     * El Builder valida estructura: campos obligatorios presentes y no vacíos.
     * Las reglas de negocio (disponibilidad, batería, estado) viven en
     * ValidadorMision de la capa de aplicación.
     *
     * Este Builder conserva el contrato de Piplup (Reto 02) y es compatible
     * con los tests existentes de ValidadorMision.
     */
    public static class Builder {
        private String id;
        private DroneAcuatico drone;
        private String puntoPartida;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private EstadoMision estadoMision;
        private Prioridad prioridad;

        private int cargaGramos = 1; // por defecto 1g (no nulo)

        public Builder id(String id) { this.id = id; return this; }
        public Builder drone(DroneAcuatico drone) { this.drone = drone; return this; }
        public Builder puntoPartida(String puntoPartida) { this.puntoPartida = puntoPartida; return this; }
        public Builder puntoLlegada(String puntoLlegada) { this.puntoLlegada = puntoLlegada; return this; }
        public Builder tipoCarga(TipoCarga tipoCarga) { this.tipoCarga = tipoCarga; return this; }
        public Builder estadoMision(EstadoMision estadoMision) { this.estadoMision = estadoMision; return this; }
        public Builder prioridad(Prioridad prioridad) { this.prioridad = prioridad; return this; }
        public Builder cargaGramos(int gramos){
            if (gramos <= 0){
                throw new IllegalArgumentException("La carga debe ser > 0");
            }
            this.cargaGramos = gramos;
            return this;
        }

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
                    tipoCarga, cargaGramos, estadoMision, prioridad);
        }
    }
}