import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  CreateUserRequest,
  UserRole,
  UserService
} from '../../../core/services/user.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './user-form.html',
  styleUrl: './user-form.scss'
})
export class UserForm {

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  formData: CreateUserRequest = {
    firstName: '',
    lastName: '',
    email: '',
    role: 'CREW_MEMBER',
    password: ''
  };

  readonly roles: {
    value: UserRole;
    label: string;
    description: string;
  }[] = [
    {
      value: 'ADMIN',
      label: 'Admin',
      description:
        'Tüm sistem üzerinde yönetim yetkisine sahiptir.'
    },
    {
      value: 'CREW_MEMBER',
      label: 'Kazı Ekibi',
      description:
        'Buluntu oluşturabilir, düzenleyebilir ve ekip verilerine erişebilir.'
    },
    {
      value: 'LOOKUP_USER',
      label: 'Görüntüleme',
      description:
        'Yalnızca erişimine açık buluntuları görüntüleyebilir.'
    }
  ];


  constructor(
    private router: Router,
    private userService: UserService
  ) {}


  // --------------------------------------------------
  // KULLANICI OLUŞTUR
  // --------------------------------------------------

  saveUser(): void {

    this.errorMessage.set('');

    const firstName =
      this.formData.firstName.trim();

    const lastName =
      this.formData.lastName.trim();

    const email =
      this.formData.email.trim();

    const password =
      this.formData.password;


    // --------------------------------------------------
    // AD
    // --------------------------------------------------

    if (!firstName) {

      this.errorMessage.set(
        'Ad alanı zorunludur.'
      );

      return;
    }


    // --------------------------------------------------
    // SOYAD
    // --------------------------------------------------

    if (!lastName) {

      this.errorMessage.set(
        'Soyad alanı zorunludur.'
      );

      return;
    }


    // --------------------------------------------------
    // E-POSTA
    // --------------------------------------------------

    if (!email) {

      this.errorMessage.set(
        'E-posta alanı zorunludur.'
      );

      return;
    }

    if (!this.isValidEmail(email)) {

      this.errorMessage.set(
        'Geçerli bir e-posta adresi giriniz.'
      );

      return;
    }


    // --------------------------------------------------
    // ROL
    // --------------------------------------------------

    if (!this.formData.role) {

      this.errorMessage.set(
        'Kullanıcı rolü seçilmelidir.'
      );

      return;
    }


    // --------------------------------------------------
    // GEÇİCİ ŞİFRE
    // --------------------------------------------------

    if (!password) {

      this.errorMessage.set(
        'Geçici şifre zorunludur.'
      );

      return;
    }

    if (password.length < 8) {

      this.errorMessage.set(
        'Şifre en az 8 karakter olmalıdır.'
      );

      return;
    }

    if (!/[A-Za-z]/.test(password)) {

      this.errorMessage.set(
        'Şifre en az bir harf içermelidir.'
      );

      return;
    }

    if (!/\d/.test(password)) {

      this.errorMessage.set(
        'Şifre en az bir rakam içermelidir.'
      );

      return;
    }


    // --------------------------------------------------
    // REQUEST
    // --------------------------------------------------

    const request: CreateUserRequest = {
      firstName,
      lastName,
      email,
      role: this.formData.role,
      password
    };


    // --------------------------------------------------
    // API
    // --------------------------------------------------

    this.saving.set(true);

    this.userService
      .createUser(request)
      .subscribe({

        next: () => {

          this.saving.set(false);

          this.router.navigate([
            '/users'
          ]);
        },

        error: (error) => {

          this.saving.set(false);

          if (error.status === 400) {

            this.errorMessage.set(
              error.error?.message ??
              'Kullanıcı bilgileri geçerli değil.'
            );

            return;
          }

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Kullanıcı oluşturma yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 409) {

            this.errorMessage.set(
              error.error?.message ??
              'Bu e-posta adresiyle kayıtlı bir kullanıcı zaten bulunuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Kullanıcı oluşturulurken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // E-POSTA KONTROLÜ
  // --------------------------------------------------

  private isValidEmail(
    email: string
  ): boolean {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      .test(email);
  }


  // --------------------------------------------------
  // İPTAL
  // --------------------------------------------------

  cancel(): void {

    this.router.navigate([
      '/users'
    ]);
  }


  // --------------------------------------------------
  // ANA SAYFA
  // --------------------------------------------------

  goHome(): void {

    this.router.navigate([
      '/home'
    ]);
  }
}