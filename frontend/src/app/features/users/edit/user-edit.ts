import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  UpdateUserRequest,
  UserRole,
  UserService
} from '../../../core/services/user.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './user-edit.html',
  styleUrl: './user-edit.scss'
})
export class UserEdit implements OnInit {

  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  userId: number | null = null;

  private originalRole:
    UserRole | null = null;

  formData: UpdateUserRequest = {
    firstName: '',
    lastName: '',
    email: '',
    role: 'CREW_MEMBER'
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
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    public authService: AuthService
  ) {}


  // --------------------------------------------------
  // SAYFA AÇILIŞI
  // --------------------------------------------------

  ngOnInit(): void {

    const userIdParam =
      this.route.snapshot.paramMap.get(
        'userId'
      );

    if (!userIdParam) {

      this.loading.set(false);

      this.errorMessage.set(
        'Kullanıcı bilgisi bulunamadı.'
      );

      return;
    }

    const userId =
      Number(userIdParam);

    if (
      Number.isNaN(userId) ||
      userId <= 0
    ) {

      this.loading.set(false);

      this.errorMessage.set(
        'Geçersiz kullanıcı numarası.'
      );

      return;
    }

    this.userId =
      userId;

    this.loadUser();
  }


  // --------------------------------------------------
  // KULLANICIYI GETİR
  // --------------------------------------------------

  private loadUser(): void {

    if (this.userId === null) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.userService
      .getUserById(
        this.userId
      )
      .subscribe({

        next: (user) => {

          this.originalRole =
            user.role;

          this.formData = {
            firstName:
              user.firstName ?? '',
            lastName:
              user.lastName ?? '',
            email:
              user.email ?? '',
            role:
              user.role
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

          if (error.status === 403) {

            this.errorMessage.set(
              'Bu kullanıcıyı görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'Kullanıcı bulunamadı.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Kullanıcı bilgileri yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // KENDİ HESABIM MI?
  // --------------------------------------------------

  isCurrentUser(): boolean {

    const currentUser =
      this.authService.getCurrentUser();

    if (
      !currentUser ||
      this.userId === null
    ) {
      return false;
    }

    return (
      currentUser.userId ===
      this.userId
    );
  }


  // --------------------------------------------------
  // KULLANICIYI GÜNCELLE
  // --------------------------------------------------

  saveUser(): void {

    this.errorMessage.set('');

    if (this.userId === null) {

      this.errorMessage.set(
        'Kullanıcı ID bilgisi bulunamadı.'
      );

      return;
    }

    const firstName =
      this.formData.firstName.trim();

    const lastName =
      this.formData.lastName.trim();

    const email =
      this.formData.email.trim();


    if (!firstName) {

      this.errorMessage.set(
        'Ad alanı zorunludur.'
      );

      return;
    }


    if (!lastName) {

      this.errorMessage.set(
        'Soyad alanı zorunludur.'
      );

      return;
    }


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


    if (!this.formData.role) {

      this.errorMessage.set(
        'Kullanıcı rolü seçilmelidir.'
      );

      return;
    }


    let role =
      this.formData.role;

    // Kendi hesabını düzenleyen admin
    // rolünü değiştiremez.
    if (
      this.isCurrentUser() &&
      this.originalRole
    ) {

      role =
        this.originalRole;
    }


    const request:
      UpdateUserRequest = {

        firstName,
        lastName,
        email,
        role
      };


    this.saving.set(true);

    this.userService
      .updateUser(
        this.userId,
        request
      )
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
              'Bu kullanıcıyı düzenleme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'Kullanıcı bulunamadı.'
            );

            return;
          }

          if (error.status === 409) {

            this.errorMessage.set(
              error.error?.message ??
              'Bu e-posta adresi başka bir kullanıcı tarafından kullanılıyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Kullanıcı güncellenirken bir hata oluştu.'
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