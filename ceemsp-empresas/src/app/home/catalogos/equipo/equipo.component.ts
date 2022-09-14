import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Equipo from "../../../_models/Equipo";
import {ToastService} from "../../../_services/toast.service";
import {ToastType} from "../../../_enums/ToastType";
import {EquipoService} from "../../../_services/equipo.service";

@Component({
  selector: 'app-equipo',
  templateUrl: './equipo.component.html',
  styleUrls: ['./equipo.component.css']
})
export class EquipoComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData: Equipo[] = [];
  equipo: Equipo;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearEquipoForm: FormGroup;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private equipoService: EquipoService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.equipoService.obtenerEquipos().subscribe((data: Equipo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los puestos del personal. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearEquipoForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: [''],
      formaEjecucion: ['', Validators.required]
    });
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;

    this.equipoService.obtenerEquipoByUuid(this.uuid).subscribe((data: Equipo) => {
      this.equipo = data

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se pudo descargar el puesto de trabajo",
        ToastType.ERROR
      )
    })
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarEquipo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    let valid: Equipo = form.value;

    /*this.equipoService.guardarEquipo(valid).subscribe((data: Equipo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el equipo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })*/
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
