package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaypointTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Crea un waypoint válido")
        void creaValido() {
            Waypoint w = new Waypoint("Embalse de Investigación", 0);
            assertAll(
                    () -> assertEquals("Embalse de Investigación", w.zona()),
                    () -> assertEquals(0, w.orden())
            );
        }

        @Test
        @DisplayName("Zona nula lanza IllegalArgumentException")
        void zonaNula_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Waypoint(null, 0));
        }

        @Test
        @DisplayName("Zona vacía lanza IllegalArgumentException")
        void zonaVacia_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Waypoint("   ", 0));
        }

        @Test
        @DisplayName("Orden negativo lanza IllegalArgumentException")
        void ordenNegativo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Waypoint("Zona", -1));
        }
    }

    @Nested
    @DisplayName("Constructores de conveniencia")
    class Conveniencia {

        @Test
        @DisplayName("origen() crea waypoint con orden 0")
        void origen() {
            Waypoint w = Waypoint.origen("Red de Canales");
            assertEquals(0, w.orden());
            assertEquals("Red de Canales", w.zona());
        }

        @Test
        @DisplayName("intermedio() crea waypoint con orden indicado")
        void intermedio() {
            Waypoint w = Waypoint.intermedio("Laguna de Reserva", 2);
            assertEquals(2, w.orden());
            assertEquals("Laguna de Reserva", w.zona());
        }
    }
}