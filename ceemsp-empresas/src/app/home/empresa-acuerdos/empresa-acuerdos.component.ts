import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {EmpresaAcuerdosService} from "../../_services/empresa-acuerdos.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";
import Acuerdo from "../../_models/Acuerdo";
import {
  BotonEmpresaDomiciliosComponent
} from "../../_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component";
import {
  BotonEmpresaAcuerdosComponent
} from "../../_components/botones/boton-empresa-acuerdos/boton-empresa-acuerdos.component";

@Component({
  selector: 'app-empresa-acuerdos',
  templateUrl: './empresa-acuerdos.component.html',
  styleUrls: ['./empresa-acuerdos.component.css']
})
export class EmpresaAcuerdosComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  modal: NgbModalRef;
  pdfActual;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Tipo', sortable: true, filter: true,resizable: true, valueGetter: function (params) {
        switch (params.data.tipo) {
          case 'AUTORIZACION_ESTATAL':
            return 'Autorizacion Estatal';
          case 'AUTORIZACION_PROVISIONAL':
            return 'Autorizacion Provisional';
          case 'REGISTRO_FEDERAL':
            return 'Registro Federal';
          case 'REGISTRO_SERVICIOS_PROPIOS':
            return 'Registro de Servicios Propios';
          case 'REFRENDO':
            return 'Refrendo';
          case 'REVOCACION':
            return 'Revocacion';
          case 'PERDIDA_EFICACIA':
            return 'Perdida de eficacia';
          case 'SUSPENSION':
            return 'Suspension';
          case 'CLAUSURA':
            return 'Clausura';
          case 'MULTA':
            return 'Multa';
          case 'AMONESTACION':
            return 'Amonestacion'
          case 'MANDATO_JUDICIAL':
            return 'Reactivación por mandato judicial'
        }
      }},
    {headerName: 'Fecha', field: 'fecha', sortable: true, resizable: true, filter: true },
    {headerName: 'Fecha de inicio', field: 'fechaInicio', sortable: true, resizable: true, filter: true },
    {headerName: 'Fecha de fin', field: 'fechaFin', sortable: true, resizable: true, filter: true },
    {headerName: 'Observaciones', field: 'observaciones', sortable: true, resizable: true, filter: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];

  frameworkComponents: any;

  rowData = [];
  acuerdo: Acuerdo;
  pdfBlob;

  @ViewChild('verDetallesAcuerdoModal') verDetallesAcuerdoModal;
  @ViewChild('visualizarAcuerdoModal') visualizarAcuerdoModal;

  constructor(private formBuilder: FormBuilder, private empresaAcuerdoService: EmpresaAcuerdosService, private modalService: NgbModal,
              private toastService: ToastService) { }

  ngOnInit(): void {

    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaAcuerdosComponent
    }

    this.empresaAcuerdoService.obtenerAcuerdosPorEmpresa().subscribe((data: Acuerdo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los acuerdos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  mostrarModalDetalles(rowData) {
    let uuid = rowData.uuid;

    this.empresaAcuerdoService.obtenerAcuerdoPorUuid(uuid).subscribe((data: Acuerdo) => {
      this.modal = this.modalService.open(this.verDetallesAcuerdoModal, {size: 'xl', backdrop: 'static'})
      this.acuerdo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion del acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  mostrarAcuerdo() {
    this.modal = this.modalService.open(this.visualizarAcuerdoModal, {size: 'xl', backdrop: 'static'});

    this.empresaAcuerdoService.descargarArchivoAcuerdo(this.acuerdo.uuid).subscribe((data: Blob) => {
      this.pdfBlob = data;
      this.convertirPdf(data);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  convertirPdf(pdf: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  descargarAcuerdoPdf() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "acuerdo.pdf";
    link.click();
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
}
