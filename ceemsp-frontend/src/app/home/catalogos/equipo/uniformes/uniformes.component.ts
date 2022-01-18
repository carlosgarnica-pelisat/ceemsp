import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import {UniformeService} from "../../../../_services/uniforme.service";
import Uniforme from "../../../../_models/Uniforme";

@Component({
  selector: 'app-uniformes',
  templateUrl: './uniformes.component.html',
  styleUrls: ['./uniformes.component.css']
})
export class UniformesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData: Uniforme[] = [];
  uniforme: Uniforme;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearUniformeForm: FormGroup;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private uniformeService: UniformeService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.uniformeService.obtenerUniformes().subscribe((data: Uniforme[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los uniformes. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearUniformeForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: ['']
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

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;

    this.uniformeService.obtenerUniformeByUuid(this.uuid).subscribe((data: Uniforme) => {
      this.uniforme = data

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se pudo descargar el uniforme",
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  guardarUniforme(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados.",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el uniforme",
      ToastType.INFO
    );

    let value: Uniforme = form.value;

    this.uniformeService.guardarUniforme(value).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el uniforme con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
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
