package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TramoRutaTest {

    private final Waypoint origen = Waypoint.origen("Red de Canales");
    private final Waypoint destino = new Waypoint("Laguna de Reserva", 1);

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Crea un tramo válido")
        void creaValido() {
            TramoRuta t = new TramoRuta(origen, destino, 0);
            assertAll(
                    () -> assertEquals(origen, t.origen()),
                    () -> assertEquals(destino, t.destino()),
                    () -> assertEquals(0, t.orden()),
                    () -> assertEquals("Red de Canales", t.zonaOrigen()),
                    () -> assertEquals("Laguna de Reserva", t.zonaDestino())
            );
        }

        @Test
        @DisplayName("Origen nulo lanza IllegalArgumentException")
        void origenNulo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new TramoRuta(null, destino, 0));
        }

        @Test
        @DisplayName("Destino nulo lanza IllegalArgumentException")
        void destinoNulo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new TramoRuta(origen, null, 0));
        }

        @Test
        @DisplayName("Orden negativo lanza IllegalArgumentException")
        void ordenNegativo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new TramoRuta(origen, destino, -1));
        }

        @Test
        @DisplayName("Origen igual a destino lanza IllegalArgumentException")
        void mismoWaypoint_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new TramoRuta(origen, origen, 0));
        }
    }
}