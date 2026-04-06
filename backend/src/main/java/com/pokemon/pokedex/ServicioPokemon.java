package com.pokemon.pokedex;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioPokemon {

    private final RestClient restClient;

    public ServicioPokemon() {
        this.restClient = RestClient.create("https://pokeapi.co/api/v2/");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENDPOINT 1: Detalle completo del Pokémon (sin movimientos)
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public ModeloPokemon obtenerPokemonPorNombre(String identificador) {

        // 1. Datos Base
        Map<String, Object> baseJson = this.restClient.get()
                .uri("pokemon/{id}", identificador.toLowerCase())
                .retrieve()
                .body(Map.class);

        String nombre = capitalizar((String) baseJson.get("name"));
        Integer id    = (Integer) baseJson.get("id");
        Double  altura = ((Integer) baseJson.get("height")) / 10.0;
        Double  peso   = ((Integer) baseJson.get("weight")) / 10.0;

        Map<String, Object> sprites = (Map<String, Object>) baseJson.get("sprites");
        String imagen = (String) ((Map<String, Object>) ((Map<String, Object>) sprites.get("other")).get("official-artwork")).get("front_default");

        // Tipos
        List<String> tiposEspanol    = new ArrayList<>();
        List<String> tiposOriginales = new ArrayList<>();
        List<Map<String, Object>> typesList = (List<Map<String, Object>>) baseJson.get("types");
        for (Map<String, Object> tNode : typesList) {
            String tName = (String) ((Map<String, Object>) tNode.get("type")).get("name");
            tiposOriginales.add(tName);
            tiposEspanol.add(DiccionarioTipos.traducir(tName));
        }

        // Habilidades (con descripción desde /ability/{name})
        List<ModeloHabilidad> habilidades = new ArrayList<>();
        List<Map<String, Object>> abilList = (List<Map<String, Object>>) baseJson.get("abilities");
        for (Map<String, Object> aNode : abilList) {
            String abilName  = (String) ((Map<String, Object>) aNode.get("ability")).get("name");
            boolean esOculta = Boolean.TRUE.equals(aNode.get("is_hidden"));
            habilidades.add(obtenerHabilidad(abilName, esOculta));
        }

        // Estadísticas Base
        Map<String, Integer> stats = new LinkedHashMap<>();
        List<Map<String, Object>> statList = (List<Map<String, Object>>) baseJson.get("stats");
        for (Map<String, Object> sNode : statList) {
            String  sName  = (String)  ((Map<String, Object>) sNode.get("stat")).get("name");
            Integer sValue = (Integer) sNode.get("base_stat");
            stats.put(traducirStat(sName), sValue);
        }

        // Efectividad de tipos
        TablaEfectividad.ResultadoEfectividad efectividad = TablaEfectividad.calcular(tiposOriginales);

        ModeloPokemon modelo = new ModeloPokemon();
        modelo.setId(id);
        modelo.setNombre(nombre);
        modelo.setImagen(imagen);
        modelo.setAltura(altura);
        modelo.setPeso(peso);
        modelo.setTiposEspanol(tiposEspanol);
        modelo.setTiposOriginales(tiposOriginales);
        modelo.setEstadisticasBase(stats);
        modelo.setHabilidades(habilidades);
        modelo.setDescripcion("Sin descripción");
        modelo.setGeneracion("Desconocida");
        modelo.setGruposHuevo(new ArrayList<>());
        modelo.setGenero("Desconocido");
        modelo.setPreEvolucion(null);
        modelo.setEvolucionesFuturas(new ArrayList<>());
        modelo.setDebilidades(efectividad.debilidades());
        modelo.setResistencias(efectividad.resistencias());
        modelo.setInmunidades(efectividad.inmunidades());
        modelo.setTraduccionTipos(DiccionarioTipos.getTraduccionesMap());

        // 2. Especie
        try {
            Map<String, Object> speciesJson = this.restClient.get()
                    .uri("pokemon-species/{id}", id)
                    .retrieve()
                    .body(Map.class);

            modelo.setGeneracion(traducirGeneracion((String) ((Map<String, Object>) speciesJson.get("generation")).get("name")));

            // Género
            Integer genderRate = (Integer) speciesJson.get("gender_rate");
            modelo.setGenero(traducirGenero(genderRate));

            // Grupos Huevo
            List<String> huevos = new ArrayList<>();
            List<Map<String, Object>> eggGroups = (List<Map<String, Object>>) speciesJson.get("egg_groups");
            if (eggGroups != null) {
                for (Map<String, Object> e : eggGroups) huevos.add(traducirGrupoHuevo((String) e.get("name")));
            }
            modelo.setGruposHuevo(huevos);

            // Pre-evolución
            Map<String, Object> evolvesFrom = (Map<String, Object>) speciesJson.get("evolves_from_species");
            if (evolvesFrom != null) {
                modelo.setPreEvolucion(capitalizar((String) evolvesFrom.get("name")));
            }

            // Descripción en español
            List<Map<String, Object>> flavorText = (List<Map<String, Object>>) speciesJson.get("flavor_text_entries");
            for (Map<String, Object> f : flavorText) {
                if ("es".equals(((Map<String, Object>) f.get("language")).get("name"))) {
                    modelo.setDescripcion(((String) f.get("flavor_text")).replace("\n", " ").replace("\f", " "));
                    break;
                }
            }

            // 3. Cadena Evolutiva
            Map<String, Object> evoChain = (Map<String, Object>) speciesJson.get("evolution_chain");
            if (evoChain != null) {
                String[] parts  = ((String) evoChain.get("url")).split("/");
                String   chainId = parts[parts.length - 1];

                Map<String, Object> evoData = this.restClient.get()
                        .uri("evolution-chain/{id}", chainId)
                        .retrieve()
                        .body(Map.class);

                List<String> evolucionesFuturas = new ArrayList<>();
                recorrerCadenaEvolutiva((Map<String, Object>) evoData.get("chain"), evolucionesFuturas, nombre.toLowerCase());
                modelo.setEvolucionesFuturas(evolucionesFuturas);
            }

        } catch (Exception e) {
            System.err.println("Fallo al obtener especie/evolución: " + e.getMessage());
        }

        return modelo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENDPOINT 2: Movimientos del Pokémon (llamada separada para no ralentizar)
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public Map<String, List<ModeloMovimiento>> obtenerMovimientos(String identificador) {

        Map<String, Object> baseJson = this.restClient.get()
                .uri("pokemon/{id}", identificador.toLowerCase())
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> movesRaw = (List<Map<String, Object>>) baseJson.get("moves");

        // Mapas por método (deduplica por nombre de movimiento tomando la última entrada)
        Map<String, String[]> porMetodo = new LinkedHashMap<>(); // moveName -> [method, level]

        for (Map<String, Object> mNode : movesRaw) {
            String moveName = (String) ((Map<String, Object>) mNode.get("move")).get("name");
            List<Map<String, Object>> vgd = (List<Map<String, Object>>) mNode.get("version_group_details");
            if (vgd == null || vgd.isEmpty()) continue;

            // Usamos el último version group detail (más reciente)
            Map<String, Object> latestVGD = vgd.get(vgd.size() - 1);
            String method = (String) ((Map<String, Object>) latestVGD.get("move_learn_method")).get("name");
            String level  = String.valueOf(latestVGD.get("level_learned_at"));

            // Mantener el de método más importante si el movimiento ya estaba
            porMetodo.put(moveName, new String[]{method, level});
        }

        // Agrupar por método y ordenar
        Map<String, List<ModeloMovimiento>> resultado = new LinkedHashMap<>();
        String[] metodosOrden = {"level-up", "machine", "egg", "tutor"};
        String[] metodosEsp   = {"nivel", "maquina", "huevo", "tutor"};

        for (int i = 0; i < metodosOrden.length; i++) {
            String method    = metodosOrden[i];
            String metodoEsp = metodosEsp[i];

            List<Map.Entry<String, String[]>> filtrados = porMetodo.entrySet().stream()
                    .filter(e -> method.equals(e.getValue()[0]))
                    .collect(Collectors.toList());

            // Limitar cantidad por método
            int limite = method.equals("machine") ? 50 : 60;
            if (filtrados.size() > limite) filtrados = filtrados.subList(0, limite);

            List<ModeloMovimiento> movimientos = filtrados.parallelStream()
                    .map(entry -> {
                        String moveName = entry.getKey();
                        int nivel = method.equals("level-up") ? Integer.parseInt(entry.getValue()[1]) : 0;
                        return obtenerDetalleMovimiento(moveName, metodoEsp, nivel);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Ordenar nivel-up por nivel
            if (method.equals("level-up")) {
                movimientos.sort(Comparator.comparingInt(m -> m.getNivel() != null ? m.getNivel() : 0));
            } else {
                movimientos.sort(Comparator.comparing(m -> m.getNombre() != null ? m.getNombre() : ""));
            }

            if (!movimientos.isEmpty()) resultado.put(metodoEsp, movimientos);
        }

        return resultado;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENDPOINT 3: Grilla de Generaciones
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<MiniPokemon> obtenerGeneracion(Integer idGen) {
        Map<String, Object> genJson = this.restClient.get()
                .uri("generation/{id}", idGen)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> species = (List<Map<String, Object>>) genJson.get("pokemon_species");
        List<MiniPokemon> lista = new ArrayList<>();

        for (Map<String, Object> s : species) {
            String   name  = (String) s.get("name");
            String   url   = (String) s.get("url");
            String[] parts = url.split("/");
            Integer  id    = Integer.parseInt(parts[parts.length - 1]);
            String   img   = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
            lista.add(new MiniPokemon(id, capitalizar(name), img));
        }

        lista.sort(Comparator.comparing(MiniPokemon::getId));
        return lista;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private ModeloHabilidad obtenerHabilidad(String nombre, boolean esOculta) {
        try {
            Map<String, Object> json = this.restClient.get()
                    .uri("ability/{name}", nombre)
                    .retrieve()
                    .body(Map.class);

            // Nombre en español
            String nombreEs = capitalizar(nombre.replace("-", " "));
            List<Map<String, Object>> names = (List<Map<String, Object>>) json.get("names");
            if (names != null) {
                for (Map<String, Object> n : names) {
                    if ("es".equals(((Map<String, Object>) n.get("language")).get("name"))) {
                        nombreEs = (String) n.get("name");
                        break;
                    }
                }
            }

            // Descripción en español (flavor texts)
            String desc = null;
            List<Map<String, Object>> flavors = (List<Map<String, Object>>) json.get("flavor_text_entries");
            if (flavors != null) {
                for (int i = flavors.size() - 1; i >= 0; i--) {
                    Map<String, Object> f = flavors.get(i);
                    if ("es".equals(((Map<String, Object>) f.get("language")).get("name"))) {
                        desc = (String) f.get("flavor_text");
                        break;
                    }
                }
            }
            // Fallback a inglés si no hay español
            if (desc == null && flavors != null) {
                for (int i = flavors.size() - 1; i >= 0; i--) {
                    Map<String, Object> f = flavors.get(i);
                    if ("en".equals(((Map<String, Object>) f.get("language")).get("name"))) {
                        desc = (String) f.get("flavor_text");
                        break;
                    }
                }
            }

            return new ModeloHabilidad(nombreEs, nombre, esOculta, desc != null ? desc : "Sin descripción disponible.");
        } catch (Exception e) {
            String fallback = capitalizar(nombre.replace("-", " "));
            return new ModeloHabilidad(fallback, nombre, esOculta, "Sin descripción disponible.");
        }
    }

    @SuppressWarnings("unchecked")
    private ModeloMovimiento obtenerDetalleMovimiento(String nombre, String metodo, int nivel) {
        try {
            Map<String, Object> json = this.restClient.get()
                    .uri("move/{name}", nombre)
                    .retrieve()
                    .body(Map.class);

            // Nombre en español
            String nombreEs = capitalizar(nombre.replace("-", " "));
            List<Map<String, Object>> names = (List<Map<String, Object>>) json.get("names");
            if (names != null) {
                for (Map<String, Object> n : names) {
                    if ("es".equals(((Map<String, Object>) n.get("language")).get("name"))) {
                        nombreEs = (String) n.get("name");
                        break;
                    }
                }
            }

            // Descripción en español (última entrada es la más reciente)
            String desc = null;
            List<Map<String, Object>> flavors = (List<Map<String, Object>>) json.get("flavor_text_entries");
            if (flavors != null) {
                for (int i = flavors.size() - 1; i >= 0; i--) {
                    Map<String, Object> f = flavors.get(i);
                    if ("es".equals(((Map<String, Object>) f.get("language")).get("name"))) {
                        desc = ((String) f.get("flavor_text")).replace("\n", " ").replace("\f", " ");
                        break;
                    }
                }
                if (desc == null) {
                    for (int i = flavors.size() - 1; i >= 0; i--) {
                        Map<String, Object> f = flavors.get(i);
                        if ("en".equals(((Map<String, Object>) f.get("language")).get("name"))) {
                            desc = ((String) f.get("flavor_text")).replace("\n", " ").replace("\f", " ");
                            break;
                        }
                    }
                }
            }

            String tipoOriginal = (String) ((Map<String, Object>) json.get("type")).get("name");
            String categoriaRaw = (String) ((Map<String, Object>) json.get("damage_class")).get("name");

            Integer poder     = (Integer) json.get("power");
            Integer precision = (Integer) json.get("accuracy");
            Integer pp        = (Integer) json.get("pp");

            return new ModeloMovimiento(
                nombreEs, tipoOriginal, DiccionarioTipos.traducir(tipoOriginal),
                poder, precision, pp, nivel == 0 ? null : nivel,
                desc != null ? desc : "Sin descripción.",
                traducirCategoria(categoriaRaw), metodo
            );
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void recorrerCadenaEvolutiva(Map<String, Object> chain, List<String> evos, String currentName) {
        String speName = (String) ((Map<String, Object>) chain.get("species")).get("name");
        List<Map<String, Object>> evolvesTo = (List<Map<String, Object>>) chain.get("evolves_to");

        if (!speName.equalsIgnoreCase(currentName) && !evos.contains(capitalizar(speName))) {
            evos.add(capitalizar(speName));
        }
        if (evolvesTo != null) {
            for (Map<String, Object> next : evolvesTo) recorrerCadenaEvolutiva(next, evos, currentName);
        }
    }

    // ─── Diccionarios ───────────────────────────────────────────────────────

    private String capitalizar(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String traducirGeneracion(String raw) {
        if (raw == null) return "Desconocida";
        // "generation-ii" → "Generación II"
        String[] parts = raw.split("-");
        if (parts.length >= 2) return "Generación " + parts[1].toUpperCase();
        return capitalizar(raw);
    }

    private String traducirGenero(Integer genderRate) {
        if (genderRate == null || genderRate == -1) return "Sin género";
        if (genderRate == 0) return "♂ 100%";
        if (genderRate == 8) return "♀ 100%";
        double femaleChance = (genderRate / 8.0) * 100;
        double maleChance   = 100 - femaleChance;
        return String.format("♂ %.1f%% / ♀ %.1f%%", maleChance, femaleChance);
    }

    private String traducirGrupoHuevo(String raw) {
        switch (raw) {
            case "monster":      return "Monstruo";
            case "water1":       return "Acuático 1";
            case "water2":       return "Acuático 2";
            case "water3":       return "Acuático 3";
            case "bug":          return "Bicho";
            case "flying":       return "Volador";
            case "field":        return "Campo";
            case "fairy":        return "Hada";
            case "grass":        return "Planta";
            case "human-shape":  return "Humanoide";
            case "mineral":      return "Mineral";
            case "amorphous":    return "Amorfo";
            case "ditto":        return "Ditto";
            case "dragon":       return "Dragón";
            case "undiscovered": return "Desconocido";
            default:             return capitalizar(raw);
        }
    }

    private String traducirStat(String stat) {
        switch (stat) {
            case "hp":               return "Salud";
            case "attack":           return "Ataque";
            case "defense":          return "Defensa";
            case "special-attack":   return "Ataque Esp.";
            case "special-defense":  return "Defensa Esp.";
            case "speed":            return "Velocidad";
            default:                 return capitalizar(stat);
        }
    }

    private String traducirCategoria(String raw) {
        switch (raw) {
            case "physical": return "Físico";
            case "special":  return "Especial";
            case "status":   return "Estado";
            default:         return capitalizar(raw);
        }
    }
}
