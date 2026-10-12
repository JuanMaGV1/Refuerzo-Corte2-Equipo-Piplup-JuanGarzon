package Piplup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MisionTest {

    private DroneAcuatico droneValido() {
        return new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
    }

    @Nested
    @DisplayName("Builder — validación de estructura")
    class BuilderTest {

        @Test
        @DisplayName("Construye misión válida con todos los campos")
        void construyeValida() {
            Mision m = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .build();
            assertNotNull(m);
            assertEquals("M-001", m.getId());
            assertEquals("Embalse Norte", m.getPuntoPartida());
            assertEquals("Laboratorio Hídrico", m.getPuntoLlegada());
            assertEquals(TipoCarga.MUESTRA_AGUA, m.getTipoCarga());
            assertEquals(EstadoMision.PENDIENTE, m.getEstadoMision());
        }

        @Test
        @DisplayName("id nulo lanza IllegalStateException")
        void idNulo_lanza() {
            var b = new Mision.Builder()
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico");
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("id vacío lanza IllegalStateException")
        void idVacio_lanza() {
            var b = new Mision.Builder()
                    .id("   ")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico");
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("drone nulo lanza IllegalStateException")
        void droneNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico");
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("puntoPartida nulo lanza IllegalStateException")
        void puntoPartidaNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoLlegada("Laboratorio Hídrico");
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("puntoLlegada nulo lanza IllegalStateException")
        void puntoLlegadaNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte");
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("Builder permite construir misión con drone no disponible (validación movida al Validador)")
        void droneNoDisponible_noBloqueaBuilder() {
            DroneAcuatico droneNoDisponible = new DroneAcuatico(
                    "AR-06", "Aqua-Ranger 100", 95, false, "Embalse Norte");
            Mision m = new Mision.Builder()
                    .id("M-002")
                    .drone(droneNoDisponible)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .build();
            assertNotNull(m);
            assertFalse(m.getDrone().disponible());
        }
        @Test
        @DisplayName("Misión válida expone todos sus getters")
        void getters_expuestos() {
            DroneAcuatico drone = new DroneAcuatico(
                    "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
            Mision m = new Mision.Builder()
                    .id("M-001")
                    .drone(drone)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .build();

            assertEquals("M-001", m.getId());
            assertEquals(drone, m.getDrone());                          // ← probablemente falta
            assertEquals("Embalse Norte", m.getPuntoPartida());
            assertEquals("Laboratorio Hídrico", m.getPuntoLlegada());
            assertEquals(TipoCarga.MUESTRA_AGUA, m.getTipoCarga());     // ← probablemente falta
            assertEquals(EstadoMision.PENDIENTE, m.getEstadoMision());  // ← probablemente falta
        }
    }
}