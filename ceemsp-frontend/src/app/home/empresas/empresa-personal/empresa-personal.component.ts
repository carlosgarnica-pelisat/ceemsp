import {Component, OnInit, ViewChild} from '@angular/core';
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

@Component({
  selector: 'app-empresa-personal',
  templateUrl: './empresa-personal.component.html',
  styleUrls: ['./empresa-personal.component.css']
})
export class EmpresaPersonalComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  stepper: Stepper;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        modify: this.modify.bind(this),
        delete: this.delete.bind(this)
      }}
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

  crearPersonalForm: FormGroup;
  crearPersonalPuestoForm: FormGroup;

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
      'estatusCuip': ['', Validators.required],
      'cuip': [''],
      'numeroVolanteCuip': [''],
      'fechaVolanteCuip': [''],
      'modalidad': ['']
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
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

    let formData = form.value;
    this.stepper.next();
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

    this.personalService.obtenerPuestosPersonal().subscribe((data: PersonalPuestoTrabajo[]) => {
      this.puestosTrabajo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los puestos de trabajo. Motivo: ${error}`,
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
    })

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

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
