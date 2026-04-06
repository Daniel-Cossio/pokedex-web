package com.pokemon.pokedex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloMovimiento {
    private String nombre;        // Nombre en español (o inglés si no hay traducción)
    private String tipo;          // Nombre original del tipo (para CSS)
    private String tipoEspanol;   // Nombre del tipo en español
    private Integer poder;        // null si es movimiento de estado
    private Integer precision;    // null si nunca falla
    private Integer pp;
    private Integer nivel;        // Solo para movimientos por nivel
    private String descripcion;   // Descripción en español
    private String categoria;     // "Físico", "Especial", "Estado"
    private String metodo;        // "nivel", "maquina", "huevo", "tutor"
}
