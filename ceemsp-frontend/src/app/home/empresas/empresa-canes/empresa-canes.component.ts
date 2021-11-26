import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import Stepper from "bs-stepper";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import Cliente from "../../../_models/Cliente";
import {ToastType} from "../../../_enums/ToastType";

@Component({
  selector: 'app-empresa-canes',
  templateUrl: './empresa-canes.component.html',
  styleUrls: ['./empresa-canes.component.css']
})
export class EmpresaCanesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  domicilios: EmpresaDomicilio[] = [];
  clientes: Cliente[] = [];

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

  crearEmpresaCanForm: FormGroup;
  crearEmpresaCanCertificadoSaludForm: FormGroup;
  crearEmpresaCanCartillaVacunacionForm: FormGroup;
  crearEmpresaCanEntrenamientoForm: FormGroup;

  stepper: Stepper;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEmpresaCanForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      genero: ['', Validators.required],
      raza: ['', Validators.required],
      razaOtro: [''],
      domicilioAsignado: ['', Validators.required],
      fechaIngreso: ['', Validators.required],
      edad: ['', Validators.required],
      descripcion: ['', Validators.required],
      chip: ['', Validators.required],
      tatuaje: ['', Validators.required],
      origen: ['', Validators.required],
      status: ['', Validators.required],
      elementoAsignado: [''],
      clienteAsignado: [''],
      domicilioClienteAsignado: [''],
      motivos: ['']
    });

    this.crearEmpresaCanCertificadoSaludForm = this.formBuilder.group({
      expedidoPor: ['', Validators.required],
      cedula: ['', Validators.required],
      fechaExpedicion: ['', Validators.required]
    })

    this.crearEmpresaCanCartillaVacunacionForm = this.formBuilder.group({
      expedidoPor: ['', Validators.required],
      cedula: ['', Validators.required],
      fechaExpedicion: ['', Validators.required]
    })

    this.crearEmpresaCanEntrenamientoForm = this.formBuilder.group({
      nombreInstructor: ['', Validators.required],
      tipoAdiestramiento: ['', Validators.required],
      fechaConstancia: ['', Validators.required]
    })

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
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
