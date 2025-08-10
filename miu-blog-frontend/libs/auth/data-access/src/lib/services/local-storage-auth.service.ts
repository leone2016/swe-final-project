import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { User } from '@realworld/core/api-types';

@Injectable({ providedIn: 'root' })
export class LocalStorageAuthService {
  getItem(): Observable<string | null> {
    const data = localStorage.getItem('jwtToken');
    if (data) {
      return of(data);
    }
    return of(null);
  }

  setItem(data: string): Observable<string> {
    localStorage.setItem('jwtToken', data);
    return of(data);
  }

  removeItem(): Observable<boolean> {
    localStorage.removeItem('jwtToken');
    return of(true);
  }

  setUserActive(data: User): Observable<User> {
    localStorage.setItem('user', JSON.stringify(data));
    return of(data);
  }

  getUserActive(): Observable<string | null> {
    const data = localStorage.getItem('jwtToken');
    if (data) {
      return of(data);
    }
    return of(null);
  }
}
