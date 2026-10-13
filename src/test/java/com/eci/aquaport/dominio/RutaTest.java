package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RutaTest {

    private List<Waypoint> waypointsValidos() {
        return List.of(
                Waypoint.origen("Red de Canales"),
                new Waypoint("Laguna de Reserva", 1),
                new Waypoint("Laboratorio Hídrico Central", 2)
        );
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Crea una ruta válida de 3 waypoints")
        void creaValida() {
            Ruta ruta = new Ruta(waypointsValidos());
            assertAll(
                    () -> assertEquals(3, ruta.waypoints().size()),
                    () -> assertEquals("Red de Canales", ruta.zonaOrigen()),
                    () -> assertEquals("Laboratorio Hídrico Central", ruta.zonaDestino())
            );
        }

        @Test
        @DisplayName("Lista nula lanza IllegalArgumentException")
        void listaNula_lanza() {
            assertThrows(IllegalArgumentException.class, () -> new Ruta(null));
        }

        @Test
        @DisplayName("Menos de 2 waypoints lanza IllegalArgumentException")
        void menosDeDos_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Ruta(List.of(Waypoint.origen("Zona"))));
        }

        @Test
        @DisplayName("Órdenes no consecutivos lanzan IllegalArgumentException")
        void ordenNoConsecutivo_lanza() {
            List<Waypoint> malOrdenados = List.of(
                    Waypoint.origen("A"),
                    new Waypoint("B", 2)   // falta el 1
            );
            assertThrows(IllegalArgumentException.class, () -> new Ruta(malOrdenados));
        }

        @Test
        @DisplayName("Órdenes empezando en 1 lanzan IllegalArgumentException")
        void ordenEmpezandoEnUno_lanza() {
            List<Waypoint> malOrdenados = List.of(
                    new Waypoint("A", 1),
                    new Waypoint("B", 2)
            );
            assertThrows(IllegalArgumentException.class, () -> new Ruta(malOrdenados));
        }
    }

    @Nested
    @DisplayName("tramos()")
    class Tramos {

        @Test
        @DisplayName("Deriva N-1 tramos de N waypoints")
        void derivarTramos() {
            Ruta ruta = new Ruta(waypointsValidos());
            List<TramoRuta> tramos = ruta.tramos();

            assertAll(
                    () -> assertEquals(2, tramos.size()),
                    () -> assertEquals(2, ruta.numeroTramos()),
                    () -> assertEquals("Red de Canales", tramos.get(0).zonaOrigen()),
                    () -> assertEquals("Laguna de Reserva", tramos.get(0).zonaDestino()),
                    () -> assertEquals("Laguna de Reserva", tramos.get(1).zonaOrigen()),
                    () -> assertEquals("Laboratorio Hídrico Central", tramos.get(1).zonaDestino())
            );
        }

        @Test
        @DisplayName("Los tramos tienen orden consecutivo desde 0")
        void ordenConsecutivo() {
            Ruta ruta = new Ruta(waypointsValidos());
            List<TramoRuta> tramos = ruta.tramos();
            assertEquals(0, tramos.get(0).orden());
            assertEquals(1, tramos.get(1).orden());
        }

        @Test
        @DisplayName("Ruta mínima (2 waypoints) tiene 1 tramo")
        void rutaMinima() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("A"),
                    new Waypoint("B", 1)));
            assertEquals(1, ruta.tramos().size());
        }
    }

    @Nested
    @DisplayName("zonas()")
    class Zonas {

        @Test
        @DisplayName("Retorna zonas únicas de la ruta")
        void zonasUnicas() {
            Ruta ruta = new Ruta(waypointsValidos());
            Set<String> zonas = ruta.zonas();

            assertAll(
                    () -> assertEquals(3, zonas.size()),
                    () -> assertTrue(zonas.contains("Red de Canales")),
                    () -> assertTrue(zonas.contains("Laguna de Reserva")),
                    () -> assertTrue(zonas.contains("Laboratorio Hídrico Central"))
            );
        }

        @Test
        @DisplayName("Deduplica zonas repetidas")
        void deduplicaZonas() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("A"),
                    new Waypoint("B", 1),
                    new Waypoint("A", 2)));
            assertEquals(2, ruta.zonas().size());
        }
    }

    @Nested
    @DisplayName("pasaPor()")
    class PasaPor {

        @Test
        @DisplayName("Retorna true si la zona está en la ruta")
        void pasaPorZona() {
            Ruta ruta = new Ruta(waypointsValidos());
            assertTrue(ruta.pasaPor("Laguna de Reserva"));
        }

        @Test
        @DisplayName("Retorna false si la zona no está en la ruta")
        void noPasaPorZona() {
            Ruta ruta = new Ruta(waypointsValidos());
            assertFalse(ruta.pasaPor("Zona Fantasma"));
        }
    }

    @Nested
    @DisplayName("Inmutabilidad")
    class Inmutabilidad {

        @Test
        @DisplayName("La lista original mutada no afecta a la ruta")
        void listaOriginalMutada_noAfecta() {
            List<Waypoint> original = new ArrayList<>(waypointsValidos());
            Ruta ruta = new Ruta(original);

            original.clear();

            assertEquals(3, ruta.waypoints().size(),
                    "La ruta debe copiar la lista para ser inmutable");
        }
    }
}