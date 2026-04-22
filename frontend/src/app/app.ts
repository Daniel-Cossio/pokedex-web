import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { PokeService, SuggestionDTO } from './services/poke.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private router = inject(Router);
  private pokeService = inject(PokeService);

  busqueda = signal('');
  allSuggestions = signal<SuggestionDTO[]>([]);
  showSuggestions = signal(false);

  // Sugerencias filtradas (máximo 8 para no saturar la vista)
  filteredSuggestions = computed(() => {
    const q = this.busqueda().toLowerCase().trim();
    if (q.length < 2) return [];
    return this.allSuggestions()
      .filter(s => s.nombre.toLowerCase().includes(q) || s.id.toString().includes(q))
      .slice(0, 8);
  });

  ngOnInit() {
    this.pokeService.getSuggestions().subscribe({
      next: (data) => this.allSuggestions.set(data),
      error: (err) => console.error('Error cargando sugerencias:', err)
    });
  }

  buscar() {
    const q = this.busqueda().trim();
    if (!q) return;
    this.navegar(q);
  }

  navegar(idOrName: string | number) {
    this.router.navigate(['/pokemon', idOrName.toString().toLowerCase()]);
    this.busqueda.set('');
    this.showSuggestions.set(false);
  }

  irAHome() {
    this.router.navigate(['/']);
  }

  onInputFocus() {
    this.showSuggestions.set(true);
  }

  onInputBlur() {
    // Retraso para permitir que el click en la sugerencia se procese antes de ocultar el menú
    setTimeout(() => this.showSuggestions.set(false), 200);
  }
}
