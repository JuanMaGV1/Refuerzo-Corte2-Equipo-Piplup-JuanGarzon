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
 *   · SUPERFICIAL     → opera en aguas calmas y poco profundas.
 *                        No puede sumergirse ni operar en aguas agitadas
 *                        (riesgo de vuelco).
 *                        Zonas: Canal Central, Punto Ribereño Este,
 *                        Laboratorio Hídrico (entrega final).
 *
 *   · SEMISUMERGIDO   → opera parcialmente sumergido; tolera aguas
 *                        agitadas y profundidad media, pero no alcanza
 *                        el fondo del Embalse Norte.
 *                        Zonas: Canal Central, Laguna Sur,
 *                        Punto Ribereño Este, Laboratorio Hídrico.
 *
 *   · BUCEADOR        → se sumerge completamente; el único tipo capaz de
 *                        operar en el Embalse Norte (profundo) y de tomar
 *                        muestras a profundidad en Laguna Sur.
 *                        Zonas: Embalse Norte, Laguna Sur,
 *                        Laboratorio Hídrico.
 *
 * El Laboratorio Hídrico es el destino común de todas las muestras, por
 * eso aparece como zona compatible con los 3 tipos.
 */
public enum TipoDrone {

    SUPERFICIAL(Set.of(
            Zonas.CANAL_CENTRAL,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO
    )),

    SEMISUMERGIDO(Set.of(
            Zonas.CANAL_CENTRAL,
            Zonas.LAGUNA_SUR,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO
    )),

    BUCEADOR(Set.of(
            Zonas.EMBALSE_NORTE,
            Zonas.LAGUNA_SUR,
            Zonas.LABORATORIO_HIDRICO
    ));

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
     *
     * Útil para mostrar al operador en el panel: "¿dónde puede operar
     * este drone?". La copia es inmutable para que no se pueda modificar
     * el comportamiento del enum desde fuera.
     */
    public Set<String> zonasCompatibles() {
        return Set.copyOf(zonasCompatibles);
    }
    /**
     * Catálogo centralizado de zonas hídricas.
     *
     * Se declara como clase anidada estática y no como campos del propio
     * enum porque en Java los valores del enum se inicializan ANTES que
     * los campos estáticos del propio enum; referenciarlos durante la
     * construcción de los valores daría null.
     */
    public static final class Zonas {
        public static final String EMBALSE_NORTE       = "Embalse Norte";
        public static final String CANAL_CENTRAL       = "Canal Central";
        public static final String LAGUNA_SUR          = "Laguna Sur";
        public static final String PUNTO_RIBERENO_ESTE = "Punto Ribereño Este";
        public static final String LABORATORIO_HIDRICO = "Laboratorio Hídrico";

        private Zonas() {}
    }
}