import {Component, OnInit, ViewChild} from '@angular/core';
import PersonalPuestoTrabajo from "../../../../_models/PersonalPuestoTrabajo";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {PersonalService} from "../../../../_services/personal.service";
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import PersonalNacionalidad from "../../../../_models/PersonalNacionalidad";
import VehiculoUso from "../../../../_models/VehiculoUso";
import Uniforme from "../../../../_models/Uniforme";
import {AuthenticationService} from "../../../../_services/authentication.service";
import {Router} from "@angular/router";
import Usuario from "../../../../_models/Usuario";

@Component({
  selector: 'app-nacionalidades',
  templateUrl: './nacionalidades.component.html',
  styleUrls: ['./nacionalidades.component.css']
})
export class NacionalidadesComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];
  rowData: PersonalNacionalidad[] = [];

  nacionalidad: PersonalNacionalidad;

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearNacionalidadForm: FormGroup;
  usuarioActual: Usuario;

  @ViewChild("mostrarNacionalidadModal") mostrarNacionalidadModal;
  @ViewChild("editarNacionalidadModal") editarNacionalidadModal;
  @ViewChild("eliminarNacionalidadModal") eliminarNacionalidadModal;

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder, private authenticationService: AuthenticationService,
              private personalService: PersonalService, private toastService: ToastService, private router: Router) { }

  ngOnInit(): void {
    let usuario = this.authenticationService.currentUserValue;
    this.usuarioActual = usuario.usuario;

    if(this.usuarioActual.rol !== 'CEEMSP_SUPERUSER') {
      this.router.navigate(['/home']);
    }

    this.personalService.obtenerNacionalidades().subscribe((data: PersonalNacionalidad[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se pudieron descargar los puestos del personal",
        ToastType.ERROR
      )
    })

    this.crearNacionalidadForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(100)]]
    });
  }

  checkForDetails(data, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.nacionalidad = this.rowData.filter(x => x.uuid === data.uuid)[0]

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

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

  guardarNacionalidad(form) {
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
      "Estamos guardando la nacionalidad",
      ToastType.INFO
    );

    let formData: PersonalNacionalidad = form.value;

    this.personalService.guardarNacionalidad(formData).subscribe((data: PersonalNacionalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la nacionalidad con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la nacionalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
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

    let nacionalidad: PersonalNacionalidad = form.value;

    this.personalService.modificarNacionalidad(this.nacionalidad.uuid, nacionalidad).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado con exito la nacionalidad",
        ToastType.SUCCESS
      )

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la nacionalidad. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  confirmarEliminar() {
    this.personalService.eliminarNacionalidad(this.nacionalidad.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la nacionalidad con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la nacionalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModificarNacionalidadModal() {
    this.crearNacionalidadForm.patchValue({
      nombre: this.nacionalidad.nombre,
      descripcion: this.nacionalidad.descripcion
    });

    this.modalService.open(this.editarNacionalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarNacionalidadModal() {
    this.modal = this.modalService.open(this.eliminarNacionalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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
