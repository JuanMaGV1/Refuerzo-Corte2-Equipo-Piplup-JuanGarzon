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

class ValidadorCapacidadCargaTest {

    private ValidadorCapacidadCarga validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorCapacidadCarga();
    }

    private Mision misionCon(DroneAcuatico drone, int cargaGramos) {
        return new Mision.Builder()
                .id("M-001").drone(drone)
                .puntoPartida("Red de Canales")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.EQUIPO_MEDICION)
                .cargaGramos(cargaGramos)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    @Test
    @DisplayName("Carga dentro de capacidad pasa")
    void cargaDentroDeCapacidad_pasa() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        assertTrue(validador.validar(misionCon(d, 200)).esValido());
    }

    @Test
    @DisplayName("Carga exactamente igual a capacidad pasa")
    void cargaEnElLimite_pasa() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        // BUCEADOR tiene capacidad 300g
        assertTrue(validador.validar(misionCon(d, 300)).esValido());
    }

    @Test
    @DisplayName("Carga que excede capacidad falla con razón específica")
    void cargaExcedeCapacidad_falla() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        ResultadoValidacion r = validador.validar(misionCon(d, 500));
        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("500")),
                () -> assertTrue(r.razon().contains("300")),
                () -> assertTrue(r.razon().contains("BUCEADOR"))
        );
    }
}