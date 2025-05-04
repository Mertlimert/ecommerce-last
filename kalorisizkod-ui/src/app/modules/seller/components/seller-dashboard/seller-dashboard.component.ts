import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: '<div>Seller Dashboard</div>',
  styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent {
  constructor() { }
}
