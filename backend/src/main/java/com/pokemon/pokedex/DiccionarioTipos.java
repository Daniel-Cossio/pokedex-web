package com.pokemon.pokedex;

public enum DiccionarioTipos {
    NORMAL("normal", "Normal"),
    FIGHTING("fighting", "Lucha"),
    FLYING("flying", "Volador"),
    POISON("poison", "Veneno"),
    GROUND("ground", "Tierra"),
    ROCK("rock", "Roca"),
    BUG("bug", "Bicho"),
    GHOST("ghost", "Fantasma"),
    STEEL("steel", "Acero"),
    FIRE("fire", "Fuego"),
    WATER("water", "Agua"),
    GRASS("grass", "Planta"),
    ELECTRIC("electric", "Eléctrico"),
    PSYCHIC("psychic", "Psíquico"),
    ICE("ice", "Hielo"),
    DRAGON("dragon", "Dragón"),
    DARK("dark", "Siniestro"),
    FAIRY("fairy", "Hada");

    private final String ingles;
    private final String espanol;

    DiccionarioTipos(String ingles, String espanol) {
        this.ingles = ingles;
        this.espanol = espanol;
    }

    public String getIngles() { return ingles; }
    public String getEspanol() { return espanol; }

    public static String traducir(String ingles) {
        if (ingles == null) return "Desconocido";
        for (DiccionarioTipos t : values()) {
            if (t.getIngles().equalsIgnoreCase(ingles)) {
                return t.getEspanol();
            }
        }
        return ingles.substring(0, 1).toUpperCase() + ingles.substring(1);
    }
}
