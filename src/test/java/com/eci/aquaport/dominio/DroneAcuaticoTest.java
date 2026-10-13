package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneAcuaticoTest {

    @Nested
    @DisplayName("Constructor base (sin historial)")
    class ConstructorBase {

        @Test
        @DisplayName("Los contadores inician en 0")
        void contadoresEnCero() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-01", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Embalse de Investigación");

            assertAll(
                    () -> assertEquals(0, d.entregasExitosas()),
                    () -> assertEquals(0, d.entregasFallidas()),
                    () -> assertEquals(0, d.totalMisiones()),
                    () -> assertEquals(0.0, d.ratioExito())
            );
        }
    }

    @Nested
    @DisplayName("Constructor extendido (con historial)")
    class ConstructorExtendido {

        @Test
        @DisplayName("Preserva los contadores pasados")
        void preservaContadores() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-02", "Aqua-Ranger 200", 85,
                    EstadoDrone.DISPONIBLE, "Embalse de Investigación",
                    30, 5);

            assertAll(
                    () -> assertEquals(30, d.entregasExitosas()),
                    () -> assertEquals(5, d.entregasFallidas()),
                    () -> assertEquals(35, d.totalMisiones())
            );
        }

        @Test
        @DisplayName("Contador negativo lanza IllegalArgumentException")
        void contadorNegativo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneBuceador(
                            "AR-03", "Aqua-Ranger 200", 80,
                            EstadoDrone.DISPONIBLE, "Zona",
                            -1, 0));
        }
    }

    @Nested
    @DisplayName("ratioExito()")
    class RatioExito {

        @Test
        @DisplayName("Sin misiones retorna 0.0")
        void sinMisiones_retornaCero() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-04", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona");
            assertEquals(0.0, d.ratioExito());
        }

        @Test
        @DisplayName("Todas exitosas retorna 1.0")
        void todasExitosas() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-05", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 20, 0);
            assertEquals(1.0, d.ratioExito());
        }

        @Test
        @DisplayName("Mitad exitosas retorna 0.5")
        void mitadExitosas() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-06", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 10, 10);
            assertEquals(0.5, d.ratioExito());
        }

        @Test
        @DisplayName("Ratio calculado correctamente con valores mixtos")
        void ratioMixto() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-07", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 75, 25);
            assertEquals(0.75, d.ratioExito(), 0.0001);
        }
    }

    @Nested
    @DisplayName("Registro de entregas")
    class RegistroEntregas {

        @Test
        @DisplayName("registrarEntregaExitosa incrementa el contador")
        void incrementaExitosas() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-08", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona");
            d.registrarEntregaExitosa();
            d.registrarEntregaExitosa();
            assertEquals(2, d.entregasExitosas());
        }

        @Test
        @DisplayName("registrarEntregaFallida incrementa el contador")
        void incrementaFallidas() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-09", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona");
            d.registrarEntregaFallida();
            assertEquals(1, d.entregasFallidas());
        }

        @Test
        @DisplayName("Los dos contadores se acumulan independientemente")
        void acumulacionIndependiente() {
            DroneAcuatico d = new DroneBuceador(
                    "AR-10", "Aqua-Ranger 200", 90,
                    EstadoDrone.DISPONIBLE, "Zona");
            d.registrarEntregaExitosa();
            d.registrarEntregaExitosa();
            d.registrarEntregaFallida();

            assertAll(
                    () -> assertEquals(2, d.entregasExitosas()),
                    () -> assertEquals(1, d.entregasFallidas()),
                    () -> assertEquals(3, d.totalMisiones())
            );
        }
    }

    @Nested
    @DisplayName("Validaciones del constructor")
    class Validaciones {

        @Test
        @DisplayName("Id nulo lanza IllegalArgumentException")
        void idNulo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneBuceador(null, "Modelo", 90,
                            EstadoDrone.DISPONIBLE, "Zona"));
        }

        @Test
        @DisplayName("Batería fuera de rango lanza IllegalArgumentException")
        void bateriaFueraDeRango_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneBuceador("AR-X", "Modelo", 101,
                            EstadoDrone.DISPONIBLE, "Zona"));
        }

        @Test
        @DisplayName("Estado nulo lanza IllegalArgumentException")
        void estadoNulo_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneBuceador("AR-X", "Modelo", 90,
                            null, "Zona"));
        }

        @Test
        @DisplayName("Zona vacía lanza IllegalArgumentException")
        void zonaVacia_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DroneBuceador("AR-X", "Modelo", 90,
                            EstadoDrone.DISPONIBLE, "   "));
        }
    }
}