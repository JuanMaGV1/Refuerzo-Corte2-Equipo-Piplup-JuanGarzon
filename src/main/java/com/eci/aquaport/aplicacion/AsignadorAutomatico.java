package com.eci.aquaport.aplicacion;

import com.eci.aquaport.aplicacion.estrategias.EstrategiaSeleccion;
import com.eci.aquaport.aplicacion.observadores.ObservadorMision;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.Mision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsignadorAutomatico {

    private static final int BATERIA_MINIMA = 35;

    private final List<DroneAcuatico> flota;
    private final EstrategiaSeleccion estrategia;
    private final List<ObservadorMision> observadores = new ArrayList<>();

    /**
     * Constructor injection: la flota y la estrategia se reciben al
     * construir el asignador. No hay setters mutables.
     */
    public AsignadorAutomatico(List<DroneAcuatico> flota,
                                EstrategiaSeleccion estrategia) {
        if (flota == null) {
            throw new IllegalArgumentException("La flota no puede ser nula");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser nula");
        }
        this.flota = List.copyOf(flota);
        this.estrategia = estrategia;
    }

    public void registrarObservador(ObservadorMision observador) {
        if (observador != null) {
            observadores.add(observador);
        }
    }

    public void eliminarObservador(ObservadorMision observador) {
        observadores.remove(observador);
    }

    /**
     * Asigna un drone a la misión usando la estrategia activa.
     *
     * Distingue tres escenarios de fallo:
     *   1. Todos los drones compatibles están en FALLO.
     *   2. No hay drones disponibles (ninguno en DISPONIBLE).
     *   3. La estrategia no encuentra candidato adecuado.
     *
     * En los 3 casos notifica a los observadores exactamente una vez.
     */
    public Optional<DroneAcuatico> asignar(Mision mision) {
        List<DroneAcuatico> candidatos = filtrarCandidatos(mision);

        // Escenario 1: hay drones compatibles con la zona, pero TODOS en FALLO
        boolean hayDronesEnFalloEnLaZona = flota.stream()
                .filter(d -> d.estado() == EstadoDrone.FALLO)
                .anyMatch(d -> d.tipo().esCompatibleCon(mision.puntoLlegada()));

        if (candidatos.isEmpty() && hayDronesEnFalloEnLaZona) {
            notificarFallo(mision, "Drone(s) en FALLO en la zona destino");
            return Optional.empty();
        }

        // Escenario 2: no hay candidatos (ninguno disponible, batería, etc.)
        if (candidatos.isEmpty()) {
            notificarFallo(mision, "Sin drones candidatos para la misión");
            return Optional.empty();
        }

        // Escenario 3: la estrategia no encuentra ninguno adecuado
        Optional<DroneAcuatico> elegido = estrategia.seleccionar(candidatos, mision);
        if (elegido.isEmpty()) {
            notificarFallo(mision, "La estrategia no encontró drone adecuado");
            return Optional.empty();
        }

        DroneAcuatico drone = elegido.get();
        drone.setEstado(EstadoDrone.EN_MISION);
        notificarAsignacion(mision, drone);
        return Optional.of(drone);
    }

    private List<DroneAcuatico> filtrarCandidatos(Mision mision) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(d -> d.tipo().esCompatibleCon(mision.puntoLlegada()))
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .filter(d -> d.capacidadCargaGramos() >= mision.cargaGramos())
                .toList();
    }

    private void notificarAsignacion(Mision mision, DroneAcuatico drone) {
        observadores.forEach(o -> o.notificarAsignacion(mision, drone));
    }

    private void notificarFallo(Mision mision, String razon) {
        observadores.forEach(o -> o.notificarFalloAsignacion(mision, razon));
    }
}