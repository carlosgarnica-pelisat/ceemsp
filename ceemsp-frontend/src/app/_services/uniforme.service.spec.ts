import { TestBed } from '@angular/core/testing';

import { UniformeService } from './uniforme.service';

describe('UniformeService', () => {
  let service: UniformeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UniformeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
