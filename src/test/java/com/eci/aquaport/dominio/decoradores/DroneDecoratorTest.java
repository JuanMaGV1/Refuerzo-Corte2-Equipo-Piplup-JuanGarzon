package com.eci.aquaport.dominio.decoradores;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.dominio.DroneOperable;
import com.eci.aquaport.dominio.DroneSuperficial;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneDecoratorTest {

    private DroneOperable drone;

    @BeforeEach
    void setUp() {
        drone = TestFixtures.superficial("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Red de Canales");
    }

    // ═══════════════════════════════════════════════════════════════
    // Delegación pura
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Delegación de operaciones al drone envuelto")
    class Delegacion {

        @Test
        @DisplayName("Los getters delegan en el envuelto")
        void gettersDelegan() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            assertAll(
                    () -> assertEquals(drone.id(), decorador.id()),
                    () -> assertEquals(drone.modelo(), decorador.modelo()),
                    () -> assertEquals(drone.bateria(), decorador.bateria()),
                    () -> assertEquals(drone.estado(), decorador.estado()),
                    () -> assertEquals(drone.zona(), decorador.zona()),
                    () -> assertEquals(drone.tipo(), decorador.tipo()),
                    () -> assertEquals(drone.capacidadCargaGramos(),
                            decorador.capacidadCargaGramos())
            );
        }

        @Test
        @DisplayName("setEstado delega en el envuelto")
        void setEstadoDelega() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            decorador.setEstado(EstadoDrone.EN_MISION);
            assertEquals(EstadoDrone.EN_MISION, drone.estado(),
                    "El estado del envuelto se modificó");
        }

        @Test
        @DisplayName("setBateria delega en el envuelto")
        void setBateriaDelega() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            decorador.setBateria(50);
            assertEquals(50, drone.bateria());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DroneConMonitoreo: añade telemetría sin alterar el envuelto
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DroneConMonitoreo — registra telemetría")
    class Monitoreo {

        @Test
        @DisplayName("Registra cada cambio de estado en la telemetría")
        void registraCambiosDeEstado() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            decorador.setEstado(EstadoDrone.EN_MISION);
            decorador.setEstado(EstadoDrone.RECARGANDO);

            assertAll(
                    () -> assertEquals(2, decorador.cantidadEventos()),
                    () -> assertTrue(decorador.telemetria().get(0).contains("DISPONIBLE")),
                    () -> assertTrue(decorador.telemetria().get(0).contains("EN_MISION")),
                    () -> assertTrue(decorador.telemetria().get(1).contains("RECARGANDO"))
            );
        }

        @Test
        @DisplayName("Registra cambios de batería")
        void registraCambiosDeBateria() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            decorador.setBateria(75);
            assertTrue(decorador.telemetria().get(0).contains("90"));
            assertTrue(decorador.telemetria().get(0).contains("75"));
        }

        @Test
        @DisplayName("La telemetría es inmutable desde fuera")
        void telemetriaInmutable() {
            DroneConMonitoreo decorador = new DroneConMonitoreo(drone);
            decorador.setEstado(EstadoDrone.EN_MISION);
            assertThrows(UnsupportedOperationException.class,
                    () -> decorador.telemetria().add("evento falso"));
        }

        @Test
        @DisplayName("El comportamiento del envuelto NO se altera")
        void noAlteraComportamiento() {
            // Sin decorador
            DroneOperable sinDecorador = TestFixtures.superficial("AR-01", 90,
                    EstadoDrone.DISPONIBLE, "Red de Canales");
            sinDecorador.setEstado(EstadoDrone.EN_MISION);
            sinDecorador.setBateria(50);

            // Con decorador
            DroneOperable decorado = TestFixtures.superficial("AR-01", 90,
                    EstadoDrone.DISPONIBLE, "Red de Canales");
            DroneConMonitoreo conMonitoreo = new DroneConMonitoreo(decorado);
            conMonitoreo.setEstado(EstadoDrone.EN_MISION);
            conMonitoreo.setBateria(50);

            assertAll(
                    () -> assertEquals(sinDecorador.estado(), decorado.estado()),
                    () -> assertEquals(sinDecorador.bateria(), decorado.bateria())
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DroneConCifrado
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DroneConCifrado — añade marca de cifrado")
    class Cifrado {

        @Test
        @DisplayName("estaCifrado retorna true")
        void estaCifrado() {
            DroneConCifrado decorador = new DroneConCifrado(drone);
            assertTrue(decorador.estaCifrado());
        }

        @Test
        @DisplayName("algoritmoCifrado retorna AES-256")
        void algoritmo() {
            DroneConCifrado decorador = new DroneConCifrado(drone);
            assertEquals("AES-256", decorador.algoritmoCifrado());
        }

        @Test
        @DisplayName("Delega las operaciones en el envuelto")
        void delegaOperaciones() {
            DroneConCifrado decorador = new DroneConCifrado(drone);
            assertEquals(drone.id(), decorador.id());
            assertEquals(drone.tipo(), decorador.tipo());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Apilamiento de decoradores
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Apilamiento de decoradores")
    class Apilamiento {

        @Test
        @DisplayName("Cifrado + Monitoreo en cualquier orden")
        void apilar() {
            DroneOperable base = TestFixtures.buceador("AR-01", 90,
                    EstadoDrone.DISPONIBLE, "Embalse de Investigación");

            DroneConMonitoreo monitoreo = new DroneConMonitoreo(base);
            DroneConCifrado cifrado = new DroneConCifrado(monitoreo);

            cifrado.setEstado(EstadoDrone.EN_MISION);

            assertAll(
                    () -> assertTrue(cifrado.estaCifrado()),
                    () -> assertEquals("AR-01", cifrado.id()),
                    () -> assertEquals(EstadoDrone.EN_MISION, base.estado()),
                    () -> assertEquals(1, monitoreo.cantidadEventos())
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Validación del constructor
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validaciones")
    class Validaciones {

        @Test
        @DisplayName("Envolver null lanza IllegalArgumentException")
        void envueltoNulo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneConMonitoreo(null));
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneConCifrado(null));
        }
    }
}