import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  ChangePasswordRequest
} from '../../../core/models/auth.model';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './change-password.html',
  styleUrl: './change-password.scss'
})
export class ChangePassword {

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly successMessage =
    signal('');

  form: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: '',
    newPasswordConfirm: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}


  // --------------------------------------------------
  // ŞİFREYİ DEĞİŞTİR
  // --------------------------------------------------

  changePassword(): void {

    this.errorMessage.set('');
    this.successMessage.set('');

    if (
      !this.form.currentPassword ||
      !this.form.newPassword ||
      !this.form.newPasswordConfirm
    ) {

      this.errorMessage.set(
        'Tüm şifre alanlarını doldurunuz.'
      );

      return;
    }

    if (
      this.form.newPassword !==
      this.form.newPasswordConfirm
    ) {

      this.errorMessage.set(
        'Yeni şifreler birbiriyle eşleşmiyor.'
      );

      return;
    }

    if (
      this.form.currentPassword ===
      this.form.newPassword
    ) {

      this.errorMessage.set(
        'Yeni şifreniz mevcut şifrenizden farklı olmalıdır.'
      );

      return;
    }

    const wasForcedPasswordChange =
      this.authService
        .getCurrentUser()
        ?.mustChangePassword === true;

    this.saving.set(true);

    this.authService
      .changePassword(this.form)
      .subscribe({

        next: () => {

          this.saving.set(false);

          this.form = {
            currentPassword: '',
            newPassword: '',
            newPasswordConfirm: ''
          };

          // İlk girişte zorunlu şifre değişikliğiyse
          // artık normal sisteme girebilir.
          if (wasForcedPasswordChange) {

            this.router.navigate([
              '/home'
            ]);

            return;
          }

          // Profil üzerinden normal şifre değişikliğiyse
          // profile geri dön.
          this.router.navigate([
            '/profile'
          ]);
        },

        error: (error) => {

          this.saving.set(false);

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 400) {

            this.errorMessage.set(
              error.error?.message ??
              'Şifre bilgileri geçerli değil.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Şifre değiştirilirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // GERİ
  // --------------------------------------------------

  goBack(): void {

    const user =
      this.authService.getCurrentUser();

    // Şifre değişikliği zorunluysa
    // kullanıcı profile kaçamaz.
    if (user?.mustChangePassword) {
      return;
    }

    this.router.navigate([
      '/profile'
    ]);
  }


  // --------------------------------------------------
  // ANA SAYFA
  // --------------------------------------------------

  goHome(): void {

    const user =
      this.authService.getCurrentUser();

    // Şifre değişikliği zorunluysa
    // kullanıcı home'a gidemez.
    if (user?.mustChangePassword) {
      return;
    }

    this.router.navigate([
      '/home'
    ]);
  }
}