import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {VehiculosService} from "../../../_services/vehiculos.service";
import VehiculoMarca from "../../../_models/VehiculoMarca";
import {ToastType} from "../../../_enums/ToastType";
import ArmaMarca from "../../../_models/ArmaMarca";

@Component({
  selector: 'app-vehiculos',
  templateUrl: './vehiculos.component.html',
  styleUrls: ['./vehiculos.component.css']
})
export class VehiculosComponent implements OnInit {

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
      nombre: ['', Validators.required],
      descripcion: ['']
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    //this.modal = this.modalService.open(showCustomerDetailsModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;
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

    this.vehiculoService.guardarVehiculoMarca(vehiculoMarca).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "La marca del vehiculo se ha guardado con exito",
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La narca del arna no se ha podido guardar. ${error}`,
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

}
