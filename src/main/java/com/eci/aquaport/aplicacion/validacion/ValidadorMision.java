package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.dominio.Mision;

/**
 * Interfaz del patrón Chain of Responsibility.
 *
 * Cada validador concreto implementa una validación específica y puede
 * delegar al siguiente eslabón si su validación pasa. Si su validación
 * falla, retorna un ResultadoValidacion inválido y la cadena se detiene.
 */
public interface ValidadorMision {

    /**
     * Establece el siguiente eslabón de la cadena.
     *
     * @param siguiente siguiente validador
     * @return el validador pasado como parámetro, para encadenar fluentemente
     */
    ValidadorMision setNext(ValidadorMision siguiente);

    /**
     * Ejecuta la validación.
     *
     * @param mision misión a validar
     * @return ResultadoValidacion.valido() si pasa toda la cadena;
     *         ResultadoValidacion.invalido(razon) si algún eslabón falla
     */
    ResultadoValidacion validar(Mision mision);
}