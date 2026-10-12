package com.eci.aquaport.aplicacion;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.EstadoDrone;

public class ConsultorFlota {
    
    public List<DroneAcuatico> listaDronesDisponiblesOrdenados(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(d -> d.estado() == EstadoDrone.DISPONIBLE && d.bateria() >= 35)
                     .sorted(Comparator.comparing(DroneAcuatico::bateria).reversed())
                     .toList();
    }

    public List<String> listaDronesDisponiblesId(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(d -> d.estado() == EstadoDrone.DISPONIBLE)
                     .map(DroneAcuatico::id)
                     .toList();
    }

    public boolean existeDronDisponibleConBateria(List<DroneAcuatico> drones){
        return drones.stream()
                     .anyMatch(n -> n.bateria() >= 35 && n.estado() == EstadoDrone.DISPONIBLE);
    }

    public long cantidadDronDisponible(List<DroneAcuatico> drones){
        return drones.stream()
                     .filter(d -> d.estado() == EstadoDrone.DISPONIBLE)
                     .count();
    }

    public Optional<DroneAcuatico> dronMayorBateria(List<DroneAcuatico> drones){
        return drones.stream()
                     .max(Comparator.comparing(DroneAcuatico::bateria));
    }
}
