import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import Persona from "../../../_models/Persona";
import Vehiculo from "../../../_models/Vehiculo";
import Can from "../../../_models/Can";
import Arma from "../../../_models/Arma";
import {EmpresaService} from "../../../_services/empresa.service";
import Cliente from "../../../_models/Cliente";
import {ToastType} from "../../../_enums/ToastType";
import Incidencia from "../../../_models/Incidencia";

@Component({
  selector: 'app-empresa-incidencias',
  templateUrl: './empresa-incidencias.component.html',
  styleUrls: ['./empresa-incidencias.component.css']
})
export class EmpresaIncidenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };
  clienteInvolucrado: boolean = false;

  crearIncidenciaForm: FormGroup;
  crearPersonalIncidenciaForm: FormGroup;
  crearVehiculoIncidenciaForm: FormGroup;
  crearCanIncidenciaForm: FormGroup;
  crearArmaIncidenciaForm: FormGroup;

  personalInvolucrado: Persona[] = [];
  vehiculosInvolucrados: Vehiculo[] = [];
  canesInvolucrados: Can[] = [];
  armasInvolucradas: Arma[] = [];

  clientes: Cliente[] = [];
  armas: Arma[] = [];
  canes: Can[] = [];
  vehiculos: Vehiculo[] = [];
  personales: Persona[] = [];

  mostrarAgregarCanForm: boolean = false;
  mostrarAgregarVehiculoForm: boolean = false;
  mostrarAgregarArmaForm: boolean = false;
  mostrarAgregarPersonalForm: boolean = false;

  pestanaActualInvolucramiento: string = 'PERSONAL';

  editorData: string = "<p>Favor de escribir con detalle el relato de la incidencia</p>"

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearIncidenciaForm = this.formBuilder.group({
      'fechaIncidencia': ['', Validators.required],
      'clienteInvolucrado': ['', Validators.required],
      'cliente': [''],
      'relatoDeHechos': ['']
    });

    this.crearPersonalIncidenciaForm = this.formBuilder.group({
      'personaInvolucrada': ['', Validators.required]
    });

    this.crearArmaIncidenciaForm = this.formBuilder.group({
      'armaInvolucrada': ['', Validators.required]
    });

    this.crearVehiculoIncidenciaForm = this.formBuilder.group({
      'vehiculoInvolucrado': ['', Validators.required]
    });

    this.crearCanIncidenciaForm = this.formBuilder.group({
      'canInvolucrado': ['', Validators.required]
    })

    this.empresaService.obtenerClientes(this.uuid).subscribe((data: Cliente[]) => {
      this.clientes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerCanes(this.uuid).subscribe((data: Can[]) => {
      this.canes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los canes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerVehiculos(this.uuid).subscribe((data: Vehiculo[]) => {
      this.vehiculos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personales = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los personales. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarInvolucramientoCliente(target) {
    this.clienteInvolucrado = target.value === 'true';
  }

  modify() {

  }

  delete() {

  }

  conmutarAgregarPersonalForm() {
    this.mostrarAgregarPersonalForm = !this.mostrarAgregarPersonalForm;
  }

  conmutarAgregarArmaForm() {
    this.mostrarAgregarArmaForm = !this.mostrarAgregarArmaForm;
  }

  conmutarAgregarCanForm() {
    this.mostrarAgregarCanForm = !this.mostrarAgregarCanForm;
  }

  conmutarAgregarVehiculoForm() {
    this.mostrarAgregarVehiculoForm = !this.mostrarAgregarVehiculoForm;
  }

  agregarArma() {

  }

  agregarCan() {

  }

  agregarPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    this.personalInvolucrado.push(this.personales.filter(x => x.uuid === formValue.personaInvolucrada)[0]);
    //this.personales.s
    this.conmutarAgregarPersonalForm();
  }

  agregarVehiculo() {

  }

  cambiarPestanaInvolucramientos(pestana) {
    this.pestanaActualInvolucramiento = pestana;
  }

  guardarIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos requeridos que no han sido rellenados`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos registrando la incidencia",
      ToastType.INFO
    );

    let formValue: Incidencia = form.value;
    formValue.armasInvolucradas = this.armasInvolucradas;
    formValue.vehiculosInvolucrados = this.vehiculosInvolucrados;
    formValue.personasInvolucradas = this.personalInvolucrado;
    formValue.canesInvolucrados = this.canesInvolucrados;

    this.empresaService.guardarIncidencia(this.uuid, formValue).subscribe((data: Incidencia) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la incidencia con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la incidencia en la base de datos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

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
