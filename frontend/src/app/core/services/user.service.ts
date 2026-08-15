import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  environment
} from '../../../environments/environment';


export type UserRole =
  | 'ADMIN'
  | 'CREW_MEMBER'
  | 'LOOKUP_USER';


export interface UserResponse {
  id: number;

  firstName: string;
  lastName: string;

  email: string;

  role: UserRole;

  active: boolean;

  mustChangePassword: boolean;

  createdAt: string;
  updatedAt: string;
}


export interface CreateUserRequest {
  firstName: string;
  lastName: string;

  email: string;

  role: UserRole;

  password: string;
}


export interface UpdateUserRequest {
  firstName: string;
  lastName: string;

  email: string;

  role: UserRole;
}


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl =
    `${environment.apiUrl}/users`;

  constructor(
    private http: HttpClient
  ) {}


  // --------------------------------------------------
  // TÜM KULLANICILAR
  // SADECE ADMIN
  // --------------------------------------------------

  getAllUsers():
    Observable<UserResponse[]> {

    return this.http.get<UserResponse[]>(
      this.apiUrl,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // ID İLE KULLANICI GETİR
  // SADECE ADMIN
  // --------------------------------------------------

  getUserById(
    userId: number
  ): Observable<UserResponse> {

    return this.http.get<UserResponse>(
      `${this.apiUrl}/${userId}`,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // YENİ KULLANICI OLUŞTUR
  // SADECE ADMIN
  // --------------------------------------------------

  createUser(
    request: CreateUserRequest
  ): Observable<UserResponse> {

    return this.http.post<UserResponse>(
      this.apiUrl,
      request,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // KULLANICI GÜNCELLE
  // SADECE ADMIN
  // --------------------------------------------------

  updateUser(
    userId: number,
    request: UpdateUserRequest
  ): Observable<UserResponse> {

    return this.http.put<UserResponse>(
      `${this.apiUrl}/${userId}`,
      request,
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // KULLANICIYI PASİF YAP
  // SADECE ADMIN
  // --------------------------------------------------

  deactivateUser(
    userId: number
  ): Observable<UserResponse> {

    return this.http.post<UserResponse>(
      `${this.apiUrl}/${userId}/deactivate`,
      {},
      {
        withCredentials: true
      }
    );
  }


  // --------------------------------------------------
  // KULLANICIYI AKTİF YAP
  // SADECE ADMIN
  // --------------------------------------------------

  activateUser(
    userId: number
  ): Observable<UserResponse> {

    return this.http.post<UserResponse>(
      `${this.apiUrl}/${userId}/activate`,
      {},
      {
        withCredentials: true
      }
    );
  }
}