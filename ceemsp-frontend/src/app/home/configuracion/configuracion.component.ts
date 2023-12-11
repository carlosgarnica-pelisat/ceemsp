import {Component, OnInit} from '@angular/core';
import {ToastService} from "../../_services/toast.service";
import {NotificacionesCorreoService} from "../../_services/notificaciones-correo.service";
import {ToastType} from "../../_enums/ToastType";

@Component({
  selector: 'app-configuracion',
  templateUrl: './configuracion.component.html',
  styleUrls: ['./configuracion.component.css']
})
export class ConfiguracionComponent implements OnInit {

  constructor(private toastService: ToastService, private notificacionesCorreoService: NotificacionesCorreoService) { }

  ngOnInit(): void {
  }

  generarNotificacionCorreo(tipoNotificacion: string) {
    if(tipoNotificacion === undefined) {
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos generando el envio del correo`,
      ToastType.INFO
    );

    this.notificacionesCorreoService.enviarNotificacionCorreo(tipoNotificacion).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha enviado el correo con exito`,
        ToastType.SUCCESS
      );
    }, (error) => {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        `No se ha podido enviar el correo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
