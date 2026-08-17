import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';

import {
  ArtifactListItemResponse
} from '../../../core/models/artifact.model';

import {
  ArtifactService
} from '../../../core/services/artifact.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-artifact-list',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './artifact-list.html',
  styleUrl: './artifact-list.scss'
})
export class ArtifactList implements OnInit {

  readonly artifacts =
    signal<ArtifactListItemResponse[]>([]);

  readonly deletedArtifacts =
    signal<ArtifactListItemResponse[]>([]);

  readonly activeTab =
    signal<'active' | 'deleted'>('active');

  readonly loading =
    signal(true);

  readonly exportingExcel =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly restoringId =
    signal<number | null>(null);

  constructor(
    private artifactService: ArtifactService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadArtifacts();
  }


  // --------------------------------------------------
  // AKTİF BULUNTULAR
  // --------------------------------------------------

  private loadArtifacts(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.artifactService
      .getAllArtifacts()
      .subscribe({

        next: (artifacts) => {

          this.artifacts.set(
            artifacts
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
              'Buluntuları görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntular yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // SİLİNMİŞ BULUNTULAR
  // --------------------------------------------------

  private loadDeletedArtifacts(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.artifactService
      .getDeletedArtifacts()
      .subscribe({

        next: (artifacts) => {

          this.deletedArtifacts.set(
            artifacts
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
             'Silinmiş buluntuları görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Silinmiş buluntular yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // SEKME DEĞİŞTİR
  // --------------------------------------------------

  showActiveArtifacts(): void {

    this.activeTab.set(
      'active'
    );

    this.errorMessage.set('');

    this.loadArtifacts();
  }

showDeletedArtifacts(): void {

  if (!this.canManageArtifacts()) {
    return;
  }

  this.activeTab.set(
    'deleted'
  );

  this.errorMessage.set('');

  this.loadDeletedArtifacts();
}
  // --------------------------------------------------
  // BULUNTU AÇ
  // --------------------------------------------------

  openArtifact(
    artifact: ArtifactListItemResponse
  ): void {

    if (
      this.activeTab() === 'deleted'
    ) {
      return;
    }

    this.router.navigate([
      '/artifacts',
      artifact.artifactCode
    ]);
  }


  // --------------------------------------------------
  // TOPLU EXCEL İNDİR
  // ADMIN + CREW_MEMBER
  // --------------------------------------------------

  downloadExcel(): void {

    if (!this.canExport()) {
      return;
    }

    this.exportingExcel.set(
      true
    );

    this.errorMessage.set('');

    this.artifactService
      .downloadArtifactsExcel()
      .subscribe({

        next: (blob) => {

          this.exportingExcel.set(
            false
          );

          const url =
            URL.createObjectURL(
              blob
            );

          const link =
            document.createElement(
              'a'
            );

          link.href =
            url;

          link.download =
            'philadelphia-buluntular.xlsx';

          document.body.appendChild(
            link
          );

          link.click();

          document.body.removeChild(
            link
          );

          URL.revokeObjectURL(
            url
          );
        },

        error: (error) => {

          this.exportingExcel.set(
            false
          );

          if (error.status === 401) {

            this.errorMessage.set(
              'Excel dosyasını indirmek için tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Toplu dışa aktarma yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            'Excel dosyası oluşturulurken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // GERİ YÜKLE
  // --------------------------------------------------

  restoreArtifact(
    artifact: ArtifactListItemResponse,
    event: Event
  ): void {

    event.stopPropagation();

   if (!this.canManageArtifacts()) {
  return;
}

    this.restoringId.set(
      artifact.id
    );

    this.errorMessage.set('');

    this.artifactService
      .restoreArtifact(
        artifact.id
      )
      .subscribe({

        next: () => {

          this.restoringId.set(
            null
          );

          this.loadDeletedArtifacts();
        },

        error: (error) => {

          this.restoringId.set(
            null
          );

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Buluntuyu geri yükleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntu geri yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // NAVIGATION
  // --------------------------------------------------

  goHome(): void {

    this.router.navigate([
      '/home'
    ]);
  }

  advancedSearch(): void {

    this.router.navigate([
      '/artifacts/search'
    ]);
  }
// --------------------------------------------------
// YETKİLER
// --------------------------------------------------

canExport(): boolean {

  return (
    this.authService.hasRole(
      'ADMIN'
    ) ||
    this.authService.hasRole(
      'CREW_MEMBER'
    )
  );
}


canManageArtifacts(): boolean {

  return (
    this.authService.hasRole(
      'ADMIN'
    ) ||
    this.authService.hasRole(
      'CREW_MEMBER'
    )
  );
}


isAdmin(): boolean {

  return this.authService.hasRole(
    'ADMIN'
  );
}

}