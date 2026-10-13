package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.dominio.Mision;

/**
 * Valida que el drone de la misión tenga capacidad de carga suficiente
 * para el peso de la carga.
 */
public class ValidadorCapacidadCarga extends ValidadorBase {

    @Override
    protected ResultadoValidacion validarEspecifico(Mision mision) {
        if (mision.drone() == null) {
            return ResultadoValidacion.invalido("La misión no tiene drone asignado");
        }
        int capacidad = mision.drone().capacidadCargaGramos();
        int carga = mision.cargaGramos();

        if (carga > capacidad) {
            return ResultadoValidacion.invalido(
                    "Carga de " + carga + "g supera la capacidad del drone "
                    + mision.drone().id() + " (" + mision.drone().tipo()
                    + ", máximo " + capacidad + "g)");
        }
        return ResultadoValidacion.valido();
    }
}