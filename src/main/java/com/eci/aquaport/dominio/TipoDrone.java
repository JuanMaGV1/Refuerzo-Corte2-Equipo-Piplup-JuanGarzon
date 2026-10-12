package com.eci.aquaport.dominio;

import java.util.Set;

public enum TipoDrone {

    SUPERFICIAL(Set.of(
            Zonas.CANAL_CENTRAL,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO
    )) {
        @Override
        public DroneAcuatico crear(String id, String modelo, int bateria,
                                    EstadoDrone estado, String zona) {
            return new DroneSuperficial(id, modelo, bateria, estado, zona);
        }
    },

    SEMISUMERGIDO(Set.of(
            Zonas.CANAL_CENTRAL,
            Zonas.LAGUNA_SUR,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO
    )) {
        @Override
        public DroneAcuatico crear(String id, String modelo, int bateria,
                                    EstadoDrone estado, String zona) {
            return new DroneSemisumergido(id, modelo, bateria, estado, zona);
        }
    },

    BUCEADOR(Set.of(
            Zonas.EMBALSE_NORTE,
            Zonas.LAGUNA_SUR,
            Zonas.LABORATORIO_HIDRICO
    )) {
        @Override
        public DroneAcuatico crear(String id, String modelo, int bateria,
                                    EstadoDrone estado, String zona) {
            return new DroneBuceador(id, modelo, bateria, estado, zona);
        }
    };

    private final Set<String> zonasCompatibles;

    TipoDrone(Set<String> zonasCompatibles) {
        this.zonasCompatibles = zonasCompatibles;
    }

    public boolean esCompatibleCon(String zona) {
        return zona != null && !zona.isBlank() && zonasCompatibles.contains(zona);
    }

    public Set<String> zonasCompatibles() {
        return Set.copyOf(zonasCompatibles);
    }

    /**
     * Factory Method polimórfico.
     *
     * Cada constante del enum construye su propia subclase. Añadir un
     * tipo nuevo NO requiere tocar la fábrica ni ningún switch: solo
     * agregar una constante al enum con su implementación de crear().
     * Eso es OCP real.
     */
    public abstract DroneAcuatico crear(String id, String modelo, int bateria,
                                         EstadoDrone estado, String zona);

    public static final class Zonas {
        public static final String EMBALSE_NORTE       = "Embalse Norte";
        public static final String CANAL_CENTRAL       = "Canal Central";
        public static final String LAGUNA_SUR          = "Laguna Sur";
        public static final String PUNTO_RIBERENO_ESTE = "Punto Ribereño Este";
        public static final String LABORATORIO_HIDRICO = "Laboratorio Hídrico";

        private Zonas() { }
    }
}