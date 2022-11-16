import {Component, OnInit, ViewChild} from '@angular/core';
import {faBell, faFolder, faUsers, faSignOutAlt, faBuilding, faDog, faHome, faFileContract, faCar, faLuggageCart, faBomb, faTicketAlt} from "@fortawesome/free-solid-svg-icons";
import {NotificacionesService} from "../_services/notificaciones.service";
import BuzonSalida from "../_models/BuzonSalida";
import {ToastService} from "../_services/toast.service";
import {ToastType} from "../_enums/ToastType";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  faBell = faBell;
  faFolder = faFolder;
  faUsers = faUsers;
  faSignOutAlt = faSignOutAlt;
  faBuilding = faBuilding;
  faDog = faDog;
  faHome = faHome;
  faFileContract = faFileContract;
  faCar = faCar;
  faLuggageCart = faLuggageCart;
  faBomb = faBomb;
  faTicketAlt = faTicketAlt;

  modal: NgbModalRef;

  notificaciones: BuzonSalida[] = [];
  notificacion: BuzonSalida;

  @ViewChild('leerNotificacionModal') leerNotificacionModal;

  constructor(private notificacionesService: NotificacionesService, private toastService: ToastService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.notificacionesService.obtenerNotificaciones().subscribe((data: BuzonSalida[]) => {
      this.notificaciones = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las notificaciones. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  leerNotificacion(uuid) {
    this.notificacionesService.leerNotificacion(uuid).subscribe((data: BuzonSalida) => {
      this.notificacion = data;
      this.modal = this.modalService.open(this.leerNotificacionModal, {size: "xl", backdrop: "static"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la notificacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
