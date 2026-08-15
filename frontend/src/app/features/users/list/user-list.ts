import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';

import {
  UserResponse,
  UserService
} from '../../../core/services/user.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './user-list.html',
  styleUrl: './user-list.scss'
})
export class UserList implements OnInit {

  readonly users =
    signal<UserResponse[]>([]);

  readonly loading =
    signal(true);

  readonly errorMessage =
    signal('');

  readonly processingUserId =
    signal<number | null>(null);

  constructor(
    private router: Router,
    private userService: UserService,
    public authService: AuthService
  ) {}


  // --------------------------------------------------
  // SAYFA AÇILIŞI
  // --------------------------------------------------

  ngOnInit(): void {

    this.loadUsers();
  }


  // --------------------------------------------------
  // KULLANICILARI GETİR
  // --------------------------------------------------

  private loadUsers(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.userService
      .getAllUsers()
      .subscribe({

        next: (users) => {

          this.users.set(
            users
          );

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
              'Kullanıcıları görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Kullanıcılar yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // YENİ KULLANICI
  // --------------------------------------------------

  createUser(): void {

    this.router.navigate([
      '/users/new'
    ]);
  }


  // --------------------------------------------------
  // KULLANICI DÜZENLE
  // --------------------------------------------------

  editUser(
    user: UserResponse
  ): void {

    this.router.navigate([
      '/users',
      user.id,
      'edit'
    ]);
  }


  // --------------------------------------------------
  // KENDİ HESABIM MI?
  // --------------------------------------------------

  isCurrentUser(
    user: UserResponse
  ): boolean {

    const currentUser =
      this.authService.getCurrentUser();

    if (!currentUser) {
      return false;
    }

    return (
      currentUser.userId === user.id
    );
  }


  // --------------------------------------------------
  // KULLANICIYI PASİF YAP
  // --------------------------------------------------

  deactivateUser(
    user: UserResponse
  ): void {

    if (!user.active) {
      return;
    }

    if (this.isCurrentUser(user)) {

      this.errorMessage.set(
        'Kendi kullanıcı hesabınızı pasif hale getiremezsiniz.'
      );

      return;
    }

    const confirmed =
      window.confirm(
        `${user.firstName} ${user.lastName} kullanıcısını pasif yapmak istediğinize emin misiniz?`
      );

    if (!confirmed) {
      return;
    }

    this.processingUserId.set(
      user.id
    );

    this.errorMessage.set('');

    this.userService
      .deactivateUser(
        user.id
      )
      .subscribe({

        next: (updatedUser) => {

          this.updateUserInList(
            updatedUser
          );

          this.processingUserId.set(
            null
          );
        },

        error: (error) => {

          this.processingUserId.set(
            null
          );

          this.handleActionError(
            error,
            'Kullanıcı pasif hale getirilemedi.'
          );
        }
      });
  }


  // --------------------------------------------------
  // KULLANICIYI AKTİF YAP
  // --------------------------------------------------

  activateUser(
    user: UserResponse
  ): void {

    if (user.active) {
      return;
    }

    this.processingUserId.set(
      user.id
    );

    this.errorMessage.set('');

    this.userService
      .activateUser(
        user.id
      )
      .subscribe({

        next: (updatedUser) => {

          this.updateUserInList(
            updatedUser
          );

          this.processingUserId.set(
            null
          );
        },

        error: (error) => {

          this.processingUserId.set(
            null
          );

          this.handleActionError(
            error,
            'Kullanıcı aktif hale getirilemedi.'
          );
        }
      });
  }


  // --------------------------------------------------
  // LİSTEDEKİ KULLANICIYI GÜNCELLE
  // --------------------------------------------------

  private updateUserInList(
    updatedUser: UserResponse
  ): void {

    this.users.update(
      users =>
        users.map(
          user =>
            user.id === updatedUser.id
              ? updatedUser
              : user
        )
    );
  }


  // --------------------------------------------------
  // İŞLEM HATALARI
  // --------------------------------------------------

  private handleActionError(
    error: any,
    fallbackMessage: string
  ): void {

    if (error.status === 401) {

      this.errorMessage.set(
        'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
      );

      return;
    }

    if (error.status === 403) {

      this.errorMessage.set(
        'Bu işlemi gerçekleştirme yetkiniz bulunmuyor.'
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
      fallbackMessage
    );
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