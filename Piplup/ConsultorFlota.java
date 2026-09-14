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

    
}
