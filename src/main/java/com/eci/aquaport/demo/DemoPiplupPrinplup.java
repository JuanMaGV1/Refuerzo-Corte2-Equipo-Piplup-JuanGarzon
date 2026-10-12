package com.eci.aquaport.demo;

import com.eci.aquaport.aplicacion.AnalizadorFlota;
import com.eci.aquaport.aplicacion.AnalizadorMisiones;
import com.eci.aquaport.aplicacion.ConsultorFlota;
import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;
import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoCarga;
import com.eci.aquaport.dominio.TipoDrone;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Demostración ejecutable de las consultas de Prinplup Reto 01.
 *
 * Ejecutar con:
 *   mvn -q exec:java -Dexec.mainClass=com.eci.aquaport.demo.DemoPiplupPrinplup
 *
 * O simplemente correr el main desde el IDE.
 */
public class DemoPiplupPrinplup {

    public static void main(String[] args) {

        // ─────────────────────────────────────────────────────────────
        // Flota de 15 drones (Prinplup): 5 SUPERFICIALES, 5 SEMISUMERGIDOS,
        // 5 BUCEADORES, distribuidos entre las 5 zonas hídricas.
        // ─────────────────────────────────────────────────────────────
        List<DroneAcuatico> flota = List.of(
                // SUPERFICIALES (3 disponibles, 1 en misión, 1 en mantenimiento)
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, EstadoDrone.DISPONIBLE,    "Canal Central",        TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 78, EstadoDrone.DISPONIBLE,    "Punto Ribereño Este",  TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 45, EstadoDrone.DISPONIBLE,    "Laboratorio Hídrico",  TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 60, EstadoDrone.EN_MISION,     "Canal Central",        TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-05", "Aqua-Ranger 100", 22, EstadoDrone.MANTENIMIENTO, "Punto Ribereño Este",  TipoDrone.SUPERFICIAL),

                // SEMISUMERGIDOS (4 disponibles, 1 en misión)
                new DroneAcuatico("AR-06", "Aqua-Ranger 100", 88, EstadoDrone.DISPONIBLE,    "Laguna Sur",           TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("AR-07", "Aqua-Ranger 100", 55, EstadoDrone.DISPONIBLE,    "Canal Central",        TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("AR-08", "Aqua-Ranger 100", 41, EstadoDrone.DISPONIBLE,    "Punto Ribereño Este",  TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("AR-09", "Aqua-Ranger 100", 70, EstadoDrone.DISPONIBLE,    "Laboratorio Hídrico",  TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("AR-10", "Aqua-Ranger 100", 33, EstadoDrone.EN_MISION,     "Laguna Sur",           TipoDrone.SEMISUMERGIDO),

                // BUCEADORES (3 disponibles, 1 en misión, 1 recargando)
                new DroneAcuatico("AR-11", "Aqua-Ranger 100", 95, EstadoDrone.DISPONIBLE,    "Embalse Norte",        TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-12", "Aqua-Ranger 100", 82, EstadoDrone.DISPONIBLE,    "Laguna Sur",           TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-13", "Aqua-Ranger 100", 67, EstadoDrone.DISPONIBLE,    "Laboratorio Hídrico",  TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-14", "Aqua-Ranger 100", 90, EstadoDrone.EN_MISION,     "Embalse Norte",        TipoDrone.BUCEADOR),
                new DroneAcuatico("AR-15", "Aqua-Ranger 100", 30, EstadoDrone.RECARGANDO,    "Embalse Norte",        TipoDrone.BUCEADOR)
        );

        List<Mision> misiones = List.of(
                mision("M-001", "Embalse Norte",       "Laboratorio Hídrico", Prioridad.CRITICA, EstadoMision.EN_TRANSITO),
                mision("M-002", "Canal Central",        "Laguna Sur",          Prioridad.NORMAL,  EstadoMision.EN_TRANSITO),
                mision("M-003", "Laguna Sur",           "Laboratorio Hídrico", Prioridad.ALTA,    EstadoMision.PENDIENTE),
                mision("M-004", "Punto Ribereño Este",  "Embalse Norte",       Prioridad.CRITICA, EstadoMision.PENDIENTE),
                mision("M-005", "Laboratorio Hídrico",  "Canal Central",       Prioridad.BAJA,    EstadoMision.ENTREGADA)
        );

        AnalizadorFlota analizadorFlota = new AnalizadorFlota();
        AnalizadorMisiones analizadorMisiones = new AnalizadorMisiones();
        ConsultorFlota consultorFlota = new ConsultorFlota();

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO — AquaPort Prinplup · Reto 01 (Streams avanzados)  ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Flota total: " + flota.size() + " drones | Misiones: " + misiones.size());

        // ─── Consulta 1 ─────────────────────────────────────────────
        System.out.println("\n─── 1. Agrupar drones disponibles por tipo ───────────────");
        Map<TipoDrone, List<DroneAcuatico>> agrupados =
                analizadorFlota.agruparDisponiblesPorTipo(flota);
        agrupados.forEach((tipo, lista) ->
                System.out.printf("  %-14s → %d drones: %s%n",
                        tipo, lista.size(),
                        lista.stream().map(DroneAcuatico::id).toList()));

        // ─── Consulta 2 ─────────────────────────────────────────────
        System.out.println("\n─── 2. Drone óptimo para cada zona ───────────────────────");
        for (String zona : List.of("Embalse Norte", "Canal Central",
                "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico")) {
            Optional<DroneAcuatico> optimo =
                    analizadorFlota.droneOptimoParaZona(flota, zona);
            String resultado = optimo
                    .map(d -> String.format("%s (%s, %d%%)",
                            d.id(), d.tipo(), d.bateria()))
                    .orElse("— sin candidato —");
            System.out.printf("  %-22s → %s%n", zona, resultado);
        }

        // ─── Consulta 3 ─────────────────────────────────────────────
        System.out.println("\n─── 3. Promedio de batería por tipo ──────────────────────");
        Map<TipoDrone, Double> promedios =
                analizadorFlota.promedioBateriaPorTipo(flota);
        promedios.forEach((tipo, promedio) ->
                System.out.printf("  %-14s → %.2f%%%n", tipo, promedio));

        // ─── Consulta 4 ─────────────────────────────────────────────
        System.out.println("\n─── 4. Separar misiones CRÍTICAS del resto ───────────────");
        Map<Boolean, List<Mision>> separadas =
                analizadorMisiones.separarMisionesCriticas(misiones);
        System.out.println("  CRÍTICAS    : " + separadas.get(true).stream()
                .map(Mision::id).toList());
        System.out.println("  NO CRÍTICAS : " + separadas.get(false).stream()
                .map(Mision::id).toList());

        // ─── Consulta 5 ─────────────────────────────────────────────
        System.out.println("\n─── 5. Zonas únicas de la flota activa ───────────────────");
        Set<String> zonas = analizadorFlota.zonasUnicasDeFlotaActiva(flota);
        System.out.println("  " + zonas);

        // ─── Bonus: flatMap + reduce ────────────────────────────────
        System.out.println("\n─── Bonus: flatMap ───────────────────────────────────────");
        Set<String> zonasActivas =
                analizadorMisiones.zonasDeMisionesActivas(misiones);
        System.out.println("  Zonas involucradas en misiones EN_TRANSITO: " + zonasActivas);

        System.out.println("\n─── Bonus: reduce ────────────────────────────────────────");
        int bateriaTotal = analizadorFlota.bateriaTotalFlotaDisponible(flota);
        System.out.println("  Batería total de la flota disponible: " + bateriaTotal + "%");

        // ─── Cross-check con ConsultorFlota (Piplup) ────────────────
        System.out.println("\n─── Cross-check con ConsultorFlota (Piplup) ─────────────");
        System.out.println("  Disponibles ordenados por batería: " +
                consultorFlota.listaDronesDisponiblesOrdenados(flota).stream()
                        .map(DroneAcuatico::id).toList());
        System.out.println("  Dron con mayor batería global: " +
                consultorFlota.dronMayorBateria(flota)
                        .map(DroneAcuatico::id).orElse("—"));

        System.out.println("\n✔ Demo completada.\n");
    }

    private static Mision mision(String id, String partida, String llegada,
                                  Prioridad prioridad, EstadoMision estado) {
        DroneAcuatico dronePlaceholder = new DroneAcuatico(
                "AR-01", "Aqua-Ranger 100", 92,
                EstadoDrone.DISPONIBLE, partida, TipoDrone.SUPERFICIAL);
        return new Mision.Builder()
                .id(id)
                .drone(dronePlaceholder)
                .puntoPartida(partida)
                .puntoLlegada(llegada)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estadoMision(estado)
                .prioridad(prioridad)
                .build();
    }
}