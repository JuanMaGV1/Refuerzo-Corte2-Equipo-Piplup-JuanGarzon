package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, partida, TipoDrone.SUPERFICIAL);
        return new Mision.Builder()
                .id(id).drone(drone)
                .puntoPartida(partida).puntoLlegada(llegada)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(estado).prioridad(prioridad)
                .build();
    }

    // ── separarMisionesCriticas ─────────────────────────────────────

    @Test
    @DisplayName("Separa correctamente críticas y no críticas")
    void separaCorrectamente() {
        var misiones = List.of(
                mision("M-001", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE),
                mision("M-002", "A", "B", Prioridad.NORMAL,  EstadoMision.PENDIENTE),
                mision("M-003", "A", "B", Prioridad.ALTA,    EstadoMision.PENDIENTE),
                mision("M-004", "A", "B", Prioridad.CRITICA, EstadoMision.PENDIENTE)
        );

        Map<Boolean, List<Mision>> separadas =
                analizador.separarMisionesCriticas(misiones);

        assertAll(
                () -> assertEquals(2, separadas.get(true).size()),
                () -> assertEquals(2, separadas.get(false).size())
        );
    }

    @Test
    @DisplayName("Sin críticas: true vacía, false con todas")
    void sinCriticas() {
        var misiones = List.of(
                mision("M-001", "A", "B", Prioridad.NORMAL, EstadoMision.PENDIENTE)
        );
        var separadas = analizador.separarMisionesCriticas(misiones);
        assertAll(
                () -> assertTrue(separadas.get(true).isEmpty()),
                () -> assertEquals(1, separadas.get(false).size())
        );
    }

    @Test
    @DisplayName("Lista vacía: ambas claves presentes con listas vacías")
    void listaVacia() {
        var separadas = analizador.separarMisionesCriticas(List.of());
        assertAll(
                () -> assertTrue(separadas.containsKey(true)),
                () -> assertTrue(separadas.containsKey(false)),
                () -> assertTrue(separadas.get(true).isEmpty()),
                () -> assertTrue(separadas.get(false).isEmpty())
        );
    }

    // ── zonasDeMisionesActivas ──────────────────────────────────────

    @Test
    @DisplayName("flatMap aplana partida y llegada de EN_TRANSITO")
    void zonasActivas() {
        var misiones = List.of(
                mision("M-001", "Embalse Norte", "Laboratorio Hídrico",
                        Prioridad.CRITICA, EstadoMision.EN_TRANSITO),
                mision("M-002", "Canal Central", "Laguna Sur",
                        Prioridad.NORMAL,  EstadoMision.EN_TRANSITO),
                mision("M-003", "Punto Ribereño Este", "Embalse Norte",
                        Prioridad.BAJA,    EstadoMision.PENDIENTE)  // no cuenta
        );

        Set<String> zonas = analizador.zonasDeMisionesActivas(misiones);

        assertAll(
                () -> assertTrue(zonas.contains("Embalse Norte")),
                () -> assertTrue(zonas.contains("Laboratorio Hídrico")),
                () -> assertTrue(zonas.contains("Canal Central")),
                () -> assertTrue(zonas.contains("Laguna Sur")),
                () -> assertFalse(zonas.contains("Punto Ribereño Este"),
                        "La misión pendiente no está EN_TRANSITO")
        );
    }

    @Test
    @DisplayName("Sin misiones EN_TRANSITO retorna set vacío")
    void sinMisionesActivas() {
        var misiones = List.of(
                mision("M-001", "A", "B", Prioridad.NORMAL, EstadoMision.PENDIENTE)
        );
        assertTrue(analizador.zonasDeMisionesActivas(misiones).isEmpty());
    }

        /**
     * Filtra misiones con estado PENDIENTE y la prioridad indicada.
     *
     * Responde a la pregunta del operador: "¿qué CRÍTICAS están esperando
     * asignación de drone?".
     *
     * @param misiones  lista completa de misiones del sistema
     * @param prioridad prioridad a filtrar (CRITICA, ALTA, NORMAL, BAJA)
     * @return lista inmutable con las misiones que cumplen ambos criterios
     */
    public List<Mision> misionesPendientesConPrioridad(
            List<Mision> misiones, Prioridad prioridad) {
        return misiones.stream()
                .filter(m -> m.estadoMision() == EstadoMision.PENDIENTE)
                .filter(m -> m.prioridad() == prioridad)
                .toList();
    }
}