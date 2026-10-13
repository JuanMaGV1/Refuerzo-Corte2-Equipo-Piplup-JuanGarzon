package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Ruta;
import com.eci.aquaport.dominio.TramoRuta;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Consultas específicas de Empoleon sobre flota y misiones.
 *
 * Contiene las 4 consultas nuevas del nivel Empoleon:
 *  1. Eficiencia por zona (misiones entregadas / totales).
 *  2. Drone con mejor historial de entregas.
 *  3. Planificación de ruta multi-etapa sin repetir drones.
 *  4. Resumen agregado por zona (usa Collector personalizado).
 *
 * Las consultas heredadas de Prinplup siguen viviendo en
 * {@link AnalizadorFlota} y {@link AnalizadorMisiones}.
 *
 * Clase stateless: no tiene campos mutables. Todos los métodos reciben
 * la flota o las misiones como parámetro.
 *
 * Límite de líneas de Empoleon: <= 150. Esta clase respeta ese límite.
 */
public class AnalizadorFlotaEmpoleon {

    private static final int BATERIA_MINIMA = 35;

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 1 — Eficiencia por zona
    // ═══════════════════════════════════════════════════════════════

    /**
     * Calcula la eficiencia de cada zona como el ratio entre misiones
     * ENTREGADAS y el total de misiones cuyo destino final es esa zona.
     *
     * Para misiones multi-etapa, la eficiencia se atribuye a la zona
     * del waypoint de destino final ({@code puntoLlegada} del record
     * Mision, que coincide con el último waypoint de la ruta). Los
     * waypoints intermedios son puntos de relevo, no destinos finales.
     *
     * @return Map con clave = zona destino y valor = ratio [0.0, 1.0].
     *         Zonas sin misiones no aparecen en el map.
     */
    public Map<String, Double> eficienciaPorZona(List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.groupingBy(
                        Mision::puntoLlegada,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                AnalizadorFlotaEmpoleon::calcularEficiencia)));
    }

    private static double calcularEficiencia(List<Mision> misiones) {
        if (misiones.isEmpty()) {
            return 0.0;
        }
        long entregadas = misiones.stream()
                .filter(m -> m.estadoMision() == EstadoMision.ENTREGADA)
                .count();
        return (double) entregadas / misiones.size();
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 2 — Drone con mejor historial
    // ═══════════════════════════════════════════════════════════════

    /**
     * Encuentra el drone con mejor ratio de éxito, considerando solo
     * drones con al menos {@code minimoMisiones} ejecutadas.
     *
     * Desempate:
     *   1. Mayor ratio de éxito.
     *   2. Mayor número de entregas exitosas.
     *   3. Id en orden lexicográfico ASCENDENTE (AR-01 gana a AR-99).
     *
     * Nota sobre la implementación: se usa Comparator.reverseOrder()
     * sobre el id porque Stream.max() busca el elemento MAYOR según el
     * comparador. Al invertir el orden del id, el "mayor" según el
     * comparador es el id lexicográficamente menor. Resultado: el id
     * MENOR gana en caso de empate.
     *
     * @return Optional vacío si ningún drone cumple el mínimo.
     */
    public Optional<DroneAcuatico> droneConMejorHistorial(
            List<DroneAcuatico> flota, int minimoMisiones) {
        if (minimoMisiones < 0) {
            throw new IllegalArgumentException("El mínimo de misiones no puede ser negativo");
        }
        return flota.stream()
                .filter(d -> d.totalMisiones() >= minimoMisiones)
                .max(Comparator
                        .comparingDouble(DroneAcuatico::ratioExito)
                        .thenComparingInt(DroneAcuatico::entregasExitosas)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 3 — Planificación de ruta multi-etapa
    // ═══════════════════════════════════════════════════════════════

    /**
     * Para cada tramo de la ruta, selecciona el drone óptimo entre la
     * flota. Los drones ya asignados a un tramo anterior se excluyen
     * de los tramos siguientes para evitar asignar el mismo drone a
     * dos tramos simultáneos.
     *
     * Criterio por tramo:
     *   1. Preferencia: drone disponible en la zona ORIGEN del tramo,
     *      compatible con la zona DESTINO, batería >= 35.
     *   2. Fallback: cualquier drone disponible compatible con el destino.
     *
     * Desempate: mayor batería; si empatan, id en orden lexicográfico
     * ASCENDENTE (el menor gana, aplicando la misma lógica que en
     * droneConMejorHistorial).
     *
     * @return Map con clave = orden del tramo y valor = Optional con
     *         el drone asignado (empty si no hay candidato).
     */
    public Map<Integer, Optional<DroneAcuatico>> planificarRutaMultiEtapa(
            List<DroneAcuatico> flota, Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }
        Set<String> yaAsignados = new HashSet<>();
        return ruta.tramos().stream()
                .collect(Collectors.toMap(
                        TramoRuta::orden,
                        tramo -> {
                            Optional<DroneAcuatico> elegido =
                                    seleccionarDroneParaTramo(flota, tramo, yaAsignados);
                            elegido.ifPresent(d -> yaAsignados.add(d.id()));
                            return elegido;
                        }));
    }

    private Optional<DroneAcuatico> seleccionarDroneParaTramo(
            List<DroneAcuatico> flota, TramoRuta tramo, Set<String> yaAsignados) {
        Optional<DroneAcuatico> enOrigen = flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> !yaAsignados.contains(d.id()))
                .filter(d -> d.zona().equals(tramo.zonaOrigen()))
                .filter(d -> d.tipo().esCompatibleCon(tramo.zonaDestino()))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator
                        .comparingInt(DroneAcuatico::bateria)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));

        if (enOrigen.isPresent()) {
            return enOrigen;
        }

        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> !yaAsignados.contains(d.id()))
                .filter(d -> d.tipo().esCompatibleCon(tramo.zonaDestino()))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator
                        .comparingInt(DroneAcuatico::bateria)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 4 — Resumen por zona con Collector personalizado
    // ═══════════════════════════════════════════════════════════════

    /**
     * Resumen agregado por zona: cantidad de drones, promedio de
     * batería y cantidad de disponibles, calculado en una sola pasada
     * con el Collector personalizado {@link CollectorResumenZona}.
     */
    public Map<String, ResumenZona> resumenPorZona(List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::zona,
                        CollectorResumenZona.INSTANCE));
    }

    /** Resumen agregado de una zona. */
    public record ResumenZona(
            long cantidadDrones,
            double promedioBateria,
            long cantidadDisponibles
    ) { }
}