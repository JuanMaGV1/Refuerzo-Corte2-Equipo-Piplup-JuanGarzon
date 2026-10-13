package com.eci.aquaport.aplicacion;

import com.eci.aquaport.aplicacion.estrategias.EstrategiaSeleccion;
import com.eci.aquaport.aplicacion.observadores.ObservadorMision;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock private EstrategiaSeleccion estrategiaMock;
    @Mock private ObservadorMision observador1;
    @Mock private ObservadorMision observador2;

    private Mision mision;

    @BeforeEach
    void setUp() {
        DroneAcuatico drone = new DroneBuceador(
                "AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, "Embalse Norte");
        mision = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .cargaGramos(200)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.CRITICA)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // Asignación exitosa
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Asignación exitosa")
    class AsignacionExitosa {

        @Test
        @DisplayName("Delega en la estrategia y notifica a todos los observadores")
        void asignacionExitosa_notificaATodos() {
            DroneAcuatico candidato = new DroneBuceador(
                    "AR-10", "Aqua-Ranger 100", 90,
                    EstadoDrone.DISPONIBLE, "Embalse Norte");
            when(estrategiaMock.seleccionar(any(), eq(mision)))
                    .thenReturn(Optional.of(candidato));

            AsignadorAutomatico asignador =
                    new AsignadorAutomatico(List.of(candidato), estrategiaMock);
            asignador.registrarObservador(observador1);
            asignador.registrarObservador(observador2);

            Optional<DroneAcuatico> resultado = asignador.asignar(mision);

            assertAll(
                    () -> assertTrue(resultado.isPresent()),
                    () -> assertEquals("AR-10", resultado.get().id()),
                    () -> assertEquals(EstadoDrone.EN_MISION, candidato.estado())
            );
            verify(observador1, times(1)).notificarAsignacion(mision, candidato);
            verify(observador2, times(1)).notificarAsignacion(mision, candidato);
            verify(observador1, never()).notificarFalloAsignacion(any(), anyString());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Test OBLIGATORIO del reto: drone en FALLO
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Misión con drone en FALLO")
    class DroneEnFallo {
        @Test
        @DisplayName("Drone en FALLO en la zona → notifica a todos exactamente una vez")
        void droneEnFallo_notificaATodosUnaVez() {
            DroneAcuatico enFallo = new DroneBuceador(
                    "AR-X", "Aqua-Ranger 100", 90,
                    EstadoDrone.FALLO, "Embalse Norte");

            AsignadorAutomatico asignador =
                    new AsignadorAutomatico(List.of(enFallo), estrategiaMock);
            asignador.registrarObservador(observador1);
            asignador.registrarObservador(observador2);

            Optional<DroneAcuatico> resultado = asignador.asignar(mision);

            assertTrue(resultado.isEmpty());
            verify(observador1, times(1)).notificarFalloAsignacion(eq(mision), anyString());
            verify(observador2, times(1)).notificarFalloAsignacion(eq(mision), anyString());
            verify(observador1, never()).notificarAsignacion(any(), any());
            verify(observador2, never()).notificarAsignacion(any(), any());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Otros escenarios de fallo
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Otros escenarios de fallo")
    class OtrosFallos {

        @Test
        @DisplayName("Flota vacía → notifica fallo a todos")
        void flotaVacia_notificaFallo() {
            AsignadorAutomatico asignador =
                    new AsignadorAutomatico(List.of(), estrategiaMock);
            asignador.registrarObservador(observador1);
            asignador.registrarObservador(observador2);

            assertTrue(asignador.asignar(mision).isEmpty());
            verify(observador1, times(1)).notificarFalloAsignacion(eq(mision), anyString());
            verify(observador2, times(1)).notificarFalloAsignacion(eq(mision), anyString());
        }

        @Test
        @DisplayName("Estrategia retorna empty → notifica fallo")
        void estrategiaRetornaEmpty_notificaFallo() {
            DroneAcuatico candidato = new DroneBuceador(
                    "AR-10", "Aqua-Ranger 100", 90,
                    EstadoDrone.DISPONIBLE, "Embalse Norte");
            when(estrategiaMock.seleccionar(any(), eq(mision)))
                    .thenReturn(Optional.empty());

            AsignadorAutomatico asignador =
                    new AsignadorAutomatico(List.of(candidato), estrategiaMock);
            asignador.registrarObservador(observador1);

            assertTrue(asignador.asignar(mision).isEmpty());
            verify(observador1, times(1)).notificarFalloAsignacion(eq(mision), anyString());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Validaciones del constructor
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validaciones del constructor")
    class Validaciones {

        @Test
        @DisplayName("Flota nula lanza IllegalArgumentException")
        void flotaNula_lanza() {
            assertThrows(IllegalArgumentException.class,
                    () -> new AsignadorAutomatico(null, estrategiaMock));
        }

        @Test
        @DisplayName("Estrategia nula lanza IllegalArgumentException")
        void estrategiaNula_lanza() {
        List<DroneAcuatico> flotaVacia = List.of();   // ← extraído fuera
        assertThrows(IllegalArgumentException.class,
                () -> new AsignadorAutomatico(flotaVacia, null));
        }
    }
}