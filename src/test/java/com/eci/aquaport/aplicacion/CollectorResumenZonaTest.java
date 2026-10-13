package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.EstadoDrone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CollectorResumenZonaTest {

    @Test
    @DisplayName("Calcula cantidad, promedio y disponibles en una sola pasada")
    void calculaResumen() {
        List<DroneAcuatico> drones = List.of(
                new DroneBuceador("AR-01", "M", 80, EstadoDrone.DISPONIBLE, "ZonaX"),
                new DroneBuceador("AR-02", "M", 60, EstadoDrone.EN_MISION,  "ZonaX"),
                new DroneBuceador("AR-03", "M", 100, EstadoDrone.DISPONIBLE, "ZonaX")
        );

        AnalizadorFlotaEmpoleon.ResumenZona resumen = drones.stream()
                .collect(CollectorResumenZona.INSTANCE);

        assertAll(
                () -> assertEquals(3, resumen.cantidadDrones()),
                () -> assertEquals((80 + 60 + 100) / 3.0, resumen.promedioBateria(), 0.0001),
                () -> assertEquals(2, resumen.cantidadDisponibles())
        );
    }

    @Test
    @DisplayName("Funciona con groupingBy")
    void funcionaConGroupingBy() {
        List<DroneAcuatico> drones = List.of(
                new DroneBuceador("AR-01", "M", 80, EstadoDrone.DISPONIBLE, "ZonaX"),
                new DroneBuceador("AR-02", "M", 60, EstadoDrone.DISPONIBLE, "ZonaY"),
                new DroneBuceador("AR-03", "M", 100, EstadoDrone.DISPONIBLE, "ZonaX")
        );

        var resultado = drones.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::zona,
                        CollectorResumenZona.INSTANCE));

        assertAll(
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(2, resultado.get("ZonaX").cantidadDrones()),
                () -> assertEquals(1, resultado.get("ZonaY").cantidadDrones()),
                () -> assertEquals(90.0, resultado.get("ZonaX").promedioBateria(), 0.0001),
                () -> assertEquals(60.0, resultado.get("ZonaY").promedioBateria(), 0.0001)
        );
    }

    @Test
    @DisplayName("Lista vacía no se agrega en el map")
    void listaVacia() {
        var resultado = List.<DroneAcuatico>of().stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::zona,
                        CollectorResumenZona.INSTANCE));
        assertTrue(resultado.isEmpty());
    }
}