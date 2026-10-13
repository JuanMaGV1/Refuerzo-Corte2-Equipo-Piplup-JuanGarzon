package com.eci.aquaport.aplicacion.validacion;

import com.eci.aquaport.TestFixtures;
import com.eci.aquaport.aplicacion.validacion.condiciones.CondicionesHidricas;
import com.eci.aquaport.aplicacion.validacion.condiciones.ProveedorCondicionesHidricas;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadenaValidacionTest {

    @Mock
    private ProveedorCondicionesHidricas proveedorMock;

    private ValidadorMision cadena;

    @BeforeEach
    void setUp() {
        cadena = CadenaValidacionFactory.crear(proveedorMock);
    }

    private Mision mision(DroneAcuatico drone, int cargaGramos, String destino) {
        return new Mision.Builder()
                .id("M-001").drone(drone)
                .puntoPartida("Red de Canales")
                .puntoLlegada(destino)
                .tipoCarga(TipoCarga.SENSOR)
                .cargaGramos(cargaGramos)
                .estadoMision(EstadoMision.PENDIENTE)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    @Test
    @DisplayName("Cadena completa válida retorna valido")
    void cadenaCompletaValida() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        when(proveedorMock.consultar(anyString()))
                .thenReturn(new CondicionesHidricas(
                        CondicionesHidricas.NivelAgitacion.CALMA, 20.0, 18.0));

        ResultadoValidacion r = cadena.validar(mision(d, 200, "Laguna de Reserva"));

        assertTrue(r.esValido());
    }

    /**
     * TEST OBLIGATORIO DEL RETO 03 EMPOLEON:
     * La cadena se detiene en el PRIMER validador que falla.
     * Si la batería falla, no se debe llamar al proveedor de condiciones.
     */
    @Test
    @DisplayName("La cadena se detiene en el primer validador que falla")
    void cadenaSeDetieneEnElPrimerFallo() {
        // El drone tiene batería insuficiente → falla el primer validador
        DroneAcuatico d = TestFixtures.buceador("AR-03", 20,
                EstadoDrone.DISPONIBLE, "Laguna de Reserva");

        ResultadoValidacion r = cadena.validar(mision(d, 200, "Laguna de Reserva"));

        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("batería")),
                () -> assertTrue(r.razon().contains("AR-03"))
        );
        // Verificación clave: NO se llamó al proveedor de condiciones
        verifyNoInteractions(proveedorMock);
    }

    @Test
    @DisplayName("Falla en capacidad corta la cadena antes de las condiciones")
    void fallaEnCapacidad_cortaCadena() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        // Carga 500g > capacidad 300g del BUCEADOR

        ResultadoValidacion r = cadena.validar(mision(d, 500, "Laguna de Reserva"));

        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("500"))
        );
        // El proveedor tampoco se llamó
        verifyNoInteractions(proveedorMock);
    }

    @Test
    @DisplayName("Falla en compatibilidad de zona corta la cadena")
    void fallaEnZona_cortaCadena() {
        // SUPERFICIAL no es compatible con Embalse de Investigación
        DroneAcuatico d = TestFixtures.superficial("AR-02", 90,
                EstadoDrone.DISPONIBLE, "Red de Canales");

        ResultadoValidacion r = cadena.validar(mision(d, 200,
                "Embalse de Investigación"));

        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("SUPERFICIAL")),
                () -> assertTrue(r.razon().contains("Embalse de Investigación"))
        );
        verifyNoInteractions(proveedorMock);
    }

    @Test
    @DisplayName("Falla en condiciones hídricas es el último eslabón")
    void fallaEnCondicionesHidricas() {
        DroneAcuatico d = TestFixtures.superficial("AR-02", 90,
                EstadoDrone.DISPONIBLE, "Red de Canales");
        // Condiciones agitadas + drone superficial → falla
        when(proveedorMock.consultar(anyString()))
                .thenReturn(new CondicionesHidricas(
                        CondicionesHidricas.NivelAgitacion.AGITADA, 15.0, 20.0));

        ResultadoValidacion r = cadena.validar(mision(d, 200, "Red de Canales"));

        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("AGITADA")),
                () -> assertTrue(r.razon().contains("SUPERFICIAL"))
        );
        // El proveedor SÍ fue llamado (fue el último eslabón)
        verify(proveedorMock, times(1)).consultar("Red de Canales");
    }

    @Test
    @DisplayName("Si el proveedor falla, la cadena retorna inválido con la razón")
    void proveedorFalla_retornaInvalido() {
        DroneAcuatico d = TestFixtures.buceador("AR-01", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación");
        when(proveedorMock.consultar(anyString()))
                .thenThrow(new IllegalStateException("Timeout de la API"));

        ResultadoValidacion r = cadena.validar(mision(d, 200, "Laguna de Reserva"));

        assertAll(
                () -> assertFalse(r.esValido()),
                () -> assertTrue(r.razon().contains("Timeout")),
                () -> assertTrue(r.razon().contains("Laguna de Reserva"))
        );
    }
}