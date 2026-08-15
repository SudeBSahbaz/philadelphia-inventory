import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

import {
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi,
  withXsrfConfiguration
} from '@angular/common/http';

import { provideRouter } from '@angular/router';

import { routes } from './app.routes';

import {
  csrfInterceptor
} from './core/interceptors/csrf.interceptor';


export const appConfig:
  ApplicationConfig = {

  providers: [

    provideBrowserGlobalErrorListeners(),

    provideRouter(
      routes
    ),

    provideHttpClient(

      withInterceptorsFromDi(),

      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN'
      }),

      withInterceptors([
        csrfInterceptor
      ])
    )
  ]
};