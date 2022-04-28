import {Component, OnInit, ViewChild} from '@angular/core';
import {VisitaService} from "../../_services/visita.service";
import {DashboardService} from "../../_services/dashboard.service";
import Dashboard from "../../_models/Dashboard";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";
import {faTicketAlt} from "@fortawesome/free-solid-svg-icons";
import Visita from "../../_models/Visita";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  faTicketAlt = faTicketAlt;

  dashboardData: Dashboard;

  data = []
  proximasVisitas: Visita[] = [];

  columnNames = ['Tipo', 'Porcentaje']
  type = 'PieChart';

  width = 450;
  height = 320;

  @ViewChild('chart', {static: false}) chart;

  constructor(private dashboardService: DashboardService, private visitaService: VisitaService,
              private toastService: ToastService) { }

  ngOnInit(): void {
    this.dashboardService.obtenerDatosDashboard().subscribe((data: Dashboard) => {
      this.dashboardData = data;

      // Building data
      this.data = [
        ['Tramite Federal', data.empresasRegistroFederal],
        ['Tramite Estatal',  data.empresasAutorizacionEstatal],
        ['Servicios Propios', data.empresasServiciosPropios],
        ['Aut. Provisional', data.empresasAutorizacionProvisional]
      ];


    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los datos del dashboard. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
