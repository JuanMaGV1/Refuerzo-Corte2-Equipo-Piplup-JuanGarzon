package com.eci.aquaport.dominio;

/**
 * Interfaz que expone las operaciones comunes a todo drone acuático.
 *
 * Implementada por:
 *   - DroneAcuatico (base abstracta).
 *   - DroneDecorator (decoradores que envuelven a otro DroneOperable).
 *
 * Permite que un decorador envuelva a cualquier drone sin heredar de
 * DroneAcuatico, evitando problemas con el constructor parametrizado
 * y el estado mutable.
 */
public interface DroneOperable {

    String id();
    String modelo();
    int bateria();
    EstadoDrone estado();
    String zona();

    boolean estaDisponible();
    int capacidadCargaGramos();
    TipoDrone tipo();

    void setEstado(EstadoDrone nuevo);
    void setBateria(int nueva);
    void registrarEntregaExitosa();
    void registrarEntregaFallida();
    int entregasExitosas();
    int entregasFallidas();
    int totalMisiones();
    double ratioExito();
}