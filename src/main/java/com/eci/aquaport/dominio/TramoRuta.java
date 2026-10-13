package com.eci.aquaport.dominio;

/**
 * Segmento entre dos waypoints consecutivos de una ruta multi-etapa.
 *
 * Cada tramo puede ser ejecutado por un drone distinto: los drones se
 * relevan en los waypoints intermedios. El tramo conoce su origen, su
 * destino y su posición dentro de la ruta.
 *
 * @param origen  waypoint inicial del tramo
 * @param destino waypoint final del tramo
 * @param orden   posición del tramo en la ruta (>= 0)
 */
public record TramoRuta(Waypoint origen, Waypoint destino, int orden) {

    public TramoRuta {
        if (origen == null) {
            throw new IllegalArgumentException("El waypoint de origen no puede ser nulo");
        }
        if (destino == null) {
            throw new IllegalArgumentException("El waypoint de destino no puede ser nulo");
        }
        if (orden < 0) {
            throw new IllegalArgumentException("El orden del tramo no puede ser negativo: " + orden);
        }
        if (origen.equals(destino)) {
            throw new IllegalArgumentException(
                    "El origen y el destino del tramo no pueden ser el mismo waypoint");
        }
    }

    /** Zona del waypoint de origen. */
    public String zonaOrigen() {
        return origen.zona();
    }

    /** Zona del waypoint de destino. */
    public String zonaDestino() {
        return destino.zona();
    }
}