import {
  HttpInterceptorFn
} from '@angular/common/http';


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


    // Login CSRF kontrolünden muaf.
    if (
      request.url.endsWith(
        '/api/auth/login'
      )
    ) {

      return next(
        request
      );
    }


    const csrfToken =
      getCookie(
        'XSRF-TOKEN'
      );

    if (!csrfToken) {

      return next(
        request
      );
    }


    const csrfRequest =
      request.clone({

        setHeaders: {
          'X-XSRF-TOKEN':
            decodeURIComponent(
              csrfToken
            )
        },

        withCredentials: true
      });


    return next(
      csrfRequest
    );
  };


function getCookie(
  name: string
): string | null {

  const cookiePrefix =
    `${name}=`;

  const cookies =
    document.cookie.split(
      ';'
    );


  for (
    const cookie of cookies
  ) {

    const trimmedCookie =
      cookie.trim();

    if (
      trimmedCookie.startsWith(
        cookiePrefix
      )
    ) {

      return trimmedCookie.substring(
        cookiePrefix.length
      );
    }
  }


  return null;
}