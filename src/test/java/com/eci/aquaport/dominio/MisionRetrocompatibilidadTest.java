package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que la extensión con ruta no rompe la construcción de
 * misiones de un solo tramo (comportamiento de Prinplup).
 */
class MisionRetrocompatibilidadTest {

    @Test
    @DisplayName("El Builder sin .ruta() construye misión de un solo tramo")
    void sinRuta_enElBuilder() {
        DroneAcuatico drone = new DroneBuceador(
                "AR-01", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Zona");

        Mision m = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("A")
                .puntoLlegada("B")
                .tipoCarga(TipoCarga.SENSOR)
                .cargaGramos(100)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();

        assertAll(
                () -> assertNull(m.ruta()),
                () -> assertFalse(m.esMultiEtapa()),
                () -> assertEquals(1, m.numeroTramos()),
                () -> assertEquals("M-001", m.id()),
                () -> assertEquals("A", m.puntoPartida()),
                () -> assertEquals("B", m.puntoLlegada())
        );
    }
}