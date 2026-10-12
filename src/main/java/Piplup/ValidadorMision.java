package Piplup;

import java.util.Set;

public class ValidadorMision {

    private static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte",
            "Canal Central",
            "Laguna Sur",
            "Punto Ribereño Este",
            "Laboratorio Hídrico"
    );

    private static final int BATERIA_MINIMA = 35;

    public boolean isMisionValida(Mision mision) {
        return mision != null
                && isPuntosValidos(mision)
                && isDroneValido(mision)
                && isEstadoMisionValido(mision);
    }

    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        return drone != null && drone.bateria() >= BATERIA_MINIMA;
    }

    public boolean estaDisponible(DroneAcuatico drone) {
        return drone != null && drone.disponible();
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo");
        }
        if (puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada no puede estar vacío");
        }
    }

    public boolean esZonaValida(String zona) {
        return zona != null && ZONAS_VALIDAS.contains(zona);
    }

    // ═══════════════════════════════════════════════════════
    // Validaciones privadas de la misión completa
    // ═══════════════════════════════════════════════════════

    private boolean isPuntosValidos(Mision mision) {
        return mision.getPuntoLlegada() != null
                && mision.getPuntoPartida() != null
                && !mision.getPuntoLlegada().isBlank()
                && !mision.getPuntoPartida().isBlank();
    }

    private boolean isDroneValido(Mision mision) {
        return mision.getDrone() != null
                && mision.getDrone().disponible()
                && mision.getDrone().bateria() >= BATERIA_MINIMA;
    }

    private boolean isEstadoMisionValido(Mision mision) {
        return mision.getEstadoMision() == EstadoMision.PENDIENTE;
    }
}