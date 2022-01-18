import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import PersonalNacionalidad from "../../../_models/PersonalNacionalidad";
import PersonalPuestoTrabajo from "../../../_models/PersonalPuestoTrabajo";
import {PersonalService} from "../../../_services/personal.service";
import {ToastType} from "../../../_enums/ToastType";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Stepper from "bs-stepper";
import Persona from "../../../_models/Persona";
import PersonaCertificacion from "../../../_models/PersonaCertificacion";
import PersonalSubpuestoTrabajo from "../../../_models/PersonalSubpuestoTrabajo";
import Modalidad from "../../../_models/Modalidad";

@Component({
  selector: 'app-empresa-personal',
  templateUrl: './empresa-personal.component.html',
  styleUrls: ['./empresa-personal.component.css']
})
export class EmpresaPersonalComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  stepper: Stepper;
  pestanaActual: string = "DETALLES";

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Apellido paterno', field: 'apellidoPaterno', sortable: true, filter: true },
    {headerName: 'Apellido materno', field: 'apellidoMaterno', sortable: true, filter: true},
    {headerName: 'Nombre(s)', field: 'nombres', sortable: true, filter: true}
  ];
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  nacionalidades: PersonalNacionalidad[] = [];
  puestosTrabajo: PersonalPuestoTrabajo[] = [];
  domicilios: EmpresaDomicilio[] = [];
  modalidades: EmpresaModalidad[] = [];

  puestoTrabajo: PersonalPuestoTrabajo;
  subpuestoTrabajo: PersonalSubpuestoTrabajo;

  crearPersonalForm: FormGroup;
  crearPersonalPuestoForm: FormGroup;
  crearPersonalCertificadoForm: FormGroup;
  crearPersonaFotografiaForm: FormGroup;

  persona: Persona;
  modalidad: Modalidad;
  domicilio: EmpresaDomicilio;
  cuipStatus: string;

  showCertificadoForm: boolean;
  showFotografiaForm: boolean;
  showPuestoForm: boolean;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private personalService: PersonalService, private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearPersonalForm = this.formBuilder.group({
      'curp': [''],
      'nacionalidad': ['', Validators.required],
      'apellidoPaterno': ['', Validators.required],
      'apellidoMaterno': [''],
      'nombres': ['', Validators.required],
      'fechaNacimiento': ['', Validators.required],
      'sexo': ['', Validators.required],
      'tipoSangre': ['', Validators.required],
      'fechaIngreso': ['', Validators.required],
      'estadoCivil': ['', Validators.required],
      'domicilio1': ['', Validators.required],
      'domicilio2': ['', Validators.required],
      'domicilio3': ['', Validators.required],
      'domicilio4': ['', Validators.required],
      'estado': ['', Validators.required],
      'pais': ['Mexico', Validators.required],
      'codigoPostal': ['', Validators.required],
      'telefono': ['', Validators.required],
      'correoElectronico': ['', Validators.required]
    });

    this.crearPersonalPuestoForm = this.formBuilder.group({
      'puesto': ['', Validators.required],
      'subpuesto': ['', Validators.required],
      'detallesPuesto': ['', Validators.required],
      'domicilioAsignado': ['', Validators.required],
      'estatusCuip': [''],
      'cuip': [''],
      'numeroVolanteCuip': [''],
      'fechaVolanteCuip': [''],
      'modalidad': ['']
    });

    this.crearPersonalCertificadoForm = this.formBuilder.group({
      'nombre': ['', Validators.required],
      'nombreInstructor': ['', Validators.required],
      'duracion': ['', Validators.required],
      'fechaInicio': ['', Validators.required],
      'fechaFin': ['', Validators.required]
    })

    this.crearPersonaFotografiaForm = this.formBuilder.group({
      'file': ['', Validators.required]
    })

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar el personal. ${error}`,
        ToastType.ERROR
      );
    });

    this.personalService.obtenerPuestosPersonal().subscribe((data: PersonalPuestoTrabajo[]) => {
      this.puestosTrabajo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los puestos de trabajo`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalDetalles(data, modal) {
    this.empresaService.obtenerPersonalPorUuid(this.uuid, data.uuid).subscribe((data: Persona) => {
      this.persona = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion del personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarPestana(nombrePestana) {
    this.pestanaActual = nombrePestana;
  }

  next(stepName: string, form) {
    /*if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }*/
    switch (stepName) {
      case "INFORMACION":
        let formValue: Persona = form.value;

        formValue.nacionalidad = this.nacionalidades.filter(x => x.uuid === form.value.marca)[0];

        this.empresaService.guardarPersonal(this.uuid, formValue).subscribe((data: Persona) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado la persona con exito",
            ToastType.SUCCESS
          );
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar la persona. ${error}`,
            ToastType.ERROR
          )
        });
        break;
      /*case "DOMICILIOS":

        break;*/
    }
  }

  previous() {
    //this.stepper.previous()
  }


  mostrarModalCrear(modal) {

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    })

    this.personalService.obtenerNacionalidades().subscribe((data: PersonalNacionalidad[]) => {
      this.nacionalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las nacionalidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  modify() {

  }

  delete() {

  }

  cambiarPuestoTrabajo(event) {
    this.puestoTrabajo = this.puestosTrabajo.filter(x => x.uuid === event.value)[0];
  }

  cambiarModalidad(event) {
    console.log(this.modalidades.filter(x => x?.modalidad?.uuid === event.value));
    this.modalidad = this.modalidades.filter(x => x?.modalidad?.uuid === event.value)[0].modalidad;
  }

  cambiarSubpuestoTrabajo(event) {
    this.subpuestoTrabajo = this.puestoTrabajo.subpuestos.filter(x => x.uuid === event.value)[0]
    if(!this.subpuestoTrabajo.portacion || !this.subpuestoTrabajo.cuip) {
      this.cuipStatus = "NA";
    }

    // Con la portacion, la cuip es completamente obligatoria
    if(this.subpuestoTrabajo.portacion) {
      this.cuipStatus = "TRAMITADA";
    }
  }

  cambiarStatusCuip(event) {
    this.cuipStatus = event.value;
  }

  cambiarDomicilio(event) {
    this.domicilio = this.domicilios.filter(x => x.uuid === event.value)[0];
  }

  actualizarPuesto(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    // Validando los tipos
    let value: Persona = form.value;

    value.puestoDeTrabajo = this.puestoTrabajo;
    value.subpuestoDeTrabajo = this.subpuestoTrabajo;
    value.domicilioAsignado = this.domicilio;
    value.modalidad = this.modalidad;

    this.empresaService.modificarInformacionTrabajo(this.uuid, this.persona.uuid, value).subscribe((data: Persona) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la informacion del trabajo con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la informacion del trabajo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

  }

  guardarCertificacion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la certificacion en el personal",
      ToastType.INFO
    );

    let formValue: PersonaCertificacion = form.value;

    this.empresaService.guardarPersonalCertificacion(this.uuid, this.persona.uuid, formValue).subscribe((data: PersonaCertificacion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo la certificacion con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo guardar la certificacion del personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  mostrarFormularioNuevoCertificado() {
    this.showCertificadoForm = !this.showCertificadoForm;
  }

  mostrarFormularioNuevaFotografia() {
    this.showFotografiaForm = !this.showFotografiaForm;
  }

  mostrarFormularioInformacionPuesto() {
    this.showPuestoForm = !this.showPuestoForm;
  }

  onFileChange(event) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.crearPersonaFotografiaForm.patchValue({
        fileSource: file
      });
    }
  }

  guardarFotografia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos pendientes",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la fotografia del elemento",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('foto', form.get('file').value);
    let formValue = form.value;
    console.log(formValue);

    console.log(formData);
    console.log();
  }

  private getDismissReason(reason: any): string {
    if (reason == ModalDismissReasons.ESC) {
      return `by pressing ESC`;
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return `by clicking on a backdrop`;
    } else {
      return `with ${reason}`;
    }
  }

}
