import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {PersonalService} from "../../../_services/personal.service";
import {ToastType} from "../../../_enums/ToastType";
import PersonalPuestoTrabajo from "../../../_models/PersonalPuestoTrabajo";
import PersonalSubpuestoTrabajo from "../../../_models/PersonalSubpuestoTrabajo";

@Component({
  selector: 'app-personal',
  templateUrl: './personal.component.html',
  styleUrls: ['./personal.component.css']
})
export class PersonalComponent implements OnInit {

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
  rowData: PersonalPuestoTrabajo[] = [];
  puestoTrabajo: PersonalPuestoTrabajo;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  pestanaActual: string = "DETALLES";
  rowDataClicked = {
    uuid: undefined
  };

  mostrarFormularioSubpuesto: boolean = false;

  crearPuestoDeTrabajoForm: FormGroup;
  crearSubpuestoDeTrabajoForm: FormGroup;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private personalService: PersonalService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.personalService.obtenerPuestosPersonal().subscribe((data: PersonalPuestoTrabajo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los puestos del personal. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearPuestoDeTrabajoForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: ['']
    });

    this.crearSubpuestoDeTrabajoForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      descripcion: [''],
      portacion: ['', Validators.required],
      cuip: ['', Validators.required]
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

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

  mostrarFormularioSubpuestos() {
    this.mostrarFormularioSubpuesto = !this.mostrarFormularioSubpuesto;
  }

  guardarSubpuestoTrabajo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el subpuesto de trabajo",
      ToastType.INFO
    );

    let value: PersonalSubpuestoTrabajo = form.value;

    this.personalService.guardarSubpuestoTrabajo(this.puestoTrabajo.uuid, value).subscribe((data: PersonalSubpuestoTrabajo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el subpuesto de trabajo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el subpuesto de trabajo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.uuid = data.uuid;

    this.personalService.obtenerPuestoPorUuid(this.uuid).subscribe((data: PersonalPuestoTrabajo) => {
      this.puestoTrabajo = data

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

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  guardarPuestoTrabajo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el puesto de trabajo",
      ToastType.INFO
    );

    let formData: PersonalPuestoTrabajo = form.value;

    this.personalService.guardarPuesto(formData).subscribe((data: PersonalPuestoTrabajo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el puesto de trabajo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el puesto de trabajo. Motivo: ${error}`,
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
