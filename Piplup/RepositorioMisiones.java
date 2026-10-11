package Piplup;

import java.util.List;
import java.util.Optional;

public interface RepositorioMisiones {
    public void registrar(Mision mision);
    public List<Mision> listarTodas();
    public Optional<Mision> misionPorId(String id);
}
