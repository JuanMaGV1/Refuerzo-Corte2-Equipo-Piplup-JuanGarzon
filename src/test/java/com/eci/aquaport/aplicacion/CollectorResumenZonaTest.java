package com.eci.aquaport.aplicacion;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.dominio.DroneAcuatico;
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
                TestFixtures.buceador("AR-01", 80, EstadoDrone.DISPONIBLE, "ZonaX"),
                TestFixtures.buceador("AR-02", 60, EstadoDrone.EN_MISION,  "ZonaX"),
                TestFixtures.buceador("AR-03", 100, EstadoDrone.DISPONIBLE, "ZonaX")
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
                TestFixtures.buceador("AR-01", 80, EstadoDrone.DISPONIBLE, "ZonaX"),
                TestFixtures.buceador("AR-02", 60, EstadoDrone.DISPONIBLE, "ZonaY"),
                TestFixtures.buceador("AR-03", 100, EstadoDrone.DISPONIBLE, "ZonaX")
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
    @DisplayName("El combiner acumula dos acumuladores correctamente")
    void combinaAcumuladoresCorrectamente() {
        // Test directo del combiner sin usar parallelStream
        DroneAcuatico d1 = TestFixtures.buceador("AR-01", 80, EstadoDrone.DISPONIBLE, "ZonaX");
        DroneAcuatico d2 = TestFixtures.buceador("AR-02", 60, EstadoDrone.EN_MISION, "ZonaX");

        var acumulador1 = new CollectorResumenZona.Acumulador();
        acumulador1.agregar(d1);

        var acumulador2 = new CollectorResumenZona.Acumulador();
        acumulador2.agregar(d2);

        var combinado = acumulador1.combinar(acumulador2);
        var resumen = combinado.toResumen();

        assertAll(
                () -> assertEquals(2, resumen.cantidadDrones()),
                () -> assertEquals(70.0, resumen.promedioBateria(), 0.0001),
                () -> assertEquals(1, resumen.cantidadDisponibles())
        );
    }

    @Test
    @DisplayName("Lista vacía no se agrega en el map")
    void listaVacia() {
        var resultado = TestFixtures.flotaVacia().stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::zona,
                        CollectorResumenZona.INSTANCE));
        assertTrue(resultado.isEmpty());
    }
}