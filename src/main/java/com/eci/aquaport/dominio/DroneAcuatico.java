package com.eci.aquaport.dominio;

public abstract class DroneAcuatico {

    private final String id;
    private final String modelo;
    private final String zona;
    private int bateria;
    private EstadoDrone estado;

    protected DroneAcuatico(String id, String modelo, int bateria,
                             EstadoDrone estado, String zona) {
        this.id = id;
        this.modelo = modelo;
        this.bateria = bateria;
        this.estado = estado;
        this.zona = zona;
    }

    // ─── Getters ─────────────────────────────────────────────
    public String id()       { return id; }
    public String modelo()   { return modelo; }
    public int bateria()     { return bateria; }
    public EstadoDrone estado() { return estado; }
    public String zona()     { return zona; }

    // ─── Setters controlados ─────────────────────────────────
    public void setEstado(EstadoDrone nuevo) {
        this.estado = nuevo;
    }

    public void setBateria(int nueva) {
        if (nueva < 0 || nueva > 100) {
            throw new IllegalArgumentException("Batería fuera de rango: " + nueva);
        }
        this.bateria = nueva;
    }

    // ─── Métodos derivados ───────────────────────────────────
    public boolean estaDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    /** @return tipo del drone. Cada subclase lo define. */
    public abstract TipoDrone tipo();

    /** @return capacidad de carga en gramos. Cada subclase lo define. */
    public abstract int capacidadCargaGramos();

    @Override
    public String toString() {
        return String.format("%s[%s, %s, %d%%, %s]",
                tipo(), id, modelo, bateria, estado);
    }
}