package Piplup;

public class ValidadorMision {
    public boolean isMisionValida(Mision mision){
        return !(mision == null || !isPuntosValidos(mision) || !isDroneValido(mision) || !isEstadoMisionValido(mision)) ? true : false;
    }

    private boolean isPuntosValidos(Mision mision){
        return !(mision.getPuntoLlegada() == null || mision.getPuntoPartida() == null || mision.getPuntoLlegada().isBlank() || mision.getPuntoPartida().isBlank())? true : false;
    }

    private boolean isDroneValido(Mision mision){
        return !(mision.getDrone() == null || mision.getDrone().disponible() == false || mision.getDrone().bateria() < 35) ? true : false;
    }

    private boolean isEstadoMisionValido(Mision mision){
        return !(mision.getEstadoMision() != EstadoMision.PENDIENTE) ? true : false;
    }
}
