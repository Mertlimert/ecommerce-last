import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../../services/admin.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="admin-users">
      <h1>Admin Kullanıcı Yönetimi</h1>
      <router-outlet></router-outlet>
    </div>
  `,
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  users: any[] = [];
  loading: boolean = true;
  searchTerm: string = '';
  filteredUsers: any[] = [];

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loading = true;
    this.adminService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.filteredUsers = [...this.users];
        this.loading = false;
      },
      error: (error) => {
        console.error('Kullanıcı yükleme hatası:', error);
        this.loading = false;
      }
    });
  }

  search(): void {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = [...this.users];
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.username.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term)
    );
  }

  toggleUserStatus(user: any): void {
    user.status = user.status === 'Aktif' ? 'Engelli' : 'Aktif';
    // Burada normalde API çağrısı yapılacak
  }

  deleteUser(id: number): void {
    if (confirm('Bu kullanıcıyı silmek istediğinizden emin misiniz?')) {
      this.users = this.users.filter(user => user.id !== id);
      this.filteredUsers = this.filteredUsers.filter(user => user.id !== id);
      // Burada normalde API çağrısı yapılacak
    }
  }

  getBadgeClass(role: string): string {
    switch(role) {
      case 'ADMIN': return 'bg-danger';
      case 'SELLER': return 'bg-success';
      default: return 'bg-primary';
    }
  }

  getStatusClass(status: string): string {
    return status === 'Aktif' ? 'text-success' : 'text-danger';
  }
}
