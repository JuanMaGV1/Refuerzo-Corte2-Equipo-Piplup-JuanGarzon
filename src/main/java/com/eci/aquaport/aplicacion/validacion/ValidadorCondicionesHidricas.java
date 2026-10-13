package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.aplicacion.validacion.condiciones.CondicionesHidricas;
import com.eci.aquaport.aplicacion.validacion.condiciones.ProveedorCondicionesHidricas;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.TipoDrone;

/**
 * Valida que las condiciones hídricas actuales de la zona destino
 * permitan operar al tipo del drone.
 *
 * Regla:
 *   - Agua AGITADA → solo SEMISUMERGIDO o BUCEADOR.
 *   - Agua CALMA o MODERADA → cualquier tipo.
 *
 * Este validador depende de un {@link ProveedorCondicionesHidricas}
 * inyectado por constructor. Es el último eslabón de la cadena porque
 * depende de una fuente externa (más lenta).
 */
public class ValidadorCondicionesHidricas extends ValidadorBase {

    private final ProveedorCondicionesHidricas proveedor;

    public ValidadorCondicionesHidricas(ProveedorCondicionesHidricas proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor no puede ser nulo");
        }
        this.proveedor = proveedor;
    }

    @Override
    protected ResultadoValidacion validarEspecifico(Mision mision) {
        if (mision.drone() == null) {
            return ResultadoValidacion.invalido("La misión no tiene drone asignado");
        }

        CondicionesHidricas condiciones;
        try {
            condiciones = proveedor.consultar(mision.puntoLlegada());
        } catch (RuntimeException e) {
            return ResultadoValidacion.invalido(
                    "No se pudieron obtener las condiciones hídricas de '"
                    + mision.puntoLlegada() + "': " + e.getMessage());
        }

        if (condiciones.agitacion() == CondicionesHidricas.NivelAgitacion.AGITADA
                && mision.drone().tipo() == TipoDrone.SUPERFICIAL) {
            return ResultadoValidacion.invalido(
                    "El drone " + mision.drone().id()
                    + " es SUPERFICIAL y la zona '" + mision.puntoLlegada()
                    + "' tiene agua AGITADA");
        }
        return ResultadoValidacion.valido();
    }
}