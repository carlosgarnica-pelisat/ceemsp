import {Component, OnInit} from '@angular/core';
import {faCalendar, faChevronDown, faTicketAlt, faHandshake, faUsers} from "@fortawesome/free-solid-svg-icons";
import Dashboard from "../../_models/Dashboard";
import {ToastService} from "../../_services/toast.service";
import {DashboardService} from "../../_services/dashboard.service";
import {ToastType} from "../../_enums/ToastType";
import Usuario from "../../_models/Usuario";
import {UsuariosService} from "../../_services/usuarios.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  faTicketAlt = faTicketAlt;
  faCalendar = faCalendar;
  faChevronDown = faChevronDown;
  faHandshake = faHandshake;
  faUsers = faUsers;

  usuarioActual: Usuario = undefined;

  dashboardData: Dashboard;
  tablaActual: string = "";

  constructor(private dashboardService: DashboardService, private toastService: ToastService, private usuarioService: UsuariosService) { }

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

    this.dashboardService.obtenerDatosDashboard().subscribe((data: Dashboard) => {
      this.dashboardData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los datos del dashboard. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
