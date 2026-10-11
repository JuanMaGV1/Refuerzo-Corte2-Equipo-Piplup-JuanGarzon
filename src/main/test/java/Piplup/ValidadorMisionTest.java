package Piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorMisionTest {

    private ValidadorMision validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorMision();
    }

    // ═══════════════════════════════════════════════════════
    // GRUPO 1: Batería del drone
    // ═══════════════════════════════════════════════════════

    @Test
    @DisplayName("Drone con batería ≥ 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        // ARRANGE
        DroneAcuatico d = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(d);

        // ASSERT
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado")
    void droneBateriaCritica_noAsignable() {
        // ARRANGE
        DroneAcuatico d = new DroneAcuatico(
                "AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(d);

        // ASSERT
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Drone con batería exactamente en el umbral (35%) SÍ puede asignarse")
    void droneBateriaEnElUmbral_puedeAsignarse() {
        // ARRANGE
        DroneAcuatico d = new DroneAcuatico(
                "AR-05", "Aqua-Ranger 100", 35, true, "Embalse Norte");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(d);

        // ASSERT
        assertTrue(resultado, "El umbral exacto de 35% debe ser válido");
    }

    // ═══════════════════════════════════════════════════════
    // GRUPO 2: Punto de llegada
    // ═══════════════════════════════════════════════════════

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException")
    void puntoLlegadaNulo_lanzaExcepcion() {
        // ACT + ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada(null)
        );
        assertEquals("El punto de llegada no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada vacío lanza IllegalArgumentException")
    void puntoLlegadaVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada("   ")
        );
        assertEquals("El punto de llegada no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Punto de llegada válido no lanza excepción")
    void puntoLlegadaValido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> validador.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    // ═══════════════════════════════════════════════════════
    // GRUPO 3: Zona inválida (caso edge)
    // ═══════════════════════════════════════════════════════

    @Test
    @DisplayName("Zona válida es reconocida correctamente")
    void zonaValida_esReconocida() {
        assertTrue(validador.esZonaValida("Embalse Norte"));
        assertTrue(validador.esZonaValida("Canal Central"));
        assertTrue(validador.esZonaValida("Laguna Sur"));
        assertTrue(validador.esZonaValida("Punto Ribereño Este"));
        assertTrue(validador.esZonaValida("Laboratorio Hídrico"));
    }

    @Test
    @DisplayName("Zona inválida NO es reconocida")
    void zonaInvalida_noEsReconocida() {
        assertFalse(validador.esZonaValida("Zona Fantasma"));
        assertFalse(validador.esZonaValida(null));
        assertFalse(validador.esZonaValida(""));
    }

    @Test
    @DisplayName("Drone no disponible NO puede asignarse aunque tenga batería")
    void droneNoDisponible_noAsignable() {
        // ARRANGE
        DroneAcuatico d = new DroneAcuatico(
                "AR-06", "Aqua-Ranger 100", 90, false, "Embalse Norte");

        // ACT + ASSERT
        assertFalse(validador.estaDisponible(d));
    }
}