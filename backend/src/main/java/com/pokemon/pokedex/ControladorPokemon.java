package com.pokemon.pokedex;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = "*")
public class ControladorPokemon {

    private final ServicioPokemon servicioPokemon;

    public ControladorPokemon(ServicioPokemon servicioPokemon) {
        this.servicioPokemon = servicioPokemon;
    }

    @GetMapping("/{identificador}")
    public ModeloPokemon obtenerPokemon(@PathVariable String identificador) {
        return servicioPokemon.obtenerPokemonPorNombre(identificador);
    }

    @GetMapping("/{identificador}/movimientos")
    public Map<String, List<ModeloMovimiento>> obtenerMovimientos(@PathVariable String identificador) {
        return servicioPokemon.obtenerMovimientos(identificador);
    }

    @GetMapping("/generacion/{idGen}")
    public List<MiniPokemon> obtenerGeneracion(@PathVariable Integer idGen) {
        return servicioPokemon.obtenerGeneracion(idGen);
    }

    @GetMapping("/tipos/tabla")
    public Map<String, Map<String, Double>> obtenerTablaTipos() {
        return TablaEfectividad.getTablaCompleta();
    }

    @GetMapping("/tipos/traducciones")
    public Map<String, String> obtenerTraduccionesTipos() {
        return DiccionarioTipos.getTraduccionesMap();
    }

    @GetMapping("/suggestions")
    public List<SuggestionDTO> obtenerSugerencias() {
        return servicioPokemon.obtenerSugerencias();
    }
}
