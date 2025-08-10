import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LocalStorageAuthService } from './local-storage-auth.service';

export const tokenInterceptor = (request: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> => {
  let token: string | null = null;
  inject(LocalStorageAuthService)
    .getItem()
    .subscribe((t) => (token = t));

  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Token ${token}`,
      },
    });
  }
  return next(request);
};
