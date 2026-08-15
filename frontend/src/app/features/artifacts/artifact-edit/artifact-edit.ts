import { CommonModule } from '@angular/common';
import {
  Component,
  OnDestroy,
  OnInit,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ArtifactResponse } from '../../../core/models/artifact.model';

import {
  ArtifactService
} from '../../../core/services/artifact.service';

import {
  ArtifactPhotoResponse,
  ArtifactPhotoService
} from '../../../core/services/artifact-photo.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-artifact-edit',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './artifact-edit.html',
  styleUrl: './artifact-edit.scss'
})
export class ArtifactEdit
  implements OnInit, OnDestroy {

  // --------------------------------------------------
  // BULUNTU
  // --------------------------------------------------

  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  artifactId: number | null = null;

  originalArtifactCode = '';


  // --------------------------------------------------
  // FOTOĞRAFLAR
  // --------------------------------------------------

  readonly photos =
    signal<ArtifactPhotoResponse[]>([]);

  readonly photoUrls =
    signal<Record<number, string>>({});

  readonly photosLoading =
    signal(false);

  readonly uploadingPhoto =
    signal(false);

  readonly deletingPhotoId =
    signal<number | null>(null);

  readonly photoErrorMessage =
    signal('');

  readonly photoSuccessMessage =
    signal('');

  selectedPhotoFile: File | null = null;

  selectedPhotoFileName = '';

  photoNo = '';


  // --------------------------------------------------
  // FORM
  // --------------------------------------------------

  formData = {

    artifactCode: '',

    type: '',
    formNo: '',
    inventoryNo: '',
    studyNo: '',
    bagNo: '',
    boxNo: '',
    depth: '',
    box: '',

    findLocation: '',
    locality: '',
    sector: '',
    findDate: '',
    findYear: null as number | null,
    area: '',

    form: '',
    decorationType: '',
    pasteStructure: '',
    firing: '',
    technique: '',
    temper: '',
    temperAmount: '',
    slipStructure: '',
    angle: '',
    period: '',
    kind: '',
    munsell: '',

    diameter: '',
    weight: '',
    length: '',
    width: '',
    thickness: '',

    drawingNo: '',
    preservedPart: '',
    material: '',
    productionPlace: '',
    description: '',
    bibliography: '',

    visibility: 'PRIVATE_FOR_CREW'
  };


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artifactService: ArtifactService,
    private artifactPhotoService: ArtifactPhotoService,
    public authService: AuthService
  ) {}


  // --------------------------------------------------
  // INIT
  // --------------------------------------------------

  ngOnInit(): void {

    const artifactCode =
      this.route.snapshot.paramMap.get(
        'artifactCode'
      );

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


  ngOnDestroy(): void {
    this.clearPhotoUrls();
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

          this.artifactId =
            artifact.id;

          this.originalArtifactCode =
            artifact.artifactCode;

          this.fillForm(
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
              'Bu buluntuyu düzenleme yetkiniz bulunmuyor.'
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
  // FORMU DOLDUR
  // --------------------------------------------------

  private fillForm(
    artifact: ArtifactResponse
  ): void {

    this.formData = {

      artifactCode:
        artifact.artifactCode ?? '',

      type:
        artifact.type ?? '',

      formNo:
        artifact.formNo ?? '',

      inventoryNo:
        artifact.inventoryNo ?? '',

      studyNo:
        artifact.studyNo ?? '',

      bagNo:
        artifact.bagNo ?? '',

      boxNo:
        artifact.boxNo ?? '',

      depth:
        artifact.depth ?? '',

      box:
        artifact.box ?? '',

      findLocation:
        artifact.findLocation ?? '',

      locality:
        artifact.locality ?? '',

      sector:
        artifact.sector ?? '',

      findDate:
        artifact.findDate ?? '',

      findYear:
        artifact.findYear ?? null,

      area:
        artifact.area ?? '',

      form:
        artifact.form ?? '',

      decorationType:
        artifact.decorationType ?? '',

      pasteStructure:
        artifact.pasteStructure ?? '',

      firing:
        artifact.firing ?? '',

      technique:
        artifact.technique ?? '',

      temper:
        artifact.temper ?? '',

      temperAmount:
        artifact.temperAmount ?? '',

      slipStructure:
        artifact.slipStructure ?? '',

      angle:
        artifact.angle ?? '',

      period:
        artifact.period ?? '',

      kind:
        artifact.kind ?? '',

      munsell:
        artifact.munsell ?? '',

      diameter:
        artifact.diameter ?? '',

      weight:
        artifact.weight ?? '',

      length:
        artifact.length ?? '',

      width:
        artifact.width ?? '',

      thickness:
        artifact.thickness ?? '',

      drawingNo:
        artifact.drawingNo ?? '',

      preservedPart:
        artifact.preservedPart ?? '',

      material:
        artifact.material ?? '',

      productionPlace:
        artifact.productionPlace ?? '',

      description:
        artifact.description ?? '',

      bibliography:
        artifact.bibliography ?? '',

      visibility:
        artifact.visibility
    };
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
              'Fotoğrafları görüntüleme yetkiniz bulunmuyor.'
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
              // Önizleme açılamazsa
              // placeholder gösterilir.
            }
          });
      }
    );
  }


  // --------------------------------------------------
  // OBJECT URL TEMİZLİĞİ
  // --------------------------------------------------

  private clearPhotoUrls(): void {

    Object.values(
      this.photoUrls()
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
  // DOSYA SEÇ
  // --------------------------------------------------

  onPhotoSelected(
    event: Event
  ): void {

    this.photoErrorMessage.set('');

    this.photoSuccessMessage.set('');

    const input =
      event.target as HTMLInputElement;

    if (
      !input.files ||
      input.files.length === 0
    ) {

      this.selectedPhotoFile =
        null;

      this.selectedPhotoFileName =
        '';

      return;
    }

    const file =
      input.files[0];

    if (
      !file.type.startsWith(
        'image/'
      )
    ) {

      this.selectedPhotoFile =
        null;

      this.selectedPhotoFileName =
        '';

      input.value = '';

      this.photoErrorMessage.set(
        'Lütfen geçerli bir görsel dosyası seçiniz.'
      );

      return;
    }

    this.selectedPhotoFile =
      file;

    this.selectedPhotoFileName =
      file.name;
  }


  // --------------------------------------------------
  // FOTOĞRAF YÜKLE
  // --------------------------------------------------

  uploadPhoto(
    fileInput?: HTMLInputElement
  ): void {

    this.photoErrorMessage.set('');

    this.photoSuccessMessage.set('');

    if (!this.artifactId) {

      this.photoErrorMessage.set(
        'Buluntu ID bilgisi bulunamadı.'
      );

      return;
    }

    if (!this.selectedPhotoFile) {

      this.photoErrorMessage.set(
        'Lütfen bir fotoğraf seçiniz.'
      );

      return;
    }

    this.uploadingPhoto.set(true);

    this.artifactPhotoService
      .uploadPhoto(
        this.artifactId,
        this.selectedPhotoFile,
        this.photoNo
      )
      .subscribe({

        next: () => {

          this.uploadingPhoto.set(false);

          this.selectedPhotoFile =
            null;

          this.selectedPhotoFileName =
            '';

          this.photoNo =
            '';

          if (fileInput) {
            fileInput.value = '';
          }

          this.photoSuccessMessage.set(
            'Fotoğraf başarıyla yüklendi.'
          );

          if (this.artifactId) {

            this.loadPhotos(
              this.artifactId
            );
          }
        },

        error: (error) => {

          this.uploadingPhoto.set(false);

          if (error.status === 400) {

            this.photoErrorMessage.set(
              error.error?.message ??
              'Fotoğraf dosyası geçerli değil.'
            );

            return;
          }

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

          this.photoErrorMessage.set(
            error.error?.message ??
            'Fotoğraf yüklenirken bir hata oluştu.'
          );
        }
      });
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
            'Fotoğraf görüntülenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FOTOĞRAF SİL
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
        `${photo.photoNo || photo.fileName} fotoğrafını silmek istediğinize emin misiniz?\n\nFotoğraf kalıcı olarak silinmeyecektir.`
      );

    if (!confirmed) {
      return;
    }

    this.deletingPhotoId.set(
      photo.id
    );

    this.photoErrorMessage.set('');

    this.photoSuccessMessage.set('');

    this.artifactPhotoService
      .deletePhoto(
        photo.id
      )
      .subscribe({

        next: () => {

          this.deletingPhotoId.set(
            null
          );

          this.photoSuccessMessage.set(
            'Fotoğraf silindi.'
          );

          if (this.artifactId) {

            this.loadPhotos(
              this.artifactId
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
              'Fotoğraf silme yetkiniz bulunmuyor.'
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
  // BULUNTUYU KAYDET
  // --------------------------------------------------

  saveArtifact(): void {

    this.errorMessage.set('');

    if (!this.artifactId) {

      this.errorMessage.set(
        'Buluntu ID bilgisi bulunamadı.'
      );

      return;
    }

    const artifactCode =
      this.formData.artifactCode
        .trim();

    if (!artifactCode) {

      this.errorMessage.set(
        'Buluntu kodu zorunludur.'
      );

      return;
    }

    if (!this.formData.visibility) {

      this.errorMessage.set(
        'Buluntu görünürlüğü seçilmelidir.'
      );

      return;
    }

    this.formData.artifactCode =
      artifactCode;

    this.saving.set(true);

    this.artifactService
      .updateArtifact(
        this.artifactId,
        this.formData
      )
      .subscribe({

        next: (artifact) => {

          this.saving.set(false);

          this.clearPhotoUrls();

          this.router.navigate([
            '/artifacts',
            artifact.artifactCode
          ]);
        },

        error: (error) => {

          this.saving.set(false);

          if (error.status === 400) {

            this.errorMessage.set(
              error.error?.message ??
              'Buluntu bilgileri geçerli değil.'
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
              'Bu buluntuyu düzenleme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'Buluntu bulunamadı.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntu güncellenirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // İPTAL
  // --------------------------------------------------

  cancel(): void {

    this.clearPhotoUrls();

    this.router.navigate([
      '/artifacts',
      this.originalArtifactCode
    ]);
  }


  // --------------------------------------------------
  // YETKİLER
  // --------------------------------------------------

  isAdmin(): boolean {

    return this.authService.hasRole(
      'ADMIN'
    );
  }
}