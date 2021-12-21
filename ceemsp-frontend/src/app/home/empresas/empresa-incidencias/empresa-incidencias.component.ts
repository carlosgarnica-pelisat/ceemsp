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
  relevancia: boolean = false;
  clienteInvolucrado: boolean = false;

  crearIncidenciaForm: FormGroup;

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

  editorData: string = "<p>Favor de escribir con detalle el relato de la incidencia</p>"

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearIncidenciaForm = this.formBuilder.group({
      'fechaIncidencia': ['', Validators.required],
      'relevancia': ['', Validators.required],
      'clienteInvolucrado': [''],
      'cliente': [''],
      'relatoDeHechos': ['']
    });

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

  cambiarRelevancia(target) {
    this.relevancia = target.value === 'true';
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

  agregarPersona() {

  }

  agregarVehiculo() {

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
