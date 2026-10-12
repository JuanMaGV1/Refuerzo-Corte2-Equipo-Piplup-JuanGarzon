package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consultas sobre misiones: separación por prioridad, agregación de zonas
 * involucradas en misiones activas.
 *
 * Se separa de {@link AnalizadorFlota} para mantener SRP: esta clase
 * depende del modelo Mision, no de la flota de drones.
 */
public class AnalizadorMisiones {

    /**
     * Consulta 4: Separa las misiones CRÍTICAS del resto.
     *
     * partitioningBy SIEMPRE produce dos claves (true/false), incluso si
     * una lista está vacía.
     */
    public Map<Boolean, List<Mision>> separarMisionesCriticas(
            List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.partitioningBy(
                        m -> m.prioridad() == Prioridad.CRITICA));
    }

    /**
     * Bonus: Zonas únicas involucradas en misiones EN_TRANSITO.
     * Usa flatMap para aplanar punto de partida y punto de llegada.
     */
    public Set<String> zonasDeMisionesActivas(List<Mision> misiones) {
        return misiones.stream()
                .filter(m -> m.estadoMision() == EstadoMision.EN_TRANSITO)
                .flatMap(m -> Stream.of(m.puntoPartida(), m.puntoLlegada()))
                .collect(Collectors.toSet());
    }

        /**
     * Filtra misiones con estado PENDIENTE y la prioridad indicada.
     *
     * Responde a la pregunta del operador: "¿qué CRÍTICAS están esperando
     * asignación de drone?".
     *
     * @param misiones  lista completa de misiones del sistema
     * @param prioridad prioridad a filtrar (CRITICA, ALTA, NORMAL, BAJA)
     * @return lista inmutable con las misiones que cumplen ambos criterios
     */
    public List<Mision> misionesPendientesConPrioridad(
            List<Mision> misiones, Prioridad prioridad) {
        return misiones.stream()
                .filter(m -> m.estadoMision() == EstadoMision.PENDIENTE)
                .filter(m -> m.prioridad() == prioridad)
                .toList();
    }
}