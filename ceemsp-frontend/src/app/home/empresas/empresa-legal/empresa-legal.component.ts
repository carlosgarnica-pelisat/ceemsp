import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastType} from "../../../_enums/ToastType";

@Component({
  selector: 'app-empresa-legal',
  templateUrl: './empresa-legal.component.html',
  styleUrls: ['./empresa-legal.component.css']
})
export class EmpresaLegalComponent implements OnInit {

  uuid: string;
  escrituras: EmpresaEscritura[];

  nuevaEscrituraForm: FormGroup;
  modal: NgbModalRef;
  closeResult: string;

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Domicilio', field: 'domicilio1', sortable: true, filter: true},
    {headerName: 'Colonia', field: 'domicilio2', sortable: true, filter: true},
    {headerName: 'Estado', field: 'estado', sortable: true, filter: true},
    {headerName: 'C.P.', field: 'codigoPostal', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        modify: this.modify.bind(this),
        delete: this.delete.bind(this)
      }}
  ];
  rowData = [];

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', Validators.required],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', Validators.required],
      tipoFedatario: ['', Validators.required],
      numero: ['', Validators.required],
      nombreFedatario: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.empresaService.obtenerEscrituras(this.uuid).subscribe((data: EmpresaEscritura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las escrituras. ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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
