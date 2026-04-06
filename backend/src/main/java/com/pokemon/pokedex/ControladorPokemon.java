package com.pokemon.pokedex;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = "*") // Permitimos a Angular interactuar con esta API
public class ControladorPokemon {

    private final ServicioPokemon servicioPokemon;

    public ControladorPokemon(ServicioPokemon servicioPokemon) {
        this.servicioPokemon = servicioPokemon;
    }

    @GetMapping("/{identificador}")
    public ModeloPokemon obtenerPokemon(@PathVariable String identificador) {
        return servicioPokemon.obtenerPokemonPorNombre(identificador);
    }

    @GetMapping("/generacion/{idGen}")
    public java.util.List<MiniPokemon> obtenerGeneracion(@PathVariable Integer idGen) {
        return servicioPokemon.obtenerGeneracion(idGen);
    }
}
