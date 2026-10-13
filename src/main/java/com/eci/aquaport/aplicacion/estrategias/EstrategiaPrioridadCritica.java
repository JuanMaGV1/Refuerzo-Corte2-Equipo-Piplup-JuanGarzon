package com.eci.aquaport.aplicacion.estrategias;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia para misiones CRÍTICAS (AP-08).
 *
 * Prioriza la aptitud técnica: capacidad de carga máxima. La batería
 * NO se usa como criterio de selección ni como desempate, porque el
 * requisito AP-08 dice explícitamente "independientemente de la batería".
 *
 * Si dos drones tienen la misma capacidad, se desempata por ID en
 * orden lexicográfico ascendente (AR-01 gana a AR-99). Como usamos
 * Stream.max(), el desempate se invierte con Comparator.reverseOrder()
 * para que el ID "menor" sea el elegido.
 *
 * Si la misión NO es crítica, delega en el fallback (EstrategiaMayorBateria),
 * que implementa AP-07.
 */
public class EstrategiaPrioridadCritica implements EstrategiaSeleccion {

    private final EstrategiaSeleccion fallback;

    public EstrategiaPrioridadCritica(EstrategiaSeleccion fallback) {
        if (fallback == null) {
            throw new IllegalArgumentException("El fallback no puede ser nulo");
        }
        this.fallback = fallback;
    }

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        if (mision.prioridad() != Prioridad.CRITICA) {
            return fallback.seleccionar(candidatos, mision);
        }
        return candidatos.stream()
                .max(Comparator.comparingInt(DroneAcuatico::capacidadCargaGramos)
                        .thenComparing(DroneAcuatico::id, Comparator.reverseOrder()));
    }
}