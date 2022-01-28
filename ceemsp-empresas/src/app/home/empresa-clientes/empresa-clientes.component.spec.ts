import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpresaClientesComponent } from './empresa-clientes.component';

describe('EmpresaClientesComponent', () => {
  let component: EmpresaClientesComponent;
  let fixture: ComponentFixture<EmpresaClientesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmpresaClientesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaClientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
