package com.pokemon.pokedex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionDTO {
    private Integer id;
    private String nombre;
}
