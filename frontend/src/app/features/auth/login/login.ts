import {
  Component,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  LoginRequest
} from '../../../core/models/auth.model';


@Component({
  selector: 'app-login',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],

  templateUrl: './login.html',

  styleUrl: './login.scss'
})
export class Login {

  email = '';

  password = '';

  showPassword = false;

  rememberMe = false;


  readonly loading =
    signal(false);


  readonly errorMessage =
    signal('');


  constructor(
    private authService: AuthService,
    private router: Router
  ) {}


  // --------------------------------------------------
  // GİRİŞ
  // --------------------------------------------------

  login(): void {

    this.errorMessage.set('');


    const email =
      this.email.trim();


    const password =
      this.password;


    if (
      !email ||
      !password
    ) {

      this.errorMessage.set(
        'E-posta ve şifre alanlarını doldurunuz.'
      );

      return;
    }


    const request: LoginRequest = {
      email,
      password,
      rememberMe: this.rememberMe
    };


    this.loading.set(true);


    this.authService
      .login(request)
      .subscribe({

        next: (user) => {

          this.loading.set(false);


          // --------------------------------------------------
          // İLK ŞİFRE DEĞİŞİKLİĞİ
          // --------------------------------------------------

          if (
            user.mustChangePassword
          ) {

            this.router.navigate([
              '/change-password'
            ]);

            return;
          }


          // --------------------------------------------------
          // NORMAL GİRİŞ
          // --------------------------------------------------

          this.router.navigate([
            '/home'
          ]);
        },


        error: (error) => {

          this.loading.set(false);


          // --------------------------------------------------
          // PASİF KULLANICI
          // --------------------------------------------------

          if (
            error.status === 401 &&
            error.error?.message ===
              'User account is inactive.'
          ) {

            this.errorMessage.set(
              'Bu kullanıcı hesabı pasif durumdadır. Lütfen sistem yöneticisiyle iletişime geçiniz.'
            );

            return;
          }


          // --------------------------------------------------
          // HATALI E-POSTA / ŞİFRE
          // --------------------------------------------------

          if (
            error.status === 401
          ) {

            this.errorMessage.set(
              'E-posta veya şifre hatalı.'
            );

            return;
          }


          // --------------------------------------------------
          // YETKİ
          // --------------------------------------------------

          if (
            error.status === 403
          ) {

            this.errorMessage.set(
              'Bu işlem için yetkiniz bulunmuyor.'
            );

            return;
          }


          // --------------------------------------------------
          // DİĞER HATALAR
          // --------------------------------------------------

          this.errorMessage.set(
            error.error?.message ??
            'Giriş sırasında bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // ŞİFRE GÖSTER / GİZLE
  // --------------------------------------------------

  togglePassword(): void {

    this.showPassword =
      !this.showPassword;
  }
}