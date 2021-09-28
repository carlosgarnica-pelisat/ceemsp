import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComunicadosGeneralesComponent } from './comunicados-generales.component';

describe('ComunicadosGeneralesComponent', () => {
  let component: ComunicadosGeneralesComponent;
  let fixture: ComponentFixture<ComunicadosGeneralesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ComunicadosGeneralesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ComunicadosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
