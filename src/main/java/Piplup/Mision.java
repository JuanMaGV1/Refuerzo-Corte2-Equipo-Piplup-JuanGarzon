package Piplup;

import java.util.List;

public class Mision {

    private final String id;
    private final DroneAcuatico drone;
    private final String puntoPartida;
    private final String puntoLlegada;
    private final TipoCarga tipoCarga;
    private final EstadoMision estadoMision;

    private Mision(Builder builder) {
        this.id = builder.id;
        this.drone = builder.drone;
        this.puntoPartida = builder.puntoPartida;
        this.puntoLlegada = builder.puntoLlegada;
        this.tipoCarga = builder.tipoCarga;
        this.estadoMision = builder.estadoMision;
    }

    public String getId() { return id; }
    public DroneAcuatico getDrone() { return drone; }
    public String getPuntoPartida() { return puntoPartida; }
    public String getPuntoLlegada() { return puntoLlegada; }
    public TipoCarga getTipoCarga() { return tipoCarga; }
    public EstadoMision getEstadoMision() { return estadoMision; }


    public static class Builder {
        private String id;
        private DroneAcuatico drone;
        private String puntoPartida;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private EstadoMision estadoMision;

        public Builder id(String id) { this.id = id; return this; }
        public Builder drone(DroneAcuatico drone) { this.drone = drone; return this; }
        public Builder puntoPartida(String puntoPartida) { this.puntoPartida = puntoPartida; return this; }
        public Builder puntoLlegada(String puntoLlegada) { this.puntoLlegada = puntoLlegada; return this; }
        public Builder tipoCarga(TipoCarga tipoCarga) { this.tipoCarga = tipoCarga; return this; }
        public Builder estadoMision(EstadoMision estadoMision) { this.estadoMision = estadoMision; return this; }

        /**
         * Construye una Mision sintácticamente válida.
         *
         * El Builder SOLO valida estructura: campos obligatorios presentes.
         * Las reglas de negocio (disponibilidad, batería, estado) las valida
         * ValidadorMision. Esto separa responsabilidades (SRP) y permite
         * construir misiones "crudas" para probar el validador.
         */
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
            return new Mision(this);
        }
    }
}