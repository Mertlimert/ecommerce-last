import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },
  {
    path: 'users',
    loadComponent: () => import('./components/admin-users/admin-users.component')
      .then(m => m.AdminUsersComponent)
  },
  {
    path: 'products',
    loadComponent: () => import('./components/admin-products/admin-products.component')
      .then(m => m.AdminProductsComponent)
  }
];
