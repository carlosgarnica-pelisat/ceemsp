import {Component, OnInit} from '@angular/core';
import {faBell, faFolder, faSignOutAlt, faChevronDown, faHome, faIndustry, faColumns, faComments,
faCalendar, faClipboardList, faCog, faDoorClosed, faPlus, faList, faDog, faBomb, faCarSide, faHandsHelping,
faUsers, faLuggageCart, faUser} from "@fortawesome/free-solid-svg-icons";
import {AuthenticationService} from "../_services/authentication.service";
import {Router} from "@angular/router";
import Usuario from "../_models/Usuario";
import {UsuariosService} from "../_services/usuarios.service";
import {ToastService} from "../_services/toast.service";
import {ToastType} from "../_enums/ToastType";


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

  usuarioActual: Usuario;

  constructor(private authenticationService: AuthenticationService, private router: Router, private usuarioService: UsuariosService,
              private toastService: ToastService) { }

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
  }

  logout() {
    this.authenticationService.doLogout()
      .subscribe(() => {
        this.router.navigate(['/login']);
      });
  }

}
