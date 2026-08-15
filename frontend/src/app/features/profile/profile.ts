import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  AuthService,
  ProfileResponse,
  ProfileUpdateRequest
} from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {

  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly successMessage =
    signal('');

  readonly profile =
    signal<ProfileResponse | null>(null);

  form: ProfileUpdateRequest = {
    firstName: '',
    lastName: '',
    email: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}


  // --------------------------------------------------
  // SAYFA AÇILIŞI
  // --------------------------------------------------

  ngOnInit(): void {
    this.loadProfile();
  }


  // --------------------------------------------------
  // PROFİLİ GETİR
  // --------------------------------------------------

  private loadProfile(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.authService
      .getMyProfile()
      .subscribe({

        next: (profile) => {

          this.profile.set(profile);

          this.form = {
            firstName: profile.firstName,
            lastName: profile.lastName,
            email: profile.email
          };

          this.loading.set(false);
        },

        error: (error) => {

          this.loading.set(false);

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Profil bilgileri yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // PROFİLİ KAYDET
  // --------------------------------------------------

  saveProfile(): void {

    this.errorMessage.set('');
    this.successMessage.set('');

    if (
      !this.form.firstName.trim() ||
      !this.form.lastName.trim() ||
      !this.form.email.trim()
    ) {

      this.errorMessage.set(
        'Ad, soyad ve e-posta alanları zorunludur.'
      );

      return;
    }

    const request: ProfileUpdateRequest = {

      firstName:
        this.form.firstName.trim(),

      lastName:
        this.form.lastName.trim(),

      email:
        this.form.email.trim()
    };

    this.saving.set(true);

    this.authService
      .updateMyProfile(request)
      .subscribe({

        next: (profile) => {

          this.profile.set(profile);

          this.form = {
            firstName: profile.firstName,
            lastName: profile.lastName,
            email: profile.email
          };

          this.saving.set(false);

          this.successMessage.set(
            'Profil bilgileriniz başarıyla güncellendi.'
          );
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
              'Girdiğiniz profil bilgileri geçerli değil.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Profil güncellenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // ROLÜ TÜRKÇE GÖSTER
  // --------------------------------------------------

  getRoleLabel(): string {

    const role =
      this.profile()?.role;

    switch (role) {

      case 'ADMIN':
        return 'Admin';

      case 'CREW_MEMBER':
        return 'Kazı Ekibi';

      case 'LOOKUP_USER':
        return 'Görüntüleme';

      default:
        return '—';
    }
  }


  // --------------------------------------------------
  // ŞİFRE DEĞİŞTİRME SAYFASI
  // --------------------------------------------------

  goToChangePassword(): void {

    this.router.navigate([
      '/change-password'
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