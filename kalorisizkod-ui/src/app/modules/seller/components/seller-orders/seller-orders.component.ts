import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-seller-orders',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './seller-orders.component.html',
  styleUrls: ['./seller-orders.component.css']
})
export class SellerOrdersComponent implements OnInit {
  orders: any[] = [];
  loading: boolean = true;
  filterStatus: string = 'all';

  constructor() { }

  ngOnInit(): void {
    // Normalde bir servis üzerinden siparişler çekilecek
    setTimeout(() => {
      this.orders = [
        { id: 1, customerName: 'Ali Yılmaz', totalPrice: 349.99, orderDate: '2023-11-01', status: 'PENDING', items: 3 },
        { id: 2, customerName: 'Ayşe Demir', totalPrice: 1249.50, orderDate: '2023-11-02', status: 'SHIPPED', items: 2 },
        { id: 3, customerName: 'Mehmet Kaya', totalPrice: 799.90, orderDate: '2023-11-03', status: 'DELIVERED', items: 1 },
        { id: 4, customerName: 'Zeynep Şahin', totalPrice: 124.99, orderDate: '2023-11-05', status: 'PENDING', items: 2 },
        { id: 5, customerName: 'Ahmet Özkan', totalPrice: 2499.00, orderDate: '2023-11-07', status: 'CANCELLED', items: 1 }
      ];
      this.loading = false;
    }, 800);
  }

  get filteredOrders(): any[] {
    if (this.filterStatus === 'all') {
      return this.orders;
    }
    return this.orders.filter(order => order.status === this.filterStatus);
  }

  updateOrderStatus(order: any, newStatus: string): void {
    order.status = newStatus;
    // Burada normalde bir API çağrısı yapılacak
  }

  getStatusBadgeClass(status: string): string {
    switch(status) {
      case 'PENDING': return 'bg-warning';
      case 'SHIPPED': return 'bg-info';
      case 'DELIVERED': return 'bg-success';
      case 'CANCELLED': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch(status) {
      case 'PENDING': return 'Beklemede';
      case 'SHIPPED': return 'Kargoya Verildi';
      case 'DELIVERED': return 'Teslim Edildi';
      case 'CANCELLED': return 'İptal Edildi';
      default: return status;
    }
  }
}
