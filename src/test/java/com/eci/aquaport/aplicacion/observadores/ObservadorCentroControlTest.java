package com.eci.aquaport.aplicacion.observadores;

import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ObservadorCentroControlTest {

    private ObservadorCentroControl observador;
    private Logger logger;
    private List<LogRecord> registros;
    private Handler handler;

    @BeforeEach
    void setUp() {
        observador = new ObservadorCentroControl();
        logger = Logger.getLogger(ObservadorCentroControl.class.getName());
        logger.setUseParentHandlers(false);

        registros = new ArrayList<>();
        handler = new Handler() {
            @Override public void publish(LogRecord r) { registros.add(r); }
            @Override public void flush() {}
            @Override public void close() {}
        };
        logger.addHandler(handler);
    }

    @AfterEach
    void tearDown() {
        logger.removeHandler(handler);
    }

    private Mision mision() {
        DroneAcuatico drone = new DroneBuceador(
                "AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, "Embalse Norte");
        return new Mision.Builder()
                .id("M-001").drone(drone)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    @Test
    @DisplayName("notificarAsignacion registra log INFO con id de misión y drone")
    void notificarAsignacion_loggeaInfo() {
        Mision m = mision();
        DroneAcuatico drone = m.drone();

        observador.notificarAsignacion(m, drone);

        assertEquals(1, registros.size());
        LogRecord registro = registros.get(0);
        assertAll(
                () -> assertEquals(Level.INFO, registro.getLevel()),
                () -> assertTrue(registro.getMessage().contains("M-001")),
                () -> assertTrue(registro.getMessage().contains("AR-01"))
        );
    }

    @Test
    @DisplayName("notificarFalloAsignacion registra log WARNING con id y razón")
    void notificarFalloAsignacion_loggeaWarning() {
        Mision m = mision();

        observador.notificarFalloAsignacion(m, "Sin drones compatibles");

        assertEquals(1, registros.size());
        LogRecord registro = registros.get(0);
        assertAll(
                () -> assertEquals(Level.WARNING, registro.getLevel()),
                () -> assertTrue(registro.getMessage().contains("M-001")),
                () -> assertTrue(registro.getMessage().contains("Sin drones compatibles"))
        );
    }
}