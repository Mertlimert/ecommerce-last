import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService, UserRole } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // 1. Kullanıcının kimlik doğrulamasını kontrol et
    if (!this.authService.isAuthenticated()) {
      // Kullanıcı giriş yapmamış, login sayfasına yönlendir
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: state.url }  // Giriş sonrası dönülecek URL'i sakla
      });
      return false;
    }

    // 2. Rol bazlı erişim kontrolü - eğer route'da roller belirtilmişse
    const requiredRoles = route.data['roles'] as UserRole[];

    // Rol belirtilmemişse sadece giriş yapılmış olması yeterli
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    // 3. Kullanıcının en az bir gerekli role sahip olup olmadığını kontrol et
    const hasRequiredRole = requiredRoles.some(role =>
      this.authService.hasRole(role)
    );

    if (!hasRequiredRole) {
      // Yetkisiz erişim - ana sayfaya yönlendir
      this.router.navigate(['/']);

      // İsteğe bağlı: Yetki hatası mesajı gösterebilirsiniz
      // this.toastService.error('Bu sayfaya erişim yetkiniz yok');

      return false;
    }

    // 4. Erişime izin ver
    return true;
  }
}
