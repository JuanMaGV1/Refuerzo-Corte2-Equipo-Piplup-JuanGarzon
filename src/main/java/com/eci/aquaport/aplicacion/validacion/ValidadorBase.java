package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.dominio.Mision;

/**
 * Clase base para los validadores concretos de la cadena.
 *
 * Implementa el manejo del "siguiente" eslabón y la lógica de
 * delegación. Las subclases solo implementan:
 *   - la validación concreta (metodo abstracto validarEspecifico)
 *   - el nombre del validador para la razón de fallo (nombre)
 */
public abstract class ValidadorBase implements ValidadorMision {

    private ValidadorMision siguiente;

    @Override
    public ValidadorMision setNext(ValidadorMision siguiente) {
        if (siguiente == null) {
            throw new IllegalArgumentException("El siguiente validador no puede ser nulo");
        }
        this.siguiente = siguiente;
        return siguiente;
    }

    @Override
    public ResultadoValidacion validar(Mision mision) {
        if (mision == null) {
            return ResultadoValidacion.invalido("La misión no puede ser nula");
        }

        ResultadoValidacion resultado = validarEspecifico(mision);
        if (!resultado.esValido()) {
            return resultado;   // corta la cadena
        }

        if (siguiente != null) {
            return siguiente.validar(mision);
        }

        return ResultadoValidacion.valido();
    }

    /**
     * Validación específica del eslabón.
     * Las subclases implementan aquí su regla.
     */
    protected abstract ResultadoValidacion validarEspecifico(Mision mision);
}