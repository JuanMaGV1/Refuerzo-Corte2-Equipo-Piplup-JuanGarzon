package Piplup;

public class ValidadorMision {
    public boolean isMisionValida(Mision mision){
        return (mision != null && isPuntosValidos(mision) && isDroneValido(mision) && isEstadoMisionValido(mision));
    }

    private boolean isPuntosValidos(Mision mision){
        return (mision.getPuntoLlegada() != null && mision.getPuntoPartida() != null && !mision.getPuntoLlegada().isBlank() && !mision.getPuntoPartida().isBlank());
    }

    private boolean isDroneValido(Mision mision){
        return (mision.getDrone() != null && mision.getDrone().disponible() && mision.getDrone().bateria() > 35);
    }

    private boolean isEstadoMisionValido(Mision mision){
        return (mision.getEstadoMision() == EstadoMision.PENDIENTE);
    }
}
