package com.eci.aquaport.aplicacion.observadores;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.logging.Logger;

public class ObservadorCentroControl implements ObservadorMision {

    private static final Logger LOGGER =
            Logger.getLogger(ObservadorCentroControl.class.getName());

    @Override
    public void notificarAsignacion(Mision mision, DroneAcuatico drone) {
        LOGGER.info(() -> "Centro de control: misión '" + mision.id()
                + "' asignada al drone '" + drone.id() + "'");
    }

    @Override
    public void notificarFalloAsignacion(Mision mision, String razon) {
        LOGGER.warning(() -> "Centro de control: fallo al asignar misión '"
                + mision.id() + "'. Razón: " + razon);
    }
}