import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PokeService } from '../../services/poke.service';

@Component({
  selector: 'app-types-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './types-chart.component.html',
  styleUrl: './types-chart.component.css'
})
export class TypesChartComponent implements OnInit {
  private pokeService = inject(PokeService);

  tabla        = signal<{ [atk: string]: { [def: string]: number } } | null>(null);
  traducciones = signal<{ [key: string]: string }>({});
  loading      = signal(true);

  // Orden específico de tipos para la matriz (estándar de la industria)
  tiposOrden = [
    'normal', 'fire', 'water', 'electric', 'grass', 'ice', 'fighting', 'poison',
    'ground', 'flying', 'psychic', 'bug', 'rock', 'ghost', 'dragon', 'dark', 'steel', 'fairy'
  ];

  hoverAtk = signal<string | null>(null);
  hoverDef = signal<string | null>(null);

  ngOnInit() {
    this.pokeService.getTraduccionesTipos().subscribe(t => this.traducciones.set(t));
    this.pokeService.getTablaTipos().subscribe({
      next: data => {
        this.tabla.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  getMult(atk: string, def: string): number {
    return this.tabla()?.[atk]?.[def] ?? 1.0;
  }

  formatMult(m: number): string {
    if (m === 2) return '2';
    if (m === 0.5) return '½';
    if (m === 0) return '0';
    return '';
  }

  getClass(m: number): string {
    if (m === 2) return 'super-effective';
    if (m === 0.5) return 'not-very-effective';
    if (m === 0) return 'no-effect';
    return 'neutral';
  }
}
