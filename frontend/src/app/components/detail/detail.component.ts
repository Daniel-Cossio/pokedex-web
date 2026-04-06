import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PokeService, ModeloPokemon } from '../../services/poke.service';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detail.component.html',
  styleUrl: './detail.component.css'
})
export class DetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pokeService = inject(PokeService);

  pokemon = signal<ModeloPokemon | null>(null);
  loading = signal<boolean>(true);
  error = signal<boolean>(false);

  // Helper to convert Object to KeyValue array for the template
  objectKeys = Object.keys;

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.cargarPokemon(id);
      }
    });
  }

  cargarPokemon(id: string) {
    this.loading.set(true);
    this.error.set(false);
    this.pokeService.getPokemonInfo(id).subscribe({
      next: (data) => {
        this.pokemon.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      }
    });
  }

  volver() {
    this.router.navigate(['/']);
  }
}
