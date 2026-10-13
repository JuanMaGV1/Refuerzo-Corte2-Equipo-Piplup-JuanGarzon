package com.eci.aquaport.dominio;

import java.util.Set;

/**
 * Tipos de drones acuáticos de AquaPort y su compatibilidad con zonas hídricas.
 *
 * ─── Regla de dominio: compatibilidad tipo ↔ zona ─────────────────────
 *
 * La compatibilidad depende de las condiciones físicas del cuerpo de agua
 * y del diseño de cada tipo de drone:
 *
 *   · SUPERFICIAL     → aguas calmas y poco profundas. No puede sumergirse
 *                        ni operar en aguas agitadas.
 *   · SEMISUMERGIDO   → parcialmente sumergido; tolera aguas agitadas y
 *                        profundidad media.
 *   · BUCEADOR        → se sumerge completamente; único tipo capaz de
 *                        operar en aguas profundas.
 *
 * El catálogo cubre las zonas de Prinplup y de Empoleon.
 *
 * ─── Extensibilidad (OCP) ──────────────────────────────────────────
 *
 * Cada constante del enum declara sus zonas en el constructor. Añadir un
 * tipo nuevo NO requiere tocar ningún método existente.
 */
public enum TipoDrone {

    SUPERFICIAL(Set.of(
            // Zonas de Prinplup
            Zonas.CANAL_CENTRAL,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO,
            // Zonas de Empoleon
            Zonas.RED_DE_CANALES,
            Zonas.LABORATORIO_HIDRICO_CENTRAL
    )) {
        @Override
        public DroneAcuatico crear(String id, String modelo, int bateria,
                                    EstadoDrone estado, String zona) {
            return new DroneSuperficial(id, modelo, bateria, estado, zona);
        }
    },

    SEMISUMERGIDO(Set.of(
            // Zonas de Prinplup
            Zonas.CANAL_CENTRAL,
            Zonas.LAGUNA_SUR,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO,
            // Zonas de Empoleon
            Zonas.RED_DE_CANALES,
            Zonas.LAGUNA_DE_RESERVA,
            Zonas.LABORATORIO_HIDRICO_CENTRAL
    )) {
        @Override
        public DroneAcuatico crear(String id, String modelo, int bateria,
                                    EstadoDrone estado, String zona) {
            return new DroneSemisumergido(id, modelo, bateria, estado, zona);
        }
    },

    BUCEADOR(Set.of(
            // Zonas de Prinplup
            Zonas.EMBALSE_NORTE,
            Zonas.LAGUNA_SUR,
            Zonas.LABORATORIO_HIDRICO,
            // Zonas de Empoleon
            Zonas.EMBALSE_DE_INVESTIGACION,
            Zonas.LAGUNA_DE_RESERVA,
            Zonas.LABORATORIO_HIDRICO_CENTRAL
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

    /**
     * Indica si este tipo de drone puede operar en la zona dada.
     *
     * @param zona nombre de la zona hídrica (ver {@link Zonas})
     * @return true si el tipo es técnicamente compatible con la zona
     */
    public boolean esCompatibleCon(String zona) {
        return zona != null && !zona.isBlank() && zonasCompatibles.contains(zona);
    }

    /**
     * Devuelve una copia inmutable de las zonas compatibles con este tipo.
     */
    public Set<String> zonasCompatibles() {
        return Set.copyOf(zonasCompatibles);
    }

    /**
     * Factory Method polimórfico.
     *
     * Cada constante del enum construye su propia subclase. Añadir un
     * tipo nuevo NO requiere tocar la fábrica ni ningún switch: solo
     * agregar una constante al enum con su implementación de crear().
     */
    public abstract DroneAcuatico crear(String id, String modelo, int bateria,
                                         EstadoDrone estado, String zona);

    /**
     * Catálogo centralizado de zonas hídricas.
     *
     * Contiene tanto las zonas de Prinplup (retrocompatibilidad) como las
     * de Empoleon. Se declara como clase anidada estática y no como campos
     * del propio enum porque en Java los valores del enum se inicializan
     * ANTES que los campos estáticos del propio enum.
     */
    public static final class Zonas {

        // ─── Zonas de Prinplup ────────────────────────────────
        public static final String EMBALSE_NORTE       = "Embalse Norte";
        public static final String CANAL_CENTRAL       = "Canal Central";
        public static final String LAGUNA_SUR          = "Laguna Sur";
        public static final String PUNTO_RIBERENO_ESTE = "Punto Ribereño Este";
        public static final String LABORATORIO_HIDRICO = "Laboratorio Hídrico";

        // ─── Zonas de Empoleon ────────────────────────────────
        public static final String EMBALSE_DE_INVESTIGACION    = "Embalse de Investigación";
        public static final String RED_DE_CANALES              = "Red de Canales";
        public static final String LAGUNA_DE_RESERVA           = "Laguna de Reserva";
        public static final String LABORATORIO_HIDRICO_CENTRAL = "Laboratorio Hídrico Central";

        private Zonas() {
            // Clase de utilidad: no instanciable
        }
    }
}