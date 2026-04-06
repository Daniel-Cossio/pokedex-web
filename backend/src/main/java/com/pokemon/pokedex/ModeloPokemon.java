package com.pokemon.pokedex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloPokemon {
    // Básicos
    private Integer id;
    private String nombre;
    private String imagen;
    private Double altura; // Transformado a Metros
    private Double peso;   // Transformado a kg
    
    // Tipos interactivos (Español para texto, Inglés para CSS)
    private List<String> tiposEspanol;
    private List<String> tiposOriginales;
    
    // Enciclopedia Stats (Extension Pro)
    private List<String> habilidades;
    private Map<String, Integer> estadisticasBase; // {"Salud": 45, "Ataque": 49...}
    
    // Datos de la Especie (-species logic)
    private String descripcion;
    private String generacion;
    private List<String> gruposHuevo;
    
    // Evoluciones (-chain logic)
    private String preEvolucion; // Si viene de alguno (Pikachu -> Pichu)
    private List<String> evolucionesFuturas; // Lista de siguientes formas
}
