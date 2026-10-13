package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.TipoDrone;

/**
 * Valida que el tipo del drone sea compatible con la zona destino.
 *
 * Un drone SUPERFICIAL no puede operar en el Embalse de Investigación,
 * aunque tenga batería y capacidad suficiente.
 */
public class ValidadorZonaActiva extends ValidadorBase {

    @Override
    protected ResultadoValidacion validarEspecifico(Mision mision) {
        if (mision.drone() == null) {
            return ResultadoValidacion.invalido("La misión no tiene drone asignado");
        }
        String zonaDestino = mision.puntoLlegada();
        TipoDrone tipoDrone = mision.drone().tipo();

        if (!tipoDrone.esCompatibleCon(zonaDestino)) {
            return ResultadoValidacion.invalido(
                    "El drone " + mision.drone().id() + " (" + tipoDrone
                    + ") no es compatible con la zona destino '" + zonaDestino + "'");
        }
        return ResultadoValidacion.valido();
    }
}