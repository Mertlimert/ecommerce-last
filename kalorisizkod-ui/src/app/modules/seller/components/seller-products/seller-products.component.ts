import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SellerService } from '../../../../services/seller.service';




@Component({
  selector: 'app-seller-products',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './seller-products.component.html',
  styleUrls: ['./seller-products.component.css']
})
export class SellerProductsComponent implements OnInit {
  products: any[] = [];
  categories: string[] = ['Elektronik', 'Giyim', 'Kitaplar', 'Ev & Yaşam', 'Spor'];
  loading: boolean = true;
  showAddProductModal: boolean = false;
  showEditProductModal: boolean = false;
  productForm!: FormGroup;
  editingProductId: number | null = null;

  constructor(private fb: FormBuilder, private sellerService: SellerService) {
    this.createForm();
  }

  ngOnInit(): void {
    this.loading = true;
    this.sellerService.getSellerProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Ürün yükleme hatası:', error);
        this.loading = false;
      }
    });
  }

  createForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.min(0)]],
      stock: ['', [Validators.required, Validators.min(0)]],
      category: ['', Validators.required],
      imageUrl: ['https://via.placeholder.com/150'],
      active: [true]
    });
  }

  openAddProductModal(): void {
    this.createForm(); // Form'u sıfırla
    this.showAddProductModal = true;
  }

  openEditProductModal(product: any): void {
    this.editingProductId = product.id;
    this.productForm.setValue({
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      category: product.category,
      imageUrl: product.imageUrl,
      active: product.active
    });
    this.showEditProductModal = true;
  }

  closeModals(): void {
    this.showAddProductModal = false;
    this.showEditProductModal = false;
    this.editingProductId = null;
  }

  // addProduct metodunu değiştirin
  addProduct(): void {
    if (this.productForm.invalid) {
      return;
    }

    // Sorun 1: Console log ekleyin ve formun değerlerini kontrol edin
    console.log('Form değerleri:', this.productForm.value);

    const newProduct = this.productForm.value;
    this.loading = true;

    this.sellerService.addProduct(newProduct).subscribe({
      next: (response) => {
        console.log('Başarılı yanıt:', response); // Sorun 2: Response'u kontrol edin
        this.products.push(response);
        this.closeModals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Ürün ekleme hatası:', error); // Sorun 3: Hata detayını kontrol edin
        this.loading = false;
        alert('Ürün eklenirken bir hata oluştu: ' + error.message);
      }
    });
  }

  updateProduct(): void {
    if (this.productForm.invalid || !this.editingProductId) {
      return;
    }

    const index = this.products.findIndex(p => p.id === this.editingProductId);
    if (index !== -1) {
      this.products[index] = {
        ...this.products[index],
        ...this.productForm.value
      };
    }

    this.closeModals();
    // Burada normalde bir API çağrısı yapılacak
  }

  toggleProductStatus(product: any): void {
    product.active = !product.active;
    // Burada normalde bir API çağrısı yapılacak
  }

  deleteProduct(id: number): void {
    if (confirm('Bu ürünü silmek istediğinizden emin misiniz?')) {
      this.products = this.products.filter(product => product.id !== id);
      // Burada normalde bir API çağrısı yapılacak
    }
  }
}
