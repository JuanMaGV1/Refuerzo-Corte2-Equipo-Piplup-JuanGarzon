package com.eci.aquaport.aplicacion;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.Ruta;
import com.eci.aquaport.dominio.TipoCarga;
import com.eci.aquaport.dominio.TipoDrone;
import com.eci.aquaport.dominio.Waypoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnalizadorFlotaEmpoleonTest {

    private AnalizadorFlotaEmpoleon analizador;
    private List<DroneAcuatico> flota60;

    @BeforeEach
    void setUp() {
        analizador = new AnalizadorFlotaEmpoleon();
        flota60 = TestFixtures.flotaEmpoleon60();
    }

    private Mision mision(String id, String partida, String llegada,
                          EstadoMision estado, Prioridad prioridad) {
        DroneAcuatico drone = TestFixtures.buceador(
                "AR-00", 90, EstadoDrone.DISPONIBLE, partida);
        return new Mision.Builder()
                .id(id).drone(drone)
                .puntoPartida(partida).puntoLlegada(llegada)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .cargaGramos(200)
                .estadoMision(estado)
                .prioridad(prioridad)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 1 — eficienciaPorZona
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 1 — eficienciaPorZona")
    class Eficiencia {

        @Test
        @DisplayName("Calcula la eficiencia correctamente")
        void calculaEficiencia() {
            var misiones = List.of(
                    mision("M-01", "A", "ZonaX", EstadoMision.ENTREGADA, Prioridad.NORMAL),
                    mision("M-02", "A", "ZonaX", EstadoMision.ENTREGADA, Prioridad.NORMAL),
                    mision("M-03", "A", "ZonaX", EstadoMision.FALLIDA,   Prioridad.NORMAL),
                    mision("M-04", "A", "ZonaX", EstadoMision.FALLIDA,   Prioridad.NORMAL)
            );
            Map<String, Double> eficiencia = analizador.eficienciaPorZona(misiones);
            assertEquals(0.5, eficiencia.get("ZonaX"), 0.0001);
        }

        @Test
        @DisplayName("Zona con todas entregadas retorna 1.0")
        void todasEntregadas() {
            var misiones = List.of(
                    mision("M-01", "A", "ZonaX", EstadoMision.ENTREGADA, Prioridad.NORMAL),
                    mision("M-02", "A", "ZonaX", EstadoMision.ENTREGADA, Prioridad.NORMAL)
            );
            assertEquals(1.0, analizador.eficienciaPorZona(misiones).get("ZonaX"), 0.0001);
        }

        @Test
        @DisplayName("Múltiples zonas calculadas independientemente")
        void multiplesZonas() {
            var misiones = List.of(
                    mision("M-01", "A", "ZonaX", EstadoMision.ENTREGADA, Prioridad.NORMAL),
                    mision("M-02", "A", "ZonaY", EstadoMision.FALLIDA,   Prioridad.NORMAL),
                    mision("M-03", "A", "ZonaY", EstadoMision.ENTREGADA, Prioridad.NORMAL)
            );
            Map<String, Double> eficiencia = analizador.eficienciaPorZona(misiones);
            assertAll(
                    () -> assertEquals(1.0, eficiencia.get("ZonaX"), 0.0001),
                    () -> assertEquals(0.5, eficiencia.get("ZonaY"), 0.0001)
            );
        }

        @Test
        @DisplayName("Lista vacía retorna map vacío")
        void listaVacia() {
            assertTrue(analizador.eficienciaPorZona(List.of()).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 2 — droneConMejorHistorial
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 2 — droneConMejorHistorial")
    class MejorHistorial {

        @Test
        @DisplayName("Retorna el drone con mayor ratio de éxito")
        void retornaMejorRatio() {
            var d1 = TestFixtures.droneConHistorial(
                    TipoDrone.BUCEADOR,
                    "AR-01", 90, EstadoDrone.DISPONIBLE, "Zona", 9, 1);   // 0.9
            var d2 = TestFixtures.droneConHistorial(
                    TipoDrone.BUCEADOR,
                    "AR-02", 90, EstadoDrone.DISPONIBLE, "Zona", 5, 5);   // 0.5
            var d3 = TestFixtures.droneConHistorial(
                    TipoDrone.BUCEADOR,
                    "AR-03", 90, EstadoDrone.DISPONIBLE, "Zona", 8, 2);   // 0.8

            Optional<DroneAcuatico> mejor =
                    analizador.droneConMejorHistorial(List.of(d1, d2, d3), 5);

            assertTrue(mejor.isPresent());
            assertEquals("AR-01", mejor.get().id());
        }

        @Test
        @DisplayName("Empate en ratio: desempata por ID ascendente (AR-01 gana a AR-99)")
        void empateEnRatio() {
            var d1 = TestFixtures.droneConHistorial(
                    TipoDrone.BUCEADOR,
                    "AR-01", 90, EstadoDrone.DISPONIBLE, "Zona", 8, 2);   // 0.8
            var d2 = TestFixtures.droneConHistorial(
                    TipoDrone.BUCEADOR,
                    "AR-99", 90, EstadoDrone.DISPONIBLE, "Zona", 8, 2);   // 0.8

            Optional<DroneAcuatico> mejor =
                    analizador.droneConMejorHistorial(List.of(d1, d2), 5);

            assertTrue(mejor.isPresent());
            assertEquals("AR-01", mejor.get().id(),
                    "Empate en ratio: ID menor gana (orden ascendente)");
        }

        @Test
        @DisplayName("Sin drones que cumplan el mínimo retorna empty")
        void sinMinimoCumplido() {
            var d1 = TestFixtures.buceadorConHistorial("AR-01", 1, 0);
            assertTrue(analizador.droneConMejorHistorial(List.of(d1), 10).isEmpty());
        }

        @Test
        @DisplayName("Mínimo negativo lanza IllegalArgumentException")
        void minimoNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> analizador.droneConMejorHistorial(flota60, -1));
        }

        @Test
        @DisplayName("Mínimo cero incluye drones sin misiones")
        void minimoCero() {
            var sinMisiones = TestFixtures.buceador("AR-X", 90,
                    EstadoDrone.DISPONIBLE, "Zona");
            assertTrue(analizador.droneConMejorHistorial(List.of(sinMisiones), 0).isPresent());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 3 — planificarRutaMultiEtapa
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 3 — planificarRutaMultiEtapa")
    class PlanificarRuta {

        @Test
        @DisplayName("Asigna un drone por cada tramo")
        void asignaPorTramo() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("Embalse de Investigación"),
                    new Waypoint("Laguna de Reserva", 1),
                    new Waypoint("Laboratorio Hídrico Central", 2)));

            Map<Integer, Optional<DroneAcuatico>> plan =
                    analizador.planificarRutaMultiEtapa(flota60, ruta);

            assertAll(
                    () -> assertEquals(2, plan.size()),
                    () -> assertTrue(plan.containsKey(0)),
                    () -> assertTrue(plan.containsKey(1))
            );
        }

        @Test
        @DisplayName("No asigna el mismo drone a dos tramos")
        void noRepiteDroneEnDosTramos() {
            // AR-UNICO: BUCEADOR disponible en Embalse de Investigación,
            // compatible con Laguna de Reserva. Ideal para el tramo 0.
            // AR-FB: SEMISUMERGIDO disponible en Laguna de Reserva,
            // compatible con Laboratorio Hídrico Central. Fallback para tramo 1.
            DroneAcuatico unico = TestFixtures.buceador(
                    "AR-UNICO", 90, EstadoDrone.DISPONIBLE, "Embalse de Investigación");
            DroneAcuatico fallback = TestFixtures.semisumergido(
                    "AR-FB", 80, EstadoDrone.DISPONIBLE, "Laguna de Reserva");

            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("Embalse de Investigación"),
                    new Waypoint("Laguna de Reserva", 1),
                    new Waypoint("Laboratorio Hídrico Central", 2)));

            var plan = analizador.planificarRutaMultiEtapa(
                    List.of(unico, fallback), ruta);

            assertAll(
                    () -> assertTrue(plan.get(0).isPresent(),
                            "Tramo 0 debe tener drone asignado"),
                    () -> assertTrue(plan.get(1).isPresent(),
                            "Tramo 1 debe tener drone asignado"),
                    () -> assertEquals("AR-UNICO", plan.get(0).get().id(),
                            "Tramo 0: AR-UNICO está en la zona origen"),
                    () -> assertEquals("AR-FB", plan.get(1).get().id(),
                            "Tramo 1: AR-FB es fallback porque AR-UNICO ya está asignado")
            );
        }

        @Test
        @DisplayName("Ruta nula lanza IllegalArgumentException")
        void rutaNula() {
            assertThrows(IllegalArgumentException.class,
                    () -> analizador.planificarRutaMultiEtapa(flota60, null));
        }

        @Test
        @DisplayName("Sin candidatos en la flota retorna Optional.empty por tramo")
        void sinCandidatos() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("Red de Canales"),
                    new Waypoint("Laboratorio Hídrico Central", 1)));
            var plan = analizador.planificarRutaMultiEtapa(TestFixtures.flotaVacia(), ruta);
            assertTrue(plan.get(0).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 4 — resumenPorZona
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 4 — resumenPorZona")
    class Resumen {

        @Test
        @DisplayName("Agrupa por zona y calcula agregados")
        void resumenCorrecto() {
            Map<String, AnalizadorFlotaEmpoleon.ResumenZona> resumen =
                    analizador.resumenPorZona(flota60);

            assertAll(
                    () -> assertFalse(resumen.isEmpty()),
                    () -> resumen.values().forEach(r -> {
                        assertTrue(r.cantidadDrones() > 0);
                        assertTrue(r.promedioBateria() >= 0);
                        assertTrue(r.cantidadDisponibles() >= 0);
                        assertTrue(r.cantidadDisponibles() <= r.cantidadDrones());
                    })
            );
        }

        @Test
        @DisplayName("Flota vacía retorna map vacío")
        void flotaVacia() {
            assertTrue(analizador.resumenPorZona(TestFixtures.flotaVacia()).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Fixture de Empoleon
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Fixture de 60 drones")
    class Fixture {

        @Test
        @DisplayName("Genera exactamente 60 drones")
        void genera60() {
            assertEquals(60, flota60.size());
        }

        @Test
        @DisplayName("Distribuye entre los 3 tipos en partes iguales")
        void distribuyePorTipo() {
            long superficiales = flota60.stream()
                    .filter(d -> d.tipo() == TipoDrone.SUPERFICIAL)
                    .count();
            long semisumergidos = flota60.stream()
                    .filter(d -> d.tipo() == TipoDrone.SEMISUMERGIDO)
                    .count();
            long buceadores = flota60.stream()
                    .filter(d -> d.tipo() == TipoDrone.BUCEADOR)
                    .count();

            assertAll(
                    () -> assertEquals(20, superficiales),
                    () -> assertEquals(20, semisumergidos),
                    () -> assertEquals(20, buceadores)
            );
        }
    }
}