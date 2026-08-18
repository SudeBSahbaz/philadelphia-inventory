import {
  HttpInterceptorFn
} from '@angular/common/http';

import {
  inject
} from '@angular/core';

import {
  switchMap
} from 'rxjs';

import {
  CsrfService
} from '../services/csrf.service';


export const csrfInterceptor:
  HttpInterceptorFn = (
    request,
    next
  ) => {

    const unsafeMethods = [
      'POST',
      'PUT',
      'PATCH',
      'DELETE'
    ];


    if (
      !unsafeMethods.includes(
        request.method
      )
    ) {

      return next(
        request
      );
    }


    // CSRF kontrolünden muaf endpointler
    if (
      request.url.endsWith(
        '/api/auth/login'
      ) ||
      request.url.endsWith(
        '/api/auth/forgot-password'
      ) ||
      request.url.endsWith(
        '/api/auth/reset-password'
      )
    ) {

      return next(
        request
      );
    }


    const csrfService =
      inject(CsrfService);


    const existingToken =
      csrfService.getToken();


    if (existingToken) {

      return next(
        request.clone({

          setHeaders: {
            'X-XSRF-TOKEN':
              existingToken
          },

          withCredentials: true
        })
      );
    }


    // Token yoksa backend'den al,
    // sonra asıl isteği gönder.
    return csrfService
      .loadToken()
      .pipe(

        switchMap(
          response =>

            next(
              request.clone({

                setHeaders: {
                  'X-XSRF-TOKEN':
                    response.token
                },

                withCredentials: true
              })
            )
        )
      );
  };