import {Component, OnInit, ViewChild} from '@angular/core';
import TipoInfraestructura from "../../../_models/TipoInfraestructura";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UniformeService} from "../../../_services/uniforme.service";
import {ToastService} from "../../../_services/toast.service";
import {TipoInfraestructuraService} from "../../../_services/tipo-infraestructura.service";
import Equipo from "../../../_models/Equipo";
import {ToastType} from "../../../_enums/ToastType";
import {AuthenticationService} from "../../../_services/authentication.service";
import {Router} from "@angular/router";
import Usuario from "../../../_models/Usuario";

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.css']
})
export class ClientesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData: TipoInfraestructura[] = [];
  tipoInfraestructura: TipoInfraestructura;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearTipoInfraestructuraForm: FormGroup;
  usuarioActual: Usuario;

  @ViewChild("mostrarTipoInfraestructuraModal") mostrarTipoInfraestructuraModal;
  @ViewChild('crearTipoInfraestructuraModal') crearTipoInfraestructuraModal;
  @ViewChild("editarTipoInfraestructuraModal") editarTipoInfraestructuraModal;
  @ViewChild("eliminarTipoInfraestructuraModal") eliminarTipoInfraestructuraModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder, private authenticationService: AuthenticationService,
              private tipoInfraestructuraService: TipoInfraestructuraService, private toastService: ToastService, private router: Router) { }

  ngOnInit(): void {
    let usuario = this.authenticationService.currentUserValue;
    this.usuarioActual = usuario.usuario;

    if(this.usuarioActual.rol !== 'CEEMSP_SUPERUSER') {
      this.router.navigate(['/home']);
    }

    this.tipoInfraestructuraService.obtenerTiposInfraestructura().subscribe((data: TipoInfraestructura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los puestos del personal. ${error}`,
        ToastType.ERROR
      )
    })

    this.crearTipoInfraestructuraForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    });
  }

  mostrarModalCrear() {
    this.modal = this.modalService.open(this.crearTipoInfraestructuraModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  checkForDetails(data) {
    this.modal = this.modalService.open(this.mostrarTipoInfraestructuraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.uuid = data.uuid;

    this.tipoInfraestructuraService.obtenerTipoInfraestructuraPorUuid(this.uuid).subscribe((data: TipoInfraestructura) => {
      this.tipoInfraestructura = data

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar el puesto de trabajo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarModificarTipoInfraestructuraModal() {
    this.crearTipoInfraestructuraForm.patchValue({
      nombre: this.tipoInfraestructura.nombre,
      descripcion: this.tipoInfraestructura.descripcion
    });

    this.modalService.open(this.editarTipoInfraestructuraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarTipoInfraestructuraModal() {
    this.modal = this.modalService.open(this.eliminarTipoInfraestructuraModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  guardarTipoDeInfraestructura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    let valid: TipoInfraestructura = form.value;

    this.tipoInfraestructuraService.guardarTipoInfraestructura(valid).subscribe((data: TipoInfraestructura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el tipo de infraestructura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el tipo de infraestructura. Motivo: ${error}`,
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

    let tipoInfraestructura: TipoInfraestructura = form.value;

    this.tipoInfraestructuraService.modificarTipoInfraestructura(this.tipoInfraestructura.uuid, tipoInfraestructura).subscribe((data: TipoInfraestructura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito el tipo de infraestructura",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el tipo de infraestructura. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  confirmarEliminar() {
    this.tipoInfraestructuraService.eliminarTipoInfraestructura(this.tipoInfraestructura.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el tipo de infraestructura con exito",
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
