import {Component, OnInit, ViewChild} from '@angular/core';
import {
  faBell,
  faBomb,
  faCalendar,
  faCarSide,
  faChevronDown,
  faClipboardList,
  faCog,
  faColumns,
  faComments,
  faDog,
  faDoorClosed,
  faFolder,
  faHandsHelping,
  faHome,
  faIndustry,
  faList,
  faLuggageCart,
  faPlus,
  faSearch,
  faSignOutAlt,
  faUpload,
  faUsers,
  faUserTie
} from "@fortawesome/free-solid-svg-icons";
import {AuthenticationService} from "../_services/authentication.service";
import {Router} from "@angular/router";
import Usuario from "../_models/Usuario";
import {UsuariosService} from "../_services/usuarios.service";
import {ToastService} from "../_services/toast.service";
import {ToastType} from "../_enums/ToastType";
import {BusquedaService} from "../_services/busqueda.service";
import RealizarBusqueda from "../_models/RealizarBusqueda";
import ResultadosBusqueda from "../_models/ResultadosBusqueda";
import {NotificacionesArgosService} from "../_services/notificaciones-argos.service";
import NotificacionArgos from "../_models/NotificacionArgos";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Table} from "primeng/table";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  faBell = faBell;
  faFolder = faFolder;
  faSignOutAlt = faSignOutAlt;
  faChevronDown = faChevronDown;
  faHome = faHome;
  faIndustry = faIndustry;
  faColumns = faColumns;
  faComments = faComments;
  faCalendar = faCalendar;
  faClipboardList = faClipboardList;
  faCog = faCog;
  faDoorClosed = faDoorClosed;
  faPlus = faPlus;
  faList = faList;
  faDog = faDog;
  faBomb = faBomb;
  faCarSide = faCarSide;
  faLuggageCart = faLuggageCart;
  faUsers = faUsers;
  faHandsHelping = faHandsHelping;
  faUser = faUsers;
  faUserTie = faUserTie;
  faSearch = faSearch;
  faUpload = faUpload;
  mostrandoFormularioBusqueda: boolean = false;

  usuarioActual: Usuario;
  filtroBusqueda: string;
  terminoABuscar: string;
  notificacionesSinLeer: number;
  modalAbierto: boolean = false;

  notificacionesArgos: NotificacionArgos[];
  modal: NgbModalRef;
  @ViewChild('verNotificacionesModal') verNotificacionesModal;

  constructor(private authenticationService: AuthenticationService, private router: Router, private usuarioService: UsuariosService,
              private toastService: ToastService, private busquedaService: BusquedaService, private notificacionesArgosService: NotificacionesArgosService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.notificacionesArgosService.obtenerNotificacionesArgos().subscribe((data: NotificacionArgos[]) => {
      this.notificacionesArgos = data;
      let hayNotificacionesSinLeer = this.notificacionesArgos.filter(x => x.leido === false);
      this.notificacionesSinLeer = hayNotificacionesSinLeer.length;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las notificaciones. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  logout() {
    this.authenticationService.doLogout()
      .subscribe(() => {
        this.router.navigate(['/login']);
      });
  }

  mostrarFormularioBusqueda() {
    this.mostrandoFormularioBusqueda = !this.mostrandoFormularioBusqueda;
  }

  cambiarFiltroBusqueda(nuevoFiltro: string) {
    this.filtroBusqueda = nuevoFiltro;
  }

  mostrarModalTodasNotificaciones() {
    this.modalAbierto = true;
    this.modal = this.modalService.open(this.verNotificacionesModal, {size: "xl", backdrop: "static", keyboard: false})
  }

  realizarBusqueda() {
    if(this.filtroBusqueda === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Previo a realizar la busqueda, necesitas seleccionar un filtro`,
        ToastType.WARNING
      )
      return;
    }

    if(this.terminoABuscar === "" || this.terminoABuscar === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Previo a realizar la busqueda, necesitar ingresar un texto`,
        ToastType.WARNING
      );
      return;
    }

    let realizarBusqueda: RealizarBusqueda = new RealizarBusqueda();
    realizarBusqueda.filtro = this.filtroBusqueda;
    realizarBusqueda.palabraABuscar = this.terminoABuscar;

    this.busquedaService.realizarBusqueda(realizarBusqueda).subscribe((data: ResultadosBusqueda) => {
      this.busquedaService.guardarResultadosBusqueda(data);
      this.router.routeReuseStrategy.shouldReuseRoute = () => false;
      this.router.onSameUrlNavigation = 'reload';
      this.router.navigateByUrl('/home/busqueda', {state: data})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido realizar la busqueda. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  clear(table: Table) {
    table.clear();
  }

  leerNotificacion(uuid: string) {
    this.notificacionesArgosService.leerNotificacion(uuid).subscribe((data: NotificacionArgos) => {
      if(this.modalAbierto) this.cerrarModalNotificaciones();
      this.router.navigateByUrl(data?.ubicacion)
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido leer la notificacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cerrarModalNotificaciones() {
    this.modalAbierto = false;
    this.modal.close();
  }

}
