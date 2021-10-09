import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Stepper from "bs-stepper";

@Component({
  selector: 'app-empresa-nueva',
  templateUrl: './empresa-nueva.component.html',
  styleUrls: ['./empresa-nueva.component.css']
})
export class EmpresaNuevaComponent implements OnInit {

  empresaCreacionForm: FormGroup;
  empresaDomiciliosForm: FormGroup;
  empresaLegalForm: FormGroup;

  stepper: Stepper;

  // binding data
  tipoPersona: string;
  tipoTranite: string;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.empresaCreacionForm = this.formBuilder.group({
      tipoTramite: ['', Validators.required],
      registro: ['', Validators.required],
      tipoPersona: ['', Validators.required],
      razonSocial: ['', Validators.required],
      nombreComercial: ['', Validators.required],
      rfc: ['', Validators.required],
      curp: [''],
      sexo: [''],
      correoElectronico: ['', Validators.required],
      telefono: ['', Validators.required]
    })

    this.empresaDomiciliosForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      domicilio1: ['', Validators.required],
      domicilio2: ['', Validators.required],
      domicilio3: ['', Validators.required],
      domicilio4: [''],
      codigoPostal: ['', Validators.required],
      estado: ['', Validators.required],
      pais: ['Mexico', Validators.required],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      telefonoFijo: ['', Validators.required],
      telefonoMovil: ['', Validators.required]
      //telefonoMovil: ['', Validators.required]
    });

    this.empresaLegalForm = this.formBuilder.group({

    })

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    })
  }

  next(stepName: string, form) {
    this.stepper.next();
  }

  previous() {
    this.stepper.previous()
  }

  // Funciones para la primera pagina (informacion de la empresa)
  cambiarTipoTramite(event) {
    this.tipoTranite = event.value;
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
  }


}
