package com.eci.aquaport.dominio;

/**
 * Drone acuático de AquaPort.
 *
 * Clase base abstracta para los 3 tipos de drone (Superficial,
 * Semisumergido, Buceador). Contiene el estado mutable del drone
 * (batería, estado, contadores de entregas) y los métodos derivados
 * comunes a todas las subclases.
 *
 * En Empoleon se añaden contadores de entregas exitosas y fallidas
 * para permitir consultas de historial y ratio de éxito.
 */
public abstract class DroneAcuatico implements DroneOperable {

    private final String id;
    private final String modelo;
    private final String zona;
    private int bateria;
    private EstadoDrone estado;
    private int entregasExitosas;
    private int entregasFallidas;

    /**
     * Constructor base (sin historial). Los contadores inician en 0.
     */
    protected DroneAcuatico(String id, String modelo, int bateria,
                             EstadoDrone estado, String zona) {
        this(id, modelo, bateria, estado, zona, 0, 0);
    }

    /**
     * Constructor extendido con historial de entregas.
     */
    protected DroneAcuatico(String id, String modelo, int bateria,
                             EstadoDrone estado, String zona,
                             int entregasExitosas, int entregasFallidas) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del drone no puede ser nulo ni vacío");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo no puede ser nulo ni vacío");
        }
        if (bateria < 0 || bateria > 100) {
            throw new IllegalArgumentException("Batería fuera de rango [0-100]: " + bateria);
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        if (zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("La zona no puede ser nula ni vacía");
        }
        if (entregasExitosas < 0 || entregasFallidas < 0) {
            throw new IllegalArgumentException(
                    "Los contadores de entregas no pueden ser negativos");
        }
        this.id = id;
        this.modelo = modelo;
        this.bateria = bateria;
        this.estado = estado;
        this.zona = zona;
        this.entregasExitosas = entregasExitosas;
        this.entregasFallidas = entregasFallidas;
    }

    // ─── Getters ─────────────────────────────────────────────

    public String id() { return id; }
    public String modelo() { return modelo; }
    public int bateria() { return bateria; }
    public EstadoDrone estado() { return estado; }
    public String zona() { return zona; }
    public int entregasExitosas() { return entregasExitosas; }
    public int entregasFallidas() { return entregasFallidas; }

    // ─── Setters controlados ─────────────────────────────────

    public void setEstado(EstadoDrone nuevo) {
        if (nuevo == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = nuevo;
    }

    public void setBateria(int nueva) {
        if (nueva < 0 || nueva > 100) {
            throw new IllegalArgumentException("Batería fuera de rango [0-100]: " + nueva);
        }
        this.bateria = nueva;
    }

    // ─── Registro de entregas ────────────────────────────────

    public void registrarEntregaExitosa() {
        this.entregasExitosas++;
    }

    public void registrarEntregaFallida() {
        this.entregasFallidas++;
    }

    // ─── Métodos derivados ───────────────────────────────────

    public boolean estaDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    public int totalMisiones() {
        return entregasExitosas + entregasFallidas;
    }

    /**
     * Ratio de éxito: entregas exitosas / total de misiones.
     * Retorna 0.0 si el drone no ha ejecutado ninguna misión.
     */
    public double ratioExito() {
        int total = totalMisiones();
        return total == 0 ? 0.0 : (double) entregasExitosas / total;
    }

    // ─── Contratos de subclases ──────────────────────────────

    public abstract TipoDrone tipo();
    public abstract int capacidadCargaGramos();

    @Override
    public String toString() {
        return String.format("%s[%s, %s, %d%%, %s, %d/%d]",
                tipo(), id, modelo, bateria, estado,
                entregasExitosas, totalMisiones());
    }
}