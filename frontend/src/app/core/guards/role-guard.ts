import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser =
    authService.getCurrentUser();

  const allowedRoles =
    route.data?.['roles'] as string[] | undefined;

  console.log('ROLE GUARD ÇALIŞTI');
  console.log('URL:', state.url);
  console.log('CURRENT USER:', currentUser);
  console.log('CURRENT ROLE:', currentUser?.role);
  console.log('ALLOWED ROLES:', allowedRoles);

  if (!currentUser) {
    return router.createUrlTree([
      '/login'
    ]);
  }

  if (
    !allowedRoles ||
    allowedRoles.length === 0
  ) {
    return true;
  }

  if (
    allowedRoles.includes(
      currentUser.role
    )
  ) {
    return true;
  }

  console.log(
    'YETKİ YOK → HOME'
  );

  return router.createUrlTree([
    '/home'
  ]);
};