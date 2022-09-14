import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastType} from "../../../_enums/ToastType";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalidadesService} from "../../../_services/modalidades.service";
import {ToastService} from "../../../_services/toast.service";
import Modalidad from "../../../_models/Modalidad";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import Submodalidad from "../../../_models/Submodalidad";
import {BotonCatalogosComponent} from "../../../_components/botones/boton-catalogos/boton-catalogos.component";

@Component({
  selector: 'app-modalidades',
  templateUrl: './modalidades.component.html',
  styleUrls: ['./modalidades.component.css']
})
export class ModalidadesComponent implements OnInit {
  editandoModal: boolean = false;
  private gridApi;
  private gridColumnApi;

  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

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
  rowDataClicked = {
    uuid: undefined
  };

  submodalidad: Submodalidad;

  pestanaActual: string = "DETALLES";
  tempUuidSubmodalidad: string = "";

  crearModalidadForm: FormGroup;
  crearSubmodalidadForm: FormGroup;
  modalidad: Modalidad;
  mostrarFormularioSubmodalidad: boolean = false;
  editandoSubmodalidad: boolean = false;

  @ViewChild("mostrarModalidadModal") mostrarModalidadModal;
  @ViewChild("editarModalidadModal") editarModalidadModal;
  @ViewChild("eliminarModalidadModal") eliminarModalidadModal;
  @ViewChild("eliminarSubmodalidadModal") eliminarSubmodalidadModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private modalidadesService: ModalidadesService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      catalogoButtonRenderer: BotonCatalogosComponent
    }

    this.crearModalidadForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.maxLength(100)]],
      tipo: ['', [Validators.required]]
    });

    this.crearSubmodalidadForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(60)]],
      descripcion: ['', [Validators.required, Validators.maxLength(60)]],
      canes: ['', [Validators.required]],
      armas: ['', [Validators.required]]
    });

    this.modalidadesService.obtenerModalidades().subscribe((data: Modalidad[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La lista de modalidades no se ha podido descargar. ${error}`,
        ToastType.ERROR
      )
    })
  }

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData);
  }

  editar(rowData) {
    this.modalidad = rowData.rowData;
    this.editandoModal = false;
    this.crearModalidadForm.patchValue({
      nombre: this.modalidad.nombre,
      descripcion: this.modalidad.descripcion,
      tipo: this.modalidad.tipo
    });

    this.modal = this.modalService.open(this.editarModalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  eliminar(rowData) {
    this.modalidad = rowData.rowData;
    this.mostrarEliminarModalidadModal();
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarFormularioSubmodalidades() {
    this.mostrarFormularioSubmodalidad = !this.mostrarFormularioSubmodalidad;
    if(!this.mostrarFormularioSubmodalidad) {
      this.crearSubmodalidadForm.reset();
    }
    if(this.editandoSubmodalidad) {
      this.editandoSubmodalidad = false;
      this.submodalidad = undefined;
    }
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  guardarModalidad(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos creando la nueva modalidad. Espere por favor",
      ToastType.INFO
    )

    let value: Modalidad = form.value;

    this.modalidadesService.guardarmodalidad(value).subscribe((data: Modalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la modalidad con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la modalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarSubmodalidad(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario no es valido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la submodalidad",
      ToastType.INFO
    );

    let submodalidad: Submodalidad = form.value;

    if(this.editandoSubmodalidad) {
      this.modalidadesService.modificarSubmodalidad(this.modalidad.uuid, this.submodalidad.uuid, submodalidad).subscribe((data: Submodalidad) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la submodalidad con exito`,
          ToastType.SUCCESS
        );
        this.mostrarFormularioSubmodalidades();
        this.modalidadesService.obtenerSubmodalidades(this.modalidad.uuid).subscribe((data: Submodalidad[]) => {
          console.log(data);
          this.modalidad.submodalidades = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las submodalidades. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      });
    } else {
      this.modalidadesService.guardarSubmodalidad(this.modalidad.uuid, submodalidad).subscribe((data: Submodalidad) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la submodalidad con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioSubmodalidades();
        this.modalidadesService.obtenerSubmodalidades(this.modalidad.uuid).subscribe((data: Submodalidad[]) => {
          this.modalidad.submodalidades = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las submodalidades. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la submodalidad. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  mostrarModificarModalidadModal() {
    this.editandoModal = true;
    this.crearModalidadForm.patchValue({
      nombre: this.modalidad.nombre,
      descripcion: this.modalidad.descripcion,
      tipo: this.modalidad.tipo
    });

    this.modal = this.modalService.open(this.editarModalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarSubmodalidadModal(uuid) {
    this.tempUuidSubmodalidad = uuid;

    this.modal = this.modalService.open(this.eliminarSubmodalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarCambios(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario contiene datos invalidos. Favor de corregirlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando los cambios",
      ToastType.INFO
    );

    let modalidad: Modalidad = form.value;

    this.modalidadesService.modificarModalidad(this.modalidad.uuid, modalidad).subscribe((data: Modalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardaron los cambios con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modalidad = data;
        this.modal.close();
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido guardar los cambios. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEliminarModalidadModal() {
    this.modal = this.modalService.open(this.eliminarModalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminarSubmodalidad() {
    this.modalidadesService.eliminarSubmodalidad(this.uuid, this.tempUuidSubmodalidad).subscribe((data: Submodalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la submodalidad`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.modalidadesService.obtenerSubmodalidades(this.modalidad.uuid).subscribe((data: Submodalidad[]) => {
        this.modalidad.submodalidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las submodalidades. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la modalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEditarSubmodalidad(index) {
    this.submodalidad = this.modalidad.submodalidades[index];
    this.mostrarFormularioSubmodalidades();
    this.editandoSubmodalidad = true;
    this.crearSubmodalidadForm.patchValue({
      nombre: this.submodalidad.nombre,
      descripcion: this.submodalidad.descripcion,
      canes: this.submodalidad.canes,
      armas: this.submodalidad.armas
    })
  }

  confirmarEliminar() {
    this.modalidadesService.borrarModalidad(this.modalidad.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la modalidad con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la modalidad. Motivo: ${error}`,
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

  checkForDetails(data) {
    let modalidadUuid = data.uuid;

    this.modalidadesService.obtenerModalidadByUuid(modalidadUuid).subscribe((data: Modalidad) => {
      this.modalidad = data;
      this.modal = this.modalService.open(this.mostrarModalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
      this.uuid = data.uuid;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la modalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
