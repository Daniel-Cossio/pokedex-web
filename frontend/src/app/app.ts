import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterOutlet, RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private router = inject(Router);
  busqueda = '';

  buscar() {
    const q = this.busqueda.trim();
    if (!q) return;
    this.router.navigate(['/pokemon', q.toLowerCase()]);
    this.busqueda = '';
  }

  irAHome() {
    this.router.navigate(['/']);
  }
}
