import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, of, switchMap, tap } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { LocalStorageAuthService } from '../services/local-storage-auth.service';
import { authActions } from './auth.actions';
import { formsActions, ngrxFormsQuery } from '@realworld/core/forms';
import { Store } from '@ngrx/store';

export const logout$ = createEffect(
  (actions$ = inject(Actions), localStorageJwtService = inject(LocalStorageAuthService), router = inject(Router)) => {
    return actions$.pipe(
      ofType(authActions.logout),
      tap(() => {
        localStorageJwtService.removeItem();
        router.navigateByUrl('login');
      }),
    );
  },
  { functional: true, dispatch: false },
);

export const getUser$ = createEffect(
  (actions$ = inject(Actions), authService = inject(AuthService)) => {
    return actions$.pipe(
      ofType(authActions.getUser),
      switchMap(() =>
        authService.user().pipe(
          // @ts-ignore
          map((data) => authActions.getUserSuccess({ user: data })),
          catchError((error) => of(authActions.getUserFailure({ error }))),
        ),
      ),
    );
  },
  { functional: true },
);

export const login$ = createEffect(
  (actions$ = inject(Actions), authService = inject(AuthService), store = inject(Store)) => {
    return actions$.pipe(
      ofType(authActions.login),
      concatLatestFrom(() => store.select(ngrxFormsQuery.selectData)),
      exhaustMap(([, data]) =>
        authService.login(data).pipe(
          map((response) => authActions.loginSuccess({ user: response.user })),
          catchError((result) => of(formsActions.setErrors({ errors: result.error.errors }))),
        ),
      ),
    );
  },
  { functional: true },
);

export const loginOrRegisterSuccess$ = createEffect(
  (actions$ = inject(Actions), localStorageJwtService = inject(LocalStorageAuthService), router = inject(Router)) => {
    return actions$.pipe(
      ofType(authActions.loginSuccess, authActions.registerSuccess),
      tap((action) => {
        localStorageJwtService.setItem(action.user.token);
        localStorageJwtService.setUserActive(action.user);
        router.navigateByUrl('/');
      }),
    );
  },
  { functional: true, dispatch: false },
);

export const register$ = createEffect(
  (actions$ = inject(Actions), authService = inject(AuthService), store = inject(Store)) => {
    return actions$.pipe(
      ofType(authActions.register),
      concatLatestFrom(() => store.select(ngrxFormsQuery.selectData)),
      exhaustMap(([, data]) =>
        authService.register(data).pipe(
          map((response) => authActions.registerSuccess({ user: response.user })),
          catchError((result) => of(formsActions.setErrors({ errors: result.error.errors }))),
        ),
      ),
    );
  },
  { functional: true },
);
