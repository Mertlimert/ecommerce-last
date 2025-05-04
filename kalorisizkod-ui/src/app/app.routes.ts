
import { Routes } from '@angular/router';
import { ProductListComponent } from './components/product-list/product-list.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { CartDetailsComponent } from './components/cart-details/cart-details.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { AuthGuard } from './guards/auth.guard';
import { UserRole } from './services/auth.service';

export const routes: Routes = [
  // Herkesin erişebileceği rotalar
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'category/:id', component: ProductListComponent },
  { path: 'search/:keyword', component: ProductListComponent },

  // Sadece giriş yapmış kullanıcıların erişebileceği rotalar
  {
    path: 'cart-details',
    component: CartDetailsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    canActivate: [AuthGuard]
  },

  // Sadece SELLER rolüne sahip kullanıcıların erişebileceği rotalar
  {
    path: 'seller',
    loadChildren: () => import('./modules/seller/seller.routes').then(m => m.SELLER_ROUTES),
    canActivate: [AuthGuard],
    data: { roles: [UserRole.SELLER, UserRole.ADMIN] }
  },

  // Sadece ADMIN rolüne sahip kullanıcıların erişebileceği rotalar
  {
    path: 'admin',
    loadChildren: () => import('./modules/admin/admin.routes').then(m => m.ADMIN_ROUTES),
    canActivate: [AuthGuard],
    data: { roles: [UserRole.ADMIN] }
  },

  // Varsayılan rota
  { path: '', redirectTo: '/products', pathMatch: 'full' },
  { path: '**', redirectTo: '/products', pathMatch: 'full' }
];
