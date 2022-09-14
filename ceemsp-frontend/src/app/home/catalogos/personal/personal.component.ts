import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {PersonalService} from "../../../_services/personal.service";
import {ToastType} from "../../../_enums/ToastType";
import PersonalPuestoTrabajo from "../../../_models/PersonalPuestoTrabajo";
import PersonalSubpuestoTrabajo from "../../../_models/PersonalSubpuestoTrabajo";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import {BotonCatalogosComponent} from "../../../_components/botones/boton-catalogos/boton-catalogos.component";

@Component({
  selector: 'app-personal',
  templateUrl: './personal.component.html',
  styleUrls: ['./personal.component.css']
})
export class PersonalComponent implements OnInit {
  editandoModal: boolean = false;
  private gridApi;
  private gridColumnApi;

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
  rowData: PersonalPuestoTrabajo[] = [];
  puestoTrabajo: PersonalPuestoTrabajo;
  subpuestoTrabajo: PersonalSubpuestoTrabajo;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  tempUuid: string;
  pestanaActual: string = "DETALLES";
  rowDataClicked = {
    uuid: undefined
  };

  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

  mostrarFormularioSubpuesto: boolean = false;
  editandoSubpuesto: boolean = false;

  crearPuestoDeTrabajoForm: FormGroup;
  crearSubpuestoDeTrabajoForm: FormGroup;

  @ViewChild('mostrarPuestoDetallesModal') mostrarPuestoDetallesModal;
  @ViewChild('modificarPuestoTrabajoModal') modificarPuestoTrabajoModal;
  @ViewChild("eliminarPuestoModal") eliminarPuestoModal;
  @ViewChild("eliminarSubpuestoModal") eliminarSubpuestoModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private personalService: PersonalService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      catalogoButtonRenderer: BotonCatalogosComponent
    }

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
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    });

    this.crearSubpuestoDeTrabajoForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['',  [Validators.maxLength(100)]],
      portacion: ['', [Validators.required]],
      cuip: ['', [Validators.required]]
    })
  }

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData, this.mostrarPuestoDetallesModal);
  }

  editar(rowData) {
    this.puestoTrabajo = rowData.rowData;
    this.editandoModal = false;
    this.crearPuestoDeTrabajoForm.patchValue({
      nombre: this.puestoTrabajo.nombre,
      descripcion: this.puestoTrabajo.descripcion
    });

    this.modal = this.modalService.open(this.modificarPuestoTrabajoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  eliminar(rowData) {
    this.puestoTrabajo = rowData.rowData;
    this.mostrarEliminarPuestoTrabajoModal();
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

  mostrarModificarPuestoTrabajoModal() {
    this.editandoModal = true;
    this.crearPuestoDeTrabajoForm.patchValue({
      nombre: this.puestoTrabajo.nombre,
      descripcion: this.puestoTrabajo.descripcion
    });

    this.modal = this.modalService.open(this.modificarPuestoTrabajoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarPuestoTrabajoModal() {
    this.modal = this.modalService.open(this.eliminarPuestoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarFormularioSubpuestos() {
    this.mostrarFormularioSubpuesto = !this.mostrarFormularioSubpuesto;
    if(!this.mostrarFormularioSubpuesto) {
      this.crearSubpuestoDeTrabajoForm.reset();
    }
    if(this.editandoSubpuesto) {
      this.editandoSubpuesto = false;
      this.subpuestoTrabajo = undefined;
    }
  }

  mostrarEditarPuestoTrabajo(index) {
    this.subpuestoTrabajo = this.puestoTrabajo.subpuestos[index];
    this.mostrarFormularioSubpuestos();
    this.editandoSubpuesto = true;
    this.crearSubpuestoDeTrabajoForm.patchValue({
      nombre: this.subpuestoTrabajo.nombre,
      descripcion: this.subpuestoTrabajo.descripcion,
      cuip: this.subpuestoTrabajo.cuip,
      portacion : this.subpuestoTrabajo.portacion
    });
  }

  mostrarEliminarSubpuestoModal(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarSubpuestoModal, {size: "lg"})
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
      "Se esta actualizando el puesto de trabajo",
      ToastType.INFO
    );

    let formValue: PersonalPuestoTrabajo = form.value;

    this.personalService.modificarPuesto(this.uuid, formValue).subscribe((data: PersonalPuestoTrabajo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha actualizado el puesto de trabajo con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.puestoTrabajo = data;
        this.modal.close();
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el puesto de trabajo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

    if(this.editandoSubpuesto) {
      this.personalService.modificarSubpuestoTrabajo(this.uuid, this.subpuestoTrabajo.uuid, value).subscribe((data: PersonalSubpuestoTrabajo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el subpuesto de trabajo con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioSubpuestos();
        this.personalService.obtenerSubpuestosTrabajo(this.puestoTrabajo.uuid).subscribe((data: PersonalSubpuestoTrabajo[]) => {
          this.puestoTrabajo.subpuestos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un proboema",
            `No se han podido descargar los subpuestos de trabajo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los subpuestos. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.personalService.guardarSubpuestoTrabajo(this.puestoTrabajo.uuid, value).subscribe((data: PersonalSubpuestoTrabajo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el subpuesto de trabajo con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioSubpuestos();
        this.personalService.obtenerSubpuestosTrabajo(this.puestoTrabajo.uuid).subscribe((data: PersonalSubpuestoTrabajo[]) => {
          this.puestoTrabajo.subpuestos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un proboema",
            `No se han podido descargar los subpuestos de trabajo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el subpuesto de trabajo. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
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

  confirmarEliminarPuesto() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el puesto",
      ToastType.INFO
    );

    this.personalService.borrarPuesto(this.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el puesto con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el puesto. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarSubpuesto() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del subpuesto a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el subpuesto",
      ToastType.INFO
    );

    this.personalService.eliminarSubpuestoTrabajo(this.uuid, this.tempUuid).subscribe((data: PersonalSubpuestoTrabajo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el subpuesto con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.personalService.obtenerSubpuestosTrabajo(this.puestoTrabajo.uuid).subscribe((data: PersonalSubpuestoTrabajo[]) => {
        this.puestoTrabajo.subpuestos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un proboema",
          `No se han podido descargar los subpuestos de trabajo. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El subpuesto no se ha podido eliminar. Motivo: ${error}`,
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
