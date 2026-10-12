package com.eci.aquaport.dominio;

public record DroneAcuatico(
        String id,
        String modelo,
        int bateria,
        EstadoDrone estado,
        String zona,
        TipoDrone tipo
) {

    /**
     * Indica si el drone está operativo para recibir nuevas misiones.
     *
     * Un drone está disponible cuando su estado es exactamente
     * {@link EstadoDrone#DISPONIBLE}. Este método centraliza la regla
     * para que las consultas no repitan la comparación directa contra
     * el enum.
     *
     * @return true si el drone puede recibir una nueva misión
     */
    public boolean estaDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }
}