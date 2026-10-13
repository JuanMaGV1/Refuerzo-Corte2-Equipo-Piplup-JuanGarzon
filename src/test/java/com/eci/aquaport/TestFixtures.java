package com.eci.aquaport;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.DroneBuceador;
import com.eci.aquaport.dominio.DroneSemisumergido;
import com.eci.aquaport.dominio.DroneSuperficial;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.TipoDrone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helpers para construir objetos de dominio en tests.
 *
 * Todos los métodos devuelven el tipo padre (DroneAcuatico,
 * List&lt;DroneAcuatico&gt;) para evitar el problema de invarianza de
 * genéricos en Java (List&lt;DroneSuperficial&gt; NO es un
 * List&lt;DroneAcuatico&gt;).
 *
 * Organización:
 *  - Constructores individuales de drones (uno por tipo).
 *  - Constructores con batería por defecto.
 *  - Constructores con historial de entregas (Empoleon).
 *  - Flota vacía tipada.
 *  - Fixture de Empoleon: 60 drones deterministas.
 */
public final class TestFixtures {

    private TestFixtures() {
        // clase de utilidad, no instanciable
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTORES INDIVIDUALES DE DRONES
    // ═══════════════════════════════════════════════════════════════

    public static DroneAcuatico superficial(String id, int bateria,
                                             EstadoDrone estado, String zona) {
        return new DroneSuperficial(id, "Aqua-Ranger 200", bateria, estado, zona);
    }

    public static DroneAcuatico semisumergido(String id, int bateria,
                                               EstadoDrone estado, String zona) {
        return new DroneSemisumergido(id, "Aqua-Ranger 200", bateria, estado, zona);
    }

    public static DroneAcuatico buceador(String id, int bateria,
                                          EstadoDrone estado, String zona) {
        return new DroneBuceador(id, "Aqua-Ranger 200", bateria, estado, zona);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTORES CON BATERÍA POR DEFECTO (90%)
    // ═══════════════════════════════════════════════════════════════

    public static DroneAcuatico superficialDisponible(String id, String zona) {
        return superficial(id, 90, EstadoDrone.DISPONIBLE, zona);
    }

    public static DroneAcuatico semisumergidoDisponible(String id, String zona) {
        return semisumergido(id, 90, EstadoDrone.DISPONIBLE, zona);
    }

    public static DroneAcuatico buceadorDisponible(String id, String zona) {
        return buceador(id, 90, EstadoDrone.DISPONIBLE, zona);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTORES CON HISTORIAL (Empoleon)
    // ═══════════════════════════════════════════════════════════════

    public static DroneAcuatico buceadorConHistorial(
            String id, int exitosas, int fallidas) {
        return new DroneBuceador(id, "Aqua-Ranger 200", 90,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación",
                exitosas, fallidas);
    }

    public static DroneAcuatico droneConHistorial(
            TipoDrone tipo, String id, int bateria, EstadoDrone estado,
            String zona, int exitosas, int fallidas) {
        return switch (tipo) {
            case SUPERFICIAL -> new DroneSuperficial(id, "Aqua-Ranger 200",
                    bateria, estado, zona, exitosas, fallidas);
            case SEMISUMERGIDO -> new DroneSemisumergido(id, "Aqua-Ranger 200",
                    bateria, estado, zona, exitosas, fallidas);
            case BUCEADOR -> new DroneBuceador(id, "Aqua-Ranger 200",
                    bateria, estado, zona, exitosas, fallidas);
        };
    }

    // ═══════════════════════════════════════════════════════════════
    // FLOTA VACÍA TIPADA
    // ═══════════════════════════════════════════════════════════════

    public static List<DroneAcuatico> flotaVacia() {
        return List.of();
    }

    // ═══════════════════════════════════════════════════════════════
    // FLOTA FIJA DE EJEMPLO (Prinplup)
    // ═══════════════════════════════════════════════════════════════

    public static List<DroneAcuatico> flotaPrinplup4() {
        return List.of(
                superficial("AR-01", 92, EstadoDrone.DISPONIBLE, "Canal Central"),
                semisumergido("AR-02", 45, EstadoDrone.DISPONIBLE, "Canal Central"),
                buceador("AR-03", 18, EstadoDrone.MANTENIMIENTO, "Laguna Sur"),
                superficial("AR-04", 73, EstadoDrone.DISPONIBLE, "Punto Ribereño Este")
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // FIXTURE DE EMPOLEON: 60 DRONES DETERMINISTAS
    // ═══════════════════════════════════════════════════════════════

    public static final String[] ZONAS_EMPOLEON = {
            "Embalse de Investigación",
            "Red de Canales",
            "Laguna de Reserva",
            "Laboratorio Hídrico Central"
    };

    public static final long SEED_EMPOLEON = 42L;

    public static List<DroneAcuatico> flotaEmpoleon60() {
        return flotaEmpoleon60(SEED_EMPOLEON);
    }

    public static List<DroneAcuatico> flotaEmpoleon60(long semilla) {
        Random random = new Random(semilla);
        List<DroneAcuatico> flota = new ArrayList<>(60);

        for (int i = 1; i <= 60; i++) {
            String id = String.format("AR-%02d", i);
            int bateria = 20 + random.nextInt(81);              // 20-100
            String zona = ZONAS_EMPOLEON[random.nextInt(ZONAS_EMPOLEON.length)];
            EstadoDrone estado = estadoAleatorio(random);
            int exitosas = random.nextInt(50);                  // 0-49
            int fallidas = random.nextInt(10);                  // 0-9
            TipoDrone tipo = TipoDrone.values()[(i - 1) % 3];   // round-robin

            DroneAcuatico drone = droneConHistorial(
                    tipo, id, bateria, estado, zona, exitosas, fallidas);
            flota.add(drone);
        }
        return List.copyOf(flota);
    }

    private static EstadoDrone estadoAleatorio(Random random) {
        int r = random.nextInt(10);
        return switch (r) {
            case 0, 1, 2, 3, 4, 5 -> EstadoDrone.DISPONIBLE;
            case 6, 7 -> EstadoDrone.EN_MISION;
            case 8 -> EstadoDrone.RECARGANDO;
            case 9 -> EstadoDrone.MANTENIMIENTO;
            default -> EstadoDrone.DISPONIBLE;
        };
    }

    // ═══════════════════════════════════════════════════════════════
    // FIXTURES ESPECÍFICOS
    // ═══════════════════════════════════════════════════════════════

    public static List<DroneAcuatico> flotaUnDronePorTipo() {
        return List.of(
                superficial("AR-S01", 90, EstadoDrone.DISPONIBLE, "Red de Canales"),
                semisumergido("AR-M01", 90, EstadoDrone.DISPONIBLE, "Laguna de Reserva"),
                buceador("AR-B01", 90, EstadoDrone.DISPONIBLE, "Embalse de Investigación")
        );
    }

    public static List<DroneAcuatico> flotaUnaPorZona() {
        return List.of(
                buceador("AR-01", 95, EstadoDrone.DISPONIBLE, "Embalse de Investigación"),
                superficial("AR-02", 80, EstadoDrone.DISPONIBLE, "Red de Canales"),
                semisumergido("AR-03", 70, EstadoDrone.DISPONIBLE, "Laguna de Reserva"),
                buceador("AR-04", 60, EstadoDrone.DISPONIBLE, "Laboratorio Hídrico Central")
        );
    }
}