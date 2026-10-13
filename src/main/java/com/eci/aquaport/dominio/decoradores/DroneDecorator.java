package com.eci.aquaport.dominio.decoradores;

import com.eci.aquaport.dominio.DroneOperable;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;

/**
 * Base para los decoradores de drones.
 *
 * Un DroneDecorator envuelve a un DroneOperable y delega en él todas
 * las operaciones. Las subclases añaden comportamiento sin modificar
 * la clase base ni el drone envuelto.
 *
 * Nota de diseño: se implementa DroneOperable (no se hereda de
 * DroneAcuatico) para evitar acoplarse al constructor parametrizado
 * y al estado mutable de la clase base.
 */
public abstract class DroneDecorator implements DroneOperable {

    protected final DroneOperable envuelto;

    protected DroneDecorator(DroneOperable envuelto) {
        if (envuelto == null) {
            throw new IllegalArgumentException("El drone envuelto no puede ser nulo");
        }
        this.envuelto = envuelto;
    }

    // ─── Delegación pura ──────────────────────────────────────

    @Override public String id()             { return envuelto.id(); }
    @Override public String modelo()         { return envuelto.modelo(); }
    @Override public int bateria()           { return envuelto.bateria(); }
    @Override public EstadoDrone estado()    { return envuelto.estado(); }
    @Override public String zona()           { return envuelto.zona(); }
    @Override public boolean estaDisponible(){ return envuelto.estaDisponible(); }
    @Override public TipoDrone tipo()        { return envuelto.tipo(); }
    @Override public int capacidadCargaGramos() { return envuelto.capacidadCargaGramos(); }

    @Override public void setEstado(EstadoDrone nuevo) { envuelto.setEstado(nuevo); }
    @Override public void setBateria(int nueva)        { envuelto.setBateria(nueva); }
    @Override public void registrarEntregaExitosa()    { envuelto.registrarEntregaExitosa(); }
    @Override public void registrarEntregaFallida()    { envuelto.registrarEntregaFallida(); }

    @Override public int entregasExitosas() { return envuelto.entregasExitosas(); }
    @Override public int entregasFallidas() { return envuelto.entregasFallidas(); }
    @Override public int totalMisiones()    { return envuelto.totalMisiones(); }
    @Override public double ratioExito()    { return envuelto.ratioExito(); }
}