import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { map, take } from 'rxjs';

import { LocalStorageAuthService } from './local-storage-auth.service';

export const authGuard = () => {
  const router = inject(Router);
  const storage = inject(LocalStorageAuthService);

  return storage.getItem().pipe(
    map((token) => {
      if (!token) {
        router.navigate(['/login']);
        return false;
      }
      return true;
    }),
    take(1),
  );
};
