package piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import piplup.ConsultorFlota;
import piplup.DroneAcuatico;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        // Flota de ejemplo del enunciado MVP
        flota = List.of(
            new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true,  "Embalse Norte"),
            new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true,  "Canal Central"),
            new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
            new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true,  "Punto Ribereño Este")
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // listaDronesDisponiblesOrdenados
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("listaDronesDisponiblesOrdenados")
    class ListaDisponiblesOrdenados {

        @Test
        @DisplayName("Caso normal: filtra disponibles ≥35% y ordena desc por batería")
        void casoNormal() {
            var resultado = consultor.listaDronesDisponiblesOrdenados(flota);
            assertAll(
                () -> assertEquals(3, resultado.size(), "Deben ser 3 (AR-03 no disponible)"),
                () -> assertEquals("AR-01", resultado.get(0).id(), "Primero el de mayor batería (92%)"),
                () -> assertEquals("AR-04", resultado.get(1).id(), "Segundo el de 73%"),
                () -> assertEquals("AR-02", resultado.get(2).id(), "Último el de 45%")
            );
        }

        @Test
        @DisplayName("Flota vacía: retorna lista vacía sin excepción")
        void flotaVacia() {
            var resultado = consultor.listaDronesDisponiblesOrdenados(List.of());
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Ningún drone disponible: retorna lista vacía")
        void ningunoDisponible() {
            var todosNoDisponibles = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 90, false, "Zona"),
                new DroneAcuatico("AR-X2", "Aqua-Ranger 100", 80, false, "Zona")
            );
            assertTrue(consultor.listaDronesDisponiblesOrdenados(todosNoDisponibles).isEmpty());
        }

        @Test
        @DisplayName("Disponibles pero todos con batería <35%: retorna lista vacía")
        void disponiblesPeroSinBateria() {
            var sinBateria = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 10, true, "Zona"),
                new DroneAcuatico("AR-X2", "Aqua-Ranger 100", 34, true, "Zona")
            );
            assertTrue(consultor.listaDronesDisponiblesOrdenados(sinBateria).isEmpty());
        }

        @Test
        @DisplayName("Caso edge: batería exactamente en 35% SÍ se incluye")
        void bateriaEnElUmbral() {
            var enElUmbral = List.of(
                new DroneAcuatico("AR-UMBRAL", "Aqua-Ranger 100", 35, true, "Zona")
            );
            var resultado = consultor.listaDronesDisponiblesOrdenados(enElUmbral);
            assertEquals(1, resultado.size(), "35% es el mínimo válido");
            assertEquals("AR-UMBRAL", resultado.get(0).id());
        }

        @Test
        @DisplayName("Caso edge: batería en 34% NO se incluye")
        void bateriaJustoDebajoDelUmbral() {
            var justoDebajo = List.of(
                new DroneAcuatico("AR-CASI", "Aqua-Ranger 100", 34, true, "Zona")
            );
            assertTrue(consultor.listaDronesDisponiblesOrdenados(justoDebajo).isEmpty(),
                    "34% está por debajo del umbral");
        }

        @Test
        @DisplayName("La lista retornada es inmutable")
        void listaRetornadaEsInmutable() {
            var resultado = consultor.listaDronesDisponiblesOrdenados(flota);
            DroneAcuatico drone = flota.get(0);            

            assertThrows(UnsupportedOperationException.class,
                    () -> resultado.add(drone));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // listaDronesDisponiblesId
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("listaDronesDisponiblesId")
    class ListaDisponiblesId {

        @Test
        @DisplayName("Caso normal: retorna solo IDs de disponibles")
        void casoNormal() {
            assertEquals(List.of("AR-01", "AR-02", "AR-04"),
                    consultor.listaDronesDisponiblesId(flota));
        }

        @Test
        @DisplayName("Flota vacía: retorna lista vacía")
        void flotaVacia() {
            assertTrue(consultor.listaDronesDisponiblesId(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Ninguno disponible: retorna lista vacía")
        void ningunoDisponible() {
            var todosNoDisponibles = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 90, false, "Zona")
            );
            assertTrue(consultor.listaDronesDisponiblesId(todosNoDisponibles).isEmpty());
        }

        @Test
        @DisplayName("No filtra por batería, solo por disponibilidad")
        void noFiltraPorBateria() {
            // Un drone disponible con batería baja (10%) SÍ debe aparecer en IDs
            var bateriaBaja = List.of(
                new DroneAcuatico("AR-BAJO", "Aqua-Ranger 100", 10, true, "Zona")
            );
            assertEquals(List.of("AR-BAJO"), consultor.listaDronesDisponiblesId(bateriaBaja));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // existeDronDisponibleConBateria
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("existeDronDisponibleConBateria")
    class ExisteDisponible {

        @Test
        @DisplayName("Retorna true si al menos uno cumple (disponible + batería ≥35%)")
        void existe() {
            assertTrue(consultor.existeDronDisponibleConBateria(flota));
        }

        @Test
        @DisplayName("Retorna false si todos están por debajo del umbral")
        void ningunoConBateria() {
            var sinBateria = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 10, true, "Zona"),
                new DroneAcuatico("AR-X2", "Aqua-Ranger 100", 20, true, "Zona")
            );
            assertFalse(consultor.existeDronDisponibleConBateria(sinBateria));
        }

        @Test
        @DisplayName("Retorna false si los que tienen batería no están disponibles")
        void conBateriaPeroNoDisponibles() {
            var noDisponibles = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 90, false, "Zona"),
                new DroneAcuatico("AR-X2", "Aqua-Ranger 100", 80, false, "Zona")
            );
            assertFalse(consultor.existeDronDisponibleConBateria(noDisponibles));
        }

        @Test
        @DisplayName("Flota vacía: retorna false")
        void flotaVacia() {
            assertFalse(consultor.existeDronDisponibleConBateria(List.of()));
        }

        @Test
        @DisplayName("Caso edge: exactamente 35% disponible retorna true")
        void bateriaEnElUmbral() {
            var enElUmbral = List.of(
                new DroneAcuatico("AR-UMBRAL", "Aqua-Ranger 100", 35, true, "Zona")
            );
            assertTrue(consultor.existeDronDisponibleConBateria(enElUmbral));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // cantidadDronDisponible
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("cantidadDronDisponible")
    class Cantidad {

        @Test
        @DisplayName("Cuenta 3 disponibles de 4 en la flota de ejemplo")
        void casoNormal() {
            assertEquals(3L, consultor.cantidadDronDisponible(flota));
        }

        @Test
        @DisplayName("Flota vacía: retorna 0")
        void flotaVacia() {
            assertEquals(0L, consultor.cantidadDronDisponible(List.of()));
        }

        @Test
        @DisplayName("Todos no disponibles: retorna 0")
        void ningunoDisponible() {
            var noDisponibles = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 90, false, "Zona")
            );
            assertEquals(0L, consultor.cantidadDronDisponible(noDisponibles));
        }

        @Test
        @DisplayName("Todos disponibles: retorna el total")
        void todosDisponibles() {
            var disponibles = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Zona"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 80, true, "Zona")
            );
            assertEquals(2L, consultor.cantidadDronDisponible(disponibles));
        }

        @Test
        @DisplayName("No filtra por batería, solo por disponibilidad")
        void noFiltraPorBateria() {
            var bateriaBaja = List.of(
                new DroneAcuatico("AR-BAJO", "Aqua-Ranger 100", 5, true, "Zona")
            );
            assertEquals(1L, consultor.cantidadDronDisponible(bateriaBaja));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // dronMayorBateria
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("dronMayorBateria")
    class MayorBateria {

        @Test
        @DisplayName("Retorna el drone con mayor batería de la flota")
        void retornaMayor() {
            Optional<DroneAcuatico> mayor = consultor.dronMayorBateria(flota);
            assertAll(
                () -> assertTrue(mayor.isPresent()),
                () -> assertEquals("AR-01", mayor.get().id()),
                () -> assertEquals(92, mayor.get().bateria())
            );
        }

        @Test
        @DisplayName("Retorna el drone aunque no esté disponible")
        void mayorNoDisponible() {
            var noDisponible = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 50, true,  "Zona"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 90, false, "Zona")
            );
            var mayor = consultor.dronMayorBateria(noDisponible);
            assertTrue(mayor.isPresent());
            assertEquals("AR-02", mayor.get().id(),
                    "Debe retornar el de mayor batería sin importar disponibilidad");
        }

        @Test
        @DisplayName("Flota vacía retorna Optional.empty")
        void flotaVacia() {
            assertTrue(consultor.dronMayorBateria(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Flota con un solo drone retorna ese drone")
        void unSoloDrone() {
            var uno = List.of(
                new DroneAcuatico("AR-SOLO", "Aqua-Ranger 100", 60, true, "Zona")
            );
            var mayor = consultor.dronMayorBateria(uno);
            assertTrue(mayor.isPresent());
            assertEquals("AR-SOLO", mayor.get().id());
        }

        @Test
        @DisplayName("Empate en batería máxima: retorna cualquiera de los dos")
        void empate() {
            var empate = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 80, true, "Zona"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 80, true, "Zona")
            );
            var mayor = consultor.dronMayorBateria(empate);
            assertTrue(mayor.isPresent());
            assertEquals(80, mayor.get().bateria(), "Batería máxima debe ser 80%");
        }
    }
}