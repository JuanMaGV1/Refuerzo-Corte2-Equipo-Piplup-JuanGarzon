package com.eci.aquaport.dominio;

/**
 * Punto de una ruta multi-etapa en una zona hídrica específica.
 *
 * Un Waypoint representa una parada en la ruta: puede ser el origen,
 * un punto intermedio de relevo entre drones, o el destino final.
 * El campo {@code orden} indica la posición dentro de la ruta
 * (empezando en 0) y permite reconstruir la secuencia de tramos.
 *
 * @param zona  nombre de la zona hídrica del catálogo de AquaPort
 * @param orden posición en la ruta (>= 0)
 */
public record Waypoint(String zona, int orden) {

    public Waypoint {
        if (zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("La zona del waypoint no puede ser nula ni vacía");
        }
        if (orden < 0) {
            throw new IllegalArgumentException("El orden del waypoint no puede ser negativo: " + orden);
        }
    }

    /** Constructor de conveniencia: waypoint de origen (orden 0). */
    public static Waypoint origen(String zona) {
        return new Waypoint(zona, 0);
    }

    /** Constructor de conveniencia: waypoint intermedio con orden dado. */
    public static Waypoint intermedio(String zona, int orden) {
        return new Waypoint(zona, orden);
    }
}