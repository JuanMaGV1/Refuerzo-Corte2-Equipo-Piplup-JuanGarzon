package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorBateriaTest {

    private ValidadorBateria validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorBateria();
    }

    private Mision misionConDrone(DroneAcuatico drone) {
        return new Mision.Builder()
                .id("M-001").drone(drone)
                .puntoPartida("Red de Canales")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.SENSOR)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    @Test
    @DisplayName("Batería >= 35% pasa la validación")
    void bateriaSuficiente_pasa() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        ResultadoValidacion r = validador.validar(misionConDrone(d));
        assertTrue(r.esValido());
    }

    @Test
    @DisplayName("Batería exactamente en 35% pasa")
    void bateriaEnElUmbral_pasa() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 35,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        assertTrue(validador.validar(misionConDrone(d)).esValido());
    }

    @Test
    @DisplayName("Batería 34% falla con razón específica")
    void bateriaInsuficiente_falla() {
        DroneAcuatico d = TestFixtures.buceador("AR-03", 34,
                EstadoDrone.DISPONIBLE, "Laguna de Reserva");
        ResultadoValidacion r = validador.validar(misionConDrone(d));
        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("AR-03")),
                () -> assertTrue(r.razon().contains("34")),
                () -> assertTrue(r.razon().contains("35"))
        );
    }

    @Test
    @DisplayName("Misión nula falla")
    void misionNula_falla() {
        ResultadoValidacion r = validador.validar(null);
        assertFalse(r.esValido());
    }
}