package com.eci.aquaport.aplicacion.estrategias;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.List;
import java.util.Optional;

/**
 * Estrategia de selección del drone óptimo para una misión.
 *
 * Cada implementación encapsula un criterio diferente. El
 * AsignadorAutomatico delega la decisión a la estrategia activa
 * sin conocer sus detalles.
 */
public interface EstrategiaSeleccion {

    /**
     * Selecciona el drone más adecuado para la misión entre los
     * candidatos ya filtrados por el asignador.
     *
     * @param candidatos drones disponibles y compatibles con la zona
     * @param mision     misión a asignar
     * @return el drone elegido, o Optional.empty si no hay ninguno adecuado
     */
    Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision);
}