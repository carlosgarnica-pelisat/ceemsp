import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../../_enums/ToastType";
import {EquipoService} from "../../../_services/equipo.service";
import Equipo from "../../../_models/Equipo";
import EmpresaEquipo from "../../../_models/EmpresaEquipo";
import {
  BotonEmpresaEquiposComponent
} from "../../../_components/botones/boton-empresa-equipos/boton-empresa-equipos.component";
import Empresa from "../../../_models/Empresa";
import EmpresaUniformeElementoMovimiento from "../../../_models/EmpresaUniformeElementoMovimiento";
import EmpresaEquipoMovimiento from "../../../_models/EmpresaEquipoMovimiento";

@Component({
  selector: 'app-empresa-equipo',
  templateUrl: './empresa-equipo.component.html',
  styleUrls: ['./empresa-equipo.component.css']
})
export class EmpresaEquipoComponent implements OnInit {
  editandoModal: boolean = false;
  uuid: string;
  empresa: Empresa;

  private gridApi;
  private gridColumnApi;

  modal: NgbModalRef;
  closeResult: string;
  frameworkComponents: any;

  crearEquipoForm: FormGroup;
  modificarEquipoForm: FormGroup;
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Equipo', field: 'equipo.nombre', sortable: true, filter: true },
    {headerName: 'Cantidad', field: 'cantidad', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'empresaEquipoButtonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];

  equipos: Equipo[];
  empresaEquipo: EmpresaEquipo;
  equipo: Equipo;

  altas: number = 0;
  bajas: number = 0;
  cantidadActual: number = 0;

  rowData: EmpresaEquipo[] = [];

  @ViewChild('equipoDetallesModal') equipoDetallesModal;
  @ViewChild('eliminarEquipoModal') eliminarEquipoModal;
  @ViewChild('modificarEquipoModal') modificarEquipoModal;
  @ViewChild('mostrarMovimientosModal') mostrarMovimientosModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private equipoService: EquipoService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      empresaEquipoButtonRenderer: BotonEmpresaEquiposComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");
    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.crearEquipoForm = this.formBuilder.group({
      'equipo': ['', Validators.required],
      'altas': ['', Validators.required],
      'bajas': ['', Validators.required],
      'cantidadActual': ['', Validators.required]
    });

    this.modificarEquipoForm = this.formBuilder.group({
      'altas': ['', Validators.required],
      'bajas': ['', Validators.required],
      'cantidadActual': ['', Validators.required]
    })

    this.equipoService.obtenerEquiposCalificablesParaEmpresa(this.uuid).subscribe((data: Equipo[]) => {
      this.equipos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el catalogo de equipos. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerEquipos(this.uuid).subscribe((data: EmpresaEquipo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los equipos de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.equipoDetallesModal)
  }

  editar(rowData) {
    this.empresaService.obtenerEquipoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaEquipo) => {
      this.empresaEquipo = data;
      this.editandoModal = false;
      this.modal = this.modalService.open(this.modificarEquipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});
      this.equipo = data.equipo;
      this.crearEquipoForm.setValue({
        equipo: this.empresaEquipo.equipo.uuid,
        cantidad: this.empresaEquipo.cantidad
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.empresaService.obtenerEquipoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaEquipo) => {
      this.empresaEquipo = data;
      this.mostrarModalEliminar();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el equipo. Motivo: ${error}`,
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
    this.crearEquipoForm.controls['cantidadActual'].disable();
    this.crearEquipoForm.controls['bajas'].disable();

    this.crearEquipoForm.patchValue({
      bajas: 0
    });

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalDetalles(data, modal) {
    let equipoUuid = data.uuid;

    this.empresaService.obtenerEquipoPorUuid(this.uuid, equipoUuid).subscribe((data: EmpresaEquipo) => {
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

  actualizarAltas(event) {
    let altas = event.value;
    if(altas < 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Las altas no pueden ser menores a 0`,
        ToastType.WARNING
      );
      return;
    }

    this.altas = altas;

    this.crearEquipoForm.patchValue({
      cantidadActual: (+this.cantidadActual) + (+this.altas - this.bajas)
    })

    this.modificarEquipoForm.patchValue({
      cantidadActual: (+this.cantidadActual) + (+this.altas - this.bajas)
    })
  }

  actualizarBajas(event) {
    let bajas = event.value;
    if(bajas < 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Las bajas no pueden ser menores a 0`,
        ToastType.WARNING
      );
      return;
    }

    this.bajas = bajas;

    this.crearEquipoForm.patchValue({
      cantidadActual: this.cantidadActual + (this.altas - this.bajas)
    })

    this.modificarEquipoForm.patchValue({
      cantidadActual: this.cantidadActual + (this.altas - this.bajas)
    })
  }

  mostrarMovimientos() {
    this.modal = this.modalService.open(this.mostrarMovimientosModal, {size: 'lg'})
  }

  mostrarModalModificar() {
    this.editandoModal = true;
    this.modal = this.modalService.open(this.modificarEquipoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
    this.equipo = this.empresaEquipo.equipo;
    this.modificarEquipoForm.patchValue({
      cantidadActual: this.empresaEquipo.cantidad
    });

    this.modificarEquipoForm.controls['cantidadActual'].disable();
    this.cantidadActual = this.empresaEquipo.cantidad;

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

    this.empresaService.eliminarEquipo(this.uuid, this.empresaEquipo.uuid).subscribe((data: EmpresaEquipo) => {
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
    let equipo = this.equipos.filter(x => x.uuid === event.value)[0];
    let existeEquipo = this.rowData.filter(x => equipo.uuid === x.equipo?.uuid);

    if(existeEquipo.length > 0) {
      this.equipo = undefined;
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Este equipo ya existe registrado. Favor de actualizarlo`,
        ToastType.WARNING
      );
      return;
    }

    this.equipo = equipo;
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

    let movimiento: EmpresaEquipoMovimiento = new EmpresaEquipoMovimiento();

    movimiento.altas = form.controls['altas'].value;
    movimiento.bajas = form.controls['bajas'].value;
    movimiento.cantidadActual = form.controls['cantidadActual'].value;

    value.cantidad = movimiento.cantidadActual;
    value.movimientos = [];

    value.movimientos.push(movimiento);

    this.empresaService.modificarEquipo(this.uuid, this.empresaEquipo.uuid, value).subscribe((data: EmpresaEquipo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el equipo con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerEquipos(this.uuid).subscribe((data: EmpresaEquipo[]) => {
          this.rowData = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los equipos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })

        this.empresaService.obtenerEquipoPorUuid(this.uuid, this.empresaEquipo.uuid).subscribe((data: EmpresaEquipo) => {
          this.empresaEquipo = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
              `No se ha podido descargar el equipo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      } else {
        window.location.reload();
      }
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

    if(this.equipo === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No hay equipo seleccionado o es invalido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el equipo`,
      ToastType.INFO
    );

    let value: EmpresaEquipo = form.value;
    value.equipo = this.equipo;

    let movimiento: EmpresaEquipoMovimiento = new EmpresaEquipoMovimiento();

    movimiento.altas = form.controls['altas'].value;
    movimiento.bajas = form.controls['bajas'].value;
    movimiento.cantidadActual = form.controls['cantidadActual'].value;

    value.cantidad = movimiento.cantidadActual;
    value.movimientos = [];

    value.movimientos.push(movimiento);

    this.empresaService.guardarEquipo(this.uuid, value).subscribe((data: Equipo) => {
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
