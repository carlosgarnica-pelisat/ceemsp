import {Component, OnInit, ViewChild} from '@angular/core';
import {VisitaService} from "../../_services/visita.service";
import {DashboardService} from "../../_services/dashboard.service";
import Dashboard from "../../_models/Dashboard";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";
import {faCalendar, faChevronDown, faTicketAlt} from "@fortawesome/free-solid-svg-icons";
import Visita from "../../_models/Visita";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import Usuario from "../../_models/Usuario";
import {UsuariosService} from "../../_services/usuarios.service";
import ConteoMensual from "../../_models/ConteoMensual";
import {Router} from "@angular/router";
import InformeMensual from "../../_models/InformeMensual";
import {ReporteoService} from "../../_services/reporteo.service";
import Empresa from "../../_models/Empresa";
import * as XLSX from "xlsx";

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
  fecha = new Date();
  fechaDeHoy = new Date().toISOString()?.split('T')[0];
  usuarioActual: Usuario;
  conteoMensual: ConteoMensual;
  meses = new Array("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
  fechaSeleccionada = '';
  fechaSeleccionadaEmpresasConInformes = '';
  fechaSeleccionadaEmpresasSinInformes = '';

  mesSinInformes = this.fecha.getMonth();
  anoSinInformes = this.fechaDeHoy.split("-")[0];
  mesConInformes = this.fecha.getMonth();
  anoConInformes = this.fechaDeHoy.split("-")[0];
  mesMovimientos = this.fecha.getMonth();
  anoMovimientos = this.fechaDeHoy.split("-")[0];
  tabActualMovimientos = 'PERSONAL';
  movimientosEmpresa: InformeMensual[];
  buscandoEmpresasSinInformes: boolean = false;

  @ViewChild('chart', {static: false}) chart;
  @ViewChild('visualizarApoderadosVencidosModal') visualizarApoderadosVencidosModal;
  @ViewChild('visualizarAcuerdosVencidosModal') visualizarAcuerdosVencidosModal;
  @ViewChild('visualizarLicenciasParticularesModal') visualizarLicenciasParticularesModal;
  @ViewChild('visualizarLicenciasFederalesModal') visualizarLicenciasFederalesModal;
  @ViewChild('visualizarMisIncidenciasModal') visualizarMisIncidenciasModal;
  @ViewChild('visualizarIncidenciasAbiertasModal') visualizarIncidenciasAbiertasModal;
  @ViewChild('seleccionarFechaMovimientosModal') seleccionarFechaMovimientosModal;
  @ViewChild('seleccionarFechaEmpresasConInformesModal') seleccionarFechaEmpresasConInformesModal;
  @ViewChild('seleccionarFechaEmpresasSinInformesModal') seleccionarFechaEmpresasSinInformesModal;
  @ViewChild('verMovimientosModal') verMovimientosModal;

  constructor(private dashboardService: DashboardService, private visitaService: VisitaService,
              private toastService: ToastService, private modalService: NgbModal,
              private usuarioService: UsuariosService, private router: Router, private r: ReporteoService) { }

  ngOnInit(): void {
    let currentDate = new Date();

    let currentMonth = currentDate.getMonth();
    let currentYear = currentDate.getFullYear();

    let prevMonth = currentMonth - 1;
    let previousYear = currentYear;

    if (prevMonth < 0) {
      prevMonth = 11;
      previousYear -= 1;
    }

    let fechaInicio = new Date(previousYear, prevMonth, 1).toISOString().split('T')[0];
    let fechaFin = new Date(previousYear, prevMonth + 1, 0).toISOString().split('T')[0];

    this.dashboardService.obtenerResumenMovimientosMes(fechaInicio, fechaFin).subscribe((data: ConteoMensual) => {
      this.conteoMensual = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los movimientos. ${error}`,
        ToastType.ERROR
      );
    })

    this.dashboardService.obtenerMovimientosEmpresa(fechaInicio, fechaFin).subscribe((data: InformeMensual[]) => {
      this.movimientosEmpresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener los movimientos de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

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

    if (tabla === 'SIN_INFORMES_MENSUALES') {

    } else if (tabla === 'CON_INFORMES_MENSUALES') {

    }
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

  seleccionarFechaMovimientos() {
    this.modal = this.modalService.open(this.seleccionarFechaMovimientosModal, {size: "lg", backdrop: "static"})
  }

  confirmarFecha() {
    let lastDayOfMonth = this.calcularUltimoDiaMes(this.mesMovimientos)

    this.dashboardService.obtenerMovimientosEmpresa(`${this.anoMovimientos}-${this.mesMovimientos}-01`, `${this.anoMovimientos}-${this.mesMovimientos}-${lastDayOfMonth}`).subscribe((data: InformeMensual[]) => {
      this.movimientosEmpresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener los movimientos de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })


    this.dashboardService.obtenerResumenMovimientosMes(`${this.anoMovimientos}-${this.mesMovimientos}-01`, `${this.anoMovimientos}-${this.mesMovimientos}-${lastDayOfMonth}`).subscribe((data: ConteoMensual) => {
      this.conteoMensual = data;
      this.modal.close();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los movimientos del mes. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  calcularUltimoDiaMes(mes) {
    if (mes === 1 || mes === 3 || mes === 5 || mes === 7 || mes === 8 || mes === 10 || mes === 12) {
      return 31;
    }
    else if (mes === 4 || mes === 6 || mes === 9 || mes === 11) {
      return 30;
    } else {
      return 28;
    }
  }

  verIncidencia(uuidEmpresa: string, uuid: string) {
    this.modal.close();
    let nextRoute = `/home/empresas/${uuidEmpresa}/incidencias`

    this.router.navigate([nextRoute], {queryParams: {uuid: uuid}})
  }

  mostrarMovimientosModal(tab: string) {
    this.tabActualMovimientos = tab;

    this.modal = this.modalService.open(this.verMovimientosModal, {size: "xl", backdrop: "static"})
  }

  cambiarPestana(tab: string) {
    this.tabActualMovimientos = tab;
  }

  cambiarMesEmpresasSinInformes() {
    this.modal = this.modalService.open(this.seleccionarFechaEmpresasSinInformesModal, {size: "lg", backdrop: "static"})
  }

  cambiarMesEmpresasConInformes() {
    this.modal = this.modalService.open(this.seleccionarFechaEmpresasConInformesModal, {size: "lg", backdrop: "static"})
  }

  confirmarFechaEmpresasConInformes() {

    let lastDayOfMonth = this.calcularUltimoDiaMes(this.mesConInformes)

    this.dashboardService.obtenerEmpresasConInformesMensuales(`${this.anoConInformes}-${this.mesConInformes}-01`, `${this.anoConInformes}-${this.mesConInformes}-${lastDayOfMonth}`).subscribe((data: Empresa[]) => {
      this.dashboardData.empresasConInformeMensual = data;
      this.modal.close();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar las empresas con informes mensuales. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarFechaEmpresasSinInformes() {
    if(this.mesSinInformes === undefined || this.anoSinInformes === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha seleccionado mes y anio`,
        ToastType.WARNING
      );
      return;
    }

    let lastDayOfMonth = this.calcularUltimoDiaMes(this.mesSinInformes)

    this.dashboardService.obtenerEmpresasSinInformesMensuales(`${this.anoSinInformes}-${this.mesSinInformes}-01`, `${this.anoSinInformes}-${this.mesSinInformes}-${lastDayOfMonth}`).subscribe((data: Empresa[]) => {
      this.buscandoEmpresasSinInformes = true;
      this.dashboardData.empresasSinInformeMensual = data;
      this.modal.close();
    }, (error) => {
      this.buscandoEmpresasSinInformes = false;
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar las empresas sin informes mensuales. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  excelEmpresasSinInformes() {
    let empresasSinInformes = document.getElementById('empresas-sin-informes');

    const wb: XLSX.WorkBook = XLSX.utils.book_new();

    if (empresasSinInformes !== null) {
      const personalActivosTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(empresasSinInformes);
      XLSX.utils.book_append_sheet(wb, personalActivosTableWorkSheet, 'EMPRESAS SIN INFORMES');
    }

    XLSX.writeFile(wb, `empresas-sin-informes-${this.fechaSeleccionadaEmpresasSinInformes}.xls`);
  }

  excelEmpresasConInformes() {
    let empresasConInformes = document.getElementById('empresas-con-informes');

    const wb: XLSX.WorkBook = XLSX.utils.book_new();

    if (empresasConInformes !== null) {
      const personalActivosTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(empresasConInformes);
      XLSX.utils.book_append_sheet(wb, personalActivosTableWorkSheet, 'EMPRESAS SIN INFORMES');
    }

    XLSX.writeFile(wb, `empresas-con-informes-${this.fechaSeleccionadaEmpresasConInformes}.xls`);
  }

  descargarMovimientosEmpresas() {
    let personalMovimientos = document.getElementById('personal-movimientos');
    let vehiculoMovimientos = document.getElementById('vehiculos-movimientos');
    let clientesMovimientos = document.getElementById('clientes-movimientos');

    const wb: XLSX.WorkBook = XLSX.utils.book_new();

    if (personalMovimientos !== null) {
      const personalActivosTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(personalMovimientos);
      XLSX.utils.book_append_sheet(wb, personalActivosTableWorkSheet, 'PERSONAL ACTIVO');
    }

    if (vehiculoMovimientos !== null) {
      const personalAltasTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(vehiculoMovimientos);
      XLSX.utils.book_append_sheet(wb, personalAltasTableWorkSheet, 'PERSONAL ALTAS');
    }

    if (clientesMovimientos !== null) {
      const personalBajasTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(clientesMovimientos);
      XLSX.utils.book_append_sheet(wb, personalBajasTableWorkSheet, 'PERSONAL BAJAS');
    }

    XLSX.writeFile(wb, `movimientos-empresa-${this.mesMovimientos}-${this.anoMovimientos}.xls`);
  }

}
