import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';

interface CsrfResponse {
  token: string;
  headerName: string;
  parameterName: string;
}

@Injectable({
  providedIn: 'root'
})
export class CsrfService {

  private readonly apiUrl =
    `${environment.apiUrl}/csrf`;

  private csrfToken: string | null = null;

  constructor(
    private http: HttpClient
  ) {}

  loadToken(): Observable<CsrfResponse> {

    return this.http.get<CsrfResponse>(
      this.apiUrl,
      {
        withCredentials: true
      }
    ).pipe(
      tap(response => {
        this.csrfToken =
          response.token;
      })
    );
  }

  getToken(): string | null {
    return this.csrfToken;
  }

  clearToken(): void {
    this.csrfToken = null;
  }
}