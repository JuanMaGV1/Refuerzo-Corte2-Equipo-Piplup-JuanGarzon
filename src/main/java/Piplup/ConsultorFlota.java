package Piplup;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ConsultorFlota {
    
    public List<DroneAcuatico> listaDronesDisponiblesOrdenados(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(d -> d.disponible() && d.bateria() >= 35)
                     .sorted(Comparator.comparing(DroneAcuatico::bateria).reversed())
                     .toList();
    }

    public List<String> listaDronesDisponiblesId(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(DroneAcuatico::disponible)
                     .map(DroneAcuatico::id)
                     .toList();
    }

    public boolean existeDronDisponibleConBateria(List<DroneAcuatico> drones){
        return drones.stream()
                     .anyMatch(n -> n.bateria() >= 35 && n.disponible());
    }

    public long cantidadDronDisponible(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(DroneAcuatico::disponible)
                     .count();
    }

    public Optional<DroneAcuatico> dronMayorBateria(List<DroneAcuatico> drones){
        return drones.stream()
                     .max(Comparator.comparing(DroneAcuatico::bateria));
    }

    public static void main(String[] args) {
        ConsultorFlota consultorFlota = new ConsultorFlota();
        List<DroneAcuatico> flota = List.of(
        new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true,  "Embalse Norte"),
        new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true,  "Canal Central"),
        new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
        new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true,  "Punto Ribereño Este")
        );
        
        System.out.println("1. Lista de drones disponibles con batería >= 35%, ordenados de mayor a menor batería.");
        System.out.println(consultorFlota.listaDronesDisponiblesOrdenados(flota));
        
        System.out.println("\n2. IDs de drones disponibles");
        System.out.println(consultorFlota.listaDronesDisponiblesId(flota));
        
        System.out.println("\n3. ¿Existe algún drone disponible con batería >= 35%?");
        System.out.println(consultorFlota.existeDronDisponibleConBateria(flota));
        
        System.out.println("\n4. Contar cuántos drones están disponibles");
        System.out.println(consultorFlota.cantidadDronDisponible(flota));
        
        System.out.println("\n5. Drone con mayor batería de toda la flota");
        System.out.println(consultorFlota.dronMayorBateria(flota));

    }
}
