package com.pokemon.pokedex;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;

@Service
public class ServicioPokemon {

    private final RestClient restClient;

    public ServicioPokemon() {
        this.restClient = RestClient.create("https://pokeapi.co/api/v2/");
    }

    // Endpoint 1: Detalle Masivo Pro
    public ModeloPokemon obtenerPokemonPorNombre(String identificador) {
        // 1. Datos Base
        Map baseJson = this.restClient.get()
                .uri("pokemon/{id}", identificador.toLowerCase())
                .retrieve()
                .body(Map.class);
                
        String nombre = capitalizar((String) baseJson.get("name"));
        Integer id = (Integer) baseJson.get("id");
        Double altura = ((Integer) baseJson.get("height")) / 10.0;
        Double peso = ((Integer) baseJson.get("weight")) / 10.0;
        
        Map sprites = (Map) baseJson.get("sprites");
        String imagen = (String) ((Map)((Map)sprites.get("other")).get("official-artwork")).get("front_default");

        // Tipos
        List<String> tiposEspanol = new ArrayList<>();
        List<String> tiposOriginales = new ArrayList<>();
        List<Map> typesList = (List<Map>) baseJson.get("types");
        for (Map tNode : typesList) {
            String tName = (String) ((Map)tNode.get("type")).get("name");
            tiposOriginales.add(tName);
            tiposEspanol.add(DiccionarioTipos.traducir(tName));
        }

        // Habilidades
        List<String> habilidades = new ArrayList<>();
        List<Map> abilList = (List<Map>) baseJson.get("abilities");
        for (Map aNode : abilList) {
            habilidades.add(capitalizar((String) ((Map)aNode.get("ability")).get("name")));
        }

        // Stats Base
        Map<String, Integer> stats = new HashMap<>();
        List<Map> statList = (List<Map>) baseJson.get("stats");
        for (Map sNode : statList) {
            String sName = (String) ((Map)sNode.get("stat")).get("name");
            Integer sValue = (Integer) sNode.get("base_stat");
            stats.put(traducirStat(sName), sValue);
        }

        ModeloPokemon modelo = new ModeloPokemon(id, nombre, imagen, altura, peso,
            tiposEspanol, tiposOriginales, habilidades, stats,
            "Sin descripción", "Desconocida", new ArrayList<>(), "Ninguna", new ArrayList<>()
        );

        // 2. Especies (Para descripciones y evoluciones)
        try {
            Map speciesJson = this.restClient.get()
                    .uri("pokemon-species/{id}", id)
                    .retrieve()
                    .body(Map.class);
                    
            modelo.setGeneracion(capitalizar((String) ((Map)speciesJson.get("generation")).get("name")).replace("-", " "));
            
            // Grupos huevo
            List<String> huevos = new ArrayList<>();
            List<Map> eggGroups = (List<Map>) speciesJson.get("egg_groups");
            if (eggGroups != null) {
                for (Map e : eggGroups) huevos.add(capitalizar((String) e.get("name")));
            }
            modelo.setGruposHuevo(huevos);
            
            // Pre-evolucion
            Map evolvesFrom = (Map) speciesJson.get("evolves_from_species");
            if (evolvesFrom != null) {
                modelo.setPreEvolucion(capitalizar((String) evolvesFrom.get("name")));
            }
            
            // Descripcion
            List<Map> flavorText = (List<Map>) speciesJson.get("flavor_text_entries");
            for (Map f : flavorText) {
                if ("es".equals(((Map)f.get("language")).get("name"))) {
                    modelo.setDescripcion(((String) f.get("flavor_text")).replace("\n", " ").replace("\f", " "));
                    break;
                }
            }

            // 3. Cadena Evolutiva
            Map evoChain = (Map) speciesJson.get("evolution_chain");
            if (evoChain != null) {
                String evoUrl = (String) evoChain.get("url");
                String[] parts = evoUrl.split("/");
                String chainId = parts[parts.length - 1];
                
                Map evoData = this.restClient.get()
                        .uri("evolution-chain/{id}", chainId)
                        .retrieve()
                        .body(Map.class);
                        
                List<String> evolucionesFuturas = new ArrayList<>();
                recorrerCadenaEvolutiva((Map) evoData.get("chain"), evolucionesFuturas, nombre.toLowerCase());
                modelo.setEvolucionesFuturas(evolucionesFuturas);
            }

        } catch (Exception e) {
            System.err.println("Fallo al obtener especies/evolución: " + e.getMessage());
        }

        return modelo;
    }
    
    private void recorrerCadenaEvolutiva(Map chain, List<String> evos, String currentName) {
        String speName = (String) ((Map)chain.get("species")).get("name");
        List<Map> evolvesTo = (List<Map>) chain.get("evolves_to");
        
        if (!speName.equalsIgnoreCase(currentName) && !evos.contains(capitalizar(speName))) {
            evos.add(capitalizar(speName));
        }

        if (evolvesTo != null && !evolvesTo.isEmpty()) {
            for (Map nextChain : evolvesTo) {
                recorrerCadenaEvolutiva(nextChain, evos, currentName);
            }
        }
    }

    // Endpoint 2: Grilla de Generaciones
    public List<MiniPokemon> obtenerGeneracion(Integer idGen) {
        Map genJson = this.restClient.get()
                .uri("generation/{id}", idGen)
                .retrieve()
                .body(Map.class);
                
        List<Map> species = (List<Map>) genJson.get("pokemon_species");
        List<MiniPokemon> lista = new ArrayList<>();
        
        for (Map s : species) {
            String name = (String) s.get("name");
            String url = (String) s.get("url");
            String[] splits = url.split("/");
            Integer id = Integer.parseInt(splits[splits.length - 1]);
            
            String imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
            lista.add(new MiniPokemon(id, capitalizar(name), imgUrl));
        }
        
        lista.sort(Comparator.comparing(MiniPokemon::getId));
        return lista;
    }

    private String capitalizar(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    private String traducirStat(String stat) {
        switch (stat) {
            case "hp": return "Salud";
            case "attack": return "Ataque";
            case "defense": return "Defensa";
            case "special-attack": return "Ataque Especial";
            case "special-defense": return "Defensa Especial";
            case "speed": return "Velocidad";
            default: return capitalizar(stat);
        }
    }
}
