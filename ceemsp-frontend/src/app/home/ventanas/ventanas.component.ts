import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../_enums/ToastType";
import Ventana from "../../_models/Ventana";
import {VentanasService} from "../../_services/ventanas.service";
import {BotonVentanaComponent} from "../../_components/botones/boton-ventana/boton-ventana.component";

@Component({
  selector: 'app-ventanas',
  templateUrl: './ventanas.component.html',
  styleUrls: ['./ventanas.component.css']
})
export class VentanasComponent implements OnInit {

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true,  resizable: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true, resizable: true},
    {headerName: 'Fecha de Inicio', field: 'fechaInicio', sortable: true, filter: true, resizable: true},
    {headerName: 'Fecha de Fin', field: 'fechaFin', sortable: true, filter: true, resizable: true},
    {headerName: 'Opciones', cellRenderer: 'ventanaButtonRenderer', width: 100, cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];

  frameworkComponents: any;

  crearVentanaForm: FormGroup;
  private gridApi;
  private gridColumnApi;
  rowData = [];
  ventana: Ventana;
  modal: NgbModalRef;
  closeResult: string;

  @ViewChild('crearVentanaModal') crearVentanaModal;
  @ViewChild('modificarVentanaModal') modificarVentanaModal;
  @ViewChild('verDetallesVentanaModal') verDetallesVentanaModal;
  @ViewChild('eliminarVentanaModal') eliminarVentanaModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private formBuilder: FormBuilder,
              private ventanaService: VentanasService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      ventanaButtonRenderer: BotonVentanaComponent
    }

    this.crearVentanaForm = this.formBuilder.group({
      nombre: ['', [Validators.required]],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]]
    })

    this.ventanaService.obtenerVentanas().subscribe((data: Ventana[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las ventanas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData);
  }

  editar(rowData) {
    this.ventanaService.obtenerVentanaPorUuid(rowData.rowData.uuid).subscribe((data: Ventana) => {
      this.ventana = data;
      this.mostrarModalModificar();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la visita. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.ventanaService.obtenerVentanaPorUuid(rowData.rowData.uuid).subscribe((data: Ventana) => {
      this.ventana = data;
      this.mostrarModalEliminarVentana();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la visita. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalNuevaVentana() {
    this.modal = this.modalService.open(this.crearVentanaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalDetalles(rowData) {
    let ventanaUuid = rowData.uuid;
    this.modal = this.modalService.open(this.verDetallesVentanaModal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.ventanaService.obtenerVentanaPorUuid(ventanaUuid).subscribe((data: Ventana) => {
      this.ventana = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarModalModificar() {
    this.crearVentanaForm.patchValue({
      nombre: this.ventana.nombre,
      fechaInicio: this.ventana.fechaInicio,
      fechaFin: this.ventana.fechaFin
    })
    this.modal = this.modalService.open(this.modificarVentanaModal, {ariaLabelledBy: "modal-basic-title", size: 'xl', backdrop: 'static'})
  }

  mostrarModalEliminarVentana() {
    this.modal = this.modalService.open(this.eliminarVentanaModal, {ariaLabelledBy: "modal-basic-title", size: 'xl', backdrop: 'static'})
  }

  guardarCambiosVentana(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los campos esta como nulo o invalido`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Se esta creando una nueva ventana`,
      ToastType.INFO
    );

    let ventana: Ventana = form.value;

    this.ventanaService.modificarVentana(this.ventana?.uuid, ventana).subscribe((data: Ventana) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la ventana con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la ventana. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarVentana() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando la ventana`,
      ToastType.INFO
    );

    this.ventanaService.eliminarVentana(this.ventana.uuid).subscribe((data: Ventana) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la ventana con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarVentana(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Faltan campos requeridos por llenar`,
        ToastType.WARNING
      )
      return;
    }

    let ventana: Ventana = form.value;

    this.ventanaService.guardarVentana(ventana).subscribe((data: Ventana) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la ventana con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la ventana. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
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
