import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MiniPokemon {
  id: number;
  nombre: string;
  imagen: string;
}

export interface ModeloHabilidad {
  nombre: string;
  nombreOriginal: string;
  esOculta: boolean;
  descripcion: string;
}

export interface ModeloMovimiento {
  nombre: string;
  tipo: string;
  tipoEspanol: string;
  poder: number | null;
  precision: number | null;
  pp: number;
  nivel: number | null;
  descripcion: string;
  categoria: string;
  metodo: string;
}

export interface ModeloPokemon {
  id: number;
  nombre: string;
  imagen: string;
  altura: number;
  peso: number;
  tiposEspanol: string[];
  tiposOriginales: string[];
  estadisticasBase: { [key: string]: number };
  habilidades: ModeloHabilidad[];
  descripcion: string;
  generacion: string;
  gruposHuevo: string[];
  genero: string;
  preEvolucion: string | null;
  evolucionesFuturas: string[];
  debilidades: { [tipo: string]: number };
  resistencias: { [tipo: string]: number };
  inmunidades: { [tipo: string]: string };
  traduccionTipos: { [tipo: string]: string };
}

@Injectable({ providedIn: 'root' })
export class PokeService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/pokemon';

  getPokemonInfo(id: string | number): Observable<ModeloPokemon> {
    return this.http.get<ModeloPokemon>(`${this.apiUrl}/${id.toString().toLowerCase()}`);
  }

  getMovimientos(id: string | number): Observable<{ [metodo: string]: ModeloMovimiento[] }> {
    return this.http.get<{ [metodo: string]: ModeloMovimiento[] }>(`${this.apiUrl}/${id}/movimientos`);
  }

  getGeneracion(idGen: number): Observable<MiniPokemon[]> {
    return this.http.get<MiniPokemon[]>(`${this.apiUrl}/generacion/${idGen}`);
  }
}
