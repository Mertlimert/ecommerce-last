import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" routerLink="/">E-Ticaret</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/products" routerLinkActive="active">Ürünler</a>
            </li>

            <!-- Satıcı Menüsü -->
            <li class="nav-item dropdown" *ngIf="authService.isSeller()">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                Satıcı Paneli
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" routerLink="/seller/dashboard">Dashboard</a></li>
                <li><a class="dropdown-item" routerLink="/seller/products">Ürünlerim</a></li>
                <li><a class="dropdown-item" routerLink="/seller/orders">Siparişler</a></li>
              </ul>
            </li>

            <!-- Admin Menüsü -->
            <li class="nav-item dropdown" *ngIf="authService.isAdmin()">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                Admin Paneli
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" routerLink="/admin/dashboard">Dashboard</a></li>
                <li><a class="dropdown-item" routerLink="/admin/users">Kullanıcılar</a></li>
                <li><a class="dropdown-item" routerLink="/admin/products">Ürünler</a></li>
              </ul>
            </li>
          </ul>

          <!-- Kullanıcı Giriş/Çıkış -->
          <ul class="navbar-nav">
            <ng-container *ngIf="!(authService.isLoggedIn | async); else loggedIn">
              <li class="nav-item">
                <a class="nav-link" routerLink="/login">Giriş</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" routerLink="/register">Kayıt Ol</a>
              </li>
            </ng-container>

            <ng-template #loggedIn>
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                  {{ authService.getUserName() }}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li><a class="dropdown-item" routerLink="/profile">Profilim</a></li>
                  <li><a class="dropdown-item" routerLink="/cart-details">Sepetim</a></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><a class="dropdown-item" href="#" (click)="logout($event)">Çıkış</a></li>
                </ul>
              </li>
            </ng-template>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .dropdown-item.active, .dropdown-item:active {
      background-color: #343a40;
    }
  `]
})
export class NavbarComponent {
  constructor(public authService: AuthService) {}

  logout(event: Event) {
    event.preventDefault();
    this.authService.logout();
  }
}
