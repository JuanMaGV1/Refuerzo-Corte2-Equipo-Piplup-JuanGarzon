package Piplup;

import java.util.logging.Logger;

public class NotificadorOperador {
    
    private static final Logger LOGGER = Logger.getLogger(NotificadorOperador.class.getName());
    
    public void notificarExito(Mision mision, String operacion){
        LOGGER.info("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' se ha realizado exitosamente.");
    }

    public void notificarError(Mision mision,String operacion, String Razon){
        LOGGER.warning("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' ha tenido un error. Razon: " + Razon);
    }
}
