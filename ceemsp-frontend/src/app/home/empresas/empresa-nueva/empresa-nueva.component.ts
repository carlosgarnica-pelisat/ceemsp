import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Stepper from "bs-stepper";
import Modalidad from "../../../_models/Modalidad";
import {ModalidadesService} from "../../../_services/modalidades.service";
import {ToastService} from "../../../_services/toast.service";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Empresa from "../../../_models/Empresa";
import {EmpresaService} from "../../../_services/empresa.service";
import {PublicService} from "../../../_services/public.service";
import ProximoRegistro from "../../../_models/ProximoRegistro";
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";

@Component({
  selector: 'app-empresa-nueva',
  templateUrl: './empresa-nueva.component.html',
  styleUrls: ['./empresa-nueva.component.css']
})
export class EmpresaNuevaComponent implements OnInit {

  empresaCreacionForm: FormGroup;
  empresaModalidadForm: FormGroup;
  empresaDomiciliosForm: FormGroup;
  empresaLegalForm: FormGroup;

  stepper: Stepper;

  // binding data
  tipoPersona: string;
  tipoTranite: string;
  year = new Date().getFullYear();

  modalidad: Modalidad;
  modalidades: Modalidad[] = [];
  empresaModalidades: EmpresaModalidad[] = [];

  empresa: Empresa;
  empresaDomicilios: EmpresaDomicilio[];
  empresaEscrituras: EmpresaEscritura[];
  empresaEscritura: EmpresaEscritura;

  faTrash = faTrash;

  constructor(private formBuilder: FormBuilder, private modalidadService: ModalidadesService,
              private toastService: ToastService, private empresaService: EmpresaService,
              private publicService: PublicService) { }

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

    this.empresaModalidadForm = this.formBuilder.group({
      modalidad: ['', Validators.required],
      submodalidad: [''],
      numeroRegistroFederal: [''],
      fechaInicio: [''],
      fechaFin: ['']
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
    });

    this.empresaLegalForm = this.formBuilder.group({

    })

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    })
  }

  next(stepName: string, form) {

    if(form !== undefined && !form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }

    switch (stepName) {
      case 'DOMICILIOS':
        let formData = form.value;

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando la empresa en la base de datos",
          ToastType.INFO
        );

        let empresa = new Empresa();
        empresa.tipoTramite = formData.tipoTramite;
        empresa.rfc = formData.rfc;
        empresa.nombreComercial = formData.nombreComercial;
        empresa.razonSocial = formData.razonSocial;
        empresa.tipoPersona = formData.tipoPersona;
        empresa.correoElectronico = formData.correoElectronico;
        empresa.registro = `CESP/${this.tipoTranite}/${formData.registro}/${this.year}`;
        if(empresa.tipoPersona === 'MORAL') {
          empresa.sexo = 'NA';
        } else {
          empresa.sexo = formData.sexo;
        }

        empresa.curp = formData.curp;

        empresa.modalidades = this.empresaModalidades;

        this.empresaService.guardarEmpresa(empresa).subscribe((data: Empresa) => {
          this.empresa = data;
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado la empresa con exito",
            ToastType.SUCCESS
          );
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar la empresa. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
        break;
      case 'LEGAL':
        if(this.empresaDomicilios.length < 1) {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            "No hay domicilios creados aun para la empresa.",
            ToastType.WARNING
          );
          return;
        }

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando los domicilios",
          ToastType.INFO
        );

        let allAddressesStored: boolean = true;

        this.empresaDomicilios.forEach(ed => {
          this.empresaService.guardarDomicilio(this.empresa.uuid, ed).subscribe((data) => {
            this.toastService.showGenericToast(
              "Listo",
              `Se ha guardado el domicilio ${ed.domicilio1} con exito`,
              ToastType.SUCCESS
            );
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar el domicilio ${ed.domicilio1}. Motivo: ${error}`,
              ToastType.ERROR
            );
            allAddressesStored = false;
          });
        })

        if(allAddressesStored) {
          this.stepper.next();
        }
        break;
      case 'FORMAS_EJECUCION':
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Este paso no ha sido implementado aun. Que intentas hacer",
          ToastType.ERROR
        )
    }
  }

  previous() {
    //this.stepper.previous()
  }

  // Funciones para la primera pagina (informacion de la empresa)
  cambiarTipoTramite(event) {
    this.tipoTranite = event.value;
    this.publicService.obtenerSiguienteNumero({tipo: this.tipoTranite}).subscribe((data: ProximoRegistro) => {
      this.empresaCreacionForm.patchValue({
        registro: data.numeroSiguiente
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se ha podido descargar el siguiente numero",
        ToastType.ERROR
      );
    })

    this.modalidadService.obtenerModalidadesPorFiltro("TIPO", this.tipoTranite).subscribe((data: Modalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener los tipos de tramite. ${error}`,
        ToastType.ERROR
      )
    })
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
  }

  eliminarModalidad(index) {
    this.empresaModalidades.splice(index, 1);
  }

  eliminarDomicilio(index) {
    this.empresaDomicilios.splice(index, 1);
  }

  agregarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que estan vacios",
        ToastType.ERROR
      );
      return;
    }

    let formData: EmpresaDomicilio = form.value;
    this.empresaDomicilios.push(formData);
    form.reset();
  }

  agregarModalidad(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que estan vacios",
        ToastType.WARNING
      )
      return;
    }

    let formData = form.value;

    let empresaModalidad = new EmpresaModalidad();
    empresaModalidad.modalidad = this.modalidad;
    if(this.modalidad.tieneSubmodalidades) {
      empresaModalidad.submodalidad = this.modalidad.submodalidades.filter((x => x.uuid === formData.submodalidad))[0];
    }

    empresaModalidad.fechaInicio = formData.fechaInicio;
    empresaModalidad.fechaFin = formData.fechaFin;
    empresaModalidad.numeroRegistroFederal = formData.numeroRegistroFederal;

    this.empresaModalidades.push(empresaModalidad);
    form.reset();
    this.modalidad = undefined;
  }

  seleccionarModalidad(event) {
    let existeModalidad = this.empresaModalidades.filter(m => m.modalidad.uuid === event.value)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Esta modalidad ya se encuentra registrada en la empresa. Favor de seleccionar otra",
        ToastType.WARNING
      );
      this.modalidad = undefined;
      return;
    }

    this.modalidad = this.modalidades.filter(m => m.uuid === event.value)[0];
    if(this.modalidad.submodalidades.length > 0) {
      this.modalidad.tieneSubmodalidades = true;
    }
  }

}
