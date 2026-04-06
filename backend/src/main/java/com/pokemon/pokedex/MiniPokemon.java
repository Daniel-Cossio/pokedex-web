package com.pokemon.pokedex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniPokemon {
    private Integer id;
    private String nombre;
    private String imagen;
}
