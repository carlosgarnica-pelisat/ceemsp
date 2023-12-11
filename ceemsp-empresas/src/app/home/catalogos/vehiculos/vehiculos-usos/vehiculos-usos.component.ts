import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../../../_enums/ToastType";
import {VehiculosService} from "../../../../_services/vehiculos.service";
import {ToastService} from "../../../../_services/toast.service";
import VehiculoUso from "../../../../_models/VehiculoUso";

@Component({
  selector: 'app-vehiculos-usos',
  templateUrl: './vehiculos-usos.component.html',
  styleUrls: ['./vehiculos-usos.component.css']
})
export class VehiculosUsosComponent implements OnInit {

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

  crearVehiculoUsoForm: FormGroup;

  vehiculoUso: VehiculoUso;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private vehiculoService: VehiculosService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.crearVehiculoUsoForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: ['']
    })

    this.vehiculoService.obtenerVehiculosUsos().subscribe((data: VehiculoUso[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los usos del vehiculo. ${error}`,
        ToastType.ERROR
      );
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

    this.vehiculoService.obtenerVehiculoUsoPorUuid(this.uuid).subscribe((data: VehiculoUso) => {
      this.vehiculoUso = data;
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La informacion de la marca del vehiculo no se descargo. Motivo: ${error}`,
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

  guardarUsoVehiculo(form) {

    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value: VehiculoUso = form.value;
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el uso del vehiculo",
      ToastType.INFO
    );

    this.vehiculoService.guardarVehiculoUso(value).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "El uso del vehiculo se ha guardado con exito",
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El uso del vehiculo no se ha podido guardar. ${error}`,
        ToastType.ERROR
      )
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

  changeToSubmarcas() {

  }

}
