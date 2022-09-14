import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ArmasService} from "../../../../_services/armas.service";
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import ArmaMarca from "../../../../_models/ArmaMarca";
import ArmaClase from "../../../../_models/ArmaClase";
import TipoEntrenamiento from "../../../../_models/TipoEntrenamiento";
import {BotonCatalogosComponent} from "../../../../_components/botones/boton-catalogos/boton-catalogos.component";

@Component({
  selector: 'app-armas-marcas',
  templateUrl: './armas-marcas.component.html',
  styleUrls: ['./armas-marcas.component.css']
})
export class ArmasMarcasComponent implements OnInit {
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
  rowData = [];

  armaMarca: ArmaMarca;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearArmaMarcaForm: FormGroup;

  @ViewChild("mostrarArmaMarcaModal") mostrarArmaMarcaModal;
  @ViewChild("editarArmaMarcaModal") editarArmaMarcaModal;
  @ViewChild("eliminarArmaMarcaModal") eliminarArmaMarcaModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
              private armaService: ArmasService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      catalogoButtonRenderer: BotonCatalogosComponent
    }

    this.armaService.obtenerArmaMarcas().subscribe((data: ArmaMarca[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las marcas de las armas. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearArmaMarcaForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    let armaMarcaUuid = data.uuid;
    this.armaMarca = this.rowData.filter(x => x.uuid === armaMarcaUuid)[0];
    this.modal = this.modalService.open(this.mostrarArmaMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.uuid = data.uuid;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarArmaMarca(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido llenados",
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    let armaMarca: ArmaMarca = new ArmaMarca();
    armaMarca.nombre = value.nombre;
    armaMarca.descripcion = value.descripcion;

    this.armaService.guardarArmaMarca(armaMarca).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "La marca del arma se ha guardado con exito",
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

  guardarCambios(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que no se han validado",
        ToastType.WARNING
      )
      return;
    }

    let armaMarca: ArmaMarca = form.value;

    this.armaService.modificarArmaMarca(this.armaMarca.uuid, armaMarca).subscribe((data: ArmaMarca) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito la marca del arma",
        ToastType.SUCCESS
      )

      if(this.editandoModal) {
        this.armaMarca = data;
        this.modal.close();
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la marca del arma. ${error}`,
        ToastType.ERROR
      )
    })
  }

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData);
  }

  editar(rowData) {
    this.armaMarca = rowData.rowData;
    this.editandoModal = false;
    this.crearArmaMarcaForm.patchValue({
      nombre: this.armaMarca.nombre,
      descripcion: this.armaMarca.descripcion
    });

    this.modalService.open(this.editarArmaMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  eliminar(rowData) {
    this.armaMarca = rowData.rowData;
    this.mostrarEliminarCanEntrenamientoModal();
  }

  mostrarModificarArmaMarcaModal() {
    this.editandoModal = true;
    this.crearArmaMarcaForm.patchValue({
      nombre: this.armaMarca.nombre,
      descripcion: this.armaMarca.descripcion
    });

    this.modal = this.modalService.open(this.editarArmaMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarCanEntrenamientoModal() {
    this.modal = this.modalService.open(this.eliminarArmaMarcaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminar() {
    this.armaService.borrarArmaMarca(this.armaMarca.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la marca del arma con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la marca del arma. Motivo: ${error}`,
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
