import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8081/api'; // API URL'iniz

  constructor(private http: HttpClient) { }

  // Mock veriler burada - gerçek API bağlantısı için yorum satırlarını kaldırın
  getUsers(): Observable<any[]> {
    // Gerçek API çağrısı
    // return this.http.get<any[]>(`${this.apiUrl}/admin/users`);

    // Mock veri
    return of([
      { id: 1, username: 'johndoe', email: 'john@example.com', role: 'USER', status: 'Aktif' },
      { id: 2, username: 'janesmith', email: 'jane@example.com', role: 'SELLER', status: 'Aktif' },
      { id: 3, username: 'admin', email: 'admin@example.com', role: 'ADMIN', status: 'Aktif' }
    ]).pipe(delay(800));
  }

  getAllProducts(): Observable<any[]> {
    // Gerçek API çağrısı
    // return this.http.get<any[]>(`${this.apiUrl}/admin/products`);

    // Mock veri
    return of([
      { id: 1, name: 'Gaming Laptop', price: 15999.99, stock: 20, sellerId: 2, sellerName: 'Jane Smith' },
      { id: 2, name: 'Akıllı Telefon', price: 9999.99, stock: 50, sellerId: 2, sellerName: 'Jane Smith' },
      { id: 3, name: 'Tablet', price: 4999.99, stock: 30, sellerId: 4, sellerName: 'Mike Johnson' }
    ]).pipe(delay(800));
  }

  toggleUserStatus(userId: number, active: boolean): Observable<any> {
    // return this.http.patch<any>(`${this.apiUrl}/admin/users/${userId}`, { active });
    return of({ status: 'success', message: 'Kullanıcı durumu güncellendi' }).pipe(delay(1000));
  }

  deleteUser(userId: number): Observable<any> {
    // return this.http.delete<any>(`${this.apiUrl}/admin/users/${userId}`);
    return of({ status: 'success', message: 'Kullanıcı silindi' }).pipe(delay(1000));
  }

  deleteProduct(productId: number): Observable<any> {
    // return this.http.delete<any>(`${this.apiUrl}/admin/products/${productId}`);
    return of({ status: 'success', message: 'Ürün silindi' }).pipe(delay(1000));
  }
}
