import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  template: `
    <div class="main-content page-m">
      <div class="section-content section-content-p30">
        <div class="container-fluid">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
            <div class="form-area">
              <h3>Giriş Yap</h3>

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <input formControlName="username" type="text" placeholder="Kullanıcı Adı">
                    <div *ngIf="username?.invalid && (username?.dirty || username?.touched)" class="alert alert-danger mt-1">
                      <div *ngIf="username?.errors?.['required']">Kullanıcı adı gerekli</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <input formControlName="password" type="password" placeholder="Şifre">
                    <div *ngIf="password?.invalid && (password?.dirty || password?.touched)" class="alert alert-danger mt-1">
                      <div *ngIf="password?.errors?.['required']">Şifre gerekli</div>
                      <div *ngIf="password?.errors?.['minlength']">Şifre en az 6 karakter olmalı</div>
                    </div>
                  </div>
                </div>
              </div>

              <div *ngIf="errorMessage" class="alert alert-danger">
                {{ errorMessage }}
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <button type="submit" class="btn btn-primary">Giriş Yap</button>
                    <a routerLink="/register" class="btn btn-link">Hesap oluştur</a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .form-area {
      background: white;
      padding: 20px;
      border-radius: 5px;
      max-width: 600px;
      margin: 0 auto;
    }
    .input-space {
      margin-bottom: 20px;
    }
    .input-space input {
      width: 100%;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
  `]
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get username() { return this.loginForm.get('username'); }
  get password() { return this.loginForm.get('password'); }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    const username = this.loginForm.get('username')!.value;
    const password = this.loginForm.get('password')!.value;

    this.authService.login(username, password).subscribe({
      next: (data) => {
        // Kullanıcıyı rolüne göre doğru sayfaya yönlendirin
        this.authService.redirectBasedOnRole();
      },
      error: (err) => {
        this.errorMessage = 'Giriş başarısız. Kullanıcı adı veya şifre hatalı.';
      }
    });
  }
}
