package com.eci.aquaport.aplicacion.estrategias;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.TipoCarga;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EstrategiaTipoCarga implements EstrategiaSeleccion {

    private final EstrategiaSeleccion fallback;

    public EstrategiaTipoCarga(EstrategiaSeleccion fallback) {
        if (fallback == null) {
            throw new IllegalArgumentException("El fallback no puede ser nulo");
        }
        this.fallback = fallback;
    }

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        if (mision.tipoCarga() == TipoCarga.EQUIPO_MEDICION) {
            return candidatos.stream()
                    .max(Comparator.comparingInt(DroneAcuatico::capacidadCargaGramos)
                            .thenComparingInt(DroneAcuatico::bateria));
        }
        return fallback.seleccionar(candidatos, mision);
    }
}