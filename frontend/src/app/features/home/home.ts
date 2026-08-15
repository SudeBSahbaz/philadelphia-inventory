import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/services/auth.service';
import { ArtifactService } from '../../core/services/artifact.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {

  artifactCode = '';

  readonly searching =
    signal(false);

  readonly searchErrorMessage =
    signal('');

  constructor(
    public authService: AuthService,
    private artifactService: ArtifactService,
    private router: Router
  ) {}

  searchArtifact(): void {

    const code =
      this.artifactCode.trim();

    this.searchErrorMessage.set('');

    if (!code) {

      this.searchErrorMessage.set(
        'Lütfen bir buluntu kodu giriniz.'
      );

      return;
    }

    this.searching.set(true);

    this.artifactService
      .getArtifactByCode(code)
      .subscribe({

        next: (artifact) => {

          this.searching.set(false);

          this.router.navigate([
            '/artifacts',
            artifact.artifactCode
          ]);
        },

        error: (error) => {

          this.searching.set(false);

          if (error.status === 404) {

            this.searchErrorMessage.set(
              'Bu koda ait bir buluntu bulunamadı.'
            );

            return;
          }

          if (error.status === 401) {

            this.searchErrorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.searchErrorMessage.set(
              'Bu buluntuyu görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.searchErrorMessage.set(
            error.error?.message ??
            'Buluntu aranırken bir hata oluştu.'
          );
        }
      });
  }

  createArtifact(): void {
    this.router.navigate([
      '/artifacts/new'
    ]);
  }

  advancedSearch(): void {
    this.router.navigate([
      '/artifacts/search'
    ]);
  }

  goToArtifacts(): void {
    this.router.navigate([
      '/artifacts'
    ]);
  }

  goToUsers(): void {
    this.router.navigate([
      '/users'
    ]);
  }

  goToProfile(): void {
    this.router.navigate([
      '/profile'
    ]);
  }

  logout(): void {

    this.authService
      .logout()
      .subscribe({

        next: () => {
          this.router.navigate([
            '/login'
          ]);
        },

        error: () => {
          this.router.navigate([
            '/login'
          ]);
        }

      });
  }

  canCreateArtifact(): boolean {
    return (
      this.authService.hasRole('ADMIN') ||
      this.authService.hasRole('CREW_MEMBER')
    );
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }
}