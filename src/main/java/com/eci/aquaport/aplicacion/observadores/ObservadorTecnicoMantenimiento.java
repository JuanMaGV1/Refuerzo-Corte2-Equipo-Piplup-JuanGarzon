package com.eci.aquaport.aplicacion.observadores;

import com.eci.aquaport.dominio.Mision;

import java.util.logging.Logger;

public class ObservadorTecnicoMantenimiento implements ObservadorMision {

    private static final Logger LOGGER =
            Logger.getLogger(ObservadorTecnicoMantenimiento.class.getName());

    @Override
    public void notificarFalloAsignacion(Mision mision, String razon) {
        LOGGER.warning(() -> "Técnico de mantenimiento: revisar flota tras "
                + "fallo en misión '" + mision.id() + "'. Razón: " + razon);
    }
}