import {
  Component,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  ResetPasswordRequest
} from '../../../core/models/auth.model';


@Component({
  selector: 'app-reset-password',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],

  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss'
})
export class ResetPassword {

  token = '';

  newPassword = '';
  newPasswordConfirm = '';

  showNewPassword = false;
  showNewPasswordConfirm = false;

  readonly loading =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly successMessage =
    signal('');


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {

    this.token =
      this.route.snapshot
        .queryParamMap
        .get('token') ?? '';
  }


  // --------------------------------------------------
  // ŞİFREYİ SIFIRLA
  // --------------------------------------------------

  resetPassword(): void {

    this.errorMessage.set('');
    this.successMessage.set('');


    if (!this.token) {

      this.errorMessage.set(
        'Şifre sıfırlama bağlantısı geçersiz.'
      );

      return;
    }


    if (
      !this.newPassword ||
      !this.newPasswordConfirm
    ) {

      this.errorMessage.set(
        'Yeni şifre alanlarını doldurunuz.'
      );

      return;
    }


    if (
      this.newPassword !==
      this.newPasswordConfirm
    ) {

      this.errorMessage.set(
        'Yeni şifreler eşleşmiyor.'
      );

      return;
    }


    if (
      this.newPassword.length < 8
    ) {

      this.errorMessage.set(
        'Şifre en az 8 karakter olmalıdır.'
      );

      return;
    }


    if (
      !/[A-Za-z]/.test(
        this.newPassword
      )
    ) {

      this.errorMessage.set(
        'Şifre en az bir harf içermelidir.'
      );

      return;
    }


    if (
      !/\d/.test(
        this.newPassword
      )
    ) {

      this.errorMessage.set(
        'Şifre en az bir rakam içermelidir.'
      );

      return;
    }


    const request: ResetPasswordRequest = {
      token: this.token,
      newPassword: this.newPassword,
      newPasswordConfirm:
        this.newPasswordConfirm
    };


    this.loading.set(true);


    this.authService
      .resetPassword(request)
      .subscribe({

        next: () => {

          this.loading.set(false);

          this.successMessage.set(
            'Şifreniz başarıyla değiştirildi.'
          );

          setTimeout(() => {

            this.router.navigate([
              '/login'
            ]);

          }, 1500);
        },


        error: (error) => {

          this.loading.set(false);

          const message =
            error.error?.message;


          if (
            message ===
            'Reset link has expired.'
          ) {

            this.errorMessage.set(
              'Şifre sıfırlama bağlantısının süresi dolmuş.'
            );

            return;
          }


          if (
            message ===
            'Reset link is invalid.'
          ) {

            this.errorMessage.set(
              'Şifre sıfırlama bağlantısı geçersiz veya daha önce kullanılmış.'
            );

            return;
          }


          this.errorMessage.set(
            message ??
            'Şifre sıfırlanırken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // ŞİFRE GÖSTER / GİZLE
  // --------------------------------------------------

  toggleNewPassword(): void {

    this.showNewPassword =
      !this.showNewPassword;
  }


  toggleNewPasswordConfirm(): void {

    this.showNewPasswordConfirm =
      !this.showNewPasswordConfirm;
  }
}