import { CommonModule } from '@angular/common';
import {
  Component,
  OnDestroy,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import {
  ArtifactService
} from '../../../core/services/artifact.service';

import {
  ArtifactPhotoService
} from '../../../core/services/artifact-photo.service';


interface PendingPhoto {
  id: number;
  file: File;
  fileName: string;
  photoNo: string;
  previewUrl: string;
}


@Component({
  selector: 'app-artifact-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './artifact-form.html',
  styleUrl: './artifact-form.scss'
})
export class ArtifactForm
  implements OnDestroy {

  readonly saving =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly photoErrorMessage =
    signal('');

  readonly pendingPhotos =
    signal<PendingPhoto[]>([]);

  private nextPhotoId = 1;


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
    private artifactService: ArtifactService,
    private artifactPhotoService: ArtifactPhotoService,
    private router: Router
  ) {}


  // --------------------------------------------------
  // DESTROY
  // --------------------------------------------------

  ngOnDestroy(): void {
    this.clearPendingPhotoUrls();
  }


  // --------------------------------------------------
  // FOTOĞRAF SEÇ
  // --------------------------------------------------

  onPhotosSelected(
    event: Event
  ): void {

    this.photoErrorMessage.set('');

    const input =
      event.target as HTMLInputElement;

    if (
      !input.files ||
      input.files.length === 0
    ) {
      return;
    }

    const files =
      Array.from(input.files);

    const validFiles: File[] = [];

    let invalidFileFound = false;

    files.forEach(
      (file) => {

        if (
          !file.type.startsWith('image/')
        ) {

          invalidFileFound = true;

          return;
        }

        validFiles.push(file);
      }
    );


    if (invalidFileFound) {

      this.photoErrorMessage.set(
        'Görsel olmayan dosyalar eklenmedi.'
      );
    }


    const newPhotos =
      validFiles.map(
        (file): PendingPhoto => ({

          id:
            this.nextPhotoId++,

          file,

          fileName:
            file.name,

          photoNo:
            '',

          previewUrl:
            URL.createObjectURL(file)
        })
      );


    this.pendingPhotos.update(
      current => [
        ...current,
        ...newPhotos
      ]
    );


    // Aynı dosyanın tekrar
    // seçilebilmesine izin verir.
    input.value = '';
  }


  // --------------------------------------------------
  // SEÇİLEN FOTOĞRAFI KALDIR
  // --------------------------------------------------

  removePendingPhoto(
    photoId: number
  ): void {

    const photo =
      this.pendingPhotos()
        .find(
          item =>
            item.id === photoId
        );

    if (photo) {

      URL.revokeObjectURL(
        photo.previewUrl
      );
    }

    this.pendingPhotos.update(
      current =>
        current.filter(
          item =>
            item.id !== photoId
        )
    );
  }


  // --------------------------------------------------
  // FOTOĞRAF NO GÜNCELLE
  // --------------------------------------------------

  updatePhotoNo(
    photoId: number,
    value: string
  ): void {

    this.pendingPhotos.update(
      current =>
        current.map(
          photo => {

            if (
              photo.id !== photoId
            ) {
              return photo;
            }

            return {
              ...photo,
              photoNo: value
            };
          }
        )
    );
  }


  // --------------------------------------------------
  // OBJECT URL TEMİZLE
  // --------------------------------------------------

  private clearPendingPhotoUrls(): void {

    this.pendingPhotos()
      .forEach(
        (photo) => {

          URL.revokeObjectURL(
            photo.previewUrl
          );
        }
      );
  }


  // --------------------------------------------------
  // BULUNTUYU KAYDET
  // --------------------------------------------------

  saveArtifact(): void {

    this.errorMessage.set('');
    this.photoErrorMessage.set('');

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
      .createArtifact(
        this.formData
      )
      .subscribe({

        next: (artifact) => {

          const photos =
            this.pendingPhotos();


          // FOTOĞRAF YOKSA
          // DOĞRUDAN DETAYA GİT

          if (
            photos.length === 0
          ) {

            this.saving.set(false);

            this.router.navigate([
              '/artifacts',
              artifact.artifactCode
            ]);

            return;
          }


          // FOTOĞRAFLARI
          // OLUŞAN BULUNTUYA BAĞLA

          const uploadRequests =
            photos.map(
              photo =>
                this.artifactPhotoService
                  .uploadPhoto(
                    artifact.id,
                    photo.file,
                    photo.photoNo.trim()
                  )
            );


          forkJoin(
            uploadRequests
          )
            .subscribe({

              next: () => {

                this.saving.set(false);

                this.clearPendingPhotoUrls();

                this.pendingPhotos.set([]);

                this.router.navigate([
                  '/artifacts',
                  artifact.artifactCode
                ]);
              },


              error: (error) => {

                this.saving.set(false);

                /*
                 * Buluntu bu aşamada
                 * başarıyla oluşturulmuştur.
                 *
                 * Bu nedenle tekrar create
                 * çağrısı yaptırmıyoruz.
                 * Kullanıcıyı mevcut kayda
                 * yönlendiriyoruz.
                 */

                if (error.status === 401) {

                  window.alert(
                    'Buluntu oluşturuldu ancak fotoğraflar yüklenemedi. Oturumunuz sona ermiş olabilir.'
                  );

                } else if (
                  error.status === 403
                ) {

                  window.alert(
                    'Buluntu oluşturuldu ancak fotoğraf yükleme yetkiniz bulunmadığı için fotoğraflar yüklenemedi.'
                  );

                } else {

                  window.alert(
                    error.error?.message ??
                    'Buluntu oluşturuldu ancak fotoğraflardan biri veya birkaçı yüklenemedi.'
                  );
                }


                this.clearPendingPhotoUrls();

                this.pendingPhotos.set([]);

                this.router.navigate([
                  '/artifacts',
                  artifact.artifactCode
                ]);
              }
            });
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
              'Buluntu oluşturma yetkiniz bulunmuyor.'
            );

            return;
          }


          this.errorMessage.set(
            error.error?.message ??
            'Buluntu kaydedilirken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // İPTAL
  // --------------------------------------------------

  cancel(): void {

    this.clearPendingPhotoUrls();

    this.router.navigate([
      '/home'
    ]);
  }
}