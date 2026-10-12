package Piplup;

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
    // GRUPO 1: Batería del drone
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Drone con batería ≥ 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        DroneAcuatico d = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");
        assertTrue(v.tieneBateriaSuficiente(d));
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado (con drone disponible)")
    void droneBateriaCritica_noAsignable() {
        // disponible=true para aislar la variable batería
        DroneAcuatico d = new DroneAcuatico(
                "AR-03", "Aqua-Ranger 100", 18, true, "Laguna Sur");
        assertFalse(v.tieneBateriaSuficiente(d),
                "Un drone disponible con batería < 35% no debe pasar");
    }

    @Test
    @DisplayName("Batería exactamente en el umbral (35%) SÍ es suficiente")
    void droneBateriaEnElUmbral_esSuficiente() {
        DroneAcuatico d = new DroneAcuatico(
                "AR-05", "Aqua-Ranger 100", 35, true, "Embalse Norte");
        assertTrue(v.tieneBateriaSuficiente(d),
                "35% es el mínimo válido, no debe rechazarse");
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 2: Punto de llegada
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException con mensaje específico")
    void puntoLlegadaNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> v.validarPuntoLlegada(null)
        );
        assertEquals("El punto de llegada no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada vacío lanza IllegalArgumentException con mensaje específico")
    void puntoLlegadaVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> v.validarPuntoLlegada("   ")
        );
        assertEquals("El punto de llegada no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada válido no lanza excepción")
    void puntoLlegadaValido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> v.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 3: Disponibilidad del drone
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Drone con disponible=false NO puede asignarse, aunque tenga batería alta")
    void droneNoDisponible_noAsignable() {
        DroneAcuatico d = new DroneAcuatico(
                "AR-06", "Aqua-Ranger 100", 95, false, "Embalse Norte");
        assertFalse(v.estaDisponible(d));
    }

    @Test
    @DisplayName("Drone con disponible=true SÍ pasa la verificación")
    void droneDisponible_esAsignable() {
        DroneAcuatico d = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        assertTrue(v.estaDisponible(d));
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 4: Zona válida
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
    // GRUPO 5: isMisionValida en conjunto
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isMisionValida — validación completa de una Mision")
    class IsMisionValidaTest {

        // ─────────────────────────────────────────────────────────
        // Helpers de construcción
        // ─────────────────────────────────────────────────────────

        /** Drone por defecto: disponible y con batería suficiente. */
        private DroneAcuatico droneValido() {
            return new DroneAcuatico(
                    "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        }

        /** Misión base: PENDIENTE, drone válido, puntos válidos. */
        private Mision misionBase() {
            return misionConEstado(EstadoMision.PENDIENTE);
        }

        /** Misión con el estado indicado, todo lo demás válido. */
        private Mision misionConEstado(EstadoMision estado) {
            return new Mision.Builder()
                    .id("M-001")
                    .drone(droneValido())
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .estadoMision(estado)
                    .build();
        }

        /** Misión con el drone indicado, todo lo demás válido. */
        private Mision misionConDrone(DroneAcuatico drone) {
            return new Mision.Builder()
                    .id("M-002")
                    .drone(drone)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.SENSOR)
                    .estadoMision(EstadoMision.PENDIENTE)
                    .build();
        }

        // ─────────────────────────────────────────────────────────
        // Tests
        // ─────────────────────────────────────────────────────────

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
        @DisplayName("Misión nula retorna false")
        void misionNula_retornaFalse() {
            assertFalse(v.isMisionValida(null));
        }

        /**
         * ✅ CORRECCIÓN DEL BLOQUEANTE #1
         *
         * Ahora el Builder NO rechaza drones no disponibles (solo valida
         * estructura). Esta prueba construye la misión con un drone en
         * disponible=false y verifica que el ValidadorMision la rechaza.
         *
         * El @DisplayName y el cuerpo ahora son coherentes.
         */
        @Test
        @DisplayName("Misión con drone no disponible retorna false")
        void misionConDroneNoDisponible_retornaFalse() {
            // ARRANGE — drone con batería alta pero NO disponible
            DroneAcuatico droneNoDisponible = new DroneAcuatico(
                    "AR-06", "Aqua-Ranger 100", 95, false, "Embalse Norte");
            Mision m = misionConDrone(droneNoDisponible);

            // ACT + ASSERT
            assertFalse(v.isMisionValida(m),
                    "Una misión con drone no disponible no debe ser válida para operar");
        }
        @Test
        @DisplayName("Misión con drone de batería insuficiente retorna false")
        void misionConDroneBateriaInsuficiente_retornaFalse() {
            // ARRANGE — drone disponible pero con batería crítica
            DroneAcuatico droneBateriaBaja = new DroneAcuatico(
                    "AR-03", "Aqua-Ranger 100", 18, true, "Laguna Sur");
            Mision m = misionConDrone(droneBateriaBaja);

            // ACT + ASSERT
            assertFalse(v.isMisionValida(m),
                    "Una misión con drone de batería insuficiente no debe ser válida");
        }
    }
}