package com.eci.aquaport.aplicacion.fabricas;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;

/**
 * Fábrica de drones acuáticos.
 *
 * No contiene ningún switch sobre tipos concretos: delega la creación
 * al propio TipoDrone, que es polimórfico. Añadir un nuevo tipo es
 * agregar una constante al enum, sin tocar esta clase (OCP).
 */
public class FabricaDrones {

    public DroneAcuatico crearDrone(String id, String modelo, int bateria,
                                     EstadoDrone estado, String zona,
                                     TipoDrone tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de drone no puede ser nulo");
        }
        return tipo.crear(id, modelo, bateria, estado, zona);
    }
}