import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  template: `
    <div class="main-content page-m">
      <div class="section-content section-content-p30">
        <div class="container-fluid">
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="form-area">
              <h3>Kayıt Ol</h3>

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
                    <input formControlName="firstName" type="text" placeholder="Ad">
                    <div *ngIf="firstName?.invalid && (firstName?.dirty || firstName?.touched)" class="alert alert-danger mt-1">
                      <div *ngIf="firstName?.errors?.['required']">Ad gerekli</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <input formControlName="lastName" type="text" placeholder="Soyad">
                    <div *ngIf="lastName?.invalid && (lastName?.dirty || lastName?.touched)" class="alert alert-danger mt-1">
                      <div *ngIf="lastName?.errors?.['required']">Soyad gerekli</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <input formControlName="email" type="email" placeholder="Email">
                    <div *ngIf="email?.invalid && (email?.dirty || email?.touched)" class="alert alert-danger mt-1">
                      <div *ngIf="email?.errors?.['required']">Email gerekli</div>
                      <div *ngIf="email?.errors?.['email']">Geçerli bir email adresi girin</div>
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

              <div class="row">
                <div class="col-md-6">
                  <div class="input-space">
                    <input formControlName="confirmPassword" type="password" placeholder="Şifre Tekrar">
                    <div *ngIf="confirmPassword?.invalid && (confirmPassword?.dirty || confirmPassword?.touched)"
                         class="alert alert-danger mt-1">
                      <div *ngIf="confirmPassword?.errors?.['required']">Şifre tekrarı gerekli</div>
                      <div *ngIf="confirmPassword?.errors?.['passwordMismatch']">Şifreler eşleşmiyor</div>
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
                    <button type="submit" class="btn btn-primary">Kayıt Ol</button>
                    <a routerLink="/login" class="btn btn-link">Zaten hesabın var mı? Giriş yap</a>
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
    .btn-link {
      margin-left: 10px;
      text-decoration: none;
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validator: this.passwordMatchValidator
    });
  }

  get username() { return this.registerForm.get('username'); }
  get firstName() { return this.registerForm.get('firstName'); }
  get lastName() { return this.registerForm.get('lastName'); }
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get confirmPassword() { return this.registerForm.get('confirmPassword'); }

  passwordMatchValidator(g: FormGroup) {
    const password = g.get('password')?.value;
    const confirmPassword = g.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { 'passwordMismatch': true };
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { username, email, password, firstName, lastName } = this.registerForm.value;

    this.authService.register(username, email, password, firstName, lastName).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Kayıt işlemi başarısız';
      }
    });
  }
}
