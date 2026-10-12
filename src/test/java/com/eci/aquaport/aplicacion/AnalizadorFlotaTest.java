package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnalizadorFlotaTest {

    private AnalizadorFlota analizador;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        analizador = new AnalizadorFlota();

        // Flota representativa de Prinplup.
        // Incluye casos edge: drones en distintos estados, tipos y zonas.
        // AR-01 es SUPERFICIAL en Embalse Norte (zona incompatible con su tipo)
        //  → sirve para probar que la consulta 2 filtra por TIPO, no por ubicación.
        flota = List.of(
            new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92,
                    EstadoDrone.DISPONIBLE,    "Embalse Norte",       TipoDrone.SUPERFICIAL),
            new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45,
                    EstadoDrone.DISPONIBLE,    "Canal Central",       TipoDrone.SEMISUMERGIDO),
            new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18,
                    EstadoDrone.MANTENIMIENTO, "Laguna Sur",          TipoDrone.BUCEADOR),
            new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73,
                    EstadoDrone.DISPONIBLE,    "Punto Ribereño Este", TipoDrone.SUPERFICIAL),
            new DroneAcuatico("AR-05", "Aqua-Ranger 100", 88,
                    EstadoDrone.EN_MISION,     "Embalse Norte",       TipoDrone.BUCEADOR),
            new DroneAcuatico("AR-06", "Aqua-Ranger 100", 55,
                    EstadoDrone.DISPONIBLE,    "Embalse Norte",       TipoDrone.BUCEADOR)
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 1 — agruparDisponiblesPorTipo
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 1 — agruparDisponiblesPorTipo")
    class AgruparDisponibles {

        @Test
        @DisplayName("Agrupa correctamente los drones disponibles por tipo")
        void agrupaCorrectamente() {
            Map<TipoDrone, List<DroneAcuatico>> agrupados =
                    analizador.agruparDisponiblesPorTipo(flota);

            assertAll(
                () -> assertEquals(2, agrupados.get(TipoDrone.SUPERFICIAL).size(),
                        "AR-01 y AR-04 son SUPERFICIALES disponibles"),
                () -> assertEquals(1, agrupados.get(TipoDrone.SEMISUMERGIDO).size(),
                        "AR-02 es el único SEMISUMERGIDO disponible"),
                () -> assertEquals(1, agrupados.get(TipoDrone.BUCEADOR).size(),
                        "AR-06 es el único BUCEADOR disponible (AR-03 mant., AR-05 en misión)")
            );
        }

        @Test
        @DisplayName("Los drones no disponibles no aparecen en ningún grupo")
        void excluyeNoDisponibles() {
            Map<TipoDrone, List<DroneAcuatico>> agrupados =
                    analizador.agruparDisponiblesPorTipo(flota);

            boolean contieneAR03 = agrupados.values().stream()
                    .flatMap(List::stream)
                    .anyMatch(d -> d.id().equals("AR-03"));
            boolean contieneAR05 = agrupados.values().stream()
                    .flatMap(List::stream)
                    .anyMatch(d -> d.id().equals("AR-05"));

            assertAll(
                () -> assertFalse(contieneAR03, "AR-03 está en MANTENIMIENTO"),
                () -> assertFalse(contieneAR05, "AR-05 está EN_MISION")
            );
        }

        @Test
        @DisplayName("Flota vacía retorna map vacío")
        void flotaVacia() {
            assertTrue(analizador.agruparDisponiblesPorTipo(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Sin drones disponibles retorna map vacío")
        void sinDisponibles() {
            var sinDisponibles = List.of(
                new DroneAcuatico("AR-X", "Aqua-Ranger 100", 90,
                        EstadoDrone.MANTENIMIENTO, "Zona", TipoDrone.SUPERFICIAL)
            );
            assertTrue(analizador.agruparDisponiblesPorTipo(sinDisponibles).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 2 — droneOptimoParaZona (criterio: tipo compatible)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 2 — droneOptimoParaZona (criterio: tipo compatible)")
    class DroneOptimo {

        @Test
        @DisplayName("Retorna el BUCEADOR de mayor batería disponible para Embalse Norte")
        void retornaMayorBateriaCompatible() {
            // Embalse Norte solo acepta BUCEADOR.
            // Candidatos: AR-06 (55%, disponible).
            // AR-05 está EN_MISION. AR-03 está en MANTENIMIENTO.
            // AR-01 es SUPERFICIAL aunque esté en la zona → NO compatible.
            Optional<DroneAcuatico> optimo =
                    analizador.droneOptimoParaZona(flota, "Embalse Norte");

            assertTrue(optimo.isPresent(), "Debe haber un BUCEADOR disponible");
            assertEquals("AR-06", optimo.get().id());
        }

        @Test
        @DisplayName("AR-01 SUPERFICIAL en Embalse Norte NO es elegido (ubicación ≠ compatibilidad)")
        void noEligePorUbicacionFisica() {
            // AR-01 (SUPERFICIAL, 92%) está físicamente en Embalse Norte,
            // pero su tipo NO es compatible con esa zona.
            Optional<DroneAcuatico> optimo =
                    analizador.droneOptimoParaZona(flota, "Embalse Norte");

            assertTrue(optimo.isPresent());
            assertNotEquals("AR-01", optimo.get().id(),
                    "No basta con estar en la zona; el tipo debe ser compatible");
        }

        @Test
        @DisplayName("Para Canal Central gana el drone compatible de mayor batería")
        void canalCentralEligeMayorBateriaCompatible() {
            // Canal Central acepta SUPERFICIAL y SEMISUMERGIDO.
            // SUPERFICIAL: AR-01 (92%), AR-04 (73%)
            // SEMISUMERGIDO: AR-02 (45%)
            // Debe ganar AR-01 (92%).
            Optional<DroneAcuatico> optimo =
                    analizador.droneOptimoParaZona(flota, "Canal Central");

            assertTrue(optimo.isPresent());
            assertEquals("AR-01", optimo.get().id());
        }

        @Test
        @DisplayName("Zona sin drones compatibles retorna Optional.empty")
        void zonaSinCompatible() {
            // Solo SUPERFICIALES disponibles; Laguna Sur no acepta SUPERFICIAL.
            var soloSuperficiales = List.of(
                new DroneAcuatico("AR-X1", "Aqua-Ranger 100", 90,
                        EstadoDrone.DISPONIBLE, "Cualquier Zona", TipoDrone.SUPERFICIAL)
            );
            assertTrue(analizador.droneOptimoParaZona(soloSuperficiales, "Laguna Sur").isEmpty());
        }

        @Test
        @DisplayName("Candidato compatible con batería < 35% no es elegido")
        void candidatoSinBateria() {
            var buceadorSinBateria = List.of(
                new DroneAcuatico("AR-X", "Aqua-Ranger 100", 20,
                        EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR)
            );
            assertTrue(analizador.droneOptimoParaZona(buceadorSinBateria, "Embalse Norte").isEmpty());
        }

        @Test
        @DisplayName("Zona nula retorna empty sin excepción")
        void zonaNula() {
            assertTrue(analizador.droneOptimoParaZona(flota, null).isEmpty());
        }

        @Test
        @DisplayName("Zona inexistente retorna empty")
        void zonaInexistente() {
            assertTrue(analizador.droneOptimoParaZona(flota, "Zona Fantasma").isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 3 — promedioBateriaPorTipo
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 3 — promedioBateriaPorTipo")
    class PromedioBateria {

        @Test
        @DisplayName("Calcula el promedio correcto por tipo")
        void promedioCorrecto() {
            Map<TipoDrone, Double> promedios =
                    analizador.promedioBateriaPorTipo(flota);

            assertAll(
                () -> assertEquals((92 + 73) / 2.0,
                        promedios.get(TipoDrone.SUPERFICIAL), 0.01),
                () -> assertEquals(45.0,
                        promedios.get(TipoDrone.SEMISUMERGIDO), 0.01),
                () -> assertEquals((18 + 88 + 55) / 3.0,
                        promedios.get(TipoDrone.BUCEADOR), 0.01)
            );
        }

        @Test
        @DisplayName("Considera TODOS los drones, no solo disponibles")
        void consideraTodos() {
            Map<TipoDrone, Double> promedios =
                    analizador.promedioBateriaPorTipo(flota);

            assertTrue(promedios.containsKey(TipoDrone.BUCEADOR));
            assertEquals(3,
                    flota.stream().filter(d -> d.tipo() == TipoDrone.BUCEADOR).count(),
                    "Hay 3 BUCEADORES en la flota (sin importar su estado)");
        }

        @Test
        @DisplayName("Flota vacía retorna map vacío")
        void flotaVacia() {
            assertTrue(analizador.promedioBateriaPorTipo(List.of()).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSULTA 4 — zonasUnicasDeFlotaActiva
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consulta 4 — zonasUnicasDeFlotaActiva")
    class ZonasUnicas {

        @Test
        @DisplayName("Excluye la zona de un drone en MANTENIMIENTO")
        void excluyeMantenimiento() {
            Set<String> zonas = analizador.zonasUnicasDeFlotaActiva(flota);

            assertAll(
                () -> assertFalse(zonas.contains("Laguna Sur"),
                        "El único drone en Laguna Sur (AR-03) está en MANTENIMIENTO"),
                () -> assertTrue(zonas.contains("Embalse Norte")),
                () -> assertTrue(zonas.contains("Canal Central")),
                () -> assertTrue(zonas.contains("Punto Ribereño Este"))
            );
        }

        @Test
        @DisplayName("Deduplica zonas repetidas")
        void deduplica() {
            var tresEnLaMismaZona = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90,
                        EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 80,
                        EstadoDrone.EN_MISION, "Embalse Norte", TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 70,
                        EstadoDrone.DISPONIBLE, "Embalse Norte", TipoDrone.BUCEADOR)
            );
            assertEquals(1, analizador.zonasUnicasDeFlotaActiva(tresEnLaMismaZona).size());
        }

        @Test
        @DisplayName("Flota vacía retorna set vacío")
        void flotaVacia() {
            assertTrue(analizador.zonasUnicasDeFlotaActiva(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Todos en MANTENIMIENTO retorna set vacío")
        void todosEnMantenimiento() {
            var todosEnMant = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90,
                        EstadoDrone.MANTENIMIENTO, "Embalse Norte", TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 80,
                        EstadoDrone.MANTENIMIENTO, "Canal Central", TipoDrone.SUPERFICIAL)
            );
            assertTrue(analizador.zonasUnicasDeFlotaActiva(todosEnMant).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // BONUS — reduce
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Bonus — bateriaTotalFlotaDisponible (reduce)")
    class BonusReduce {

        @Test
        @DisplayName("Suma correctamente la batería de los disponibles")
        void bateriaTotal() {
            // Disponibles: AR-01 (92), AR-02 (45), AR-04 (73), AR-06 (55)
            int total = analizador.bateriaTotalFlotaDisponible(flota);
            assertEquals(92 + 45 + 73 + 55, total);
        }

        @Test
        @DisplayName("Sin disponibles retorna 0")
        void bateriaTotalVacia() {
            assertEquals(0, analizador.bateriaTotalFlotaDisponible(List.of()));
        }

        @Test
        @DisplayName("Todos no disponibles retorna 0")
        void todosNoDisponibles() {
            var noDisponibles = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90,
                        EstadoDrone.MANTENIMIENTO, "Zona", TipoDrone.BUCEADOR)
            );
            assertEquals(0, analizador.bateriaTotalFlotaDisponible(noDisponibles));
        }
    }
}