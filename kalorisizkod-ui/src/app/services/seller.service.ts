import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private apiUrl = 'http://localhost:8081/api'; // API URL'iniz

  constructor(private http: HttpClient) { }

  // Mock veriler burada - gerçek API bağlantısı için yorum satırlarını kaldırın
  getSellerProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/products`);
  }

  getSellerOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders`);
  }

  // seller.service.ts
  addProduct(product: any): Observable<any> {
    // Backend'e gönderilecek veriyi düzenleyin
    const productToSend = {
      name: product.name,
      description: product.description,
      unitPrice: Number(product.price), // Sayı olarak gönder
      unitsInStock: Number(product.stock), // Sayı olarak gönder
      category: product.category,
      imageUrl: product.imageUrl,
      active: product.active
    };

    console.log('Gönderilen veri:', productToSend);

    return this.http.post<any>(`${this.apiUrl}/products`, productToSend);
  }

  updateProduct(id: number, product: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/products/${id}`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/products/${id}`);
  }
  updateSellerProfile(profileData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile`, profileData);
  }

  getSellerProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile`);
  }

  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/orders/${id}/status`, { status });
  }



}
