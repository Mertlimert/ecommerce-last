import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';

const AUTH_API = 'http://localhost:8081/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

export enum UserRole {
  USER = 'ROLE_USER',
  SELLER = 'ROLE_SELLER',
  ADMIN = 'ROLE_ADMIN'
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  public isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('currentUser') || 'null'));
    this.currentUser = this.currentUserSubject.asObservable();
    this.isLoggedIn.next(this.isAuthenticated());
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }



  login(username: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password
    }, httpOptions).pipe(
      map(user => {
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        this.isLoggedIn.next(true);
        return user;
      })
    );
  }

  register(username: string, email: string, password: string, firstName: string, lastName: string): Observable<any> {
    return this.http.post(AUTH_API + 'signup', {
      username,
      email,
      password,
      firstName,
      lastName
    }, httpOptions);
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.isLoggedIn.next(false);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    const currentUser = this.currentUserValue;
    const token = this.getToken();
    return currentUser !== null && token !== null;
  }

  getToken(): string | null {
    const currentUser = this.currentUserValue;
    return currentUser ? currentUser.token : null;
  }

  getUserRole(): string[] {
    const currentUser = this.currentUserValue;
    return currentUser ? currentUser.roles : [];
  }

  getUserName(): string {
    const currentUser = this.currentUserValue;
    return currentUser ? currentUser.username : '';
  }

  hasRole(role: UserRole): boolean {
    const roles = this.getUserRole();
    return roles.includes(role);
  }

  getFullName(): string {
    const currentUser = this.currentUserValue;
    return currentUser ? `${currentUser.firstName} ${currentUser.lastName}` : '';
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  autoLogout(expirationDate: Date) {
    const timeOut = new Date(expirationDate).getTime() - new Date().getTime();
    setTimeout(() => {
      this.logout();
    }, timeOut);
  }

  isAdmin(): boolean {
    return this.hasRole(UserRole.ADMIN);
  }

  isSeller(): boolean {
    return this.hasRole(UserRole.SELLER);
  }

  isUser(): boolean {
    return this.hasRole(UserRole.USER);
  }

  redirectBasedOnRole() {
    if (this.isAdmin()) {
      this.router.navigate(['/admin/dashboard']);
    } else if (this.isSeller()) {
      this.router.navigate(['/seller/dashboard']);
    } else {
      this.router.navigate(['/products']);
    }
  }

}
