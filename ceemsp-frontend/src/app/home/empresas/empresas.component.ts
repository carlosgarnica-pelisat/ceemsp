import { Component, OnInit } from '@angular/core';
import {ModalDismissReasons, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import Empresa from "../../_models/Empresa";
import {ToastType} from "../../_enums/ToastType";
import {Router} from "@angular/router";

@Component({
  selector: 'app-empresas',
  templateUrl: './empresas.component.html',
  styleUrls: ['./empresas.component.css']
})
export class EmpresasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true },
    {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
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

  constructor(private toastService: ToastService, private empresaService: EmpresaService, private router: Router) { }

  ngOnInit(): void {
    this.empresaService.obtenerEmpresas().subscribe((data: Empresa[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las empresas. ${error}`,
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
    this.uuid = data.uuid;

    this.router.navigate([`/home/empresas/${this.uuid}`]);
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
