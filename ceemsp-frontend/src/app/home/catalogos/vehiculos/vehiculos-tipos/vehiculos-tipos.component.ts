import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {VehiculosService} from "../../../../_services/vehiculos.service";
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import VehiculoTipo from "../../../../_models/VehiculoTipo";
import Uniforme from "../../../../_models/Uniforme";

@Component({
  selector: 'app-vehiculos-tipos',
  templateUrl: './vehiculos-tipos.component.html',
  styleUrls: ['./vehiculos-tipos.component.css']
})
export class VehiculosTiposComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData = [];

  vehiculoTipo: VehiculoTipo;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearVehiculoTipoForm: FormGroup;

  @ViewChild("mostrarVehiculoTipoModal") mostrarVehiculoTipoModal;
  @ViewChild("editarVehiculoTipoModal") editarVehiculoTipoModal;
  @ViewChild("eliminarVehiculoTipoModal") eliminarVehiculoTipoModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private vehiculoService: VehiculosService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.crearVehiculoTipoForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    });

    this.vehiculoService.obtenerVehiculosTipos().subscribe((data: VehiculoTipo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los tipos de los vehiculos. ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    this.vehiculoTipo = this.rowData.filter(x => x.uuid === data.uuid)[0]
    this.modal = this.modalService.open(this.mostrarVehiculoTipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  guardarTipoVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que necesitan llenarse",
        ToastType.WARNING
      )
      return;
    }

    let formData: VehiculoTipo = form.value;

    this.vehiculoService.guardarVehiculoTipo(formData).subscribe((data: VehiculoTipo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el tipo del vehiculo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el tipo del vehiculo. ${error}`,
        ToastType.ERROR
      )
    })
  }

  guardarCambios(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que no se han validado",
        ToastType.WARNING
      )
      return;
    }

    let vehiculoTipo: VehiculoTipo = form.value;

    this.vehiculoService.modificarVehiculoTipo(this.vehiculoTipo.uuid, vehiculoTipo).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito el tipo de vehiculo",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el tipo de vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }


  confirmarEliminar() {
    this.vehiculoService.borrarVehiculoTipo(this.vehiculoTipo.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el tipo de vehiculo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el tipo de vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalCrear(modal) {
    this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModificarVehiculoTipoModal() {
    this.crearVehiculoTipoForm.patchValue({
      nombre: this.vehiculoTipo.nombre,
      descripcion: this.vehiculoTipo.descripcion
    });

    this.modalService.open(this.editarVehiculoTipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarVehiculoTipoModal() {
    this.modal = this.modalService.open(this.eliminarVehiculoTipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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
