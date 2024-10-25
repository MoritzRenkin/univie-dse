import { TestBed } from '@angular/core/testing';

import { HubsService } from './hubs.service';

describe('HubsService', () => {
  let service: HubsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HubsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
