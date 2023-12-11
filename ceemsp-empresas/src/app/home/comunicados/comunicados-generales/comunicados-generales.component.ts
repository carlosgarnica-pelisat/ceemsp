import { Component, OnInit } from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import ComunicadoGeneral from "../../../_models/ComunicadoGeneral";
import {ToastType} from "../../../_enums/ToastType";
import {ComunicadosGeneralesService} from "../../../_services/comunicados-generales.service";
import {ToastService} from "../../../_services/toast.service";

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
    {headerName: 'Fecha de publicacion', field: 'fechaPublicacion', sortable: true, filter: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        modify: this.modify.bind(this),
        delete: this.delete.bind(this)
      }}
  ];
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };


  constructor(private modalService: NgbModal, private comunicadosGeneralesService: ComunicadosGeneralesService,
              private toastService: ToastService) { }

  ngOnInit(): void {
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
    //this.modal = this.modalService.open(showCustomerDetailsModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;
  }

  modify(rowData) {

  }

  delete(rowData) {

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
