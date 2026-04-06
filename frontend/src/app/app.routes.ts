import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { DetailComponent } from './components/detail/detail.component';
import { TypesChartComponent } from './components/types-chart/types-chart.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'pokemon/:id', component: DetailComponent },
  { path: 'tipos', component: TypesChartComponent },
  { path: '**', redirectTo: '' }
];
