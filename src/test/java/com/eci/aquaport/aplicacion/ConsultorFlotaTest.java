package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = List.of(
            new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92,
                    EstadoDrone.DISPONIBLE,    "Embalse Norte",       TipoDrone.BUCEADOR),
            new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45,
                    EstadoDrone.DISPONIBLE,    "Canal Central",       TipoDrone.SEMISUMERGIDO),
            new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18,
                    EstadoDrone.MANTENIMIENTO, "Laguna Sur",          TipoDrone.BUCEADOR),
            new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73,
                    EstadoDrone.DISPONIBLE,    "Punto Ribereño Este", TipoDrone.SUPERFICIAL)
        );
    }

    @Nested
    @DisplayName("listaDronesDisponiblesOrdenados")
    class ListaDisponiblesOrdenados {

        @Test
        @DisplayName("Caso normal: filtra disponibles ≥35% y ordena desc por batería")
        void casoNormal() {
            var resultado = consultor.listaDronesDisponiblesOrdenados(flota);
            assertAll(
                () -> assertEquals(3, resultado.size()),
                () -> assertEquals("AR-01", resultado.get(0).id()),
                () -> assertEquals("AR-04", resultado.get(1).id()),
                () -> assertEquals("AR-02", resultado.get(2).id())
            );
        }

        @Test
        @DisplayName("Flota vacía retorna lista vacía")
        void flotaVacia() {
            assertTrue(consultor.listaDronesDisponiblesOrdenados(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Caso edge: batería exactamente en 35% SÍ se incluye")
        void bateriaEnElUmbral() {
            var enElUmbral = List.of(
                new DroneAcuatico("AR-UMBRAL", "Aqua-Ranger 100", 35,
                        EstadoDrone.DISPONIBLE, "Zona", TipoDrone.SUPERFICIAL));
            var resultado = consultor.listaDronesDisponiblesOrdenados(enElUmbral);
            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("listaDronesDisponiblesId")
    class ListaDisponiblesId {

        @Test
        @DisplayName("Caso normal: retorna solo IDs de disponibles")
        void casoNormal() {
            assertEquals(List.of("AR-01", "AR-02", "AR-04"),
                    consultor.listaDronesDisponiblesId(flota));
        }
    }

    @Nested
    @DisplayName("existeDronDisponibleConBateria")
    class ExisteDisponible {

        @Test
        @DisplayName("Retorna true si al menos uno cumple")
        void existe() {
            assertTrue(consultor.existeDronDisponibleConBateria(flota));
        }

        @Test
        @DisplayName("Retorna false si todos están por debajo del umbral")
        void ningunoConBateria() {
            var sinBateria = List.of(
                new DroneAcuatico("AR-X", "Aqua-Ranger 100", 10,
                        EstadoDrone.DISPONIBLE, "Zona", TipoDrone.SUPERFICIAL));
            assertFalse(consultor.existeDronDisponibleConBateria(sinBateria));
        }
    }

    @Nested
    @DisplayName("cantidadDronDisponible")
    class Cantidad {

        @Test
        @DisplayName("Cuenta correctamente los disponibles")
        void cuenta() {
            assertEquals(3L, consultor.cantidadDronDisponible(flota));
        }
    }

    @Nested
    @DisplayName("dronMayorBateria")
    class MayorBateria {

        @Test
        @DisplayName("Retorna el drone con mayor batería")
        void retornaMayor() {
            Optional<DroneAcuatico> mayor = consultor.dronMayorBateria(flota);
            assertTrue(mayor.isPresent());
            assertEquals("AR-01", mayor.get().id());
        }

        @Test
        @DisplayName("Flota vacía retorna Optional.empty")
        void flotaVacia_empty() {
            assertTrue(consultor.dronMayorBateria(List.of()).isEmpty());
        }
    }
}