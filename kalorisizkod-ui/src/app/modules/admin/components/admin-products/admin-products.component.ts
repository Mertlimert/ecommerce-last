import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="admin-products">
      <h1>Ürün Yönetimi</h1>
      <router-outlet></router-outlet>
    </div>
  `,
  styleUrls: ['./admin-products.component.css']
})
export class AdminProductsComponent implements OnInit {
  products: any[] = [];
  loading: boolean = true;

  constructor() { }

  ngOnInit(): void {
    // Burada normalde bir servis üzerinden ürünler çekilecek
    setTimeout(() => {
      this.products = [
        { id: 1, name: 'Laptop', price: 4999.99, stock: 50, category: 'Elektronik' },
        { id: 2, name: 'Akıllı Telefon', price: 2999.99, stock: 100, category: 'Elektronik' },
        { id: 3, name: 'Tablet', price: 1999.99, stock: 30, category: 'Elektronik' },
        { id: 4, name: 'Kulaklık', price: 599.99, stock: 200, category: 'Elektronik' }
      ];
      this.loading = false;
    }, 800);
  }

  deleteProduct(id: number): void {
    // Silme işlemi burada gerçekleşecek
    this.products = this.products.filter(product => product.id !== id);
  }

  updateProduct(product: any): void {
    // Güncelleme işlemi burada gerçekleşecek
    console.log('Ürün güncelleniyor:', product);
  }
}
