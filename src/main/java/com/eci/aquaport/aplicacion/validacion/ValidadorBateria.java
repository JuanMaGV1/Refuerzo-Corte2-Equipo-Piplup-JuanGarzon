package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.dominio.Mision;

/**
 * Valida que el drone de la misión tenga batería >= 35%.
 *
 * Este es el primer eslabón de la cadena porque es la validación más
 * rápida y la más propensa a fallar.
 */
public class ValidadorBateria extends ValidadorBase {

    private static final int BATERIA_MINIMA = 35;

    @Override
    protected ResultadoValidacion validarEspecifico(Mision mision) {
        if (mision.drone() == null) {
            return ResultadoValidacion.invalido("La misión no tiene drone asignado");
        }
        if (mision.drone().bateria() < BATERIA_MINIMA) {
            return ResultadoValidacion.invalido(
                    "El drone " + mision.drone().id()
                    + " tiene batería insuficiente (" + mision.drone().bateria()
                    + "%). Mínimo requerido: " + BATERIA_MINIMA + "%");
        }
        return ResultadoValidacion.valido();
    }
}