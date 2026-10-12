package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorMisionTest {

    private ValidadorMision v;

    @BeforeEach
    void setUp() {
        v = new ValidadorMision();
    }

    // ═══════════════════════════════════════════════════════════════
    // Batería del drone
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Drone con batería ≥ 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85,
                EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR);
        assertTrue(v.isBateriaSuficiente(d));
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado (con drone disponible)")
    void droneBateriaCritica_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18,
                EstadoDrone.DISPONIBLE, "Laguna Sur", TipoDrone.BUCEADOR);
        assertFalse(v.isBateriaSuficiente(d));
    }

    @Test
    @DisplayName("Batería exactamente en el umbral (35%) SÍ es suficiente")
    void droneBateriaEnElUmbral_esSuficiente() {
        DroneAcuatico d = new DroneAcuatico("AR-05", "Aqua-Ranger 100", 35,
                EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR);
        assertTrue(v.isBateriaSuficiente(d));
    }

    // ═══════════════════════════════════════════════════════════════
    // Punto de llegada
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException con mensaje específico")
    void puntoLlegadaNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> v.validarPuntoLlegada(null));
        assertEquals("El punto de llegada no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada vacío lanza IllegalArgumentException con mensaje específico")
    void puntoLlegadaVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> v.validarPuntoLlegada("   "));
        assertEquals("El punto de llegada no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada válido no lanza excepción")
    void puntoLlegadaValido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> v.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    // ═══════════════════════════════════════════════════════════════
    // Disponibilidad del drone
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Drone con estado != DISPONIBLE no es asignable")
    void droneNoDisponible_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-06", "Aqua-Ranger 100", 95,
                EstadoDrone.MANTENIMIENTO, "Embalse Norte", TipoDrone.BUCEADOR);
        assertFalse(v.isDisponible(d));
    }

    @Test
    @DisplayName("Drone con estado DISPONIBLE SÍ pasa la verificación")
    void droneDisponible_esAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR);
        assertTrue(v.isDisponible(d));
    }

    @Test
    @DisplayName("Drone EN_MISION no es asignable")
    void droneEnMision_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-07", "Aqua-Ranger 100", 90,
                EstadoDrone.EN_MISION, "Embalse Norte", TipoDrone.BUCEADOR);
        assertFalse(v.isDisponible(d));
    }

    // ═══════════════════════════════════════════════════════════════
    // Zona válida
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Zonas del catálogo son reconocidas como válidas")
    void zonasValidas_sonReconocidas() {
        assertAll(
                () -> assertTrue(v.esZonaValida("Embalse Norte")),
                () -> assertTrue(v.esZonaValida("Canal Central")),
                () -> assertTrue(v.esZonaValida("Laguna Sur")),
                () -> assertTrue(v.esZonaValida("Punto Ribereño Este")),
                () -> assertTrue(v.esZonaValida("Laboratorio Hídrico"))
        );
    }

    @Test
    @DisplayName("Zonas inválidas NO son reconocidas")
    void zonasInvalidas_noSonReconocidas() {
        assertAll(
                () -> assertFalse(v.esZonaValida("Zona Fantasma")),
                () -> assertFalse(v.esZonaValida(null)),
                () -> assertFalse(v.esZonaValida(""))
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // isMisionValida en conjunto
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isMisionValida — validación completa")
    class IsMisionValidaTest {

        private DroneAcuatico droneValido() {
            return new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92,
                    EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR);
        }

        private Mision misionBase() {
            return misionConEstado(EstadoMision.PENDIENTE);
        }

        private Mision misionConEstado(EstadoMision estado) {
            return new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .estadoMision(estado)
                    .prioridad(Prioridad.NORMAL)
                    .build();
        }

        private Mision misionConDrone(DroneAcuatico drone) {
            return new Mision.Builder()
                    .id("M-002")
                    .drone(drone)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .prioridad(Prioridad.NORMAL)
                    .build();
        }

        @Test
        @DisplayName("Misión válida retorna true")
        void misionValida_retornaTrue() {
            assertTrue(v.isMisionValida(misionBase()));
        }

        @Test
        @DisplayName("Misión con estado EN_TRANSITO retorna false")
        void misionConEstadoNoPendiente_retornaFalse() {
            assertFalse(v.isMisionValida(misionConEstado(EstadoMision.EN_TRANSITO)));
        }

        @Test
        @DisplayName("Misión con estado ENTREGADA retorna false")
        void misionConEstadoEntregada_retornaFalse() {
            assertFalse(v.isMisionValida(misionConEstado(EstadoMision.ENTREGADA)));
        }

        @Test
        @DisplayName("Misión con estado ABORTADA retorna false")
        void misionConEstadoAbortada_retornaFalse() {
            assertFalse(v.isMisionValida(misionConEstado(EstadoMision.ABORTADA)));
        }

        @Test
        @DisplayName("Misión nula retorna false")
        void misionNula_retornaFalse() {
            assertFalse(v.isMisionValida(null));
        }

        @Test
        @DisplayName("Misión con drone no disponible retorna false")
        void misionConDroneNoDisponible_retornaFalse() {
            DroneAcuatico droneNoDisponible = new DroneAcuatico(
                    "AR-06", "Aqua-Ranger 100", 95,
                    EstadoDrone.MANTENIMIENTO, "Embalse Norte", TipoDrone.BUCEADOR);
            Mision m = misionConDrone(droneNoDisponible);
            assertFalse(v.isMisionValida(m));
        }

        @Test
        @DisplayName("Misión con drone de batería insuficiente retorna false")
        void misionConDroneBateriaInsuficiente_retornaFalse() {
            DroneAcuatico droneBateriaBaja = new DroneAcuatico(
                    "AR-03", "Aqua-Ranger 100", 18,
                    EstadoDrone.DISPONIBLE, "Laguna Sur", TipoDrone.BUCEADOR);
            Mision m = misionConDrone(droneBateriaBaja);
            assertFalse(v.isMisionValida(m));
        }
    }
}