package com.eci.aquaport.aplicacion.observadores;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

public interface ObservadorMision {

    /**
     * Notificado cuando una misión es asignada exitosamente.
     * Por defecto no hace nada.
     */
    default void notificarAsignacion(Mision mision, DroneAcuatico drone) {
        // no-op por defecto
    }

    /**
     * Notificado cuando la asignación falla.
     * Por defecto no hace nada.
     */
    default void notificarFalloAsignacion(Mision mision, String razon) {
        // no-op por defecto
    }
}