package src.main.java.Piplup;

public class NotificadorOperador {
    public void notificarExito(Mision mision, String operacion){System.out.println("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' se ha realizado exitosamente.");}

    public void notificarError(Mision mision,String operacion, String Razon){System.out.println("La operación '" + operacion +"'" + "relacionada con la misión '" + mision.getId() + "' ha tenido un error. Razon: " + Razon);}
}
