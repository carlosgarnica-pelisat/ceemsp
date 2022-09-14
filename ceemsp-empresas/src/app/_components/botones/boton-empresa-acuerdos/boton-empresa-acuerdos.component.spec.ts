import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BotonEmpresaAcuerdosComponent } from './boton-empresa-acuerdos.component';

describe('BotonEmpresaAcuerdosComponent', () => {
  let component: BotonEmpresaAcuerdosComponent;
  let fixture: ComponentFixture<BotonEmpresaAcuerdosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BotonEmpresaAcuerdosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BotonEmpresaAcuerdosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
