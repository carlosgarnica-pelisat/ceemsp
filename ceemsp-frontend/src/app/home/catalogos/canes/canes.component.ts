import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CanesService} from "../../../_services/canes.service";
import {ToastService} from "../../../_services/toast.service";
import CanRaza from "../../../_models/CanRaza";
import {ToastType} from "../../../_enums/ToastType";

@Component({
  selector: 'app-canes',
  templateUrl: './canes.component.html',
  styleUrls: ['./canes.component.css']
})
export class CanesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData = [];

  canRaza: CanRaza;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearCanRazaForm: FormGroup;

  @ViewChild("mostrarCanRazaModal") mostrarCanRazaModal;
  @ViewChild("editarCanRazaModal") editarCanRazaModal;
  @ViewChild("eliminarCanRazaModal") eliminarCanRazaModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private canesService: CanesService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.canesService.getAllRazas().subscribe((response: CanRaza[]) => {
      this.rowData = response;
    }, (error => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las ordenes. ${error}`,
        ToastType.ERROR
      )
    }))

    this.crearCanRazaForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    let canUuid = data.uuid;
    this.canRaza = this.rowData.filter(x => x.uuid === canUuid)[0];
    this.modal = this.modalService.open(this.mostrarCanRazaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.uuid = data.uuid;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminar() {
    this.canesService.deleteRazaByUuid(this.canRaza.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la raza del can con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la raza del can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarCanRaza(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que no se han validado",
        ToastType.WARNING
      )
      return;
    }

    let canRaza: CanRaza = form.value;

    this.canesService.saveRaza(canRaza).subscribe((data: CanRaza) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha creado con exito la raza",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la raza. ${error}`,
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

    let canRaza: CanRaza = form.value;

    this.canesService.modificarRaza(this.canRaza.uuid, canRaza).subscribe((data: CanRaza) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito la raza",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la raza. ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarModificarCanRazaModal() {
    this.crearCanRazaForm.patchValue({
      nombre: this.canRaza.nombre,
      descripcion: this.canRaza.descripcion
    });

    this.modalService.open(this.editarCanRazaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarCanRazaModal() {
    this.modal = this.modalService.open(this.eliminarCanRazaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

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
