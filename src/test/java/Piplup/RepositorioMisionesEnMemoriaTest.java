package Piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import piplup.DroneAcuatico;
import piplup.EstadoMision;
import piplup.Mision;
import piplup.RepositorioMisionesEnMemoria;
import piplup.TipoCarga;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioMisionesEnMemoriaTest {

    private RepositorioMisionesEnMemoria repo;

    @BeforeEach
    void setUp() {
        repo = new RepositorioMisionesEnMemoria();
    }

    private Mision mision(String id) {
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        return new Mision.Builder()
                .id(id)
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(EstadoMision.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Registrar y listar misiones")
    void registrarYListar() {
        repo.registrar(mision("M-001"));
        repo.registrar(mision("M-002"));
        assertEquals(2, repo.listarTodas().size());
    }

    @Test
    @DisplayName("Buscar por id existente lo encuentra")
    void buscarExistente() {
        repo.registrar(mision("M-001"));
        var encontrada = repo.misionPorId("M-001");
        assertTrue(encontrada.isPresent());
        assertEquals("M-001", encontrada.get().getId());
    }

    @Test
    @DisplayName("Buscar por id inexistente retorna Optional.empty")
    void buscarInexistente() {
        repo.registrar(mision("M-001"));
        assertTrue(repo.misionPorId("M-999").isEmpty());
    }

    @Test
    @DisplayName("listarTodas retorna copia inmutable")
    void listaEsInmutable() {
        repo.registrar(mision("M-001"));
        var lista = repo.listarTodas();
        Mision misionExtra = mision("M-002");

        assertThrows(UnsupportedOperationException.class,
                () -> lista.add(misionExtra));
    }

    @Test
    @DisplayName("Repositorio vacío retorna lista vacía")
    void repositorioVacio() {
        assertTrue(repo.listarTodas().isEmpty());
    }
}