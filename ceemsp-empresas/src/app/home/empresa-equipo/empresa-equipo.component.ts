import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../_enums/ToastType";
import {EquipoService} from "../../_services/equipo.service";
import Equipo from "../../_models/Equipo";
import EmpresaEquipo from "../../_models/EmpresaEquipo";
import {EmpresaEquipoService} from "../../_services/empresa-equipo.service";

@Component({
  selector: 'app-empresa-equipo',
  templateUrl: './empresa-equipo.component.html',
  styleUrls: ['./empresa-equipo.component.css']
})
export class EmpresaEquipoComponent implements OnInit {

  uuid: string;

  private gridApi;
  private gridColumnApi;

  modal: NgbModalRef;
  closeResult: string;
  frameworkComponents: any;

  crearEquipoForm: FormGroup;
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Equipo', field: 'equipo.nombre', sortable: true, filter: true },
    {headerName: 'Cantidad', field: 'cantidad', sortable: true, filter: true}
  ];

  equipos: Equipo[];
  empresaEquipo: EmpresaEquipo;
  equipo: Equipo;

  rowData: EmpresaEquipo[] = [];

  @ViewChild('eliminarEquipoModal') eliminarEquipoModal;
  @ViewChild('modificarEquipoModal') modificarEquipoModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaEquipoService: EmpresaEquipoService,
              private formBuilder: FormBuilder, private equipoService: EquipoService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEquipoForm = this.formBuilder.group({
      'equipo': ['', Validators.required],
      'cantidad': ['', Validators.required]
    });

    this.equipoService.obtenerEquipos().subscribe((data: Equipo[]) => {
      this.equipos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el catalogo de equipos. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaEquipoService.obtenerEquipos().subscribe((data: EmpresaEquipo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los equipos de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
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

  mostrarModalDetalles(data, modal) {
    let equipoUuid = data.uuid;

    this.empresaEquipoService.obtenerEquipoPorUuid(equipoUuid).subscribe((data: EmpresaEquipo) => {
      this.empresaEquipo = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalModificar() {
    this.modal = this.modalService.open(this.modificarEquipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.crearEquipoForm.setValue({
      equipo: this.empresaEquipo.equipo.uuid,
      cantidad: this.empresaEquipo.cantidad
    });

    this.equipo = this.equipos.filter(x => x.uuid === this.empresaEquipo.equipo.uuid)[0];
  }

  mostrarModalEliminar() {
    this.modal = this.modalService.open(this.eliminarEquipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminarEquipo() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el equipo",
      ToastType.INFO
    );

    this.empresaEquipoService.eliminarEquipo(this.empresaEquipo.uuid).subscribe((data: EmpresaEquipo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el equipo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  exportGridData(format) {
    switch(format) {
      case "CSV":
        this.gridApi.exportDataAsCsv();
        break;
      case "PDF":
        this.toastService.showGenericToast(
          "Bajo desarrollo",
          "Actualmente estamos desarrollando esta funcionalidad",
          ToastType.INFO
        )
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No podemos exportar en dicho formato",
          ToastType.WARNING
        )
        break;
    }
  }

  seleccionarEquipo(event) {
    this.equipo = this.equipos.filter(x => x.uuid === event.value)[0];
  }

  guardarCambiosEquipo(form) {
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
      `Estamos guardando los cambios en el equipo`,
      ToastType.INFO
    );

    let value: EmpresaEquipo = form.value;
    value.equipo = this.equipo;

    this.empresaEquipoService.modificarEquipo(this.empresaEquipo.uuid, value).subscribe((data: EmpresaEquipo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el equipo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarEquipo(form) {
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
      `Estamos guardando el uniforme`,
      ToastType.INFO
    );

    let value: EmpresaEquipo = form.value;
    value.equipo = this.equipo;

    this.empresaEquipoService.guardarEquipo(value).subscribe((data: Equipo) => {
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
