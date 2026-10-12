package Piplup;

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

class NotificadorOperadorTest {

    private NotificadorOperador notificador;
    private Logger logger;
    private List<LogRecord> registrosCapturados;
    private Handler handlerCapturador;

    private Mision mision;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorOperador();
        logger = Logger.getLogger(NotificadorOperador.class.getName());
        logger.setUseParentHandlers(false);  // evita que los logs salgan a consola

        registrosCapturados = new ArrayList<>();
        handlerCapturador = new Handler() {
            @Override
            public void publish(LogRecord record) {
                registrosCapturados.add(record);
            }
            @Override public void flush() {}
            @Override public void close() {}
        };
        logger.addHandler(handlerCapturador);

        // Misión de prueba reutilizable
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        mision = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(EstadoMision.PENDIENTE)
                .build();
    }

    @AfterEach
    void tearDown() {
        logger.removeHandler(handlerCapturador);
    }

    @Test
    @DisplayName("notificarExito registra log INFO con id de misión y operación")
    void notificarExito_imprime() {
        // ACT
        notificador.notificarExito(mision, "Registrar");

        // ASSERT
        assertEquals(1, registrosCapturados.size(),
                "Debe emitirse exactamente 1 registro de log");

        LogRecord registro = registrosCapturados.get(0);
        String mensaje = registro.getMessage();

        assertAll(
                () -> assertEquals(Level.INFO, registro.getLevel(),
                        "notificarExito debe usar nivel INFO"),
                () -> assertTrue(mensaje.contains("M-001"),
                        "El mensaje debe incluir el id de la misión"),
                () -> assertTrue(mensaje.contains("Registrar"),
                        "El mensaje debe incluir el nombre de la operación"),
                () -> assertTrue(mensaje.contains("exitosamente"),
                        "El mensaje debe indicar éxito")
        );
    }

    @Test
    @DisplayName("notificarError registra log WARNING con id de misión y razón")
    void notificarError_imprime() {
        // ACT
        notificador.notificarError(mision, "Registrar", "Batería insuficiente");

        // ASSERT
        assertEquals(1, registrosCapturados.size());

        LogRecord registro = registrosCapturados.get(0);
        String mensaje = registro.getMessage();

        assertAll(
                () -> assertEquals(Level.WARNING, registro.getLevel(),
                        "notificarError debe usar nivel WARNING"),
                () -> assertTrue(mensaje.contains("M-001"),
                        "El mensaje debe incluir el id de la misión"),
                () -> assertTrue(mensaje.contains("Registrar"),
                        "El mensaje debe incluir el nombre de la operación"),
                () -> assertTrue(mensaje.contains("Batería insuficiente"),
                        "El mensaje debe incluir la razón del error")
        );
    }
}