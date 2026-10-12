package Piplup;

import java.util.logging.Logger;

public class NotificadorOperador {
    
    Logger logger = Logger.getLogger(getClass().getName());
    
    public void notificarExito(Mision mision, String operacion){
        logger.info("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' se ha realizado exitosamente.");
    }

    public void notificarError(Mision mision,String operacion, String Razon){
        logger.info("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' ha tenido un error. Razon: " + Razon);
    }
}
