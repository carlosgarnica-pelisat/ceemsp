import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ArmasService} from "../../../../_services/armas.service";
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import ArmaClase from "../../../../_models/ArmaClase";
import PersonalNacionalidad from "../../../../_models/PersonalNacionalidad";
import Uniforme from "../../../../_models/Uniforme";

@Component({
  selector: 'app-armas-clases',
  templateUrl: './armas-clases.component.html',
  styleUrls: ['./armas-clases.component.css']
})
export class ArmasClasesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData = [];

  armaClase: ArmaClase;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearArmaClaseForm: FormGroup;

  @ViewChild("mostrarArmaClaseDetallesModal") mostrarArmaClaseDetallesModal;
  @ViewChild("editarArmaClaseModal") editarArmaClaseModal;
  @ViewChild("eliminarArmaClaseModal") eliminarArmaClaseModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private armaService: ArmasService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.armaService.obtenerArmaClases().subscribe((data: ArmaClase[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion. ${error}`,
        ToastType.ERROR
      )
    });
    this.crearArmaClaseForm = this.formBuilder.group({
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
    this.modal = this.modalService.open(this.mostrarArmaClaseDetallesModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.armaClase = this.rowData.filter(x => x.uuid === data.uuid)[0]

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarArmaClase(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    let armaClase: ArmaClase = new ArmaClase();
    armaClase.nombre = value.nombre;
    armaClase.descripcion = value.descripcion;

    this.armaService.guardarArmaClase(armaClase).subscribe((response) => {
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

  guardarCambios(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que no se han validado",
        ToastType.WARNING
      )
      return;
    }

    let nacionalidad: PersonalNacionalidad = form.value;

    this.armaService.modificarArmaClase(this.armaClase.uuid, nacionalidad).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito la clase del arma",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la clase del arma. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  confirmarEliminar() {
    this.armaService.borrarArmaClase(this.armaClase.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la clase del arma con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la clase del arma. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModificarArmaClaseModal() {
    this.crearArmaClaseForm.patchValue({
      nombre: this.armaClase.nombre,
      descripcion: this.armaClase.descripcion
    });

    this.modalService.open(this.editarArmaClaseModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarArmaClaseModal() {
    this.modal = this.modalService.open(this.eliminarArmaClaseModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

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
