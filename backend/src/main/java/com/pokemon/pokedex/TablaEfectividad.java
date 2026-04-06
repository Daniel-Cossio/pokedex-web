package com.pokemon.pokedex;

import java.util.*;

/**
 * Tabla de efectividad de tipos completa (Generación VI en adelante, incluye Hada).
 * Formato: tipo atacante -> {tipo defensor -> multiplicador}
 */
public class TablaEfectividad {

    private static final Map<String, Map<String, Double>> TABLA = new LinkedHashMap<>();

    static {
        String[] tipos = {
            "normal", "fighting", "flying", "poison", "ground", "rock",
            "bug", "ghost", "steel", "fire", "water", "grass",
            "electric", "psychic", "ice", "dragon", "dark", "fairy"
        };

        // Inicializar todo en 1.0
        for (String atk : tipos) {
            Map<String, Double> row = new LinkedHashMap<>();
            for (String def : tipos) row.put(def, 1.0);
            TABLA.put(atk, row);
        }

        // NORMAL
        set("normal","rock",0.5); set("normal","ghost",0.0); set("normal","steel",0.5);

        // FIGHTING
        set("fighting","normal",2.0); set("fighting","flying",0.5); set("fighting","poison",0.5);
        set("fighting","rock",2.0); set("fighting","bug",0.5); set("fighting","ghost",0.0);
        set("fighting","steel",2.0); set("fighting","psychic",0.5); set("fighting","ice",2.0);
        set("fighting","dark",2.0); set("fighting","fairy",0.5);

        // FLYING
        set("flying","fighting",2.0); set("flying","rock",0.5); set("flying","bug",2.0);
        set("flying","steel",0.5); set("flying","grass",2.0); set("flying","electric",0.5);

        // POISON
        set("poison","poison",0.5); set("poison","ground",0.5); set("poison","rock",0.5);
        set("poison","ghost",0.5); set("poison","steel",0.0);
        set("poison","grass",2.0); set("poison","fairy",2.0);

        // GROUND
        set("ground","flying",0.0); set("ground","bug",0.5); set("ground","grass",0.5);
        set("ground","fire",2.0); set("ground","electric",2.0); set("ground","poison",2.0);
        set("ground","rock",2.0); set("ground","steel",2.0);

        // ROCK
        set("rock","fighting",0.5); set("rock","ground",0.5); set("rock","steel",0.5);
        set("rock","flying",2.0); set("rock","bug",2.0); set("rock","fire",2.0); set("rock","ice",2.0);

        // BUG
        set("bug","fighting",0.5); set("bug","flying",0.5); set("bug","poison",0.5);
        set("bug","ghost",0.5); set("bug","steel",0.5); set("bug","fire",0.5); set("bug","fairy",0.5);
        set("bug","grass",2.0); set("bug","psychic",2.0); set("bug","dark",2.0);

        // GHOST
        set("ghost","normal",0.0); set("ghost","dark",0.5);
        set("ghost","psychic",2.0); set("ghost","ghost",2.0);

        // STEEL
        set("steel","steel",0.5); set("steel","fire",0.5); set("steel","water",0.5);
        set("steel","electric",0.5); set("steel","ice",2.0); set("steel","rock",2.0); set("steel","fairy",2.0);

        // FIRE
        set("fire","rock",0.5); set("fire","fire",0.5); set("fire","water",0.5); set("fire","dragon",0.5);
        set("fire","grass",2.0); set("fire","ice",2.0); set("fire","bug",2.0); set("fire","steel",2.0);

        // WATER
        set("water","water",0.5); set("water","grass",0.5); set("water","dragon",0.5);
        set("water","fire",2.0); set("water","ground",2.0); set("water","rock",2.0);

        // GRASS
        set("grass","flying",0.5); set("grass","poison",0.5); set("grass","bug",0.5);
        set("grass","steel",0.5); set("grass","fire",0.5); set("grass","grass",0.5); set("grass","dragon",0.5);
        set("grass","water",2.0); set("grass","ground",2.0); set("grass","rock",2.0);

        // ELECTRIC
        set("electric","grass",0.5); set("electric","electric",0.5); set("electric","dragon",0.5);
        set("electric","ground",0.0); set("electric","water",2.0); set("electric","flying",2.0);

        // PSYCHIC
        set("psychic","psychic",0.5); set("psychic","steel",0.5); set("psychic","dark",0.0);
        set("psychic","fighting",2.0); set("psychic","poison",2.0);

        // ICE
        set("ice","steel",0.5); set("ice","fire",0.5); set("ice","water",0.5); set("ice","ice",0.5);
        set("ice","grass",2.0); set("ice","ground",2.0); set("ice","flying",2.0); set("ice","dragon",2.0);

        // DRAGON
        set("dragon","steel",0.5); set("dragon","fairy",0.0); set("dragon","dragon",2.0);

        // DARK
        set("dark","fighting",0.5); set("dark","dark",0.5); set("dark","fairy",0.5);
        set("dark","psychic",2.0); set("dark","ghost",2.0);

        // FAIRY
        set("fairy","poison",0.5); set("fairy","steel",0.5); set("fairy","fire",0.5);
        set("fairy","dragon",2.0); set("fairy","dark",2.0); set("fairy","fighting",2.0);
    }

    private static void set(String atk, String def, double val) {
        TABLA.get(atk).put(def, val);
    }

    /**
     * Calcula todas las efectividades de tipos de ataque contra un Pokémon defensor.
     * Retorna tres mapas: debilidades (>1), resistencias (<1 y >0) e inmunidades (=0)
     */
    public static ResultadoEfectividad calcular(List<String> tiposDefensores) {
        Map<String, Double> debilidades  = new LinkedHashMap<>();
        Map<String, Double> resistencias = new LinkedHashMap<>();
        Map<String, String>  inmunidades  = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Double>> entry : TABLA.entrySet()) {
            String tipoAtk = entry.getKey();
            double mult = 1.0;
            for (String def : tiposDefensores) {
                Double v = entry.getValue().get(def);
                if (v != null) mult *= v;
            }
            if (mult == 0.0) {
                inmunidades.put(tipoAtk, DiccionarioTipos.traducir(tipoAtk));
            } else if (mult > 1.0) {
                debilidades.put(tipoAtk, mult);
            } else if (mult < 1.0) {
                resistencias.put(tipoAtk, mult);
            }
        }

        return new ResultadoEfectividad(debilidades, resistencias, inmunidades);
    }

    public record ResultadoEfectividad(
        Map<String, Double> debilidades,
        Map<String, Double> resistencias,
        Map<String, String>  inmunidades
    ) {}
}
