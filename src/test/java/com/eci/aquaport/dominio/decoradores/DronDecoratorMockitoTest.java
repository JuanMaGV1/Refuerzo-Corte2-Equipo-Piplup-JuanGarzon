package com.eci.aquaport.dominio.decoradores;

import com.eci.aquaport.dominio.DroneOperable;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneDecoratorMockitoTest {

    @Mock
    private DroneOperable envueltoMock;

    private DroneConMonitoreo decorador;

    @BeforeEach
    void setUp() {
        decorador = new DroneConMonitoreo(envueltoMock);
    }

    @Test
    @DisplayName("Los getters delegan en el mock exactamente una vez")
    void gettersDeleganEnMock() {
        when(envueltoMock.id()).thenReturn("AR-01");
        when(envueltoMock.bateria()).thenReturn(90);
        when(envueltoMock.tipo()).thenReturn(TipoDrone.BUCEADOR);

        assertAll(
                () -> assertEquals("AR-01", decorador.id()),
                () -> assertEquals(90, decorador.bateria()),
                () -> assertEquals(TipoDrone.BUCEADOR, decorador.tipo())
        );

        verify(envueltoMock, times(1)).id();
        verify(envueltoMock, times(1)).bateria();
        verify(envueltoMock, times(1)).tipo();
    }

    @Test
    @DisplayName("setEstado delega en el mock")
    void setEstadoDelegaEnMock() {
        when(envueltoMock.estado()).thenReturn(EstadoDrone.DISPONIBLE);

        decorador.setEstado(EstadoDrone.EN_MISION);

        verify(envueltoMock, times(1)).setEstado(EstadoDrone.EN_MISION);
    }

    @Test
    @DisplayName("setBateria delega en el mock")
    void setBateriaDelegaEnMock() {
        when(envueltoMock.bateria()).thenReturn(90);

        decorador.setBateria(50);

        verify(envueltoMock, times(1)).setBateria(50);
    }

    @Test
    @DisplayName("No se llama al envuelto si no se usa")
    void noLlamaSinUso() {
        verifyNoInteractions(envueltoMock);
    }
}