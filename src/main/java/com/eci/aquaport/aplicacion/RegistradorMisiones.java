package com.eci.aquaport.aplicacion;

import java.util.Optional;

import com.eci.aquaport.dominio.Mision;

public class RegistradorMisiones {
    private ValidadorMision validador;
    private NotificadorOperador notificador;
    private final RepositorioMisiones repo;

    public RegistradorMisiones(ValidadorMision validador, NotificadorOperador notificador, RepositorioMisiones r){
        this.validador = validador;
        this.notificador = notificador;
        this.repo = r;
    }
    public void registrarMision(Mision mision){
        if (validador.isMisionValida(mision)){
            repo.registrar(mision);
            notificador.notificarExito(mision, "Registrar");
        }else{
            notificador.notificarError(mision, "Registrar", "No se cumplió una de las reglas de negocio. Verifique valores de la misión.");
        }
    }

    public Optional<Mision> consultarMisionPorId(String id){
        return repo.misionPorId(id);
        
    }
}
