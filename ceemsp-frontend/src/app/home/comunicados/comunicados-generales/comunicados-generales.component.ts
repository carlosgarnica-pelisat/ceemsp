import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import ComunicadoGeneral from "../../../_models/ComunicadoGeneral";
import {ToastType} from "../../../_enums/ToastType";
import {ComunicadosGeneralesService} from "../../../_services/comunicados-generales.service";
import {ToastService} from "../../../_services/toast.service";
import {SanitizeHtmlPipe} from "../../../_pipes/sanitize-html.pipe";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-comunicados-generales',
  templateUrl: './comunicados-generales.component.html',
  styleUrls: ['./comunicados-generales.component.css']
})
export class ComunicadosGeneralesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Titulo', field: 'titulo', sortable: true, filter: true },
    {headerName: 'Fecha de publicacion', field: 'fechaPublicacion', sortable: true, filter: true}
  ];
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };
  fechaDeHoy = new Date().toISOString().split('T')[0];

  nuevoComunicadoForm: FormGroup;
  comunicadoGeneral: ComunicadoGeneral;
  closeResult: string;

  model = {
    editorData: '<p>Escribe con detalle el comunicado. Puedes utilizar los botones de la parte superior</p>'
  }

  @ViewChild("mostrarComunicadoModal") mostrarComunicadoModal;
  @ViewChild("editarComunicadoModal") editarComunicadoModal;
  @ViewChild("eliminarComunicadoModal") eliminarComunicadoModal;

  constructor(private modalService: NgbModal, private comunicadosGeneralesService: ComunicadosGeneralesService,
              private toastService: ToastService, private sanitizeHtmlPipe: SanitizeHtmlPipe,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.nuevoComunicadoForm = this.formBuilder.group({
      titulo: ['', [Validators.required, Validators.maxLength(100)]],
      fechaPublicacion: ['', Validators.required]
    })

    this.comunicadosGeneralesService.obtenerComunicados().subscribe((data: ComunicadoGeneral[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los comunicados. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    this.comunicadosGeneralesService.obtenerComunicadoPorUuid(data.uuid).subscribe((data: ComunicadoGeneral) => {
      this.comunicadoGeneral = data;
      this.modal = this.modalService.open(this.mostrarComunicadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el comunicado general. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
    //this.modal = this.modalService.open(showCustomerDetailsModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;
  }

  guardarCambios(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido guardados",
        ToastType.WARNING
      );
      return;
    }

    if(this.model.editorData === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El campo del comunicado no tiene contenido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el comunicado",
      ToastType.INFO
    );

    let formData: ComunicadoGeneral = form.value;
    formData.descripcion = this.sanitizeHtmlPipe.transform(this.model.editorData)['changingThisBreaksApplicationSecurity'];

    this.comunicadosGeneralesService.modificarComunicado(this.comunicadoGeneral.uuid, formData).subscribe((data: ComunicadoGeneral) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha actualizado el comunicado con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo guardar el comunicado. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminar() {
    this.comunicadosGeneralesService.eliminarComunicado(this.comunicadoGeneral.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el comunicado con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el comunicado. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModificarComunicadoModal() {
    this.nuevoComunicadoForm.patchValue({
      titulo: this.comunicadoGeneral.titulo,
      fechaPublicacion: this.comunicadoGeneral.fechaPublicacion
    });

    this.model.editorData = this.comunicadoGeneral.descripcion;

    this.modalService.open(this.editarComunicadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarEquipoModal() {
    this.modal = this.modalService.open(this.eliminarComunicadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

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
