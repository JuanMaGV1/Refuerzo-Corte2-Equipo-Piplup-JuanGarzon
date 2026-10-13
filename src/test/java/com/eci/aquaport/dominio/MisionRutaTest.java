package com.eci.aquaport.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MisionRutaTest {

    private DroneAcuatico drone;

    @BeforeEach
    void setUp() {
        drone = new DroneBuceador("AR-01", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
    }

    private Mision.Builder builderBase() {
        return new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse de Investigación")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL);
    }

    @Nested
    @DisplayName("Misión sin ruta (un solo tramo)")
    class SinRuta {

        @Test
        @DisplayName("Se construye con ruta = null")
        void rutaNula() {
            Mision m = builderBase().build();
            assertNull(m.ruta());
        }

        @Test
        @DisplayName("esMultiEtapa() retorna false")
        void noEsMultiEtapa() {
            Mision m = builderBase().build();
            assertFalse(m.esMultiEtapa());
        }

        @Test
        @DisplayName("numeroTramos() retorna 1")
        void unTramo() {
            Mision m = builderBase().build();
            assertEquals(1, m.numeroTramos());
        }
    }

    @Nested
    @DisplayName("Misión con ruta multi-etapa")
    class ConRuta {

        private Ruta ruta3Waypoints() {
            return new Ruta(List.of(
                    Waypoint.origen("Embalse de Investigación"),
                    new Waypoint("Laguna de Reserva", 1),
                    new Waypoint("Laboratorio Hídrico Central", 2)));
        }

        @Test
        @DisplayName("Se construye con la ruta presente")
        void rutaPresente() {
            Ruta ruta = ruta3Waypoints();
            Mision m = builderBase().ruta(ruta).build();

            assertAll(
                    () -> assertNotNull(m.ruta()),
                    () -> assertEquals(ruta, m.ruta()),
                    () -> assertEquals(3, m.ruta().waypoints().size())
            );
        }

        @Test
        @DisplayName("esMultiEtapa() retorna true con 2+ tramos")
        void esMultiEtapa() {
            Mision m = builderBase().ruta(ruta3Waypoints()).build();
            assertTrue(m.esMultiEtapa());
        }

        @Test
        @DisplayName("numeroTramos() retorna el número de tramos de la ruta")
        void numeroTramos() {
            Mision m = builderBase().ruta(ruta3Waypoints()).build();
            assertEquals(2, m.numeroTramos());
        }

        @Test
        @DisplayName("Ruta con solo 2 waypoints no es multi-etapa")
        void rutaMinima_noEsMultiEtapa() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("A"),
                    new Waypoint("B", 1)));
            Mision m = builderBase().ruta(ruta).build();

            assertAll(
                    () -> assertFalse(m.esMultiEtapa(),
                            "2 waypoints = 1 tramo, no es multi-etapa"),
                    () -> assertEquals(1, m.numeroTramos())
            );
        }
    }

    @Nested
    @DisplayName("Validaciones del record")
    class Validaciones {

        @Test
        @DisplayName("El constructor directo valida los campos obligatorios")
        void constructorDirecto() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Mision(null, drone, "A", "B",
                            TipoCarga.SENSOR, 100,
                            EstadoMision.PENDIENTE, Prioridad.NORMAL, null));
        }

        @Test
        @DisplayName("Carga cero lanza IllegalArgumentException")
        void cargaCero_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Mision("M-001", drone, "A", "B",
                            TipoCarga.SENSOR, 0,
                            EstadoMision.PENDIENTE, Prioridad.NORMAL, null));
        }
    }
}