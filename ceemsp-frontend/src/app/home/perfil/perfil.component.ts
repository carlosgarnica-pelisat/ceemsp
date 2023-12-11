import {Component, OnInit, ViewChild} from '@angular/core';
import {AuthenticationService} from "../../_services/authentication.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import Usuario from "../../_models/Usuario";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../_services/toast.service";
import * as sha256 from "js-sha256";
import {ToastType} from "../../_enums/ToastType";
import ActualizarContrasena from "../../_models/ActualizarContrasena";
import {UsuariosService} from "../../_services/usuarios.service";
import {PerfilService} from "../../_services/perfil.service";

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent implements OnInit {
  modal: NgbModalRef;
  usuarioActual: Usuario;
  reiniciarContrasenaForm: FormGroup;
  mostrarContrasena: boolean = false;

  @ViewChild('cambiarContrasenaModal') cambiarContrasenaModal;

  constructor(private authenticationService: AuthenticationService, private modalService: NgbModal,  private formBuilder: FormBuilder,
              private toastService: ToastService, private perfilService: PerfilService) { }

  ngOnInit(): void {
    this.reiniciarContrasenaForm = this.formBuilder.group({
      actualPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(15)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15)]],
      verificarPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15)]]
    })

    let usuario = this.authenticationService.currentUserValue;
    this.usuarioActual = usuario.usuario;
  }

  mostrarModalCambiarContrasena() {
    this.modal = this.modalService.open(this.cambiarContrasenaModal, {size: "lg", backdrop: "static"});
  }

  actualizarContrasena(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos obligatorios que no se han llenado`,
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value

    let password = sha256.sha256(formValue.password)
    let verificarPassword = sha256.sha256(formValue.verificarPassword)

    if(password !== verificarPassword) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Las nuevas contrasenas no coinciden`,
        ToastType.WARNING
      );
      return;
    }

    let actualizarContrasena = new ActualizarContrasena();
    actualizarContrasena.actualPassword = sha256.sha256(formValue.actualPassword)
    actualizarContrasena.password = password

    this.perfilService.cambiarContrasnea(actualizarContrasena).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha modificado la contrasena con exito`,
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la contrasena. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  conmutarMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  autogenerarContrasena() {
    this.reiniciarContrasenaForm.patchValue({
      password: this.hacerFalsoUuid(12)
    })
  }

  private hacerFalsoUuid(longitud) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < longitud; i++ ) {
      result += characters.charAt(Math.floor(Math.random() *
        charactersLength));
    }
    return result;
  }

}
