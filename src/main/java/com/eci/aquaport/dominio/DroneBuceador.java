package com.eci.aquaport.dominio;

public class DroneBuceador extends DroneAcuatico {

    public static final int CAPACIDAD_MAXIMA_GRAMOS = 300;

    public DroneBuceador(String id, String modelo, int bateria,
                          EstadoDrone estado, String zona) {
        super(id, modelo, bateria, estado, zona);
    }

    public DroneBuceador(String id, String modelo, int bateria,
                          EstadoDrone estado, String zona,
                          int entregasExitosas, int entregasFallidas) {
        super(id, modelo, bateria, estado, zona, entregasExitosas, entregasFallidas);
    }

    @Override
    public TipoDrone tipo() {
        return TipoDrone.BUCEADOR;
    }

    @Override
    public int capacidadCargaGramos() {
        return CAPACIDAD_MAXIMA_GRAMOS;
    }
}