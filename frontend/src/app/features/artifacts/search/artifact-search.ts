import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  ArtifactListItemResponse
} from '../../../core/models/artifact.model';

import {
  ArtifactService
} from '../../../core/services/artifact.service';

@Component({
  selector: 'app-artifact-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './artifact-search.html',
  styleUrl: './artifact-search.scss'
})
export class ArtifactSearch {

  readonly loading =
    signal(false);

  readonly searched =
    signal(false);

  readonly errorMessage =
    signal('');

  readonly results =
    signal<ArtifactListItemResponse[]>([]);

  filters = {

    artifactCode: '',

    type: '',

    findLocation: '',

    locality: '',

    sector: '',

    findYear: null as number | null,

    period: '',

    material: '',

    form: '',

    decorationType: '',

    technique: '',

    munsell: ''
  };

  constructor(
    private router: Router,
    private artifactService: ArtifactService
  ) {}


  // --------------------------------------------------
  // ARAMA
  // --------------------------------------------------

  search(): void {

    this.errorMessage.set('');
    this.loading.set(true);
    this.searched.set(true);

    this.artifactService
      .searchArtifacts(this.filters)
      .subscribe({

        next: (artifacts) => {

          this.results.set(artifacts);

          this.loading.set(false);
        },

        error: (error) => {

          this.loading.set(false);

          this.results.set([]);

          if (error.status === 401) {

            this.errorMessage.set(
              'Oturumunuz sona ermiş. Lütfen tekrar giriş yapınız.'
            );

            return;
          }

          if (error.status === 403) {

            this.errorMessage.set(
              'Arama yapma yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 400) {

            this.errorMessage.set(
              error.error?.message ??
              'Arama kriterleri geçerli değil.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Buluntular aranırken bir hata oluştu.'
          );
        }
      });
  }


  // --------------------------------------------------
  // FİLTRELERİ TEMİZLE
  // --------------------------------------------------

  clearFilters(): void {

    this.filters = {

      artifactCode: '',

      type: '',

      findLocation: '',

      locality: '',

      sector: '',

      findYear: null,

      period: '',

      material: '',

      form: '',

      decorationType: '',

      technique: '',

      munsell: ''
    };

    this.results.set([]);

    this.searched.set(false);

    this.errorMessage.set('');
  }


  // --------------------------------------------------
  // BULUNTUYU AÇ
  // --------------------------------------------------

  openArtifact(
    artifact: ArtifactListItemResponse
  ): void {

    this.router.navigate([
      '/artifacts',
      artifact.artifactCode
    ]);
  }


  // --------------------------------------------------
  // BULUNTULARA DÖN
  // --------------------------------------------------

  goBack(): void {

    this.router.navigate([
      '/artifacts'
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