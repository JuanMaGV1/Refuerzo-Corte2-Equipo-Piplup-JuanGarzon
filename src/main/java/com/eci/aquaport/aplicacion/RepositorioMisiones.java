package com.eci.aquaport.aplicacion;

import java.util.List;
import java.util.Optional;

import com.eci.aquaport.dominio.Mision;

public interface RepositorioMisiones {
    public void registrar(Mision mision);
    public List<Mision> listarTodas();
    public Optional<Mision> misionPorId(String id);
}
