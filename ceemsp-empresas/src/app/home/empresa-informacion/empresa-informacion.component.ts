import { Component, OnInit } from '@angular/core';
import Usuario from "../../_models/Usuario";
import {ToastType} from "../../_enums/ToastType";
import {UsuariosService} from "../../_services/usuarios.service";
import {ToastService} from "../../_services/toast.service";

@Component({
  selector: 'app-empresa-informacion',
  templateUrl: './empresa-informacion.component.html',
  styleUrls: ['./empresa-informacion.component.css']
})
export class EmpresaInformacionComponent implements OnInit {
  usuarioActual: Usuario;
  logo: any;

  constructor(private usuarioService: UsuariosService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;

      this.usuarioService.obtenerLogoEmpresaActual().subscribe((data: Blob) => {
        this.convertirImagenLogo(data);
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la fotografia. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  convertirImagenLogo(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.logo = reader.result
    });

    if(imagen) {
      reader.readAsDataURL(imagen)
    }
  }

}
