import {
  Component,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';


@Component({
  selector: 'app-forgot-password',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],

  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss'
})
export class ForgotPassword {

  email = '';

  readonly loading =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly successMessage =
    signal('');


  constructor(
    private authService: AuthService
  ) {}


  // --------------------------------------------------
  // ŞİFRE SIFIRLAMA BAĞLANTISI GÖNDER
  // --------------------------------------------------

  sendResetLink(): void {

    this.errorMessage.set('');
    this.successMessage.set('');


    const email =
      this.email.trim();


    if (!email) {

      this.errorMessage.set(
        'E-posta adresinizi giriniz.'
      );

      return;
    }


    this.loading.set(true);


    this.authService
      .forgotPassword(email)
      .subscribe({

        next: () => {

          this.loading.set(false);

          this.successMessage.set(
            'Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.'
          );
        },


        error: () => {

          this.loading.set(false);

          this.errorMessage.set(
            'Şifre sıfırlama isteği sırasında bir hata oluştu. Lütfen tekrar deneyiniz.'
          );
        }
      });
  }
}