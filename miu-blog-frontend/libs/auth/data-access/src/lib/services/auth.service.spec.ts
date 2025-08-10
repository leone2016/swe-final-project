import { ApiService } from '@realworld/core/http-client';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { LocalStorageAuthService } from './local-storage-auth.service';
import { MockProvider } from 'ng-mocks';

describe('AuthService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService, ApiService, LocalStorageAuthService, MockProvider(ApiService)],
    });
  });

  it('should be created', inject([AuthService], (service: AuthService) => {
    expect(service).toBeTruthy();
  }));
});
