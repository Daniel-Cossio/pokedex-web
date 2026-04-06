import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PokeService, ModeloPokemon, ModeloMovimiento } from '../../services/poke.service';

type Tab = 'info' | 'habilidades' | 'movimientos' | 'efectividad';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detail.component.html',
  styleUrl: './detail.component.css'
})
export class DetailComponent implements OnInit {
  private route       = inject(ActivatedRoute);
  private router      = inject(Router);
  private pokeService = inject(PokeService);

  pokemon      = signal<ModeloPokemon | null>(null);
  loading      = signal(true);
  error        = signal(false);

  tabActiva         = signal<Tab>('info');
  movimientos       = signal<{ [m: string]: ModeloMovimiento[] } | null>(null);
  loadingMovs       = signal(false);
  tabMovActiva      = signal<string>('nivel');

  objectKeys = Object.keys;

  // Metadatos de tabs de movimientos con etiquetas
  metodosLabel: Record<string, string> = {
    nivel: '📈 Por Nivel', maquina: '💿 MT/MO', huevo: '🥚 Huevo', tutor: '🎓 Tutor'
  };

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) this.cargarPokemon(id);
    });
  }

  cargarPokemon(id: string) {
    this.loading.set(true);
    this.error.set(false);
    this.pokemon.set(null);
    this.movimientos.set(null);
    this.tabActiva.set('info');

    this.pokeService.getPokemonInfo(id).subscribe({
      next:  data => { this.pokemon.set(data); this.loading.set(false); },
      error: ()   => { this.error.set(true);  this.loading.set(false); }
    });
  }

  cambiarTab(tab: Tab) {
    this.tabActiva.set(tab);
    if (tab === 'movimientos' && this.movimientos() === null) {
      this.loadingMovs.set(true);
      const id = this.pokemon()?.id ?? this.route.snapshot.paramMap.get('id');
      this.pokeService.getMovimientos(id!).subscribe({
        next:  data => { this.movimientos.set(data); this.loadingMovs.set(false); },
        error: ()   => { this.movimientos.set({}); this.loadingMovs.set(false); }
      });
    }
  }

  metodosDisponibles(): string[] {
    return this.movimientos() ? this.objectKeys(this.movimientos()!) : [];
  }

  // Multiplier display helpers
  multLabel(v: number): string {
    return v === 4 ? '×4' : v === 2 ? '×2' : v === 0.5 ? '×½' : v === 0.25 ? '×¼' : `×${v}`;
  }

  volver() { this.router.navigate(['/']); }
}
