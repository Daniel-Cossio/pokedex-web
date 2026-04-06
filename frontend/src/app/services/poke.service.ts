import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MiniPokemon {
  id: number;
  nombre: string;
  imagen: string;
}

export interface ModeloPokemon {
  id: number;
  nombre: string;
  imagen: string;
  altura: number;
  peso: number;
  tiposEspanol: string[];
  tiposOriginales: string[];
  habilidades: string[];
  estadisticasBase: { [key: string]: number };
  descripcion: string;
  generacion: string;
  gruposHuevo: string[];
  preEvolucion: string | null;
  evolucionesFuturas: string[];
}

@Injectable({
  providedIn: 'root'
})
export class PokeService {
  private http = inject(HttpClient);
  private backendUrl = 'http://localhost:8080/api/pokemon';

  getPokemonInfo(identificador: string | number): Observable<ModeloPokemon> {
    return this.http.get<ModeloPokemon>(`${this.backendUrl}/${identificador.toString().toLowerCase()}`);
  }

  getGeneracion(idGen: number): Observable<MiniPokemon[]> {
    return this.http.get<MiniPokemon[]>(`${this.backendUrl}/generacion/${idGen}`);
  }
}
