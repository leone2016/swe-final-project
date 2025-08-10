import { inject, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';

import { LocalStorageAuthService } from './local-storage-auth.service';
import { TokenInterceptorService } from './token-interceptor.service';

describe('TokenInterceptorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [TokenInterceptorService, LocalStorageAuthService],
    });
  });

  it('should be created', inject([TokenInterceptorService], (service: TokenInterceptorService) => {
    expect(service).toBeTruthy();
  }));
});
