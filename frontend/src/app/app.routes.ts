import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/stylist',
    pathMatch: 'full'
  },
  {
    path: 'stylist',
    loadComponent: () => import('./pages/stylist/stylist.component').then(m => m.StylistComponent)
  },
  {
    path: 'recommendations',
    loadComponent: () => import('./pages/recommendations/recommendations.component').then(m => m.RecommendationsComponent)
  },
  {
    path: 'recommendation/:id',
    loadComponent: () => import('./pages/recommendation-detail/recommendation-detail.component').then(m => m.RecommendationDetailComponent)
  },
  {
    path: '**',
    redirectTo: '/stylist'
  }
];
