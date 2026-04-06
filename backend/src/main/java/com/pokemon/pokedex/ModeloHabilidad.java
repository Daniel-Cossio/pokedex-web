package com.pokemon.pokedex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloHabilidad {
    private String nombre;
    private String nombreOriginal;
    private boolean esOculta;
    private String descripcion;
}
