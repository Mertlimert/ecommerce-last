import { Routes } from '@angular/router';

export const SELLER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/seller-dashboard/seller-dashboard.component')
      .then(m => m.SellerDashboardComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/seller-dashboard/seller-dashboard.component')
      .then(m => m.SellerDashboardComponent)
  },
  {
    path: 'products',
    loadComponent: () => import('./components/seller-products/seller-products.component')
      .then(m => m.SellerProductsComponent)
  },
  {
    path: 'orders',
    loadComponent: () => import('./components/seller-orders/seller-orders.component')
      .then(m => m.SellerOrdersComponent)
  }
];
