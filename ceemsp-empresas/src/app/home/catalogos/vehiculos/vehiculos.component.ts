import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {VehiculosService} from "../../../_services/vehiculos.service";
import VehiculoMarca from "../../../_models/VehiculoMarca";
import {ToastType} from "../../../_enums/ToastType";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-vehiculos',
  templateUrl: './vehiculos.component.html',
  styleUrls: ['./vehiculos.component.css']
})
export class VehiculosComponent implements OnInit {

  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
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

  vehiculoMarca: VehiculoMarca;

  currentTab: string = "DETALLES";

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private vehiculoService: VehiculosService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.vehiculoService.obtenerVehiculosMarcas().subscribe((data: VehiculoMarca[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las marcas de los vehiculos. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearVehiculoMarcaForm = this.formBuilder.group({
      submarcaNombre: ['', Validators.required],
      submarcaDescripcion: ['']
    })

    this.crearVehiculoSubmarcaForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: ['']
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;

    this.vehiculoService.obtenerVehiculoMarcaPorUuid(this.uuid).subscribe((data: VehiculoMarca) => {
      this.vehiculoMarca = data;
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La informacion de la marca del vehiculo no se descargo. Motivo: ",
        ToastType.ERROR
      );
    })
  }

  modify(rowData) {

  }

  delete(rowData) {

  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarMarcaVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    let vehiculoMarca: VehiculoMarca = new VehiculoMarca();
    vehiculoMarca.nombre = value.nombre;
    vehiculoMarca.descripcion = value.descripcion;
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

  cambiarASubmarcas() {
    this.currentTab = "SUBMARCAS";
  }

  cambiarADetalles() {
    this.currentTab = "DETALLES"
  }

}
