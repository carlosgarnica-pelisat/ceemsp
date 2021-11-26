import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import VehiculoMarca from "../../../_models/VehiculoMarca";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import VehiculoSubmarca from "../../../_models/VehiculoSubmarca";
import {VehiculosService} from "../../../_services/vehiculos.service";
import {ToastType} from "../../../_enums/ToastType";
import VehiculoTipo from "../../../_models/VehiculoTipo";
import {ActivatedRoute} from "@angular/router";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import VehiculoUso from "../../../_models/VehiculoUso";
import Stepper from "bs-stepper";

@Component({
  selector: 'app-empresa-vehiculos',
  templateUrl: './empresa-vehiculos.component.html',
  styleUrls: ['./empresa-vehiculos.component.css']
})
export class EmpresaVehiculosComponent implements OnInit {

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

  crearVehiculoMarcaForm: FormGroup;
  crearVehiculoSubmarcaForm: FormGroup;

  crearVehiculoForm: FormGroup;
  crearColorForm: FormGroup;

  marca: VehiculoMarca;
  marcas: VehiculoMarca[];
  submarcas: VehiculoSubmarca[];
  tipos: VehiculoTipo[];
  domicilios: EmpresaDomicilio[] = [];
  usos: VehiculoUso[] = [];

  blindado: boolean = false;
  origen: string = "";

  stepper: Stepper;

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private empresaService: EmpresaService, private formBuilder: FormBuilder,
              private vehiculosService: VehiculosService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearVehiculoForm = this.formBuilder.group({
      placas: ['', Validators.required],
      serie: ['', Validators.required],
      tipo: ['', Validators.required],
      marca: ['', Validators.required],
      submarca: ['', Validators.required],
      anio: ['', Validators.required],
      rotulado: ['', Validators.required],
      uso: ['', Validators.required],
      domicilio: ['', Validators.required],
      origen: ['', Validators.required],
      blindado: ['', Validators.required],
      serieBlindaje: [''],
      fechaBlindaje: [''],
      numeroHolograma: [''],
      placaMetalica: [''],
      empresaBlindaje: [''],
      nivelBlindaje: [''],
      razonSocial: [''],
      fechaInicio: [''],
      fechaFin: ['']
      // TODO: Agregar campos para fotos y documentos; asi como constancia de blindaje
    })

    this.crearColorForm = this.formBuilder.group({
      color: ['', Validators.required],
      descripcion: ['', Validators.required]
    })
  }

  modify() {

  }

  delete() {

  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  seleccionarBlindado(event) {
    this.blindado = event.value;
  }

  seleccionarOrigen(event) {
    this.origen = event.value;
  }

  seleccionarMarca(event) {
    let marcaUuid = event.value;

    this.vehiculosService.obtenerVehiculoMarcaPorUuid(marcaUuid).subscribe((data: VehiculoMarca) => {
      this.marca = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la marca. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
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
    });

    // Obteniendo la informacion
    this.vehiculosService.obtenerVehiculosMarcas().subscribe((data: VehiculoMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de las marcas. ${error}`,
        ToastType.ERROR
      );
    })

    // Obteniendo el resto de informacion
    this.vehiculosService.obtenerVehiculosUsos().subscribe((data: VehiculoUso[]) => {
      this.usos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de los usos de vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.vehiculosService.obtenerVehiculosTipos().subscribe((data: VehiculoTipo[]) => {
      this.tipos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de los tipos de vehiculo. ${error}`,
        ToastType.ERROR
      );
    });

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


  guardarVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que necesitan ser rellenados",
        ToastType.WARNING
      );
      return;
    }
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
