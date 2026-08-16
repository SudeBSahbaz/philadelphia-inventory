import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { ArtifactService } from '../../core/services/artifact.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
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


  // --------------------------------------------------
  // BULUNTU ARA
  // --------------------------------------------------

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

            this.router.navigate(
              ['/artifacts/search'],
              {
                queryParams: {
                  artifactCode: code
                }
              }
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


  // --------------------------------------------------
  // BULUNTU OLUŞTUR
  // --------------------------------------------------

  createArtifact(): void {

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

        // Kod zaten varsa yeni kayıt oluşturulamaz.
        next: () => {

          this.searching.set(false);

          this.searchErrorMessage.set(
            'Bu kodla kayıtlı bir buluntu zaten bulunmaktadır.'
          );
        },

        error: (error) => {

          this.searching.set(false);

          // Kod bulunamadıysa yeni buluntu oluşturma
          // formuna kodu da beraber gönder.
          if (error.status === 404) {

            this.router.navigate(
              ['/artifacts/new'],
              {
                queryParams: {
                  code
                }
              }
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
              'Buluntu oluşturma yetkiniz bulunmuyor.'
            );

            return;
          }

          this.searchErrorMessage.set(
            error.error?.message ??
            'Buluntu kontrol edilirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // GELİŞMİŞ ARAMA
  // --------------------------------------------------

  advancedSearch(): void {

    this.router.navigate([
      '/artifacts/search'
    ]);
  }


  // --------------------------------------------------
  // BULUNTULAR
  // --------------------------------------------------

  goToArtifacts(): void {

    this.router.navigate([
      '/artifacts'
    ]);
  }


  // --------------------------------------------------
  // KULLANICILAR
  // --------------------------------------------------

  goToUsers(): void {

    this.router.navigate([
      '/users'
    ]);
  }


  // --------------------------------------------------
  // PROFİL
  // --------------------------------------------------

  goToProfile(): void {

    this.router.navigate([
      '/profile'
    ]);
  }


  // --------------------------------------------------
  // ÇIKIŞ
  // --------------------------------------------------

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


  // --------------------------------------------------
  // YETKİLER
  // --------------------------------------------------

  canCreateArtifact(): boolean {

    return (
      this.authService.hasRole('ADMIN') ||
      this.authService.hasRole('CREW_MEMBER')
    );
  }


  isAdmin(): boolean {

    return this.authService.hasRole(
      'ADMIN'
    );
  }
}