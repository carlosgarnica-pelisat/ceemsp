import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CanesService} from "../../../../_services/canes.service";
import {ToastService} from "../../../../_services/toast.service";
import TipoEntrenamiento from "../../../../_models/TipoEntrenamiento";
import {ToastType} from "../../../../_enums/ToastType";

@Component({
  selector: 'app-canes-entrenamientos',
  templateUrl: './canes-entrenamientos.component.html',
  styleUrls: ['./canes-entrenamientos.component.css']
})
export class CanesEntrenamientosComponent implements OnInit {

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

  crearTipoAdiestramientoForm: FormGroup;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private canesService: CanesService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.canesService.getAllEntrenamientos().subscribe((response: TipoEntrenamiento[]) => {
      this.rowData = response
    }, (error => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los entrenamientos. ${error}`,
        ToastType.ERROR
      )
    }))

    this.crearTipoAdiestramientoForm = this.formBuilder.group({
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

  guardarCanEntrenamiento(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    let canEntrenamiento: TipoEntrenamiento = new TipoEntrenamiento();
    canEntrenamiento.nombre = value.nombre;
    canEntrenamiento.descripcion = value.descripcion;

    this.canesService.saveEntrenamiento(canEntrenamiento).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "El entrenamiento se ha guardado con exito",
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El entrenamiento no se ha podido guardar. Motivo: ${error}`,
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
