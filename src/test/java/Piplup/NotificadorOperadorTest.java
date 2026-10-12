package Piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class NotificadorOperadorTest {

    private NotificadorOperador notificador;
    private final ByteArrayOutputStream salida = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorOperador();
        System.setOut(new PrintStream(salida));
    }

    @Test
    @DisplayName("notificarExito imprime mensaje con id de misión")
    void notificarExito_imprime() {
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        Mision m = new Mision.Builder()
                .id("M-001").drone(drone)
                .puntoPartida("Embalse Norte").puntoLlegada("Lab")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(EstadoMision.PENDIENTE).build();

        notificador.notificarExito(m, "Registrar");
        System.setOut(originalOut);

        String salidaStr = salida.toString();
        assertTrue(salidaStr.contains("M-001"));
        assertTrue(salidaStr.contains("Registrar"));
        assertTrue(salidaStr.contains("exitosamente"));
    }

    @Test
    @DisplayName("notificarError imprime mensaje con id y razón")
    void notificarError_imprime() {
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        Mision m = new Mision.Builder()
                .id("M-002").drone(drone)
                .puntoPartida("Embalse Norte").puntoLlegada("Lab")
                .tipoCarga(TipoCarga.SENSOR)
                .estadoMision(EstadoMision.PENDIENTE).build();

        notificador.notificarError(m, "Registrar", "Batería insuficiente");
        System.setOut(originalOut);

        String salidaStr = salida.toString();
        assertTrue(salidaStr.contains("M-002"));
        assertTrue(salidaStr.contains("Batería insuficiente"));
        assertTrue(salidaStr.contains("error"));
    }
}