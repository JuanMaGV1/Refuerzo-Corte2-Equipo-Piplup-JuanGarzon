package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneSubclasesTest {

    @Test
    @DisplayName("DroneSuperficial expone su tipo y capacidad")
    void superficial() {
        DroneAcuatico d = new DroneSuperficial(
                "AR-01", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Red de Canales");
        assertAll(
                () -> assertEquals(TipoDrone.SUPERFICIAL, d.tipo()),
                () -> assertEquals(500, d.capacidadCargaGramos())
        );
    }

    @Test
    @DisplayName("DroneSemisumergido expone su tipo y capacidad")
    void semisumergido() {
        DroneAcuatico d = new DroneSemisumergido(
                "AR-02", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Laguna de Reserva");
        assertAll(
                () -> assertEquals(TipoDrone.SEMISUMERGIDO, d.tipo()),
                () -> assertEquals(1500, d.capacidadCargaGramos())
        );
    }

    @Test
    @DisplayName("DroneBuceador expone su tipo y capacidad")
    void buceador() {
        DroneAcuatico d = new DroneBuceador(
                "AR-03", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        assertAll(
                () -> assertEquals(TipoDrone.BUCEADOR, d.tipo()),
                () -> assertEquals(300, d.capacidadCargaGramos())
        );
    }
}