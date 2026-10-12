package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Consultas agregadas sobre la flota de drones: agrupaciones, promedios
 * y selecciones por criterio compuesto.
 *
 * Complementa a {@link ConsultorFlota}, que expone consultas simples
 * (filtrar, ordenar, contar). Este analizador se centra en las operaciones
 * avanzadas de Streams: groupingBy, averagingInt, max con filtro, toSet.
 */
public class AnalizadorFlota {

    private static final int BATERIA_MINIMA = 35;

    /**
     * Consulta 1: Agrupa los drones disponibles por tipo.
     *
     * @return Map cuya clave es el tipo y el valor es la lista de drones
     *         disponibles de ese tipo. Si un tipo no tiene drones
     *         disponibles, no aparece como clave.
     */
    public Map<TipoDrone, List<DroneAcuatico>> agruparDisponiblesPorTipo(
            List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .collect(Collectors.groupingBy(DroneAcuatico::tipo));
    }

    /**
     * Consulta 2: Encuentra el drone óptimo para una zona dada.
     *
     * Criterios:
     *  - El drone debe estar disponible.
     *  - El TIPO del drone debe ser compatible con la zona.
     *  - Batería >= 35%.
     *  - Se elige el de mayor batería entre los candidatos.
     *
     * @return Optional.empty si no hay candidatos válidos
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
     * Considera TODOS los drones, no solo los disponibles.
     */
    public Map<TipoDrone, Double> promedioBateriaPorTipo(
            List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::tipo,
                        Collectors.averagingInt(DroneAcuatico::bateria)));
    }

    /**
     * Consulta 4: Zonas únicas que cubre la flota activa.
     * "Activa" = no está en MANTENIMIENTO.
     */
    public Set<String> zonasUnicasDeFlotaActiva(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(d -> d.estado() != EstadoDrone.MANTENIMIENTO)
                .map(DroneAcuatico::zona)
                .collect(Collectors.toSet());
    }

    /**
     * Bonus: Batería total de la flota disponible (reduce).
     */
    public int bateriaTotalFlotaDisponible(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .map(DroneAcuatico::bateria)
                .reduce(0, Integer::sum);
    }

     /**
     * Devuelve los drones cuya batería está por debajo del umbral operativo.
     *
     * Útil para planificar recargas antes de asignar misiones: el operador
     * puede ver de un vistazo qué drones necesitan pasar por la estación
     * de carga.
     *
     * @return lista inmutable con los drones con batería < 35%
     */
    public List<DroneAcuatico> dronesConBateriaCritica(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(d -> d.bateria() < BATERIA_MINIMA)
                .toList();
    }
}