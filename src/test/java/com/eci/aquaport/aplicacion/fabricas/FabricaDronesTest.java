package com.eci.aquaport.aplicacion.fabricas;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.DroneSemisumergido;
import com.eci.aquaport.dominio.DroneSuperficial;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FabricaDronesTest {

    private FabricaDrones fabrica;

    @BeforeEach
    void setUp() {
        fabrica = new FabricaDrones();
    }

    @Test
    @DisplayName("Crea un DroneSuperficial cuando el tipo es SUPERFICIAL")
    void creaSuperficial() {
        DroneAcuatico d = fabrica.crearDrone("AR-01", "Aqua-Ranger 100",
                90, EstadoDrone.DISPONIBLE, "Canal Central", TipoDrone.SUPERFICIAL);
        assertInstanceOf(DroneSuperficial.class, d);
        assertEquals(TipoDrone.SUPERFICIAL, d.tipo());
        assertEquals(500, d.capacidadCargaGramos());
    }

    @Test
    @DisplayName("Crea un DroneSemisumergido cuando el tipo es SEMISUMERGIDO")
    void creaSemisumergido() {
        DroneAcuatico d = fabrica.crearDrone("AR-02", "Aqua-Ranger 100",
                80, EstadoDrone.DISPONIBLE, "Laguna Sur", TipoDrone.SEMISUMERGIDO);
        assertInstanceOf(DroneSemisumergido.class, d);
        assertEquals(1500, d.capacidadCargaGramos());
    }

    @Test
    @DisplayName("Crea un DroneBuceador cuando el tipo es BUCEADOR")
    void creaBuceador() {
        DroneAcuatico d = fabrica.crearDrone("AR-03", "Aqua-Ranger 100",
                70, EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR);
        assertInstanceOf(DroneBuceador.class, d);
        assertEquals(300, d.capacidadCargaGramos());
    }

    @Test
    @DisplayName("Tipo nulo lanza IllegalArgumentException")
    void tipoNulo_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> fabrica.crearDrone("AR-01", "Aqua-Ranger 100",
                        90, EstadoDrone.DISPONIBLE, "Zona", null));
    }
}