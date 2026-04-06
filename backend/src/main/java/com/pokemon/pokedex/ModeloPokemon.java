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
    // ── Básicos ──────────────────────────────────────────────────────
    private Integer id;
    private String nombre;
    private String imagen;
    private Double altura;
    private Double peso;

    // ── Tipos ────────────────────────────────────────────────────────
    private List<String> tiposEspanol;
    private List<String> tiposOriginales;

    // ── Estadísticas de combate ───────────────────────────────────────
    private Map<String, Integer> estadisticasBase;

    // ── Habilidades (con descripción) ─────────────────────────────────
    private List<ModeloHabilidad> habilidades;

    // ── Especie ───────────────────────────────────────────────────────
    private String descripcion;
    private String generacion;
    private List<String> gruposHuevo;
    private String genero;

    // ── Evoluciones ───────────────────────────────────────────────────
    private String preEvolucion;
    private List<String> evolucionesFuturas;

    // ── Efectividad de Tipos ──────────────────────────────────────────
    private Map<String, Double> debilidades;    // tipo → ×2 o ×4
    private Map<String, Double> resistencias;   // tipo → ×0.5 o ×0.25
    private Map<String, String>  inmunidades;    // tipo → nombre español (×0)

    private Map<String, String> traduccionTipos; // tipo ingles -> tipo espanol
}
