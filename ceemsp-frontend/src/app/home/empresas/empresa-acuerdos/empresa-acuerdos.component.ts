import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import Empresa from "../../../_models/Empresa";
import Acuerdo from "../../../_models/Acuerdo";
import {
  BotonEmpresaAcuerdosComponent
} from "../../../_components/botones/boton-empresa-acuerdos/boton-empresa-acuerdos.component";
import {AuthenticationService} from "../../../_services/authentication.service";
import Usuario from "../../../_models/Usuario";
import {ReporteEmpresasService} from "../../../_services/reporte-empresas.service";

@Component({
  selector: 'app-empresa-acuerdos',
  templateUrl: './empresa-acuerdos.component.html',
  styleUrls: ['./empresa-acuerdos.component.css']
})
export class EmpresaAcuerdosComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  VALOR_UMA_PESOS: number = 96.22;

  uuid: string;
  empresa: Empresa;
  rowDataClicked = {
    uuid: undefined
  };
  tempFile;
  pdfActual;

  motivosEliminacionForm: FormGroup;

  frameworkComponents: any;
  fechaDeHoy = new Date().toISOString().split('T')[0];

  modal: NgbModalRef;
  usuarioActual: Usuario;

  nuevoAcuerdoForm: FormGroup;
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
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', resizable: true, cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];

  rowData = [];
  acuerdo: Acuerdo;
  tipoAcuerdo: string;

  multaPesos: number = 0;
  pdfBlob;

  @ViewChild('verDetallesAcuerdoModal') verDetallesAcuerdoModal;
  @ViewChild('crearAcuerdoModal') crearAcuerdoModal;
  @ViewChild('modificarAcuerdoModal') modificarAcuerdoModal;
  @ViewChild('visualizarAcuerdoModal') visualizarAcuerdoModal;
  @ViewChild('eliminarAcuerdoModal') eliminarAcuerdoModal;

  constructor(private formBuilder: FormBuilder, private empresaService: EmpresaService, private modalService: NgbModal,
              private toastService: ToastService, private route: ActivatedRoute, private authenticationService: AuthenticationService,
              private reporteEmpresaService: ReporteEmpresasService) { }

  ngOnInit(): void {
    let usuario = this.authenticationService.currentUserValue;
    this.usuarioActual = usuario.usuario;
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaAcuerdosComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la empresa por uuid. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerAcuerdos(this.uuid).subscribe((data: Acuerdo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los acuerdos de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.nuevoAcuerdoForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      fecha: ['', Validators.required],
      observaciones: [''],
      fechaInicio: [''],
      fechaFin: [''],
      multaUmas: [''],
      multaPesos: ['']
    });

    this.nuevoAcuerdoForm.controls['multaPesos'].disable()

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });
  }

  cambiarTipoAcuerdo(tipoAcuerdo) {
    this.tipoAcuerdo = tipoAcuerdo.value;

    if(this.tipoAcuerdo === 'AUTORIZACION_ESTATAL' || this.tipoAcuerdo === 'AUTORIZACION_PROVISIONAL' || this.tipoAcuerdo === 'REGISTRO_FEDERAL' || this.tipoAcuerdo === 'REGISTRO_SERVICIOS_PROPIOS') {
      this.nuevoAcuerdoForm.controls['fechaInicio'].setValidators([Validators.required])
      this.nuevoAcuerdoForm.controls['fechaInicio'].updateValueAndValidity();
      this.nuevoAcuerdoForm.controls['fechaFin'].setValidators([Validators.required])
      this.nuevoAcuerdoForm.controls['fechaFin'].updateValueAndValidity();
    } else if(this.tipoAcuerdo === 'MULTA') {
      this.nuevoAcuerdoForm.controls['multaUmas'].setValidators([Validators.required])
      this.nuevoAcuerdoForm.controls['multaUmas'].updateValueAndValidity();
      this.nuevoAcuerdoForm.controls['multaPesos'].setValidators([Validators.required])
      this.nuevoAcuerdoForm.controls['multaPesos'].updateValueAndValidity();
    }
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  editar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede editar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaService.obtenerAcuerdoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Acuerdo) => {
      this.acuerdo = data;

      this.nuevoAcuerdoForm.patchValue({
        fecha: this.acuerdo.fecha,
        observaciones: this.acuerdo.observaciones,
        tipo: this.acuerdo.tipo,
        fechaInicio: this.acuerdo.fechaInicio,
        fechaFin: this.acuerdo.fechaFin,
        multaUmas: this.acuerdo.multaUmas,
        multaPesos: this.acuerdo.multaPesos
      })

      this.tipoAcuerdo = data.tipo;

      this.modal = this.modalService.open(this.modificarAcuerdoModal, {'size': 'xl'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }


  eliminar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede editar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaService.obtenerAcuerdoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Acuerdo) => {
      this.acuerdo = data;
      this.mostrarModalEliminarAcuerdo();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalDetalles(rowData) {
    let uuid = rowData.uuid;

    this.empresaService.obtenerAcuerdoPorUuid(this.uuid, uuid).subscribe((data: Acuerdo) => {
      this.acuerdo = data;
      this.modal = this.modalService.open(this.verDetallesAcuerdoModal, {size: "xl"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el acuerdo por uuid. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  calcularUmasAPesos(event) {
    let umas = event.value;
    if(umas < 100 || umas > 500) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La cantidad de umas deben de ser entre 100 y 500`,
        ToastType.WARNING
      );
      return;
    }

    this.nuevoAcuerdoForm.patchValue({
      multaPesos: (+umas) * (+this.VALOR_UMA_PESOS)
    })

    this.multaPesos = (+umas) * (+this.VALOR_UMA_PESOS)
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  guardarAcuerdo(form) {
    console.log(form.value);
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Faltan algunos campos por rellenar`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempFile === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de adjuntar el archivo del acuerdo`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el acuerdo`,
      ToastType.INFO
    );

    let formValue: Acuerdo = form.value;

    // Validando las fechas
    if(formValue.fechaInicio !== undefined && formValue.fechaFin !== undefined) {
      let fechaInicio = new Date(formValue.fechaInicio);
      let fechaFin = new Date(formValue.fechaFin);
      if(fechaInicio > fechaFin) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La fecha de inicio es mayor que la del final",
          ToastType.WARNING
        )
        return;
      }
    }

    formValue.multaPesos = this.multaPesos
    formValue.multaUmas = (formValue.tipo === 'MULTA') ? formValue.multaUmas : 0;

    let formData: FormData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('acuerdo', JSON.stringify(formValue));

    this.empresaService.guardarAcuerdo(this.uuid, formData).subscribe((data: Acuerdo) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado el acuerdo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerAcuerdos(this.uuid).subscribe((data: Acuerdo[]) => {
        this.rowData = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los acuerdos de la empresa. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo guardar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarCambiosAcuerdo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Faltan campos requeridos por rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos guardando el acuerdo`,
      ToastType.INFO
    );

    let formValue: Acuerdo = form.value;

    if(formValue.fechaInicio !== undefined && formValue.fechaFin !== undefined) {
      let fechaInicio = new Date(formValue.fechaInicio);
      let fechaFin = new Date(formValue.fechaFin);
      if(fechaInicio > fechaFin) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La fecha de inicio es mayor que la del final",
          ToastType.WARNING
        )
        return;
      }
    }

    formValue.multaPesos = this.multaPesos

    let formData: FormData = new FormData();
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null);
    }
    formData.append('acuerdo', JSON.stringify(formValue));

    this.empresaService.modificarAcuerdo(this.uuid, this.acuerdo?.uuid, formData).subscribe((data: Acuerdo) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado el acuerdo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerAcuerdos(this.uuid).subscribe((data: Acuerdo[]) => {
        this.rowData = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los acuerdos de la empresa. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarAcuerdo() {
    this.modal = this.modalService.open(this.visualizarAcuerdoModal, {size: "xl"});

    this.empresaService.descargarAcuerdo(this.uuid, this.acuerdo?.uuid).subscribe((data: Blob) => {
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

  mostrarModificarAcuerdoModal() {
    this.nuevoAcuerdoForm.patchValue({
      fecha: this.acuerdo.fecha,
      observaciones: this.acuerdo.observaciones,
      tipo: this.acuerdo.tipo,
      fechaInicio: this.acuerdo.fechaInicio,
      fechaFin: this.acuerdo.fechaFin,
      multaUmas: this.acuerdo.multaUmas,
      multaPesos: this.acuerdo.multaPesos
    })

    this.tipoAcuerdo = this.acuerdo.tipo

    this.modal = this.modalService.open(this.modificarAcuerdoModal, {'size': 'xl'})
  }

  mostrarModalEliminarAcuerdo() {
    this.modal = this.modalService.open(this.eliminarAcuerdoModal, {'size': 'lg'})
  }

  confirmarEliminarAcuerdo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros obligatorios viene como vacio`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el acuerdo`,
      ToastType.INFO
    );

    let acuerdo: Acuerdo = form.value;

    let formData = new FormData();
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null);
    }
    formData.append('acuerdo', JSON.stringify(acuerdo));

    this.empresaService.eliminarAcuerdo(this.uuid, this.acuerdo?.uuid, formData).subscribe((data: Acuerdo) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el acuerdo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerAcuerdos(this.uuid).subscribe((data: Acuerdo[]) => {
        this.rowData = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los acuerdos de la empresa. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el acuerdo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
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

  descargarAcuerdoPdf() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "acuerdo.pdf";
    link.click();
  }
  generarReporteExcelAcuerdos() {
    this.reporteEmpresaService.generarReporteAcuerdos(this.uuid).subscribe((data) => {
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
  mostrarModalCrear() {
    this.modal = this.modalService.open(this.crearAcuerdoModal, {size: 'xl'})
  }

}
