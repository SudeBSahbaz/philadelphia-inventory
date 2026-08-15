import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  ArtifactHistoryResponse,
  ArtifactService
} from '../../../core/services/artifact.service';

@Component({
  selector: 'app-artifact-history',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './artifact-history.html',
  styleUrl: './artifact-history.scss'
})
export class ArtifactHistory implements OnInit {

  readonly history =
    signal<ArtifactHistoryResponse[]>([]);

  readonly loading =
    signal(true);

  readonly errorMessage =
    signal('');

  private artifactId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artifactService: ArtifactService
  ) {}

  ngOnInit(): void {

    const artifactIdParam =
      this.route.snapshot.paramMap.get('artifactId');

    if (!artifactIdParam) {

      this.loading.set(false);

      this.errorMessage.set(
        'Buluntu bilgisi bulunamadı.'
      );

      return;
    }

    const artifactId =
      Number(artifactIdParam);

    if (Number.isNaN(artifactId)) {

      this.loading.set(false);

      this.errorMessage.set(
        'Geçersiz buluntu numarası.'
      );

      return;
    }

    this.artifactId = artifactId;

    this.loadHistory();
  }

  private loadHistory(): void {

    if (this.artifactId === null) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.artifactService
      .getArtifactHistory(this.artifactId)
      .subscribe({

        next: (history) => {

          this.history.set(history);

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
              'Değişiklik geçmişini görüntüleme yetkiniz bulunmuyor.'
            );

            return;
          }

          if (error.status === 404) {

            this.errorMessage.set(
              'Buluntu geçmişi bulunamadı.'
            );

            return;
          }

          this.errorMessage.set(
            error.error?.message ??
            'Değişiklik geçmişi yüklenirken bir hata oluştu.'
          );
        }
      });
  }

  goBack(): void {
    window.history.back();
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }
}