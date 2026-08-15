import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import {
  ChangePasswordRequest,
  LoginRequest,
  LoginResponse,
  MessageResponse,
  ResetPasswordRequest
} from '../models/auth.model';

import {
  environment
} from '../../../environments/environment';


export interface ProfileResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'CREW_MEMBER' | 'LOOKUP_USER';
  mustChangePassword: boolean;
}

export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl =
    `${environment.apiUrl}/auth`;

  private readonly storageKey =
    'philadelphia_current_user';


  private readonly currentUserSignal =
    signal<LoginResponse | null>(
      this.loadStoredUser()
    );


  readonly currentUser =
    this.currentUserSignal.asReadonly();


  constructor(
    private http: HttpClient
  ) {}


  // --------------------------------------------------
  // LOGIN
  // --------------------------------------------------

  login(
    request: LoginRequest
  ): Observable<LoginResponse> {

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      request,
      {
        withCredentials: true
      }
    ).pipe(

      tap(user => {

        this.currentUserSignal.set(user);


        // Önce iki storage'ı da temizle.
        this.clearStoredUser();


        if (request.rememberMe) {

          localStorage.setItem(
            this.storageKey,
            JSON.stringify(user)
          );

        } else {

          sessionStorage.setItem(
            this.storageKey,
            JSON.stringify(user)
          );
        }
      })
    );
  }


  // --------------------------------------------------
  // ŞİFREMİ UNUTTUM
  // --------------------------------------------------

  forgotPassword(
    email: string
  ): Observable<MessageResponse> {

    return this.http.post<MessageResponse>(
      `${this.apiUrl}/forgot-password`,
      {
        email
      },
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // ŞİFRE SIFIRLA
  // --------------------------------------------------

  resetPassword(
    request: ResetPasswordRequest
  ): Observable<MessageResponse> {

    return this.http.post<MessageResponse>(
      `${this.apiUrl}/reset-password`,
      request,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // LOGOUT
  // --------------------------------------------------

  logout(): Observable<MessageResponse> {

    return this.http.post<MessageResponse>(
      `${this.apiUrl}/logout`,
      {},
      {
        withCredentials: true
      }
    ).pipe(

      tap(() => {

        this.currentUserSignal.set(null);

        this.clearStoredUser();
      })
    );
  }


  // --------------------------------------------------
  // KENDİ PROFİLİMİ GETİR
  // --------------------------------------------------

  getMyProfile():
    Observable<ProfileResponse> {

    return this.http.get<ProfileResponse>(
      `${this.apiUrl}/me`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // KENDİ PROFİLİMİ GÜNCELLE
  // --------------------------------------------------

  updateMyProfile(
    request: ProfileUpdateRequest
  ): Observable<ProfileResponse> {

    return this.http.put<ProfileResponse>(
      `${this.apiUrl}/me`,
      request,
      {
        withCredentials: true
      }
    ).pipe(

      tap(profile => {

        const currentUser =
          this.currentUserSignal();

        if (!currentUser) {
          return;
        }


        const updatedUser: LoginResponse = {
          ...currentUser,
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
          role: profile.role,
          mustChangePassword:
            profile.mustChangePassword
        };


        this.currentUserSignal.set(
          updatedUser
        );


        this.updateStoredUser(
          updatedUser
        );
      })
    );
  }


  // --------------------------------------------------
  // ŞİFRE DEĞİŞTİR
  // --------------------------------------------------

  changePassword(
    request: ChangePasswordRequest
  ): Observable<MessageResponse> {

    return this.http.post<MessageResponse>(
      `${this.apiUrl}/change-password`,
      request,
      {
        withCredentials: true
      }
    ).pipe(

      tap(() => {

        const currentUser =
          this.currentUserSignal();

        if (!currentUser) {
          return;
        }


        const updatedUser: LoginResponse = {
          ...currentUser,
          mustChangePassword: false
        };


        this.currentUserSignal.set(
          updatedUser
        );


        this.updateStoredUser(
          updatedUser
        );
      })
    );
  }


  // --------------------------------------------------
  // CURRENT USER
  // --------------------------------------------------

  getCurrentUser():
    LoginResponse | null {

    return this.currentUserSignal();
  }


  // --------------------------------------------------
  // LOGIN KONTROLÜ
  // --------------------------------------------------

  isLoggedIn(): boolean {

    return (
      this.currentUserSignal() !== null
    );
  }


  // --------------------------------------------------
  // ROL KONTROLÜ
  // --------------------------------------------------

  hasRole(
    role: string
  ): boolean {

    return (
      this.currentUserSignal()?.role === role
    );
  }


  // --------------------------------------------------
  // BENİ HATIRLA KONTROLÜ
  // --------------------------------------------------

  isRemembered(): boolean {

    return (
      localStorage.getItem(
        this.storageKey
      ) !== null
    );
  }


  // --------------------------------------------------
  // KULLANICIYI STORAGE'DAN GERİ YÜKLE
  // --------------------------------------------------

  private loadStoredUser():
    LoginResponse | null {

    const localUser =
      localStorage.getItem(
        this.storageKey
      );


    const sessionUser =
      sessionStorage.getItem(
        this.storageKey
      );


    const storedUser =
      localUser ?? sessionUser;


    if (!storedUser) {
      return null;
    }


    try {

      return JSON.parse(
        storedUser
      ) as LoginResponse;

    } catch {

      this.clearStoredUser();

      return null;
    }
  }


  // --------------------------------------------------
  // STORAGE'DAKİ KULLANICIYI GÜNCELLE
  // --------------------------------------------------

  private updateStoredUser(
    user: LoginResponse
  ): void {

    const serializedUser =
      JSON.stringify(user);


    if (
      localStorage.getItem(
        this.storageKey
      )
    ) {

      localStorage.setItem(
        this.storageKey,
        serializedUser
      );

      return;
    }


    if (
      sessionStorage.getItem(
        this.storageKey
      )
    ) {

      sessionStorage.setItem(
        this.storageKey,
        serializedUser
      );
    }
  }


  // --------------------------------------------------
  // TÜM STORAGE KAYITLARINI TEMİZLE
  // --------------------------------------------------

  private clearStoredUser(): void {

    sessionStorage.removeItem(
      this.storageKey
    );

    localStorage.removeItem(
      this.storageKey
    );
  }
}