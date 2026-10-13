package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TipoDroneTest {

    @Nested
    @DisplayName("Zonas compatibles con SUPERFICIAL")
    class Superficial {

        @Test
        @DisplayName("Es compatible con las zonas de Prinplup esperadas")
        void zonasPrinplup() {
            Set<String> zonas = TipoDrone.SUPERFICIAL.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.CANAL_CENTRAL)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.PUNTO_RIBERENO_ESTE)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO))
            );
        }

        @Test
        @DisplayName("Es compatible con las zonas de Empoleon esperadas")
        void zonasEmpoleon() {
            Set<String> zonas = TipoDrone.SUPERFICIAL.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.RED_DE_CANALES)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO_CENTRAL))
            );
        }

        @Test
        @DisplayName("No es compatible con zonas profundas")
        void noZonasProfundas() {
            assertAll(
                    () -> assertFalse(TipoDrone.SUPERFICIAL.esCompatibleCon(
                            TipoDrone.Zonas.EMBALSE_NORTE)),
                    () -> assertFalse(TipoDrone.SUPERFICIAL.esCompatibleCon(
                            TipoDrone.Zonas.EMBALSE_DE_INVESTIGACION)),
                    () -> assertFalse(TipoDrone.SUPERFICIAL.esCompatibleCon(
                            TipoDrone.Zonas.LAGUNA_SUR)),
                    () -> assertFalse(TipoDrone.SUPERFICIAL.esCompatibleCon(
                            TipoDrone.Zonas.LAGUNA_DE_RESERVA))
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

    @Nested
    @DisplayName("Zonas compatibles con SEMISUMERGIDO")
    class Semisumergido {

        @Test
        @DisplayName("Es compatible con las zonas de Prinplup esperadas")
        void zonasPrinplup() {
            Set<String> zonas = TipoDrone.SEMISUMERGIDO.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.CANAL_CENTRAL)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_SUR)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.PUNTO_RIBERENO_ESTE)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO))
            );
        }

        @Test
        @DisplayName("Es compatible con las zonas de Empoleon esperadas")
        void zonasEmpoleon() {
            Set<String> zonas = TipoDrone.SEMISUMERGIDO.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.RED_DE_CANALES)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_DE_RESERVA)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO_CENTRAL))
            );
        }

        @Test
        @DisplayName("No es compatible con el Embalse de Investigación")
        void noEmbalse() {
            assertAll(
                    () -> assertFalse(TipoDrone.SEMISUMERGIDO.esCompatibleCon(
                            TipoDrone.Zonas.EMBALSE_NORTE)),
                    () -> assertFalse(TipoDrone.SEMISUMERGIDO.esCompatibleCon(
                            TipoDrone.Zonas.EMBALSE_DE_INVESTIGACION))
            );
        }
    }

    @Nested
    @DisplayName("Zonas compatibles con BUCEADOR")
    class Buceador {

        @Test
        @DisplayName("Es compatible con las zonas de Prinplup esperadas")
        void zonasPrinplup() {
            Set<String> zonas = TipoDrone.BUCEADOR.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.EMBALSE_NORTE)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_SUR)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO))
            );
        }

        @Test
        @DisplayName("Es compatible con las zonas de Empoleon esperadas")
        void zonasEmpoleon() {
            Set<String> zonas = TipoDrone.BUCEADOR.zonasCompatibles();
            assertAll(
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.EMBALSE_DE_INVESTIGACION)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LAGUNA_DE_RESERVA)),
                    () -> assertTrue(zonas.contains(TipoDrone.Zonas.LABORATORIO_HIDRICO_CENTRAL))
            );
        }
    }

    @Nested
    @DisplayName("Comportamiento general")
    class ComportamientoGeneral {

        @Test
        @DisplayName("Zona nula o vacía no es compatible con ningún tipo")
        void zonaNulaOVacia() {
            for (TipoDrone tipo : TipoDrone.values()) {
                assertFalse(tipo.esCompatibleCon(null), tipo + " con null");
                assertFalse(tipo.esCompatibleCon(""), tipo + " con vacío");
                assertFalse(tipo.esCompatibleCon("   "), tipo + " con espacios");
            }
        }

        @Test
        @DisplayName("Zona inexistente no es compatible con ningún tipo")
        void zonaInexistente() {
            for (TipoDrone tipo : TipoDrone.values()) {
                assertFalse(tipo.esCompatibleCon("Zona Fantasma"), tipo.toString());
            }
        }

        @Test
        @DisplayName("Cada tipo tiene al menos 3 zonas compatibles")
        void alMenosTresZonas() {
            for (TipoDrone tipo : TipoDrone.values()) {
                assertTrue(tipo.zonasCompatibles().size() >= 3,
                        tipo + " tiene " + tipo.zonasCompatibles().size() + " zonas");
            }
        }
    }
}