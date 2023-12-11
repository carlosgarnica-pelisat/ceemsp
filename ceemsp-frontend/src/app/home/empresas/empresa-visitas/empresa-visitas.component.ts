import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastType} from "../../../_enums/ToastType";
import {faCheck, faDownload} from "@fortawesome/free-solid-svg-icons";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import Empresa from "../../../_models/Empresa";
import {ActivatedRoute} from "@angular/router";
import {ValidacionService} from "../../../_services/validacion.service";
import Visita from "../../../_models/Visita";
import {
  BotonEmpresaVisitasComponent
} from "../../../_components/botones/boton-empresa-visitas/boton-empresa-visitas.component";
import {ReporteEmpresasService} from "../../../_services/reporte-empresas.service";

@Component({
  selector: 'app-empresa-visitas',
  templateUrl: './empresa-visitas.component.html',
  styleUrls: ['./empresa-visitas.component.css']
})
export class EmpresaVisitasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faCheck = faCheck;
  faDownload = faDownload;

  mostrandoEliminados: boolean = false;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true,  resizable: true },
    {headerName: 'Tipo', field: 'tipoVisita', sortable: true, filter: true, resizable: true },
    {headerName: 'Num. Orden', field: 'numeroOrden', sortable: true, filter: true,  resizable: true},
    {headerName: 'Nombre empresa', field: 'nombreComercial', sortable: true, filter: true,  pinned: 'left', resizable: true, width: 400, minWidth: 250, maxWidth: 450},
    {headerName: 'Domicilio', sortable: true, filter: true,  resizable: true, valueGetter: function (params) { return params.data.domicilio1 + " " + params.data.numeroExterior}},
    {headerName: 'Fecha de visita', field: 'fechaVisita', sortable: true, filter: true, resizable: true, width: 150, minWidth: 150, maxWidth: 200},
    {headerName: 'Fecha de termino', field: 'fechaTermino', sortable: true, filter: true, resizable: true, width: 150, minWidth: 150, maxWidth: 200},
    {headerName: 'Opciones', pinned: 'right', resizable: true, cellRenderer: 'empresaVisitaButtonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];

  uuid: string;
  empresa: Empresa;
  visitas: Visita[] = [];
  visita: Visita;
  modal: NgbModalRef;

  pestanaActual: string = "DETALLES";

  frameworkComponents: any;

  allColumnDefs = this.columnDefs;

  @ViewChild('mostrarDetallesVisitaModal') mostrarDetallesVisitaModal;

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private empresaService: EmpresaService, private route: ActivatedRoute,
              private reporteEmpresasService: ReporteEmpresasService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      empresaVisitaButtonRenderer: BotonEmpresaVisitasComponent
    }

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

    this.empresaService.obtenerVisitas(this.uuid).subscribe((data: Visita[]) => {
      this.visitas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
          `No se han podido descargar las visitas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalDetalles(rowData) {
    let visita = rowData.uuid;
    this.modal = this.modalService.open(this.mostrarDetallesVisitaModal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaService.obtenerVisitaEmpresaPorUuid(this.uuid, visita).subscribe((data: Visita) => {
      this.visita = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  mostrarModalInformacionCrear() {

  }

  mostrarEliminados() {

  }

  ocultarEliminados() {

  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  toggleColumn(field: string) {
    let columnDefinitionIndex = this.columnDefs.findIndex(s => s.field === field);
    if(columnDefinitionIndex === -1) {
      let columnDefinition = this.allColumnDefs.filter(s => s.field === field)[0];

      let newColumnDef = {
        headerName: columnDefinition.headerName,
        field: columnDefinition.field,
        sortable: true,
        filter: true,
        resizable: true
      };

      this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
  }

  exportGridData(format) {
    switch(format) {
      case "CSV":
        this.gridApi.exportDataAsCsv();
        break;
      case "PDF":
        this.toastService.showGenericToast(
          "Bajo desarrollo",
          "Actualmente estamos desarrollando esta funcionalidad",
          ToastType.INFO
        )
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No podemos exportar en dicho formato",
          ToastType.WARNING
        )
        break;
    }
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  generarReporteExcel() {
    this.reporteEmpresasService.generarReporteVisitas(this.uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte en excel. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }
  descargarArchivo(uuid) {
    this.empresaService.obtenerVisitaArchivoPorUuid(this.uuid, this.visita.uuid, uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "archivo";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el archivo de la visita`,
        ToastType.ERROR
      )
    })
  }

}
