import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import { AuthService } from '../services/auth.service';

export const passwordChangeGuard: CanActivateFn = (
  route,
  state
) => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);

  const user =
    authService.getCurrentUser();

  // Kullanıcı giriş yapmamışsa
  if (!user) {

    return router.createUrlTree([
      '/login'
    ]);
  }

  // Kullanıcının şifresini değiştirmesi gerekiyorsa
  // sadece change-password sayfasına gidebilir
  if (
    user.mustChangePassword &&
    state.url !== '/change-password'
  ) {

    return router.createUrlTree([
      '/change-password'
    ]);
  }

  return true;
};