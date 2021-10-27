import {Component, OnInit} from '@angular/core';
import {ToastService} from "../../../_services/toast.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import {ActivatedRoute} from "@angular/router";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastType} from "../../../_enums/ToastType";

@Component({
  selector: 'app-empresa-domicilios',
  templateUrl: './empresa-domicilios.component.html',
  styleUrls: ['./empresa-domicilios.component.css']
})
export class EmpresaDomiciliosComponent implements OnInit {

  uuid: string;
  domicilios: EmpresaDomicilio[];

  nuevoDomicilioForm: FormGroup;
  modal: NgbModalRef;
  closeResult: string;

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
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

  constructor(private toastService: ToastService, private formbuilder: FormBuilder,
              private empresaService: EmpresaService, private route: ActivatedRoute,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevoDomicilioForm = this.formbuilder.group({
      nombre: ['', Validators.required],
      domicilio1: ['', Validators.required],
      domicilio2: ['', Validators.required],
      domicilio3: ['', Validators.required],
      domicilio4: [''],
      codigoPostal: ['', Validators.required],
      estado: ['', Validators.required],
      pais: ['Mexico', Validators.required],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      telefonoFijo: ['', Validators.required],
      telefonoMovil: ['', Validators.required]
    })

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
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

  guardarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay camops del formulario requeridos que no tienen informacion",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el nuevo domicilio",
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilio = form.value;

    this.empresaService.guardarDomicilio(this.uuid, domicilio).subscribe((data: EmpresaDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la empresa. Motivo: ${error}`,
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
