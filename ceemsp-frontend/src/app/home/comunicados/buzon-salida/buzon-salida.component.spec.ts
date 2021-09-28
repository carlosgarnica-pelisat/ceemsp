import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuzonSalidaComponent } from './buzon-salida.component';

describe('BuzonSalidaComponent', () => {
  let component: BuzonSalidaComponent;
  let fixture: ComponentFixture<BuzonSalidaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BuzonSalidaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BuzonSalidaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
