import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpParams
} from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  ArtifactListItemResponse,
  ArtifactResponse
} from '../models/artifact.model';

import {
  environment
} from '../../../environments/environment';


export interface ArtifactFieldChange {
  fieldName: string;
  oldValue: string | null;
  newValue: string | null;
}


export interface ArtifactHistoryResponse {
  id: number;
  changeType: string;
  changedById: number | null;
  changedByName: string | null;
  changedAt: string;
  fieldChanges: ArtifactFieldChange[];
}


export interface ArtifactSearchFilters {
  artifactCode?: string;
  type?: string;
  findLocation?: string;
  locality?: string;
  sector?: string;
  findYear?: number | null;
  period?: string;
  material?: string;
  form?: string;
  decorationType?: string;
  technique?: string;
  munsell?: string;
}


@Injectable({
  providedIn: 'root'
})
export class ArtifactService {

  private readonly apiUrl =
    `${environment.apiUrl}/artifacts`;

  constructor(
    private http: HttpClient
  ) {}


  // --------------------------------------------------
  // TÜM BULUNTULAR
  // --------------------------------------------------

  getAllArtifacts():
    Observable<ArtifactListItemResponse[]> {

    return this.http.get<ArtifactListItemResponse[]>(
      this.apiUrl,
      {
        withCredentials: true
      }
    );
  }
// --------------------------------------------------
// BULUNTU KODU MEVCUT MU?
// AKTİF + SİLİNMİŞ TÜM KAYITLAR
// --------------------------------------------------
artifactCodeStatus(
  artifactCode: string
): Observable<
  'ACTIVE' | 'DELETED' | 'AVAILABLE'
> {

  const encodedCode =
    encodeURIComponent(
      artifactCode
    );

  return this.http.get<
    'ACTIVE' | 'DELETED' | 'AVAILABLE'
  >(
    `${this.apiUrl}/code/${encodedCode}/status`,
    {
      withCredentials: true,
      responseType: 'text' as 'json'
    }
  );
}
  // --------------------------------------------------
  // GELİŞMİŞ ARAMA
  // --------------------------------------------------

  searchArtifacts(
    filters: ArtifactSearchFilters
  ): Observable<ArtifactListItemResponse[]> {

    let params =
      new HttpParams();

    if (filters.artifactCode?.trim()) {
      params = params.set(
        'artifactCode',
        filters.artifactCode.trim()
      );
    }

    if (filters.type?.trim()) {
      params = params.set(
        'type',
        filters.type.trim()
      );
    }

    if (filters.findLocation?.trim()) {
      params = params.set(
        'findLocation',
        filters.findLocation.trim()
      );
    }

    if (filters.locality?.trim()) {
      params = params.set(
        'locality',
        filters.locality.trim()
      );
    }

    if (filters.sector?.trim()) {
      params = params.set(
        'sector',
        filters.sector.trim()
      );
    }

    if (
      filters.findYear !== null &&
      filters.findYear !== undefined
    ) {
      params = params.set(
        'findYear',
        filters.findYear.toString()
      );
    }

    if (filters.period?.trim()) {
      params = params.set(
        'period',
        filters.period.trim()
      );
    }

    if (filters.material?.trim()) {
      params = params.set(
        'material',
        filters.material.trim()
      );
    }

    if (filters.form?.trim()) {
      params = params.set(
        'form',
        filters.form.trim()
      );
    }

    if (filters.decorationType?.trim()) {
      params = params.set(
        'decorationType',
        filters.decorationType.trim()
      );
    }

    if (filters.technique?.trim()) {
      params = params.set(
        'technique',
        filters.technique.trim()
      );
    }

    if (filters.munsell?.trim()) {
      params = params.set(
        'munsell',
        filters.munsell.trim()
      );
    }

    return this.http.get<ArtifactListItemResponse[]>(
      `${this.apiUrl}/search`,
      {
        params,
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // BULUNTU KODUNA GÖRE GETİR
  // --------------------------------------------------

  getArtifactByCode(
    artifactCode: string
  ): Observable<ArtifactResponse> {

    const encodedCode =
      encodeURIComponent(
        artifactCode
      );

    return this.http.get<ArtifactResponse>(
      `${this.apiUrl}/code/${encodedCode}`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // PUBLIC BULUNTULAR
  // --------------------------------------------------

  getPublicArtifacts():
    Observable<ArtifactListItemResponse[]> {

    return this.http.get<ArtifactListItemResponse[]>(
      `${this.apiUrl}/public`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // YENİ BULUNTU OLUŞTUR
  // --------------------------------------------------

  createArtifact(
    request: unknown
  ): Observable<ArtifactResponse> {

    return this.http.post<ArtifactResponse>(
      this.apiUrl,
      request,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // BULUNTU GÜNCELLE
  // --------------------------------------------------

  updateArtifact(
    artifactId: number,
    request: unknown
  ): Observable<ArtifactResponse> {

    return this.http.put<ArtifactResponse>(
      `${this.apiUrl}/${artifactId}`,
      request,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // BULUNTU PDF İNDİR
  // --------------------------------------------------

  downloadArtifactPdf(
    artifactId: number
  ): Observable<Blob> {

    return this.http.get(
      `${this.apiUrl}/${artifactId}/pdf`,
      {
        withCredentials: true,
        responseType: 'blob'
      }
    );
  }


  // --------------------------------------------------
  // TÜM AKTİF BULUNTULARI EXCEL İNDİR
  // ADMIN + CREW_MEMBER
  // --------------------------------------------------

  downloadArtifactsExcel():
    Observable<Blob> {

    return this.http.get(
      `${this.apiUrl}/excel`,
      {
        withCredentials: true,
        responseType: 'blob'
      }
    );
  }


  // --------------------------------------------------
  // BULUNTU DEĞİŞİKLİK GEÇMİŞİ
  // --------------------------------------------------

  getArtifactHistory(
    artifactId: number
  ): Observable<ArtifactHistoryResponse[]> {

    return this.http.get<ArtifactHistoryResponse[]>(
      `${this.apiUrl}/${artifactId}/history`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // SİLİNMİŞ BULUNTULAR
  // SADECE ADMIN
  // --------------------------------------------------

  getDeletedArtifacts():
    Observable<ArtifactListItemResponse[]> {

    return this.http.get<ArtifactListItemResponse[]>(
      `${this.apiUrl}/deleted`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // BULUNTU SİL
  // SADECE ADMIN
  // --------------------------------------------------

  deleteArtifact(
    artifactId: number
  ): Observable<ArtifactResponse> {

    return this.http.delete<ArtifactResponse>(
      `${this.apiUrl}/${artifactId}`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // SİLİNMİŞ BULUNTUYU GERİ YÜKLE
  // SADECE ADMIN
  // --------------------------------------------------

  restoreArtifact(
    artifactId: number
  ): Observable<ArtifactResponse> {

    return this.http.post<ArtifactResponse>(
      `${this.apiUrl}/${artifactId}/restore`,
      {},
      {
        withCredentials: true
      }
    );
  }
}