package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MisionTest {

    private DroneAcuatico droneValido() {
        return new DroneBuceador("AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, "Embalse Norte");
    }

    // ═══════════════════════════════════════════════════════════════
    // Builder — construcción válida
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Builder — construcción válida")
    class ConstruccionValida {

        @Test
        @DisplayName("Construye misión completa y expone todos los getters")
        void construyeCompleta() {
            Mision m = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL)
                    .build();

            assertAll(
                () -> assertEquals("M-001", m.id()),
                () -> assertEquals("Embalse Norte", m.puntoPartida()),
                () -> assertEquals("Laboratorio Hídrico", m.puntoLlegada()),
                () -> assertEquals(TipoCarga.MUESTRA_AGUA, m.tipoCarga()),
                () -> assertEquals(EstadoMision.PENDIENTE, m.estadoMision()),
                () -> assertEquals(Prioridad.NORMAL, m.prioridad()),
                () -> assertNotNull(m.drone())
            );
        }

        @Test
        @DisplayName("Prioridad CRITICA se preserva en el record")
        void prioridadCritica() {
            Mision m = new Mision.Builder()
                    .id("M-002")
                    .drone(droneValido())
                    .puntoPartida("A")
                    .puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.CRITICA)
                    .build();

            assertEquals(Prioridad.CRITICA, m.prioridad());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Builder — validaciones de estructura
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Builder — validaciones de estructura")
    class ValidacionesEstructura {

        @Test
        @DisplayName("id nulo lanza IllegalStateException")
        void idNulo_lanza() {
            var b = new Mision.Builder()
                    .drone(droneValido())
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("id vacío lanza IllegalStateException")
        void idVacio_lanza() {
            var b = new Mision.Builder()
                    .id("   ")
                    .drone(droneValido())
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("drone nulo lanza IllegalStateException")
        void droneNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("puntoPartida nulo lanza IllegalStateException")
        void puntoPartidaNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("puntoLlegada nulo lanza IllegalStateException")
        void puntoLlegadaNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("A")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("tipoCarga nulo lanza IllegalStateException")
        void tipoCargaNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("A").puntoLlegada("B")
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("estadoMision nulo lanza IllegalStateException")
        void estadoMisionNulo_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .prioridad(Prioridad.NORMAL);
            assertThrows(IllegalStateException.class, b::build);
        }

        @Test
        @DisplayName("prioridad nula lanza IllegalStateException")
        void prioridadNula_lanza() {
            var b = new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE);
            assertThrows(IllegalStateException.class, b::build);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Builder — el Builder NO valida reglas de negocio
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Builder — no valida reglas de negocio")
    class NoValidaNegocio {

        @Test
        @DisplayName("Permite construir misión con drone NO disponible (ValidadorMision lo rechaza)")
        void droneNoDisponible_noBloqueaBuilder() {
            DroneAcuatico droneNoDisponible = new DroneBuceador(
                    "AR-06", "Aqua-Ranger 100", 95,
                    EstadoDrone.MANTENIMIENTO, "Embalse Norte");

            Mision m = new Mision.Builder()
                    .id("M-002")
                    .drone(droneNoDisponible)
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL)
                    .build();

            assertNotNull(m);
            assertEquals(EstadoDrone.MANTENIMIENTO, m.drone().estado());
        }

        @Test
        @DisplayName("Permite construir misión con drone de batería baja (ValidadorMision lo rechaza)")
        void droneBateriaBaja_noBloqueaBuilder() {
            DroneAcuatico droneBateriaBaja = new DroneBuceador(
                    "AR-03", "Aqua-Ranger 100", 18,
                    EstadoDrone.DISPONIBLE, "Laguna Sur");

            Mision m = new Mision.Builder()
                    .id("M-003")
                    .drone(droneBateriaBaja)
                    .puntoPartida("A").puntoLlegada("B")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL)
                    .build();

            assertNotNull(m);
            assertEquals(18, m.drone().bateria());
        }
    }
}