package com.eci.aquaport.aplicacion.validacion.condiciones;

/**
 * Puerto (interfaz) para obtener las condiciones hídricas de una zona.
 *
 * El dominio y la aplicación dependen de esta abstracción. La
 * implementación concreta (Adapter hacia la API externa, o un stub para
 * tests) se inyecta por constructor.
 *
 * Esto prepara el terreno para el patrón Adapter del reto 03 Empoleon.
 */
public interface ProveedorCondicionesHidricas {

    /**
     * Consulta las condiciones hídricas de una zona.
     *
     * @param zona nombre de la zona (ver TipoDrone.Zonas)
     * @return condiciones hídricas actuales de la zona
     * @throws IllegalStateException si la API no responde
     */
    CondicionesHidricas consultar(String zona);
}