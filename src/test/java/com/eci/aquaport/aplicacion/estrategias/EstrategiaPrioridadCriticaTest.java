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

class EstrategiaPrioridadCriticaTest {

    private EstrategiaPrioridadCritica estrategia;
    private Mision misionCritica;

    @BeforeEach
    void setUp() {
        estrategia = new EstrategiaPrioridadCritica(new EstrategiaMayorBateria());

        DroneAcuatico placeholder = new DroneSemisumergido(
                "AR-00", "Aqua-Ranger 100", 90,
                EstadoDrone.DISPONIBLE, "Canal Central");
        misionCritica = new Mision.Builder()
                .id("M-001").drone(placeholder)
                .puntoPartida("Canal Central").puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.EQUIPO_MEDICION)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.CRITICA)
                .build();
    }

    @Test
    @DisplayName("Elige mayor capacidad aunque tenga menos batería (AP-08 literal)")
    void eligeMayorCapacidadSinImportarBateria() {
        // Semisumergido: 1500g de capacidad, 40% batería
        // Buceador: 300g de capacidad, 95% batería
        DroneAcuatico semisumergido = new DroneSemisumergido(
                "AR-A", "Aqua-Ranger 100", 40, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico buceador = new DroneBuceador(
                "AR-B", "Aqua-Ranger 100", 95, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> elegido =
                estrategia.seleccionar(List.of(semisumergido, buceador), misionCritica);

        assertTrue(elegido.isPresent());
        assertEquals("AR-A", elegido.get().id(),
                "Debe elegir por capacidad (1500g > 300g), no por batería");
    }

    @Test
    @DisplayName("Empate en capacidad: desempata por ID (no por batería)")
    void empateEnCapacidadDesempataPorId() {
        // Dos semisumergidos con la misma capacidad (1500g), distinta batería
        DroneAcuatico a = new DroneSemisumergido(
                "AR-A", "Aqua-Ranger 100", 50, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico b = new DroneSemisumergido(
                "AR-B", "Aqua-Ranger 100", 90, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> elegido =
                estrategia.seleccionar(List.of(a, b), misionCritica);

        assertTrue(elegido.isPresent());
        assertEquals("AR-A", elegido.get().id(),
                "Empate en capacidad: debe desempatar por ID (AR-A < AR-B), no por batería");
    }

    @Test
    @DisplayName("Misión NO crítica: delega en el fallback (mayor batería)")
    void misionNoCriticaDelegaEnFallback() {
        DroneAcuatico placeholder = new DroneSemisumergido(
                "AR-00", "Aqua-Ranger 100", 90,
                EstadoDrone.DISPONIBLE, "Canal Central");
        Mision misionNormal = new Mision.Builder()
                .id("M-002").drone(placeholder)
                .puntoPartida("Canal Central").puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .cargaGramos(100)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();

        DroneAcuatico bajoBateria = new DroneSemisumergido(
                "AR-A", "Aqua-Ranger 100", 40, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico altoBateria = new DroneSemisumergido(
                "AR-B", "Aqua-Ranger 100", 90, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> elegido =
                estrategia.seleccionar(List.of(bajoBateria, altoBateria), misionNormal);

        assertTrue(elegido.isPresent());
        assertEquals("AR-B", elegido.get().id(),
                "Misión no crítica: debe delegar en EstrategiaMayorBateria");
    }
}