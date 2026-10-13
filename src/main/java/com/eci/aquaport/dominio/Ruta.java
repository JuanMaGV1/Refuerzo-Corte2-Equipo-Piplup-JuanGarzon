package com.eci.aquaport.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ruta multi-etapa de una misión de Empoleon.
 *
 * Una ruta es una secuencia ordenada de waypoints. El primero es el
 * origen, el último es el destino final, y los intermedios son puntos
 * de relevo entre drones.
 *
 * Los tramos se derivan de los waypoints: N waypoints producen N-1 tramos.
 * Los waypoints deben tener órdenes consecutivos desde 0.
 */
public record Ruta(List<Waypoint> waypoints) {

    public Ruta {
        if (waypoints == null) {
            throw new IllegalArgumentException("La lista de waypoints no puede ser nula");
        }
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException(
                    "Una ruta necesita al menos 2 waypoints (origen y destino)");
        }
        // Verificar que los órdenes sean consecutivos desde 0
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).orden() != i) {
                throw new IllegalArgumentException(
                        "Los waypoints deben tener orden consecutivo desde 0. "
                        + "Se esperaba " + i + " pero se encontró " + waypoints.get(i).orden());
            }
        }
        // Defensa contra listas mutables
        waypoints = List.copyOf(waypoints);
    }

    /** Waypoint de origen. */
    public Waypoint origen() {
        return waypoints.get(0);
    }

    /** Waypoint de destino final. */
    public Waypoint destino() {
        return waypoints.get(waypoints.size() - 1);
    }

    /** Zona del waypoint de origen. */
    public String zonaOrigen() {
        return origen().zona();
    }

    /** Zona del waypoint de destino final. */
    public String zonaDestino() {
        return destino().zona();
    }

    /**
     * Deriva los tramos de la ruta: N-1 tramos para N waypoints.
     *
     * @return lista inmutable de tramos ordenados por {@code orden}
     */
    public List<TramoRuta> tramos() {
        List<TramoRuta> tramos = new ArrayList<>(waypoints.size() - 1);
        for (int i = 0; i < waypoints.size() - 1; i++) {
            tramos.add(new TramoRuta(waypoints.get(i), waypoints.get(i + 1), i));
        }
        return List.copyOf(tramos);
    }

    /** Número de tramos (waypoints.size() - 1). */
    public int numeroTramos() {
        return waypoints.size() - 1;
    }

    /** Zonas únicas que atraviesa la ruta (sin duplicados). */
    public Set<String> zonas() {
        return waypoints.stream()
                .map(Waypoint::zona)
                .collect(Collectors.toSet());
    }

    /**
     * Indica si la ruta pasa por la zona indicada.
     */
    public boolean pasaPor(String zona) {
        return waypoints.stream()
                .anyMatch(w -> w.zona().equals(zona));
    }
}