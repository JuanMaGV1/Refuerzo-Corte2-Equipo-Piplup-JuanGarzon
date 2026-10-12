package com.eci.aquaport.aplicacion;

import java.util.Set;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.TipoDrone.Zonas;

public class ValidadorMision {

    private static final Set<String> ZONAS_VALIDAS = Set.of(
            Zonas.EMBALSE_NORTE,
            Zonas.CANAL_CENTRAL,
            Zonas.LAGUNA_SUR,
            Zonas.PUNTO_RIBERENO_ESTE,
            Zonas.LABORATORIO_HIDRICO
    );
    
    private static final int BATERIA_MINIMA = 35;

    public boolean isMisionValida(Mision mision) {
        return mision != null
                && isPuntosValidos(mision)
                && isDroneValido(mision)
                && isEstadoMisionValido(mision);
    }

    public boolean isBateriaSuficiente(DroneAcuatico drone) {
        return drone != null && drone.bateria() >= BATERIA_MINIMA;
    }

    public boolean isDisponible(DroneAcuatico drone) {
        return drone != null && drone.estado() == EstadoDrone.DISPONIBLE;
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo");
        }
        if (puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada no puede estar vacío");
        }
    }

    public boolean esZonaValida(String zona) {
        return zona != null && ZONAS_VALIDAS.contains(zona);
    }

    // ═══════════════════════════════════════════════════════
    // Validaciones privadas de la misión completa
    // ═══════════════════════════════════════════════════════

    private boolean isPuntosValidos(Mision mision) {
        return mision.puntoLlegada() != null
                && mision.puntoPartida() != null
                && !mision.puntoLlegada().isBlank()
                && !mision.puntoPartida().isBlank();
    }

    private boolean isDroneValido(Mision mision) {
        return mision.drone() != null
                && mision.drone().estado() == EstadoDrone.DISPONIBLE
                && mision.drone().bateria() >= BATERIA_MINIMA;
    }

    private boolean isEstadoMisionValido(Mision mision) {
        return mision.estadoMision() == EstadoMision.PENDIENTE;
    }
}