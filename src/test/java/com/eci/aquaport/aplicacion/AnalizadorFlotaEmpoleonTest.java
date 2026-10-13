package com.eci.aquaport.aplicacion;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.DroneSemisumergido;
import com.eci.aquaport.dominio.DroneSuperficial;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnalizadorFlotaEmpoleonTest {

    private AnalizadorFlotaEmpoleon analizador;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        analizador = new AnalizadorFlotaEmpoleon();
        flota = TestFixtures.flotaEmpoleon60();
    }

    private Mision mision(String id, String partida, String llegada,
                          EstadoMision estado, Prioridad prioridad) {
        DroneAcuatico drone = new DroneBuceador(
                "AR-01", "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, partida);
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
    // CONSULTAS 1-5 — Heredadas
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consultas heredadas de Prinplup")
    class Heredadas {

        @Test
        @DisplayName("Consulta 1: agrupa disponibles por tipo")
        void consulta1() {
            Map<TipoDrone, List<DroneAcuatico>> agrupados =
                    analizador.agruparDisponiblesPorTipo(flota);
            assertAll(
                    () -> assertFalse(agrupados.isEmpty()),
                    () -> assertTrue(agrupados.values().stream()
                            .flatMap(List::stream)
                            .allMatch(DroneAcuatico::estaDisponible))
            );
        }

        @Test
        @DisplayName("Consulta 2: drone óptimo para zona con candidatos")
        void consulta2() {
            Optional<DroneAcuatico> optimo =
                    analizador.droneOptimoParaZona(flota, "Embalse de Investigación");
            optimo.ifPresent(d -> assertTrue(d.bateria() >= 35));
        }

        @Test
        @DisplayName("Consulta 3: promedio de batería por tipo")
        void consulta3() {
            Map<TipoDrone, Double> promedios =
                    analizador.promedioBateriaPorTipo(flota);
            assertAll(
                    () -> assertTrue(promedios.containsKey(TipoDrone.SUPERFICIAL)),
                    () -> assertTrue(promedios.containsKey(TipoDrone.SEMISUMERGIDO)),
                    () -> assertTrue(promedios.containsKey(TipoDrone.BUCEADOR)),
                    () -> assertTrue(promedios.get(TipoDrone.SUPERFICIAL) >= 0)
            );
        }

        @Test
        @DisplayName("Consulta 4: separa misiones críticas")
        void consulta4() {
            var misiones = List.of(
                    mision("M-01", "A", "B", EstadoMision.PENDIENTE, Prioridad.CRITICA),
                    mision("M-02", "A", "B", EstadoMision.PENDIENTE, Prioridad.NORMAL)
            );
            Map<Boolean, List<Mision>> separadas =
                    analizador.separarMisionesCriticas(misiones);
            assertAll(
                    () -> assertEquals(1, separadas.get(true).size()),
                    () -> assertEquals(1, separadas.get(false).size())
            );
        }

        @Test
        @DisplayName("Consulta 5: zonas únicas de flota activa")
        void consulta5() {
            Set<String> zonas = analizador.zonasUnicasDeFlotaActiva(flota);
            assertFalse(zonas.isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 6 — Eficiencia por zona
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 6 — eficienciaPorZona")
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
        @DisplayName("Lista vacía retorna map vacío")
        void listaVacia() {
            assertTrue(analizador.eficienciaPorZona(List.of()).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 7 — Drone con mejor historial
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 7 — droneConMejorHistorial")
    class MejorHistorial {

        @Test
        @DisplayName("Retorna el drone con mayor ratio de éxito")
        void retornaMejorRatio() {
            var d1 = new DroneBuceador("AR-01", "M", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 9, 1);   // ratio 0.9
            var d2 = new DroneBuceador("AR-02", "M", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 5, 5);   // ratio 0.5
            var d3 = new DroneBuceador("AR-03", "M", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 8, 2);   // ratio 0.8

            Optional<DroneAcuatico> mejor =
                    analizador.droneConMejorHistorial(List.of(d1, d2, d3), 5);

            assertTrue(mejor.isPresent());
            assertEquals("AR-01", mejor.get().id());
        }

        @Test
        @DisplayName("Sin drones que cumplan el mínimo retorna empty")
        void sinMinimoCumplido() {
            var d1 = new DroneBuceador("AR-01", "M", 90,
                    EstadoDrone.DISPONIBLE, "Zona", 1, 0);   // total 1
            assertTrue(analizador.droneConMejorHistorial(List.of(d1), 10).isEmpty());
        }

        @Test
        @DisplayName("Mínimo negativo lanza IllegalArgumentException")
        void minimoNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> analizador.droneConMejorHistorial(flota, -1));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 8 — Planificación de ruta multi-etapa
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 8 — planificarRutaMultiEtapa")
    class PlanificarRuta {

        @Test
        @DisplayName("Asigna un drone por cada tramo")
        void asignaPorTramo() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("Embalse de Investigación"),
                    new Waypoint("Laguna de Reserva", 1),
                    new Waypoint("Laboratorio Hídrico Central", 2)));

            Map<Integer, Optional<DroneAcuatico>> plan =
                    analizador.planificarRutaMultiEtapa(flota, ruta);

            assertAll(
                    () -> assertEquals(2, plan.size()),
                    () -> assertTrue(plan.containsKey(0)),
                    () -> assertTrue(plan.containsKey(1))
            );
        }

        @Test
        @DisplayName("Ruta nula lanza IllegalArgumentException")
        void rutaNula() {
            assertThrows(IllegalArgumentException.class,
                    () -> analizador.planificarRutaMultiEtapa(flota, null));
        }

        @Test
        @DisplayName("Sin candidatos en la flota retorna Optional.empty por tramo")
        void sinCandidatos() {
            Ruta ruta = new Ruta(List.of(
                    Waypoint.origen("A"),
                    new Waypoint("B", 1)));
            var flotaVacia = List.<DroneAcuatico>of();
            var plan = analizador.planificarRutaMultiEtapa(flotaVacia, ruta);
            assertTrue(plan.get(0).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 9 — Resumen por zona con Collector personalizado
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 9 — resumenPorZona")
    class Resumen {

        @Test
        @DisplayName("Agrupa por zona y calcula agregados")
        void resumenCorrecto() {
            Map<String, AnalizadorFlotaEmpoleon.ResumenZona> resumen =
                    analizador.resumenPorZona(flota);

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
            assertTrue(analizador.resumenPorZona(List.of()).isEmpty());
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
            assertEquals(60, flota.size());
        }

        @Test
        @DisplayName("Distribuye entre los 3 tipos")
        void distribuyePorTipo() {
            long superficiales = flota.stream()
                    .filter(d -> d.tipo() == TipoDrone.SUPERFICIAL).count();
            long semisumergidos = flota.stream()
                    .filter(d -> d.tipo() == TipoDrone.SEMISUMERGIDO).count();
            long buceadores = flota.stream()
                    .filter(d -> d.tipo() == TipoDrone.BUCEADOR).count();

            assertAll(
                    () -> assertEquals(20, superficiales),
                    () -> assertEquals(20, semisumergidos),
                    () -> assertEquals(20, buceadores)
            );
        }
    }
}