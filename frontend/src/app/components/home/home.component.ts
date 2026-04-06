import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PokeService, MiniPokemon } from '../../services/poke.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private pokeService = inject(PokeService);
  private router = inject(Router);

  generaciones = [1, 2, 3, 4, 5, 6, 7, 8, 9];
  genActiva = signal<number>(1);
  pokemonList = signal<MiniPokemon[]>([]);
  loading = signal<boolean>(false);

  ngOnInit() {
    this.cargarGeneracion(1);
  }

  cargarGeneracion(genId: number) {
    this.genActiva.set(genId);
    this.loading.set(true);
    this.pokemonList.set([]);

    this.pokeService.getGeneracion(genId).subscribe({
      next: (data) => {
        this.pokemonList.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  verDetalle(id: number) {
    this.router.navigate(['/pokemon', id]);
  }
}
