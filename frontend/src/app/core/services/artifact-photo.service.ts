import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  environment
} from '../../../environments/environment';


export interface ArtifactPhotoResponse {
  id: number;
  artifactId: number;
  photoNo: string | null;
  fileName: string;
  contentType: string | null;
  uploadedById: number | null;
  uploadedByName: string | null;
  uploadedAt: string;
  deleted: boolean;
  deletedById: number | null;
  deletedByName: string | null;
  deletedAt: string | null;
}


@Injectable({
  providedIn: 'root'
})
export class ArtifactPhotoService {

  private readonly apiUrl =
    `${environment.apiUrl}/artifacts`;

  constructor(
    private http: HttpClient
  ) {}


  // --------------------------------------------------
  // AKTİF FOTOĞRAFLAR
  // --------------------------------------------------

  getPhotos(
    artifactId: number
  ): Observable<ArtifactPhotoResponse[]> {

    return this.http.get<ArtifactPhotoResponse[]>(
      `${this.apiUrl}/${artifactId}/photos`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // SİLİNMİŞ FOTOĞRAFLAR
  // --------------------------------------------------

  getDeletedPhotos(
    artifactId: number
  ): Observable<ArtifactPhotoResponse[]> {

    return this.http.get<ArtifactPhotoResponse[]>(
      `${this.apiUrl}/${artifactId}/photos/deleted`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // FOTOĞRAF YÜKLE
  // --------------------------------------------------

  uploadPhoto(
    artifactId: number,
    file: File,
    photoNo?: string
  ): Observable<ArtifactPhotoResponse> {

    const formData =
      new FormData();

    formData.append(
      'file',
      file
    );

    if (photoNo?.trim()) {

      formData.append(
        'photoNo',
        photoNo.trim()
      );
    }

    return this.http.post<ArtifactPhotoResponse>(
      `${this.apiUrl}/${artifactId}/photos`,
      formData,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // FOTOĞRAF DOSYASI
  // --------------------------------------------------

  getPhotoFile(
    photoId: number
  ): Observable<Blob> {

    return this.http.get(
      `${this.apiUrl}/photos/${photoId}/file`,
      {
        withCredentials: true,
        responseType: 'blob'
      }
    );
  }


  // --------------------------------------------------
  // FOTOĞRAF SİL
  // --------------------------------------------------

  deletePhoto(
    photoId: number
  ): Observable<ArtifactPhotoResponse> {

    return this.http.delete<ArtifactPhotoResponse>(
      `${this.apiUrl}/photos/${photoId}`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // FOTOĞRAFI GERİ YÜKLE
  // --------------------------------------------------

  restorePhoto(
    photoId: number
  ): Observable<ArtifactPhotoResponse> {

    return this.http.post<ArtifactPhotoResponse>(
      `${this.apiUrl}/photos/${photoId}/restore`,
      {},
      {
        withCredentials: true
      }
    );
  }
}