package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.Mision;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificadorOperador {

    private static final Logger LOGGER = Logger.getLogger(NotificadorOperador.class.getName());

    public void notificarExito(Mision mision, String operacion) {
        LOGGER.log(Level.INFO,
                "La operación ''{0}'' relacionada con la misión ''{1}'' se ha realizado exitosamente.",
                new Object[]{operacion, mision.id()});
    }

    public void notificarError(Mision mision, String operacion, String razon) {
        LOGGER.log(Level.WARNING,
                "La operación ''{0}'' relacionada con la misión ''{1}'' ha tenido un error. Razón: {2}",
                new Object[]{operacion, mision.id(), razon});
    }
}