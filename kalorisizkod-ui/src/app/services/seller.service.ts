import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay, switchMap, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private apiUrl = 'http://localhost:8081/api'; // API URL'iniz

  constructor(private http: HttpClient) { }


  checkSellerProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/seller/profile`).pipe(
      catchError(error => {
        if (error.status === 404) {
          // Profil bulunamadı, yeni profil oluştur
          return this.createSellerProfile();
        }
        return throwError(() => error);
      })
    );
  }
  createSellerProfile(): Observable<any> {
    // Varsayılan profil bilgileri
    const defaultProfile = {
      businessName: 'Satıcı İşletmem',
      description: 'Yeni satıcı hesabım',
      phone: '555-555-5555',
      address: 'İstanbul, Türkiye'
    };

    return this.http.post<any>(`${this.apiUrl}/seller/profile`, defaultProfile);
  }
  // Mock veriler burada - gerçek API bağlantısı için yorum satırlarını kaldırın
  getSellerProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/products`);
  }

  getSellerOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders`);
  }

  // seller.service.ts
  addProduct(product: any): Observable<any> {
    if (typeof product.category === 'string') {
      return this.getCategoryByName(product.category).pipe(
        switchMap(category => {
          const productToSend = {
            ...product,
            category: { id: category.id }
            // Satıcı bilgisi otomatik olarak backend tarafından eklenecek
          };
          console.log('Sunucuya gönderilen veri:', productToSend);
          // Doğru endpoint: /seller/products
          return this.http.post(`${this.apiUrl}/seller/products`, productToSend);
        })
      );
    } else {
      // Doğru endpoint: /seller/products
      return this.http.post(`${this.apiUrl}/seller/products`, product);
    }
  }
getCategoryByName(name: string): Observable<any> {
  return this.http.get<any[]>(`${this.apiUrl}/product-category/search/findByCategoryName?categoryName=${name}`);
}
  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/product-category`);
  }

  // Kategori mapini dinamik olarak oluşturun
  private categoryMap: {[key: string]: number} = {};

  loadCategories(): void {
    this.getCategories().subscribe({
      next: (categories) => {
        categories.forEach(category => {
          this.categoryMap[category.categoryName] = category.id;
        });
        console.log('Kategori eşleştirme:', this.categoryMap);
      },
      error: (err) => console.error('Kategori yükleme hatası:', err)
    });
  }

  private getCategoryIdByName(categoryName: string): number {
    const categoryMap: {[key: string]: number} = {
      'Elektronik': 1,
      'Giyim': 2,
      'Kitaplar': 3,
      'Ev & Yaşam': 4,
      'Spor': 5
    };

    return categoryMap[categoryName] || 1; // Varsayılan olarak 1 döndür
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

  getSellerInfo(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/seller/profile`);
  }

}
