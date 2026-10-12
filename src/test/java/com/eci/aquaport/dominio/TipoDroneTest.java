package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TipoDroneTest {

    @Test
    @DisplayName("SUPERFICIAL expone sus zonas compatibles")
    void superficialZonas() {
        Set<String> zonas = TipoDrone.SUPERFICIAL.zonasCompatibles();
        assertAll(
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.CANAL_CENTRAL)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.PUNTO_RIBERENO_ESTE)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO)),
            () -> assertEquals(3, zonas.size())
        );
    }

    @Test
    @DisplayName("SEMISUMERGIDO expone sus zonas compatibles")
    void semisumergidoZonas() {
        Set<String> zonas = TipoDrone.SEMISUMERGIDO.zonasCompatibles();
        assertAll(
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.CANAL_CENTRAL)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_SUR)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.PUNTO_RIBERENO_ESTE)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO)),
            () -> assertEquals(4, zonas.size())
        );
    }

    @Test
    @DisplayName("BUCEADOR expone sus zonas compatibles")
    void buceadorZonas() {
        Set<String> zonas = TipoDrone.BUCEADOR.zonasCompatibles();
        assertAll(
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.EMBALSE_NORTE)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_SUR)),
            () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO)),
            () -> assertEquals(3, zonas.size())
        );
    }

    @Test
    @DisplayName("La colección retornada es inmutable")
    void zonasInmutable() {
        Set<String> zonas = TipoDrone.SUPERFICIAL.zonasCompatibles();
        assertThrows(UnsupportedOperationException.class,
                () -> zonas.add("Zona Fantasma"));
    }
}