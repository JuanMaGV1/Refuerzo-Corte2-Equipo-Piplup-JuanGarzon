package com.eci.aquaport.dominio.decoradores;

import com.eci.aquaport.dominio.DroneOperable;

/**
 * Decorador que marca un drone como cifrado.
 *
 * En AquaPort, los drones con cifrado transmiten sus datos de forma
 * cifrada. Este decorador expone esa propiedad sin modificar el
 * comportamiento base del drone.
 *
 * Se puede apilar con otros decoradores:
 *   new DroneConCifrado(new DroneConMonitoreo(droneBase))
 */
public class DroneConCifrado extends DroneDecorator {

    private static final String ALGORITMO = "AES-256";

    public DroneConCifrado(DroneOperable envuelto) {
        super(envuelto);
    }

    /** Indica que este drone transmite datos cifrados. */
    public boolean estaCifrado() {
        return true;
    }

    /** Algoritmo de cifrado utilizado. */
    public String algoritmoCifrado() {
        return ALGORITMO;
    }
}