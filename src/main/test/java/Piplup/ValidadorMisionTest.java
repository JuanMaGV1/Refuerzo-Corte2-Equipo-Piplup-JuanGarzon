package src.main.test.java.Piplup;

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

    // ─────────────────────────────────────────────────────
    // Batería
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Drone con batería > 35% puede ser asignado")
    void droneBateriaAlta_puedeAsignarse() {
        // ARRANGE
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100",
                                            85, true, "Embalse Norte");
        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(d);
        // ASSERT
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone con batería EXACTAMENTE 35% puede ser asignado (frontera)")
    void droneBateriaExactaFrontera_puedeAsignarse() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100",
                                            35, true, "Embalse Norte");
        assertTrue(validador.tieneBateriaSuficiente(d),
            "El umbral es >= 35%, no > 35%");
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado")
    void droneBateriaCritica_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-03", "Aqua-Ranger 100",
                                            18, false, "Laguna Sur");
        assertFalse(validador.tieneBateriaSuficiente(d));
    }

    @Test
    @DisplayName("Drone nulo no tiene batería suficiente")
    void droneNulo_noTieneBateriaSuficiente() {
        assertFalse(validador.tieneBateriaSuficiente(null));
    }

    // ─────────────────────────────────────────────────────
    // Punto de llegada
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException")
    void puntoLlegadaNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> validador.validarPuntoLlegada(null));
    }

    @Test
    @DisplayName("Punto de llegada vacío lanza IllegalArgumentException")
    void puntoLlegadaVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> validador.validarPuntoLlegada("   "));
    }

    @Test
    @DisplayName("Punto de llegada válido no lanza excepción")
    void puntoLlegadaValido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> validador.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    // ─────────────────────────────────────────────────────
    // Drone disponible
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Drone no disponible no es asignable")
    void droneNoDisponible_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-03", "Aqua-Ranger 100",
                                            80, false, "Laguna Sur");
        assertFalse(validador.estaDisponible(d));
    }

    @Test
    @DisplayName("Drone disponible es asignable")
    void droneDisponible_asignable() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100",
                                            80, true, "Embalse Norte");
        assertTrue(validador.estaDisponible(d));
    }

    // ─────────────────────────────────────────────────────
    // Zona válida (edge case)
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Zona válida del catálogo es aceptada")
    void zonaValida_aceptada() {
        assertTrue(validador.esZonaValida("Embalse Norte"));
    }

    @Test
    @DisplayName("Zona inválida fuera del catálogo es rechazada (edge case)")
    void zonaInvalida_rechazada() {
        assertFalse(validador.esZonaValida("Río Inventado"));
    }

    @Test
    @DisplayName("Zona nula es rechazada")
    void zonaNula_rechazada() {
        assertFalse(validador.esZonaValida(null));
    }

    // ─────────────────────────────────────────────────────
    // Validación integral de la misión
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Misión con todos los datos válidos es aceptada")
    void misionCompletaValida_aceptada() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100",
                                            85, true, "Embalse Norte");
        Mision m = new Mision.Builder()
            .id("M-001")
            .drone(d)
            .puntoPartida("Embalse Norte")
            .puntoLlegada("Laboratorio Hídrico")
            .tipoCarga(TipoCarga.MUESTRA_AGUA)
            .estadoMision(EstadoMision.PENDIENTE)
            .build();
        assertTrue(validador.isMisionValida(m));
    }

    @Test
    @DisplayName("Misión nula es rechazada")
    void misionNula_rechazada() {
        assertFalse(validador.isMisionValida(null));
    }

    @Test
    @DisplayName("Misión con estado distinto a PENDIENTE es rechazada")
    void misionNoPendiente_rechazada() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100",
                                            85, true, "Embalse Norte");
        Mision m = new Mision.Builder()
            .id("M-002")
            .drone(d)
            .puntoPartida("Embalse Norte")
            .puntoLlegada("Laboratorio Hídrico")
            .tipoCarga(TipoCarga.SENSOR)
            .estadoMision(EstadoMision.ENTREGADA)
            .build();
        assertFalse(validador.isMisionValida(m));
    }
}