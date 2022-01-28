import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteoComponent } from './reporteo.component';

describe('ReporteoComponent', () => {
  let component: ReporteoComponent;
  let fixture: ComponentFixture<ReporteoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReporteoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
