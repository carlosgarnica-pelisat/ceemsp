import {Component, OnInit, ViewChild} from '@angular/core';
import Empresa from "../../../_models/Empresa";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import * as datefns from "date-fns";
import {EmpresaInformesMensualesService} from "../../../_services/empresa-informes-mensuales.service";
import {UsuariosService} from "../../../_services/usuarios.service";
import Usuario from "../../../_models/Usuario";
import {ToastType} from "../../../_enums/ToastType";
import InformeMensual from "../../../_models/InformeMensual";
import {EmpresaService} from "../../../_services/empresa.service";
import Persona from "../../../_models/Persona";
import Cliente from "../../../_models/Cliente";
import Vehiculo from "../../../_models/Vehiculo";

@Component({
  selector: 'app-empresa-reportes-mensuales',
  templateUrl: './empresa-reportes-mensuales.component.html',
  styleUrls: ['./empresa-reportes-mensuales.component.css']
})
export class EmpresaReportesMensualesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  pestanaActualMovimientosPersonal: string = "ALTAS";
  pestanaActualMovimientosClientes: string = "ALTAS";
  pestanaActualMovimientosVehiculos: string = "ALTAS";

  personalAltas: Persona[] = [];
  personalBajas: Persona[] = [];
  clienteAltas: Cliente[] = [];
  clienteBajas: Cliente[] = [];
  vehiculosAltas: Vehiculo[] = [];
  vehiculosBajas: Vehiculo[] = [];
  frameworkComponents: any;
  uuid: string;
  empresa: Empresa;
  rowData = [];
  usuarioActual: Usuario;
  reporte: InformeMensual;
  fechaReporte: Date;
  fechaReporteLocal: string;
  mes: string;
  ano: string;
  modal: NgbModalRef;
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Mes', sortable: true, filter: true, valueGetter: function (params) {
        let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
        return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'}).split(" ")[3].toUpperCase()
      }},
    {headerName: 'Año', sortable: true, filter: true, valueGetter: function (params) {
        let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
        return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'}).split(" ")[5]
      }},
    {headerName: 'Fecha creacion', field: 'fechaCreacion', sortable: true, filter: true },
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];

  @ViewChild('mostrarDetallesReporteModal') mostrarDetallesReporteModal;
  @ViewChild('eliminarReporteModal') mostrarEliminarModal;
  @ViewChild('mostrarPersonalMovimientosReporteModal') mostrarPersonalMovimientosReporteModal;
  @ViewChild('mostrarClienteMovimientosReporteModal') mostrarClienteMovimientosReporteModal;
  @ViewChild('mostrarVehiculosMovimientosReporteModal') mostrarVehiculosMovimientosReporteModal;

  constructor(private route: ActivatedRoute, private empresaInformesMensualesService: EmpresaInformesMensualesService,
              private toastService: ToastService, private modalService: NgbModal,
              private usuarioService: UsuariosService, private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la empresa. Motivo: ${error}`,
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

    this.empresaInformesMensualesService.obtenerInformesMensualesPorEmpresa(this.uuid).subscribe((data: InformeMensual[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los informes mensuales. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  mostrarModalDetalles(rowData) {
    let reporteUuid = rowData.uuid;

    this.empresaInformesMensualesService.obtenerInformeMensualPorUuid(this.uuid, reporteUuid).subscribe((data: InformeMensual) => {
      this.reporte = data;
      this.fechaReporte = datefns.subMonths(new Date(this.reporte.fechaCreacion), 1);
      this.fechaReporteLocal = this.fechaReporte.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'});
      let fechaActualSplice = this.fechaReporteLocal.split(" ");
      this.mes = fechaActualSplice[3].toUpperCase()
      this.ano = fechaActualSplice[5]
      this.modal = this.modalService.open(this.mostrarDetallesReporteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarEliminarReporteModal() {
    this.modal = this.modalService.open(this.mostrarEliminarModal, {size: "lg", backdrop: "static"})
  }

  confirmarEliminar() {
    this.empresaService.eliminarInformeMensualPorUuid(this.uuid, this.reporte.uuid).subscribe((reporte) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el reporte con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el informe. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalMovimientosPersonal() {
    this.modal = this.modalService.open(this.mostrarPersonalMovimientosReporteModal, {size: "xl", backdrop: "static"})
    this.empresaInformesMensualesService.obtenerMovimientosPersonalAltas(this.uuid, this.reporte?.uuid).subscribe((data: Persona[]) => {
      this.personalAltas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosPersonalBajas(this.uuid, this.reporte?.uuid).subscribe((data: Persona[]) => {
      this.personalBajas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalMovimientosClientes() {
    this.modal = this.modalService.open(this.mostrarClienteMovimientosReporteModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalMovimientosVehiculos() {
    this.modal = this.modalService.open(this.mostrarVehiculosMovimientosReporteModal, {size: "xl", backdrop: "static"})
  }

  cambiarPestanaMovimientosPersona(pestana: string) {
    this.pestanaActualMovimientosPersonal = pestana;
  }

  cambiarPestanaMovimientosCliente(pestana: string) {
    this.pestanaActualMovimientosClientes = pestana;
  }

  cambiarPestanaMovimientosVehiculos(pestana: string) {
    this.pestanaActualMovimientosVehiculos = pestana;
  }
}
