import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComunicadoGeneralNuevoComponent } from './comunicado-general-nuevo.component';

describe('ComunicadoGeneralNuevoComponent', () => {
  let component: ComunicadoGeneralNuevoComponent;
  let fixture: ComponentFixture<ComunicadoGeneralNuevoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ComunicadoGeneralNuevoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ComunicadoGeneralNuevoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
