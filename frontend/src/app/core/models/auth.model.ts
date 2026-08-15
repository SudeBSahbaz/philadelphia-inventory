export type UserRole =
  | 'ADMIN'
  | 'CREW_MEMBER'
  | 'LOOKUP_USER';


export interface LoginRequest {
  email: string;
  password: string;
  rememberMe: boolean;
}


export interface LoginResponse {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  mustChangePassword: boolean;
}


export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  newPasswordConfirm: string;
}


// --------------------------------------------------
// ŞİFRE SIFIRLAMA
// --------------------------------------------------

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  newPasswordConfirm: string;
}


export interface MessageResponse {
  message: string;
}


export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}