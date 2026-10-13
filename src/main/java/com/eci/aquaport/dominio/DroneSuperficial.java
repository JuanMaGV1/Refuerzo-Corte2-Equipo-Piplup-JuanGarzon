package com.eci.aquaport.dominio;

public class DroneSuperficial extends DroneAcuatico {

    public static final int CAPACIDAD_MAXIMA_GRAMOS = 500;

    public DroneSuperficial(String id, String modelo, int bateria,
                             EstadoDrone estado, String zona) {
        super(id, modelo, bateria, estado, zona);
    }

    public DroneSuperficial(String id, String modelo, int bateria,
                             EstadoDrone estado, String zona,
                             int entregasExitosas, int entregasFallidas) {
        super(id, modelo, bateria, estado, zona, entregasExitosas, entregasFallidas);
    }

    @Override
    public TipoDrone tipo() {
        return TipoDrone.SUPERFICIAL;
    }

    @Override
    public int capacidadCargaGramos() {
        return CAPACIDAD_MAXIMA_GRAMOS;
    }
}