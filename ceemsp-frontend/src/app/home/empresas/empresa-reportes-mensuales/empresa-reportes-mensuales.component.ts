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
import Arma from "../../../_models/Arma";
import Can from "../../../_models/Can";
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-empresa-reportes-mensuales',
  templateUrl: './empresa-reportes-mensuales.component.html',
  styleUrls: ['./empresa-reportes-mensuales.component.css']
})
export class EmpresaReportesMensualesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  pestanaActualMovimientosPersonal: string = "ACTIVOS";
  pestanaActualMovimientosClientes: string = "ACTIVOS";
  pestanaActualMovimientosVehiculos: string = "ACTIVOS";
  pestanaActualMovimientosArmasModalidad1: string = "ACTIVOS";
  pestanaActualMovimientosArmasModalidad2: string = "ACTIVOS";
  pestanaActualMovimientosArmasModalidad3: string = "ACTIVOS";
  pestanaActualMovimientosCanes: string = "ACTIVOS";

  personalAltas: Persona[] = [];
  personalBajas: Persona[] = [];
  personalActivos: Persona[] = [];
  clienteActivos: Cliente[] = [];
  clienteAltas: Cliente[] = [];
  clienteBajas: Cliente[] = [];
  vehiculosActivos: Vehiculo[] = [];
  vehiculosAltas: Vehiculo[] = [];
  vehiculosBajas: Vehiculo[] = [];
  armasModalidad1Activos: Arma[] = [];
  armasModalidad2Activos: Arma[] = [];
  armasModalidad3Activos: Arma[] = [];
  armasModalidad1Altas: Arma[] = [];
  armasModalidad2Altas: Arma[] = [];
  armasModalidad3Altas: Arma[] = [];
  armasModalidad1Bajas: Arma[] = [];
  armasModalidad2Bajas: Arma[] = [];
  armasModalidad3Bajas: Arma[] = [];
  canesActivos: Can[] = [];
  canesAltas: Can[] = [];
  canesBajas: Can[] = [];
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
  logo: any;
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Mes', sortable: true, filter: true, valueGetter: function (params) {
        let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
        return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'})?.split(" ")[3].toUpperCase()
      }},
    {headerName: 'Año', sortable: true, filter: true, valueGetter: function (params) {
        let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
        return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'})?.split(" ")[5]
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
  @ViewChild('mostrarArmasModalidad1MovimientosReporteModal') mostrarArmasModalidad1MovimientosReporteModal;
  @ViewChild('mostrarArmasModalidad2MovimientosReporteModal') mostrarArmasModalidad2MovimientosReporteModal;
  @ViewChild('mostrarArmasModalidad3MovimientosReporteModal') mostrarArmasModalidad3MovimientosReporteModal;
  @ViewChild('mostrarCanesMovimientosReporteModal') mostrarCanesMovimientosReporteModal;

  constructor(private route: ActivatedRoute, private empresaInformesMensualesService: EmpresaInformesMensualesService,
              private toastService: ToastService, private modalService: NgbModal,
              private usuarioService: UsuariosService, private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;

      this.empresaService.obtenerEmpresaLogo(this?.uuid).subscribe((data: Blob) => {
        this.convertirImagenLogo(data);
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la fotografia. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
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
      let fechaActualSplice = this.fechaReporteLocal?.split(" ");
      this.mes = fechaActualSplice[3].toUpperCase()
      this.ano = fechaActualSplice[5]

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

      this.empresaInformesMensualesService.obtenerMovimientosPersonalActivos(this.uuid, this.reporte?.uuid).subscribe((data: Persona[]) => {
        this.personalActivos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosClientesActivos(this.uuid, this.reporte?.uuid).subscribe((data: Cliente[]) => {
        this.clienteActivos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de los clientes activos . Motivo: ${error}`,
          ToastType.ERROR
        );
      })


      this.empresaInformesMensualesService.obtenerMovimientosClientesAltas(this.uuid, this.reporte?.uuid).subscribe((data: Cliente[]) => {
        this.clienteAltas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosClientesBajas(this.uuid, this.reporte?.uuid).subscribe((data: Cliente[]) => {
        this.clienteBajas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosVehiculosActivos(this.uuid, this.reporte?.uuid).subscribe((data: Vehiculo[]) => {
        this.vehiculosActivos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosVehiculosAltas(this.uuid, this.reporte?.uuid).subscribe((data: Vehiculo[]) => {
        this.vehiculosAltas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosVehiculosBajas(this.uuid, this.reporte?.uuid).subscribe((data: Vehiculo[]) => {
        this.vehiculosBajas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosCanesActivos(this.uuid, this.reporte?.uuid).subscribe((data: Can[]) => {
        this.canesActivos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosCanesAltas(this.uuid, this.reporte?.uuid).subscribe((data: Can[]) => {
        this.canesAltas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaInformesMensualesService.obtenerMovimientosCanesBajas(this.uuid, this.reporte?.uuid).subscribe((data: Can[]) => {
        this.canesBajas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

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
  }

  mostrarModalMovimientosClientes() {
    this.modal = this.modalService.open(this.mostrarClienteMovimientosReporteModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalMovimientosVehiculos() {
    this.modal = this.modalService.open(this.mostrarVehiculosMovimientosReporteModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalMovimientosArmasModalidad1() {
    this.modal = this.modalService.open(this.mostrarArmasModalidad1MovimientosReporteModal, {size: "xl", backdrop: "static"})

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad1Activas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad1Activos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las armas. ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad1Altas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad1Altas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad1Bajas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad1Bajas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalMovimientosArmasModalidad2() {
    this.modal = this.modalService.open(this.mostrarArmasModalidad2MovimientosReporteModal, {size: "xl", backdrop: "static"})

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad2Activas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad2Activos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las armas. ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad2Altas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad2Altas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad2Bajas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad2Bajas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalMovimientosArmasModalidad3() {
    this.modal = this.modalService.open(this.mostrarArmasModalidad3MovimientosReporteModal, {size: "xl", backdrop: "static"})

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad3Activas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad3Activos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener las armas. ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad3Altas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad3Altas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las altas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaInformesMensualesService.obtenerMovimientosArmasModalidad3Bajas(this.uuid, this.reporte?.uuid).subscribe((data: Arma[]) => {
      this.armasModalidad3Bajas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener los movimientos de las bajas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalMovimientosCanes() {
    this.modal = this.modalService.open(this.mostrarCanesMovimientosReporteModal, {size: "xl", backdrop: "static"})
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

  cambiarPestanaMovimientosArmasModalidad1(pestana: string) {
    this.pestanaActualMovimientosArmasModalidad1 = pestana;
  }

  cambiarPestanaMovimientosArmasModalidad2(pestana: string) {
    this.pestanaActualMovimientosArmasModalidad2 = pestana;
  }
  cambiarPestanaMovimientosArmasModalidad3(pestana: string) {
    this.pestanaActualMovimientosArmasModalidad3 = pestana;
  }

  cambiarPestanaMovimientosCanes(pestana: string) {
    this.pestanaActualMovimientosCanes = pestana;
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

  generarInformeMensualExcel() {
    let personalActivosTable = document.getElementById('personal-activos-table');
    let personalAltasTable = document.getElementById('personal-altas-table');
    let personalBajasTable = document.getElementById('personal-bajas-table');

    let vehiculosActivosTable = document.getElementById('vehiculos-activos-table');
    let vehiculosAltasTable = document.getElementById('vehiculos-altas-table');
    let vehiculosBajasTable = document.getElementById('vehiculos-bajas-table');

    let clientesActivosTable = document.getElementById('clientes-activos-table');
    let clientesAltasTable = document.getElementById('clientes-altas-table');
    let clientesBajasTable = document.getElementById('clientes-bajas-table');

    const wb: XLSX.WorkBook = XLSX.utils.book_new();

    if (personalActivosTable !== null) {
      const personalActivosTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(personalActivosTable);
      XLSX.utils.book_append_sheet(wb, personalActivosTableWorkSheet, 'PERSONAL ACTIVO');
    }

    if (personalAltasTable !== null) {
      const personalAltasTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(personalAltasTable);
      XLSX.utils.book_append_sheet(wb, personalAltasTableWorkSheet, 'PERSONAL ALTAS');
    }

    if (personalBajasTable !== null) {
      const personalBajasTableWorkSheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(personalBajasTable);
      XLSX.utils.book_append_sheet(wb, personalBajasTableWorkSheet, 'PERSONAL BAJAS');
    }

    if (vehiculosActivosTable !== null) {
      const vehiculosActivosTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(vehiculosActivosTable);
      XLSX.utils.book_append_sheet(wb, vehiculosActivosTableWorkSheet, 'VEHICULOS ACTIVOS');
    }

    if (vehiculosAltasTable !== null) {
      const vehiculosAltasTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(vehiculosAltasTable);
      XLSX.utils.book_append_sheet(wb, vehiculosAltasTableWorkSheet, 'VEHICULOS ALTAS');
    }

    if (vehiculosBajasTable !== null) {
      const vehiculosBajasTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(vehiculosBajasTable);
      XLSX.utils.book_append_sheet(wb, vehiculosBajasTableWorkSheet, 'VEHICULOS BAJAS');
    }

    if (clientesActivosTable !== null) {
      const clientesActivosTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(clientesActivosTable);
      XLSX.utils.book_append_sheet(wb, clientesActivosTableWorkSheet, 'CLIENTES ACTIVOS');
    }

    if (clientesAltasTable !== null) {
      const clientesAltasTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(clientesAltasTable);
      XLSX.utils.book_append_sheet(wb, clientesAltasTableWorkSheet, 'CLIENTES ALTAS');
    }

    if (clientesBajasTable !== null) {
      const clientesBajasTableWorkSheet: XLSX.WorkSheet =XLSX.utils.table_to_sheet(clientesBajasTable);
      XLSX.utils.book_append_sheet(wb, clientesBajasTableWorkSheet, 'CLIENTES BAJAS');
    }

    XLSX.writeFile(wb, `informe-mensual-${this.reporte?.uuid}.xls`);
  }
}
