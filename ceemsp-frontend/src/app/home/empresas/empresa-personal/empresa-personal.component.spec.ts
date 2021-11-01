import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpresaPersonalComponent } from './empresa-personal.component';

describe('EmpresaPersonalComponent', () => {
  let component: EmpresaPersonalComponent;
  let fixture: ComponentFixture<EmpresaPersonalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmpresaPersonalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaPersonalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
