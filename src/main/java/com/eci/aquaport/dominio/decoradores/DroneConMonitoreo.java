package com.eci.aquaport.dominio.decoradores;

import com.eci.aquaport.dominio.DroneOperable;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorador que añade registro de telemetría a un drone.
 *
 * Intercepta los cambios de estado y batería del drone envuelto y los
 * registra en un histórico interno. No modifica el comportamiento del
 * drone: solo lo observa.
 *
 * Es el decorador base del sistema Empoleon. Los datos de telemetría
 * se usarían para auditoría o análisis post-misión.
 */
public class DroneConMonitoreo extends DroneDecorator {

    private final List<String> telemetria = new ArrayList<>();

    public DroneConMonitoreo(DroneOperable envuelto) {
        super(envuelto);
    }

    @Override
    public void setEstado(com.eci.aquaport.dominio.EstadoDrone nuevo) {
        telemetria.add(String.format("estado: %s → %s",
                envuelto.estado(), nuevo));
        envuelto.setEstado(nuevo);
    }

    @Override
    public void setBateria(int nueva) {
        telemetria.add(String.format("batería: %d%% → %d%%",
                envuelto.bateria(), nueva));
        envuelto.setBateria(nueva);
    }

    @Override
    public void registrarEntregaExitosa() {
        telemetria.add("entrega exitosa registrada");
        envuelto.registrarEntregaExitosa();
    }

    @Override
    public void registrarEntregaFallida() {
        telemetria.add("entrega fallida registrada");
        envuelto.registrarEntregaFallida();
    }

    /**
     * Devuelve una copia inmutable del histórico de telemetría.
     */
    public List<String> telemetria() {
        return List.copyOf(telemetria);
    }

    public int cantidadEventos() {
        return telemetria.size();
    }
}