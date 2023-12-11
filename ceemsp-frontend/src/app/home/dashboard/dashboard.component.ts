import {Component, OnInit, ViewChild} from '@angular/core';
import {VisitaService} from "../../_services/visita.service";
import {DashboardService} from "../../_services/dashboard.service";
import Dashboard from "../../_models/Dashboard";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";
import {faTicketAlt, faCalendar, faChevronDown} from "@fortawesome/free-solid-svg-icons";
import Visita from "../../_models/Visita";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  faTicketAlt = faTicketAlt;
  faCalendar = faCalendar;
  faChevronDown = faChevronDown;

  dashboardData: Dashboard;
  tablaActual: string = "PROXIMAS_VISITAS";

  data = []
  proximasVisitas: Visita[] = [];

  columnNames = ['Tipo', 'Porcentaje']
  type = 'PieChart';

  width = 450;
  height = 320;

  modal: NgbModalRef;
  fechaDeHoy = new Date().toISOString().split('T')[0];

  @ViewChild('chart', {static: false}) chart;
  @ViewChild('visualizarApoderadosVencidosModal') visualizarApoderadosVencidosModal;
  @ViewChild('visualizarAcuerdosVencidosModal') visualizarAcuerdosVencidosModal;
  @ViewChild('visualizarLicenciasParticularesModal') visualizarLicenciasParticularesModal;
  @ViewChild('visualizarLicenciasFederalesModal') visualizarLicenciasFederalesModal;
  @ViewChild('visualizarMisIncidenciasModal') visualizarMisIncidenciasModal;
  @ViewChild('visualizarIncidenciasAbiertasModal') visualizarIncidenciasAbiertasModal;

  constructor(private dashboardService: DashboardService, private visitaService: VisitaService,
              private toastService: ToastService, private modalService: NgbModal) { }

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

  seleccionarTabla(tabla) {
    this.tablaActual = tabla;
  }

  mostrarApoderadosProximosModal() {
    this.modal = this.modalService.open(this.visualizarApoderadosVencidosModal, {size: "xl", backdrop: "static"})
  }

  mostrarAcuerdosProximosModal() {
    this.modal = this.modalService.open(this.visualizarAcuerdosVencidosModal, {size: "xl", backdrop: "static"})
  }

  mostrarLicenciasColectivasProximasModal() {
    this.modal = this.modalService.open(this.visualizarLicenciasParticularesModal, {size: "xl", backdrop: "static"})
  }

  mostrarLicenciasFederalesProximasModal() {
    this.modal = this.modalService.open(this.visualizarLicenciasFederalesModal, {size: "xl", backdrop: "static"})
  }

  mostrarMisIncidenciasModal() {
    this.modal = this.modalService.open(this.visualizarMisIncidenciasModal, {size: "xl", backdrop: "static"})
  }

  mostrarIncidenciasAbiertasModal() {
    this.modal = this.modalService.open(this.visualizarIncidenciasAbiertasModal, {size: "xl", backdrop: "static"})
  }

  fechaVencida(fechaFin) {
    let fechaParseada = Date.parse(fechaFin)
    let fechaHoyParseada = Date.parse(this.fechaDeHoy)
    if(fechaHoyParseada < fechaParseada)
      return "NO";
    return "SI";
  }

}
