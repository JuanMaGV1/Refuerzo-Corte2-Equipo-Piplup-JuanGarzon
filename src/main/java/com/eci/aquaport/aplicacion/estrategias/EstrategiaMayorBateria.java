package com.eci.aquaport.aplicacion.estrategias;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia por defecto: elige el drone con mayor batería entre los
 * candidatos. Prioriza autonomía sobre cualquier otro criterio.
 */
public class EstrategiaMayorBateria implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        return candidatos.stream()
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}