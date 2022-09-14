import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {VehiculosService} from "../../../_services/vehiculos.service";
import VehiculoMarca from "../../../_models/VehiculoMarca";
import {ToastType} from "../../../_enums/ToastType";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import VehiculoSubmarca from "../../../_models/VehiculoSubmarca";
import EmpresaEscrituraSocio from "../../../_models/EmpresaEscrituraSocio";
import {BotonCatalogosComponent} from "../../../_components/botones/boton-catalogos/boton-catalogos.component";

@Component({
  selector: 'app-vehiculos',
  templateUrl: './vehiculos.component.html',
  styleUrls: ['./vehiculos.component.css']
})
export class VehiculosComponent implements OnInit {
  editandoModal: boolean = false;
  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

  private gridApi;
  private gridColumnApi;

  showSubmarcaForm: boolean;
  editandoSubmarca: boolean;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'catalogoButtonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  tempUuidSubmarca: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearVehiculoMarcaForm: FormGroup;
  crearVehiculoSubmarcaForm: FormGroup;

  vehiculoMarca: VehiculoMarca;
  vehiculoSubmarca: VehiculoSubmarca;

  currentTab: string = "DETALLES";

  @ViewChild("mostrarVehiculoModal") mostrarVehiculoModal;
  @ViewChild("editarVehiculoMarcaModal") editarVehiculoMarcaModal;
  @ViewChild("eliminarVehiculoMarcaModal") eliminarVehiculoMarcaModal;
  @ViewChild("eliminarVehiculoSubmarcaModal") eliminarVehiculoSubmarcaModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private vehiculoService: VehiculosService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      catalogoButtonRenderer: BotonCatalogosComponent
    }

    this.vehiculoService.obtenerVehiculosMarcas().subscribe((data: VehiculoMarca[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las marcas de los vehiculos. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearVehiculoMarcaForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['',  [Validators.maxLength(100)]]
    })

    this.crearVehiculoSubmarcaForm = this.formBuilder.group({
      nombre: ['',  [Validators.required, Validators.maxLength(100)]],
      descripcion: ['',  [Validators.maxLength(100)]]
    })
  }

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData, this.mostrarVehiculoModal);
  }

  editar(rowData) {
    this.vehiculoMarca = rowData.rowData;
    this.editandoModal = false;
    this.crearVehiculoMarcaForm.patchValue({
      nombre: this.vehiculoMarca.nombre,
      descripcion: this.vehiculoMarca.descripcion
    });

    this.modal = this.modalService.open(this.editarVehiculoMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  eliminar(rowData) {
    this.vehiculoMarca = rowData.rowData;
    this.mostrarEliminarVehiculoMarcaModal();
  }

  mostrarEditarSubmarca(index) {
    this.vehiculoSubmarca = this.vehiculoMarca.submarcas[index];
    this.mostrarFormularioSubmarca();
    this.editandoSubmarca = true;
    this.crearVehiculoSubmarcaForm.patchValue({
      nombre: this.vehiculoSubmarca.nombre,
      descripcion: this.vehiculoSubmarca.descripcion
    });
  }

  guardarSubmarca(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la submarca",
      ToastType.INFO
    );

    let formValue: VehiculoSubmarca = form.value;

    if(this.editandoSubmarca) {
      formValue.uuid = this.vehiculoSubmarca.uuid;
      formValue.id = this.vehiculoSubmarca.id;

      this.vehiculoService.modificarSubmarca(this.uuid, this.vehiculoSubmarca.uuid, formValue).subscribe((data: VehiculoSubmarca) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado la submarca con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar la submarca. Motivo: ${error}`,
          ToastType.ERROR
        );
      });

    } else {
      this.vehiculoService.guardarSubmarca(this.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado la submarca con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la submarca. ${error}`,
          ToastType.ERROR
        );
      });
    }
  }

  mostrarFormularioSubmarca() {
    this.showSubmarcaForm = !this.showSubmarcaForm;
    if(!this.showSubmarcaForm) {
      this.crearVehiculoSubmarcaForm.reset();
    }
    if(this.editandoSubmarca) {
      this.editandoSubmarca = false;
      this.vehiculoSubmarca = undefined;
    }
  }

  mostrarModificarVehiculoMarcaModal() {
    this.editandoModal = true;
    this.crearVehiculoMarcaForm.patchValue({
      nombre: this.vehiculoMarca.nombre,
      descripcion: this.vehiculoMarca.descripcion
    });

    this.modal = this.modalService.open(this.editarVehiculoMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarVehiculoMarcaModal() {
    this.modal = this.modalService.open(this.eliminarVehiculoMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarEliminarVehiculoSubmarcaModal(uuid: string) {
    this.tempUuidSubmarca = uuid;

    this.modal = this.modalService.open(this.eliminarVehiculoSubmarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminar() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando la marca",
      ToastType.INFO
    );

    this.vehiculoService.borrarVehiculoMarcaPorUuid(this.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la marca con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la marca. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarSubmarca() {
    if(this.tempUuidSubmarca === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la submarca a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la submarca",
      ToastType.INFO
    );

    this.vehiculoService.eliminarSubmarca(this.uuid, this.tempUuidSubmarca).subscribe((data: VehiculoSubmarca) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la submarca con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La submarca no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarCambios(form) {
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
      "Se esta actualizando la marca del vehiculo",
      ToastType.INFO
    );

    let formValue: VehiculoMarca = form.value;

    this.vehiculoService.modificarVehiculoMarca(this.uuid, formValue).subscribe((data: VehiculoMarca) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha actualizado la marca del vehiculo con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.vehiculoMarca = data;
        this.modal.close();
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la marca. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;

    this.vehiculoService.obtenerVehiculoMarcaPorUuid(this.uuid).subscribe((data: VehiculoMarca) => {
      this.vehiculoMarca = data;
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La informacion de la marca del vehiculo no se descargo. Motivo: ",
        ToastType.ERROR
      );
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

  guardarMarcaVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    let vehiculoMarca: VehiculoMarca = new VehiculoMarca();
    vehiculoMarca.nombre = value.nombre;
    vehiculoMarca.descripcion = value.descripcion;

    this.vehiculoService.guardarVehiculoMarca(vehiculoMarca).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "La marca del vehiculo se ha guardado con exito",
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La narca del arna no se ha podido guardar. ${error}`,
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

  cambiarASubmarcas() {
    this.currentTab = "SUBMARCAS";
  }

  cambiarADetalles() {
    this.currentTab = "DETALLES"
  }

}
