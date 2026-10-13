package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.Ruta;
import com.eci.aquaport.dominio.TipoDrone;
import com.eci.aquaport.dominio.TramoRuta;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Consultas avanzadas de Empoleon sobre la flota de 60 drones.
 *
 * Contiene las 5 consultas del reto 01 Prinplup (agrupar por tipo,
 * drone óptimo, promedio de batería, separar críticas, zonas únicas)
 * más 4 consultas nuevas orientadas a rutas multi-etapa y análisis
 * de eficiencia.
 *
 * Diseño: la clase es stateless (no tiene campos). Todos los métodos
 * reciben la flota o las misiones como parámetro.
 */
public class AnalizadorFlotaEmpoleon {

    private static final int BATERIA_MINIMA = 35;

    // ═══════════════════════════════════════════════════════════════
    // CONSULTAS 1-5 — Heredadas de Prinplup
    // ═══════════════════════════════════════════════════════════════

    /**
     * Consulta 1: Agrupa los drones disponibles por tipo.
     */
    public Map<TipoDrone, List<DroneAcuatico>> agruparDisponiblesPorTipo(
            List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .collect(Collectors.groupingBy(DroneAcuatico::tipo));
    }

    /**
     * Consulta 2: Encuentra el drone óptimo para una zona dada.
     * Criterio: disponible + tipo compatible + batería >= 35. Desempata
     * por mayor batería.
     */
    public Optional<DroneAcuatico> droneOptimoParaZona(
            List<DroneAcuatico> flota, String zona) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> d.tipo().esCompatibleCon(zona))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }

    /**
     * Consulta 3: Promedio de batería por tipo de drone.
     */
    public Map<TipoDrone, Double> promedioBateriaPorTipo(
            List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::tipo,
                        Collectors.averagingInt(DroneAcuatico::bateria)));
    }

    /**
     * Consulta 4: Separa misiones CRÍTICAS del resto.
     */
    public Map<Boolean, List<Mision>> separarMisionesCriticas(
            List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.partitioningBy(
                        m -> m.prioridad() == Prioridad.CRITICA));
    }

    /**
     * Consulta 5: Zonas únicas cubiertas por la flota activa
     * (excluye drones en MANTENIMIENTO).
     */
    public Set<String> zonasUnicasDeFlotaActiva(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(d -> d.estado() != EstadoDrone.MANTENIMIENTO)
                .map(DroneAcuatico::zona)
                .collect(Collectors.toSet());
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 6 — Eficiencia por zona
    // ═══════════════════════════════════════════════════════════════

    /**
     * Calcula la eficiencia de cada zona: misiones ENTREGADAS / total
     * de misiones cuyo destino es esa zona.
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
        long entregadas = misiones.stream()
                .filter(m -> m.estadoMision() == EstadoMision.ENTREGADA)
                .count();
        return (double) entregadas / misiones.size();
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 7 — Drone con mejor historial
    // ═══════════════════════════════════════════════════════════════

    /**
     * Encuentra el drone con mejor ratio de éxito, considerando solo
     * drones con al menos {@code minimoMisiones} ejecutadas.
     *
     * Desempate:
     *   1. Mayor ratio de éxito.
     *   2. Mayor número de entregas exitosas.
     *   3. Id lexicográficamente menor (determinismo).
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
    // CONSULTA 8 — Planificación de ruta multi-etapa
    // ═══════════════════════════════════════════════════════════════

    /**
     * Para cada tramo de la ruta, selecciona el drone óptimo entre la
     * flota.
     *
     * Criterio de selección por tramo:
     *   1. Preferencia: drone disponible en la zona de ORIGEN del tramo,
     *      compatible con la zona DESTINO, batería >= 35.
     *   2. Fallback: cualquier drone disponible compatible con el destino.
     *
     * Desempate: mayor batería. Si empatan, id lexicográficamente menor.
     *
     * @return Map con clave = orden del tramo (0-indexado) y valor =
     *         Optional con el drone asignado (empty si no hay candidato).
     */
    public Map<Integer, Optional<DroneAcuatico>> planificarRutaMultiEtapa(
            List<DroneAcuatico> flota, Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }
        return ruta.tramos().stream()
                .collect(Collectors.toMap(
                        TramoRuta::orden,
                        tramo -> seleccionarDroneParaTramo(flota, tramo)));
    }

    private Optional<DroneAcuatico> seleccionarDroneParaTramo(
            List<DroneAcuatico> flota, TramoRuta tramo) {
        Optional<DroneAcuatico> enOrigen = flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> d.zona().equals(tramo.zonaOrigen()))
                .filter(d -> d.tipo().esCompatibleCon(tramo.zonaDestino()))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator
                        .comparingInt(DroneAcuatico::bateria)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));

        if (enOrigen.isPresent()) {
            return enOrigen;
        }

        // Fallback: cualquier drone compatible con el destino
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> d.tipo().esCompatibleCon(tramo.zonaDestino()))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator
                        .comparingInt(DroneAcuatico::bateria)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 9 — Resumen por zona con Collector personalizado
    // ═══════════════════════════════════════════════════════════════

    /**
     * Resumen agregado por zona: cantidad de drones, promedio de
     * batería y cantidad de disponibles, calculado en una sola pasada
     * con un Collector personalizado.
     *
     * @return Map con clave = zona y valor = ResumenZona.
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