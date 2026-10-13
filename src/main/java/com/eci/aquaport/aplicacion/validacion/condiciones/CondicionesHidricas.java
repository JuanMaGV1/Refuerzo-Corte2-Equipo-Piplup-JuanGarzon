package com.eci.aquaport.aplicacion.validacion.condiciones;

/**
 * Condiciones hídricas de una zona en un momento dado.
 *
 * Modelo de dominio (adaptado desde la respuesta JSON de la API externa).
 */
public record CondicionesHidricas(
        NivelAgitacion agitacion,
        double profundidadMetros,
        double temperaturaCelsius
) {

    public CondicionesHidricas {
        if (agitacion == null) {
            throw new IllegalArgumentException("El nivel de agitación no puede ser nulo");
        }
        if (profundidadMetros < 0) {
            throw new IllegalArgumentException("La profundidad no puede ser negativa");
        }
    }

    /** Nivel de agitación del agua. */
    public enum NivelAgitacion {
        CALMA,
        MODERADA,
        AGITADA
    }
}