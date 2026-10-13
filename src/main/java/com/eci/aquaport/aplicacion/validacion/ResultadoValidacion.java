package com.eci.aquaport.aplicacion.validacion;

/**
 * Resultado de una validación en la cadena de responsabilidad.
 *
 * Un resultado es válido o inválido. Si es inválido, incluye la razón
 * para poder trazarla en los logs y notificar al operador.
 *
 * Se usa un record en lugar de excepciones para no abusar de las
 * excepciones como flujo de control.
 */
public record ResultadoValidacion(boolean esValido, String razon) {

    /** Constructor de éxito. */
    public static ResultadoValidacion valido() {
        return new ResultadoValidacion(true, null);
    }

    /** Constructor de fallo con razón. */
    public static ResultadoValidacion invalido(String razon) {
        if (razon == null || razon.isBlank()) {
            throw new IllegalArgumentException("La razón del fallo no puede ser nula ni vacía");
        }
        return new ResultadoValidacion(false, razon);
    }
}