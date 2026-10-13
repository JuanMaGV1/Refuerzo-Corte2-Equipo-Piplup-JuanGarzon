package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnalizadorMisionesTest {

    private AnalizadorMisiones analizador;

    @BeforeEach
    void setUp() {
        analizador = new AnalizadorMisiones();
    }

    private Mision mision(String id, String partida, String llegada,
                          Prioridad prioridad, EstadoMision estado) {
        DroneAcuatico drone = new DroneBuceador(
                "AR-01", "Aqua-Ranger 100", 92,
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
    // separarMisionesCriticas
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("separarMisionesCriticas")
    class SepararCriticas {

        @Test
        @DisplayName("Caso mixto: separa correctamente críticas y no críticas")
        void casoMixto() {
            var misiones = List.of(
                    mision("M-001", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE),
                    mision("M-002", "A", "B", Prioridad.NORMAL,  EstadoMision.PENDIENTE),
                    mision("M-003", "A", "B", Prioridad.ALTA,    EstadoMision.PENDIENTE),
                    mision("M-004", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE)
            );

            Map<Boolean, List<Mision>> resultado =
                    analizador.separarMisionesCriticas(misiones);

            assertAll(
                    () -> assertTrue(resultado.containsKey(true)),
                    () -> assertTrue(resultado.containsKey(false)),
                    () -> assertEquals(2, resultado.get(true).size()),
                    () -> assertEquals(2, resultado.get(false).size()),
                    () -> assertTrue(resultado.get(true).stream()
                            .allMatch(m -> m.prioridad() == Prioridad.CRITICA)),
                    () -> assertTrue(resultado.get(false).stream()
                            .noneMatch(m -> m.prioridad() == Prioridad.CRITICA))
            );
        }

        @Test
        @DisplayName("Sin críticas: true vacía, false con todas")
        void sinCriticas() {
            var misiones = List.of(
                    mision("M-001", "A", "B", Prioridad.NORMAL, EstadoMision.PENDIENTE),
                    mision("M-002", "A", "B", Prioridad.BAJA,   EstadoMision.PENDIENTE)
            );

            Map<Boolean, List<Mision>> resultado =
                    analizador.separarMisionesCriticas(misiones);

            assertAll(
                    () -> assertTrue(resultado.get(true).isEmpty()),
                    () -> assertEquals(2, resultado.get(false).size())
            );
        }

        @Test
        @DisplayName("Todas críticas: false vacía, true con todas")
        void todasCriticas() {
            var misiones = List.of(
                    mision("M-001", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE),
                    mision("M-002", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE)
            );

            Map<Boolean, List<Mision>> resultado =
                    analizador.separarMisionesCriticas(misiones);

            assertAll(
                    () -> assertEquals(2, resultado.get(true).size()),
                    () -> assertTrue(resultado.get(false).isEmpty())
            );
        }

        @Test
        @DisplayName("Lista vacía: ambas claves presentes con listas vacías")
        void listaVacia() {
            Map<Boolean, List<Mision>> resultado =
                    analizador.separarMisionesCriticas(List.of());

            assertAll(
                    () -> assertTrue(resultado.containsKey(true)),
                    () -> assertTrue(resultado.containsKey(false)),
                    () -> assertTrue(resultado.get(true).isEmpty()),
                    () -> assertTrue(resultado.get(false).isEmpty())
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // zonasDeMisionesActivas
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("zonasDeMisionesActivas")
    class ZonasActivas {

        @Test
        @DisplayName("Caso normal: aplana partida y llegada de EN_TRANSITO")
        void casoNormal() {
            var misiones = List.of(
                    mision("M-001", "Embalse Norte", "Laboratorio Hídrico",
                            Prioridad.CRITICA, EstadoMision.EN_TRANSITO),
                    mision("M-002", "Canal Central", "Laguna Sur",
                            Prioridad.NORMAL, EstadoMision.EN_TRANSITO)
            );

            Set<String> zonas = analizador.zonasDeMisionesActivas(misiones);

            assertAll(
                    () -> assertTrue(zonas.contains("Embalse Norte")),
                    () -> assertTrue(zonas.contains("Laboratorio Hídrico")),
                    () -> assertTrue(zonas.contains("Canal Central")),
                    () -> assertTrue(zonas.contains("Laguna Sur")),
                    () -> assertEquals(4, zonas.size())
            );
        }

        @Test
        @DisplayName("Excluye misiones que no están EN_TRANSITO")
        void excluyeNoActivas() {
            var misiones = List.of(
                    mision("M-001", "Embalse Norte", "Laboratorio Hídrico",
                            Prioridad.CRITICA, EstadoMision.EN_TRANSITO),
                    mision("M-002", "Canal Central", "Laguna Sur",
                            Prioridad.NORMAL, EstadoMision.PENDIENTE),
                    mision("M-003", "Punto Ribereño Este", "Embalse Norte",
                            Prioridad.ALTA, EstadoMision.ENTREGADA)
            );

            Set<String> zonas = analizador.zonasDeMisionesActivas(misiones);

            assertAll(
                    () -> assertEquals(2, zonas.size()),
                    () -> assertTrue(zonas.contains("Embalse Norte")),
                    () -> assertTrue(zonas.contains("Laboratorio Hídrico")),
                    () -> assertFalse(zonas.contains("Canal Central")),
                    () -> assertFalse(zonas.contains("Laguna Sur")),
                    () -> assertFalse(zonas.contains("Punto Ribereño Este"))
            );
        }

        @Test
        @DisplayName("Deduplica zonas repetidas entre múltiples misiones")
        void deduplicaZonas() {
            var misiones = List.of(
                    mision("M-001", "Embalse Norte", "Laboratorio Hídrico",
                            Prioridad.CRITICA, EstadoMision.EN_TRANSITO),
                    mision("M-002", "Embalse Norte", "Laboratorio Hídrico",
                            Prioridad.NORMAL, EstadoMision.EN_TRANSITO)
            );

            Set<String> zonas = analizador.zonasDeMisionesActivas(misiones);

            assertEquals(2, zonas.size(),
                    "Dos misiones con las mismas zonas producen un Set de 2 elementos");
        }

        @Test
        @DisplayName("Sin misiones EN_TRANSITO retorna set vacío")
        void sinMisionesActivas() {
            var misiones = List.of(
                    mision("M-001", "A", "B", Prioridad.NORMAL, EstadoMision.PENDIENTE),
                    mision("M-002", "C", "D", Prioridad.ALTA, EstadoMision.ENTREGADA)
            );

            assertTrue(analizador.zonasDeMisionesActivas(misiones).isEmpty());
        }

        @Test
        @DisplayName("Lista vacía retorna set vacío")
        void listaVacia() {
            assertTrue(analizador.zonasDeMisionesActivas(List.of()).isEmpty());
        }

        @Test
        @DisplayName("Misión donde partida y llegada son la misma zona: el Set tiene 1 elemento")
        void partidaIgualLlegada() {
            var misiones = List.of(
                    mision("M-001", "Canal Central", "Canal Central",
                            Prioridad.NORMAL, EstadoMision.EN_TRANSITO)
            );

            Set<String> zonas = analizador.zonasDeMisionesActivas(misiones);

            assertAll(
                    () -> assertEquals(1, zonas.size()),
                    () -> assertTrue(zonas.contains("Canal Central"))
            );
        }
    }
}