package piplup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioMisionesEnMemoria implements RepositorioMisiones{
    
    private List<Mision> misiones = new ArrayList<>();
    
    @Override 
    public void registrar(Mision mision){
        misiones.add(mision);
    }
    
    @Override 
    public List<Mision> listarTodas(){
        return List.copyOf(misiones);
    }
    
    @Override 
    public Optional<Mision> misionPorId(String id){
        return misiones.stream().filter(m->m.getId().equals(id)).findAny();
    }
}
