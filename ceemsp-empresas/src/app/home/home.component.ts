import {Component, OnInit, ViewChild} from '@angular/core';
import {
  faArrowAltCircleLeft,
  faBell,
  faBomb,
  faBriefcase,
  faBuilding,
  faCar,
  faDog,
  faDoorClosed,
  faEnvelope,
  faEnvelopeOpen,
  faFileContract,
  faFolder,
  faHandshake,
  faHome,
  faInfoCircle,
  faLuggageCart,
  faSignOutAlt,
  faTicketAlt,
  faUser,
  faUsers,
  faVestPatches,
  faSearch
} from "@fortawesome/free-solid-svg-icons";
import {NotificacionesService} from "../_services/notificaciones.service";
import BuzonSalida from "../_models/BuzonSalida";
import {ToastService} from "../_services/toast.service";
import {ToastType} from "../_enums/ToastType";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {SanitizeHtmlPipe} from "../_pipes/sanitize-html.pipe";
import Usuario from "../_models/Usuario";
import {UsuariosService} from "../_services/usuarios.service";
import {AuthenticationService} from "../_services/authentication.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import ExisteUsuario from "../_models/ExisteUsuario";
import {ValidacionService} from "../_services/validacion.service";
import ActualizarContrasena from "../_models/ActualizarContrasena";
import * as sha256 from "js-sha256";
import {NotificacionesEmpresaService} from "../_services/notificaciones-empresa.service";
import NotificacionEmpresa from "../_models/NotificacionEmpresa";
import RealizarBusqueda from "../_models/RealizarBusqueda";
import ResultadosBusqueda from "../_models/ResultadosBusqueda";
import {BusquedaService} from "../_services/busqueda.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  mostrarContrasena: boolean = false;

  logo: any;
  faBell = faBell;
  faFolder = faFolder;
  faUsers = faUsers;
  faUser = faUser;
  faSignOutAlt = faSignOutAlt;
  faBuilding = faBuilding;
  faDog = faDog;
  faHome = faHome;
  faFileContract = faFileContract;
  faArrowAltCircleLeft = faArrowAltCircleLeft;
  faCar = faCar;
  faLuggageCart = faLuggageCart;
  faBomb = faBomb;
  faTicketAlt = faTicketAlt;
  faHandshake = faHandshake;
  faVestPatches = faVestPatches;
  faBriefcase = faBriefcase;
  faInfoCircle = faInfoCircle;
  faDoorClosed = faDoorClosed;
  faEnvelope = faEnvelope;
  faEnvelopeOpen = faEnvelopeOpen;
  faSearch = faSearch;
  modalAbierto: boolean = false;

  textoNotificacion;

  modal: NgbModalRef;
  usuarioActual: Usuario;

  notificaciones: BuzonSalida[] = [];
  notificacion: BuzonSalida;
  notificacionesSinLeer: number;
  notificacionesEmpresaSinLeer: number;
  reiniciarContrasenaForm: FormGroup;
  existeUsuario: ExisteUsuario;
  notificacionesEmpresa: NotificacionEmpresa[];
  filtroBusqueda: string;
  terminoABuscar: string;

  @ViewChild('leerNotificacionModal') leerNotificacionModal;
  @ViewChild('verNotificacionesModal') verNotificacionesModal;
  @ViewChild('verNotificacionEmpresaModal') verNotificacionEmpresaModal;
  @ViewChild('cambioContrasenaModal') cambioContrasenaModal;

  constructor(private notificacionesService: NotificacionesService, private toastService: ToastService, private usuarioService: UsuariosService,
              private modalService: NgbModal, private sanitizeHtml: SanitizeHtmlPipe, private authenticationService: AuthenticationService,
              private router: Router, private formBuilder: FormBuilder, private validacionService: ValidacionService,
              private notificacionEmpresaService: NotificacionesEmpresaService, private busquedaService: BusquedaService) { }

  ngOnInit(): void {
    this.reiniciarContrasenaForm = this.formBuilder.group({
      actualPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15)]],
      verificarPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15)]]
    })

    this.notificacionesService.obtenerNotificaciones().subscribe((data: BuzonSalida[]) => {
      this.notificaciones = data;
      let hayNotificacionesSinLeer = this.notificaciones.filter(x => x.leido === false);
      this.notificacionesSinLeer = hayNotificacionesSinLeer.length;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las notificaciones. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.notificacionEmpresaService.obtenerNotificacionesArgos().subscribe((data: NotificacionEmpresa[]) => {
      this.notificacionesEmpresa = data;
      let hayNotificacionesSinLeer = this.notificacionesEmpresa.filter(x => x.leido === false);
      this.notificacionesEmpresaSinLeer = hayNotificacionesSinLeer.length;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las notificaciones. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;
      this.usuarioService.obtenerLogoEmpresaActual().subscribe((data: Blob) => {
        this.convertirImagenLogo(data);
      }, (error) => {
        this.logo = undefined;
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalCambioContrasena() {
    this.modal = this.modalService.open(this.cambioContrasenaModal, {size: "xl", backdrop: "static"})
  }

  cerrarModalCambioContrasena() {
    this.modal.close();
  }

  leerNotificacion(uuid) {
    this.notificacionesService.leerNotificacion(uuid).subscribe((data: BuzonSalida) => {
      this.notificacion = data;
      this.textoNotificacion = this.sanitizeHtml.transform(this.notificacion.mensaje);
      this.modal = this.modalService.open(this.leerNotificacionModal, {size: "xl", backdrop: "static"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la notificacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  leerNotificacionEmpresa(uuid: string) {
    this.notificacionEmpresaService.leerNotificacion(uuid).subscribe((data: NotificacionEmpresa) => {
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

  logout() {
    this.authenticationService.doLogout()
      .subscribe(() => {
        this.router.navigate(['/login']);
      });
  }

  mostrarModalTodasNotificaciones() {
    this.modal = this.modalService.open(this.verNotificacionesModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalTodasNotificacionesEmpresa() {
    this.modalAbierto = true;
    this.modal = this.modalService.open(this.verNotificacionEmpresaModal, {size: "xl", backdrop: "static", keyboard: false})
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

  conmutarMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  autogenerarContrasena() {
    this.reiniciarContrasenaForm.patchValue({
      password: this.hacerFalsoUuid(12)
    })
  }

  consultarEmail(event) {
    let existeUsuario: ExisteUsuario = new ExisteUsuario();
    existeUsuario.email = event.value;

    /*this.validacionService.validarUsuario(existeUsuario).subscribe((existeUsuario: ExisteUsuario) => {
      this.existeUsuario = existeUsuario;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido consultar la existencia de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })*/
  }

  guardarUsuario(form) {
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

    this.usuarioService.actualizarContrasena(actualizarContrasena).subscribe((data) => {
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

  cerrarModalNotificaciones() {
    this.modalAbierto = false;
    this.modal.close();
  }

  cambiarFiltroBusqueda(nuevoFiltro: string) {
    this.filtroBusqueda = nuevoFiltro;
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

}
