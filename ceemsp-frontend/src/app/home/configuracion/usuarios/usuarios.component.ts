import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastService} from "../../../_services/toast.service";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {UsuariosService} from "../../../_services/usuarios.service";
import Usuario from "../../../_models/Usuario";
import {ToastType} from "../../../_enums/ToastType";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.css']
})
export class UsuariosComponent implements OnInit {

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Apellidos', field: 'apellidos', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombres', sortable: true, filter: true},
    {headerName: 'Rol', field: 'rol', sortable: true, filter: true}
  ];

  rowData = [];

  private gridApi;
  private gridColumnApi;

  frameworkComponents: any;

  closeResult: string;
  modal: NgbModalRef;
  usuario: Usuario;

  crearUsuarioForm: FormGroup;

  @ViewChild("crearUsuarioModal") crearUsuarioModal;
  @ViewChild("detallesUsuarioModal") detallesUsuarioModal;
  @ViewChild("modificarUsuarioModal") modificarUsuarioModal;
  @ViewChild("eliminarUsuarioModal") eliminarUsuarioModal;

  constructor(private toastService: ToastService, private modalService: NgbModal, private usuarioService: UsuariosService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.crearUsuarioForm = this.formBuilder.group({
      usuario: ['', [Validators.required, Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      password: ['', [Validators.minLength(8), Validators.maxLength(15)]],
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      rol: ['', Validators.required]
    })

    this.usuarioService.obtenerUsuarios().subscribe((data: Usuario[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los usuarios. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    let uuid = data.uuid;

    this.usuarioService.obtenerUsuarioByUuid(uuid).subscribe((data: Usuario) => {
      this.usuario = data;

      this.modal = this.modalService.open(this.detallesUsuarioModal, {size: "xl"});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el usuario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalModificarUsuario() {
    this.crearUsuarioForm.patchValue({
      usuario: this.usuario.username,
      email: this.usuario.email,
      nombres: this.usuario.nombres,
      apellidos: this.usuario.apellidos,
      rol: this.usuario.rol
    })

    this.modal = this.modalService.open(this.modificarUsuarioModal, {size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarUsuario() {
    this.modal = this.modalService.open(this.eliminarUsuarioModal, {size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminarUsuario() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el usuario",
      ToastType.INFO
    );

    this.usuarioService.eliminarUsuario(this.usuario.uuid).subscribe((data: Usuario) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el usuario con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el usuario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalNuevoUsuario() {
    this.modal = this.modalService.open(this.crearUsuarioModal, {size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarUsuario(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos que no han sido rellenados debidamente",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando al usuario",
      ToastType.INFO
    );

    let value: Usuario = form.value;

    this.usuarioService.guardarUsuario(value).subscribe((data: Usuario) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el usuario con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el usuario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarCambiosUsuario(form) {

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
