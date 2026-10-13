package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.aplicacion.validacion.condiciones.ProveedorCondicionesHidricas;

/**
 * Fábrica que arma la cadena de validación en el orden correcto.
 *
 * Orden:
 *   1. Batería (rápida, propensa a fallar).
 *   2. Capacidad de carga.
 *   3. Zona activa (compatibilidad tipo ↔ zona).
 *   4. Condiciones hídricas (depende de fuente externa, la más lenta).
 *
 * El orden va de más rápido/barato a más lento/costoso.
 */
public class CadenaValidacionFactory {

    private CadenaValidacionFactory() {
        // clase de utilidad
    }

    public static ValidadorMision crear(ProveedorCondicionesHidricas proveedor) {
        ValidadorMision cadena = new ValidadorBateria();
        cadena.setNext(new ValidadorCapacidadCarga())
              .setNext(new ValidadorZonaActiva())
              .setNext(new ValidadorCondicionesHidricas(proveedor));
        return cadena;
    }
}