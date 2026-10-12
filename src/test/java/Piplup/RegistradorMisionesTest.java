package Piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;   // ← ESTE FALTABA
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistradorMisionesTest {

    @Mock private ValidadorMision validadorMock;
    @Mock private NotificadorOperador notificadorMock;
    @Mock private RepositorioMisiones repoMock;
    @InjectMocks private RegistradorMisiones registrador;

    private Mision mision;

    @BeforeEach
    void setUp() {
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        mision = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(EstadoMision.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Misión válida: registra y notifica éxito")
    void valida_registraYNotificaExito() {
        when(validadorMock.isMisionValida(mision)).thenReturn(true);
        registrador.registrarMision(mision);
        verify(repoMock).registrar(mision);
        verify(notificadorMock).notificarExito(eq(mision), anyString());
        verify(notificadorMock, never()).notificarError(any(), any(), any());
    }

    @Test
    @DisplayName("Misión inválida: no registra y notifica error")
    void invalida_notificaError() {
        when(validadorMock.isMisionValida(mision)).thenReturn(false);
        registrador.registrarMision(mision);
        verify(repoMock, never()).registrar(any());
        verify(notificadorMock).notificarError(eq(mision), anyString(), anyString());
    }

    @Test
    @DisplayName("Consultar misión por id delega al repositorio")
    void consultarPorId_delegaAlRepo() {
        when(repoMock.misionPorId("M-001")).thenReturn(Optional.of(mision));
        assertEquals(Optional.of(mision), registrador.consultarMisionPorId("M-001"));
        verify(repoMock).misionPorId("M-001");
    }

    @Test
    @DisplayName("Consultar id inexistente retorna Optional.empty")
    void consultarInexistente() {
        when(repoMock.misionPorId("M-999")).thenReturn(Optional.empty());
        assertTrue(registrador.consultarMisionPorId("M-999").isEmpty());
    }
}