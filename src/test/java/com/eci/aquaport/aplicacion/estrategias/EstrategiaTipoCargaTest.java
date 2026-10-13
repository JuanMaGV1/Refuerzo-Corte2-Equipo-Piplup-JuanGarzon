package com.eci.aquaport.aplicacion.estrategias;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.DroneSemisumergido;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaTipoCargaTest {

    private EstrategiaTipoCarga estrategia;

    @BeforeEach
    void setUp() {
        estrategia = new EstrategiaTipoCarga(new EstrategiaMayorBateria());
    }

    private Mision mision(TipoCarga tipo) {
        DroneAcuatico placeholder = new DroneSemisumergido(
                "AR-00", "Aqua-Ranger 100", 90,
                EstadoDrone.DISPONIBLE, "Canal Central");
        return new Mision.Builder()
                .id("M-001").drone(placeholder)
                .puntoPartida("Canal Central").puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(tipo)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    @Test
    @DisplayName("EQUIPO_MEDICION → prioriza capacidad de carga")
    void equipoMedicion_priorizaCapacidad() {
        DroneAcuatico semisumergido = new DroneSemisumergido(
                "AR-A", "Aqua-Ranger 100", 40, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico buceador = new DroneBuceador(
                "AR-B", "Aqua-Ranger 100", 95, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> elegido =
                estrategia.seleccionar(List.of(semisumergido, buceador),
                        mision(TipoCarga.EQUIPO_MEDICION));

        assertTrue(elegido.isPresent());
        assertEquals("AR-A", elegido.get().id(),
                "Equipo de medición: debe elegir semisumergido (1500g > 300g)");
    }

    @Test
    @DisplayName("MUESTRA_AGUA → delega en el fallback (mayor batería)")
    void muestraAgua_delegaEnFallback() {
        DroneAcuatico bajoBateria = new DroneSemisumergido(
                "AR-A", "Aqua-Ranger 100", 40, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico altoBateria = new DroneSemisumergido(
                "AR-B", "Aqua-Ranger 100", 90, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> elegido =
                estrategia.seleccionar(List.of(bajoBateria, altoBateria),
                        mision(TipoCarga.MUESTRA_AGUA));

        assertTrue(elegido.isPresent());
        assertEquals("AR-B", elegido.get().id(),
                "Muestra de agua: debe elegir el de mayor batería");
    }
}