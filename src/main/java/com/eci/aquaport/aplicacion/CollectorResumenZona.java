package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector personalizado que agrega una lista de drones de la misma
 * zona en un único {@link AnalizadorFlotaEmpoleon.ResumenZona}.
 *
 * Realiza el cálculo en una sola pasada sobre los elementos, en lugar
 * de recorrerlos 3 veces para calcular cantidad, suma de baterías y
 * cantidad de disponibles por separado.
 *
 * El {@code combiner} está implementado para dar soporte a streams
 * paralelos futuros (por ejemplo, análisis batch nocturno sobre
 * millones de eventos). En el uso actual, todas las consultas son
 * secuenciales porque la flota es de 60 drones y el overhead del
 * paralelismo supera la ganancia. Ver docs/streams-paralelo-empoleon.md.
 */
public class CollectorResumenZona implements Collector<
        DroneAcuatico,
        CollectorResumenZona.Acumulador,
        AnalizadorFlotaEmpoleon.ResumenZona> {

    public static final CollectorResumenZona INSTANCE = new CollectorResumenZona();

    private CollectorResumenZona() {
        // Singleton vía INSTANCE
    }

    @Override
    public Supplier<Acumulador> supplier() {
        return Acumulador::new;
    }

    @Override
    public BiConsumer<Acumulador, DroneAcuatico> accumulator() {
        return Acumulador::agregar;
    }

    @Override
    public BinaryOperator<Acumulador> combiner() {
        return Acumulador::combinar;
    }

    @Override
    public Function<Acumulador, AnalizadorFlotaEmpoleon.ResumenZona> finisher() {
        return Acumulador::toResumen;
    }

    @Override
    public Set<Characteristics> characteristics() {
        // No declaramos IDENTITY_FINISH porque el finisher transforma
        // el Acumulador en un ResumenZona (tipos distintos).
        // No declaramos CONCURRENT porque el Acumulador no es thread-safe
        // y el combiner es necesario para paralelismo.
        return Collections.unmodifiableSet(EnumSet.noneOf(Characteristics.class));
    }

    /**
     * Acumulador mutable interno. Acumula en una sola pasada:
     * cantidad de drones, suma de baterías y cantidad de disponibles.
     */
    static final class Acumulador {
        private long cantidad = 0;
        private long sumaBateria = 0;
        private long disponibles = 0;

        void agregar(DroneAcuatico drone) {
            cantidad++;
            sumaBateria += drone.bateria();
            if (drone.estaDisponible()) {
                disponibles++;
            }
        }

        Acumulador combinar(Acumulador otro) {
            this.cantidad += otro.cantidad;
            this.sumaBateria += otro.sumaBateria;
            this.disponibles += otro.disponibles;
            return this;
        }

        AnalizadorFlotaEmpoleon.ResumenZona toResumen() {
            double promedio = cantidad == 0 ? 0.0 : (double) sumaBateria / cantidad;
            return new AnalizadorFlotaEmpoleon.ResumenZona(cantidad, promedio, disponibles);
        }
    }
}