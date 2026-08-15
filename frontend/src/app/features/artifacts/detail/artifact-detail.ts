import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ArtifactResponse } from '../../../core/models/artifact.model';
import { ArtifactService } from '../../../core/services/artifact.service';
import { AuthService } from '../../../core/services/auth.service';

import {
  ArtifactPhotoResponse,
  ArtifactPhotoService
} from '../../../core/services/artifact-photo.service';

@Component({
  selector: 'app-artifact-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './artifact-detail.html',
  styleUrl: './artifact-detail.scss'
})
export class ArtifactDetail implements OnInit {

  readonly artifact =
    signal<ArtifactResponse | null>(null);

  readonly photos =
    signal<ArtifactPhotoResponse[]>([]);

  readonly photoUrls =
    signal<Record<number, string>>({});

  readonly loading =
    signal(true);

  readonly photosLoading =
    signal(false);

  readonly uploadingPhoto =
    signal(false);

  readonly selectedPhotoFile =
    signal<File | null>(null);

  readonly downloadingPdf =
    signal(false);

  readonly deleting =
    signal(false);

  readonly deletingPhotoId =
    signal<number | null>(null);

  readonly errorMessage =
    signal('');

  readonly photoErrorMessage =
    signal('');

  photoNo = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artifactService: ArtifactService,
    private artifactPhotoService: ArtifactPhotoService,
    public authService: AuthService
  ) {}


  // --------------------------------------------------
  // SAYFA AÇILIŞI
  // --------------------------------------------------

  ngOnInit(): void {

    const artifactCode =
      this.route.snapshot.paramMap.get('artifactCode');

    if (!artifactCode) {

      this.loading.set(false);

      this.errorMessage.set(
        'Buluntu kodu bulunamadı.'
      );

      return;
    }

    this.loadArtifact(
      artifactCode
    );
  }


  // --------------------------------------------------
  // BULUNTUYU YÜKLE
  // --------------------------------------------------

  private loadArtifact(
    artifactCode: string
  ): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.artifactService
      .getArtifactByCode(
        artifactCode
      )
      .subscribe({

        next: (artifact) => {

          this.artifact.set(
            artifact
          );

          this.loading.set(false);

          this.loadPhotos(
            artifact.id
          );
        },

        error: (error) => {

          this.loading.set(false);

          if (error.status === 404) {

            this.errorMessage.set(
              'Buluntu bulunamadı.'
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
              'Bu buluntuyu görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntu yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FOTOĞRAFLARI YÜKLE
  // --------------------------------------------------

  private loadPhotos(
    artifactId: number
  ): void {

    this.photosLoading.set(true);
    this.photoErrorMessage.set('');

    this.artifactPhotoService
      .getPhotos(
        artifactId
      )
      .subscribe({

        next: (photos) => {

          this.photos.set(
            photos
          );

          this.loadPhotoPreviews(
            photos
          );

          this.photosLoading.set(false);
        },

        error: (error) => {

          this.photosLoading.set(false);

          if (error.status === 401) {

            this.photoErrorMessage.set(
              'Fotoğrafları görüntülemek için tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.photoErrorMessage.set(
              'Bu buluntunun fotoğraflarını görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.photoErrorMessage.set(
            error.error?.message ??
            'Fotoğraflar yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FOTOĞRAF SEÇ
  // --------------------------------------------------

  onPhotoSelected(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ?? null;

    this.selectedPhotoFile.set(
      file
    );

    this.photoErrorMessage.set('');
  }


  // --------------------------------------------------
  // FOTOĞRAF YÜKLE
  // ADMIN + CREW_MEMBER
  // --------------------------------------------------

  uploadPhoto(): void {

    const currentArtifact =
      this.artifact();

    const file =
      this.selectedPhotoFile();

    if (!currentArtifact) {

      this.photoErrorMessage.set(
        'Buluntu bilgisi bulunamadı.'
      );

      return;
    }

    if (!file) {

      this.photoErrorMessage.set(
        'Lütfen yüklenecek bir fotoğraf seçiniz.'
      );

      return;
    }

    if (!this.canEdit()) {

      this.photoErrorMessage.set(
        'Fotoğraf yükleme yetkiniz bulunmuyor.'
      );

      return;
    }

    this.uploadingPhoto.set(true);
    this.photoErrorMessage.set('');

    this.artifactPhotoService
      .uploadPhoto(
        currentArtifact.id,
        file,
        this.photoNo
      )
      .subscribe({

        next: () => {

          this.uploadingPhoto.set(false);

          this.selectedPhotoFile.set(
            null
          );

          this.photoNo = '';

          this.loadPhotos(
            currentArtifact.id
          );
        },

        error: (error) => {

          this.uploadingPhoto.set(false);

          if (error.status === 401) {

            this.photoErrorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.photoErrorMessage.set(
              'Fotoğraf yükleme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 400) {

            this.photoErrorMessage.set(
              error.error?.message ??
              'Seçilen fotoğraf yüklenemedi.'
            );

            return;
          }

          this.photoErrorMessage.set(
            error.error?.message ??
            'Fotoğraf yüklenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FOTOĞRAF ÖNİZLEMELERİ
  // --------------------------------------------------

  private loadPhotoPreviews(
    photos: ArtifactPhotoResponse[]
  ): void {

    this.clearPhotoUrls();

    photos.forEach(
      (photo) => {

        this.artifactPhotoService
          .getPhotoFile(
            photo.id
          )
          .subscribe({

            next: (blob) => {

              const url =
                URL.createObjectURL(
                  blob
                );

              this.photoUrls.update(
                current => ({
                  ...current,
                  [photo.id]: url
                })
              );
            },

            error: () => {
              // Önizleme yüklenemezse
              // placeholder gösterilir.
            }
          });
      }
    );
  }


  // --------------------------------------------------
  // FOTOĞRAF URL'LERİNİ TEMİZLE
  // --------------------------------------------------

  private clearPhotoUrls(): void {

    const currentUrls =
      this.photoUrls();

    Object.values(
      currentUrls
    ).forEach(
      (url) => {

        URL.revokeObjectURL(
          url
        );
      }
    );

    this.photoUrls.set({});
  }


  // --------------------------------------------------
  // FOTOĞRAFI AÇ
  // --------------------------------------------------

  openPhoto(
    photo: ArtifactPhotoResponse
  ): void {

    this.photoErrorMessage.set('');

    this.artifactPhotoService
      .getPhotoFile(
        photo.id
      )
      .subscribe({

        next: (blob) => {

          const url =
            URL.createObjectURL(
              blob
            );

          window.open(
            url,
            '_blank'
          );

          setTimeout(
            () => {

              URL.revokeObjectURL(
                url
              );
            },
            60000
          );
        },

        error: (error) => {

          if (error.status === 401) {

            this.photoErrorMessage.set(
              'Fotoğrafı görüntülemek için tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.photoErrorMessage.set(
              'Bu fotoğrafı görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.photoErrorMessage.set(
            'Fotoğraf açılırken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FOTOĞRAFI SİL
  // SADECE ADMIN
  // --------------------------------------------------

  deletePhoto(
    photo: ArtifactPhotoResponse
  ): void {

    if (!this.isAdmin()) {
      return;
    }

    const confirmed =
      window.confirm(
        `${photo.fileName} dosyasını silmek istediğinize emin misiniz?\n\nFotoğraf kalıcı olarak silinmeyecektir.`
      );

    if (!confirmed) {
      return;
    }

    this.deletingPhotoId.set(
      photo.id
    );

    this.photoErrorMessage.set('');

    this.artifactPhotoService
      .deletePhoto(
        photo.id
      )
      .subscribe({

        next: () => {

          this.deletingPhotoId.set(
            null
          );

          const currentArtifact =
            this.artifact();

          if (currentArtifact) {

            this.loadPhotos(
              currentArtifact.id
            );
          }
        },

        error: (error) => {

          this.deletingPhotoId.set(
            null
          );

          if (error.status === 401) {

            this.photoErrorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.photoErrorMessage.set(
              'Fotoğrafı silme yetkiniz bulunmuyor.'
            );

            return;
          }

          this.photoErrorMessage.set(
            error.error?.message ??
            'Fotoğraf silinirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // PDF İNDİR
  // ADMIN + CREW_MEMBER
  // --------------------------------------------------

  downloadPdf(): void {

    const currentArtifact =
      this.artifact();

    if (!currentArtifact) {
      return;
    }

    // LOOKUP_USER PDF indiremez.
    if (!this.canEdit()) {
  return;
}

    this.downloadingPdf.set(true);
    this.errorMessage.set('');

    this.artifactService
      .downloadArtifactPdf(
        currentArtifact.id
      )
      .subscribe({

        next: (blob) => {

          this.downloadingPdf.set(false);

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
            `${currentArtifact.artifactCode}.pdf`;

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

          this.downloadingPdf.set(false);

          if (error.status === 401) {

            this.errorMessage.set(
              'PDF indirmek için tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Bu buluntunun PDF dosyasını indirme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'PDF oluşturulacak buluntu bulunamadı.'
            );

            return;
          }

          this.errorMessage.set(
            'PDF oluşturulurken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // BULUNTULARA DÖN
  // --------------------------------------------------

  goBack(): void {

    this.clearPhotoUrls();

    this.router.navigate([
      '/artifacts'
    ]);
  }


  // --------------------------------------------------
  // BULUNTUYU DÜZENLE
  // --------------------------------------------------

  editArtifact(): void {

    const currentArtifact =
      this.artifact();

    if (!currentArtifact) {
      return;
    }

    this.router.navigate([
      '/artifacts',
      currentArtifact.artifactCode,
      'edit'
    ]);
  }


  // --------------------------------------------------
  // DEĞİŞİKLİK GEÇMİŞİ
  // --------------------------------------------------

  showHistory(): void {

    const currentArtifact =
      this.artifact();

    if (!currentArtifact) {
      return;
    }

    this.router.navigate([
      '/artifacts',
      currentArtifact.id,
      'history'
    ]);
  }


  // --------------------------------------------------
  // BULUNTUYU SİL
  // SADECE ADMIN
  // --------------------------------------------------

  deleteArtifact(): void {

    const currentArtifact =
      this.artifact();

    if (
      !currentArtifact ||
      !this.isAdmin()
    ) {
      return;
    }

    const confirmed =
      window.confirm(
        `${currentArtifact.artifactCode} kodlu buluntuyu silmek istediğinize emin misiniz?\n\nBuluntu kalıcı olarak silinmeyecek ve Silinmiş Buluntular bölümünden geri yüklenebilecektir.`
      );

    if (!confirmed) {
      return;
    }

    this.deleting.set(true);
    this.errorMessage.set('');

    this.artifactService
      .deleteArtifact(
        currentArtifact.id
      )
      .subscribe({

        next: () => {

          this.deleting.set(false);

          this.clearPhotoUrls();

          this.router.navigate([
            '/artifacts'
          ]);
        },

        error: (error) => {

          this.deleting.set(false);

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Buluntuyu silme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'Silinecek buluntu bulunamadı.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntu silinirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // YETKİLER
  // --------------------------------------------------

  canEdit(): boolean {

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