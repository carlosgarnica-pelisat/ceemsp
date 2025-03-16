import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../_enums/ToastType";
import EmpresaLicenciaColectiva from "../../_models/EmpresaLicenciaColectiva";
import EmpresaModalidad from "../../_models/EmpresaModalidad";
import Arma from "../../_models/Arma";
import {faCheck, faPencilAlt, faSync, faHandPaper, faTrash} from "@fortawesome/free-solid-svg-icons";
import Persona from "../../_models/Persona";
import EmpresaDomicilio from "../../_models/EmpresaDomicilio";
import ArmaMarca from "../../_models/ArmaMarca";
import ArmaClase from "../../_models/ArmaClase";
import {EmpresaLicenciasService} from "../../_services/empresa-licencias.service";
import {EmpresaPersonalService} from "../../_services/empresa-personal.service";
import {DomiciliosService} from "../../_services/domicilios.service";
import {ArmasService} from "../../_services/armas.service";
import {EmpresaModalidadesService} from "../../_services/empresa-modalidades.service";
import ExisteArma from "../../_models/ExisteArma";
import {formatDate} from "@angular/common";
import {ValidacionService} from "../../_services/validacion.service";
import {
  BotonEmpresaLicenciasComponent
} from "../../_components/botones/boton-empresa-licencias/boton-empresa-licencias.component";
import {ReporteoService} from "../../_services/reporteo.service";

@Component({
  selector: 'app-empresa-licencias',
  templateUrl: './empresa-licencias.component.html',
  styleUrls: ['./empresa-licencias.component.css']
})
export class EmpresaLicenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faSync = faSync;
  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;
  faHandPaper = faHandPaper;

  marcas: ArmaMarca[] = [];
  clases: ArmaClase[] = [];

  tempFile;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true, resizable: true },
    {headerName: 'Numero de oficio', field: 'numeroOficio', sortable: true, filter: true, resizable: true },
    {headerName: 'Fecha de Inicio', field: 'fechaInicio', sortable: true, filter: true, resizable: true},
    {headerName: 'Fecha de Término', field: 'fechaFin', sortable: true, filter: true, resizable: true},
    {headerName: 'Modalidad', field: 'modalidad.nombre', sortable: true, filter: true, resizable: true},
    {headerName: 'Armas cortas', field: 'cantidadArmasCortas', sortable: true, filter: true, resizable: true},
    {headerName: 'Armas largas', field: 'cantidadArmasLargas', sortable: true, filter: true, resizable: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];
  allColumnDefs = EmpresaLicenciaColectiva.obtenerTodasLasColumnas();
  rowData: EmpresaLicenciaColectiva[] = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  licencia: EmpresaLicenciaColectiva;
  pestanaActual: string = "DETALLES";
  armas: Arma[];
  domicilios: EmpresaDomicilio[];
  domiciliosLicenciaColectiva: EmpresaDomicilio[];
  personal: Persona[] = [];
  status: string = "ACTIVA";

  rowDataClicked = {
    uuid: undefined
  };

  pdfActual;

  modalidades: EmpresaModalidad[];
  crearDireccionForm: FormGroup;
  crearArmaForm: FormGroup;
  editarArmaForm: FormGroup;
  motivosEliminacionForm: FormGroup;
  motivosEliminacionArmaForm: FormGroup;
  mostrarModificarStatusArma: boolean = false;

  showDireccionForm: boolean = false;
  showArmaForm: boolean = false;
  modalidadSearchForm: FormGroup;

  modalidadQuery: string;
  existeArma: ExisteArma;

  modalidad: EmpresaModalidad;

  tempUuidDireccion: string;
  tempUuidArma: string;

  arma: Arma;
  editandoArma: boolean;

  fechaDeHoy = new Date().toISOString().split('T')[0];
  matriculaValida: boolean;
  pdfBlob;

  model = {
    editorData: '<p>Escribe con detalle el relato de los hechos. Toma en cuenta que al finalizar se creara una incidencia de manera automatica y el arma quedara EN CUSTODIA.</p>'
  }

  @ViewChild('mostrarLicenciaDetallesModal') mostrarLicenciaDetallesModal;
  @ViewChild('agregarArmaModal') agregarArmaModal;
  @ViewChild('editarArmaModal') editarArmaModal;
  @ViewChild('eliminarEmpresaLicenciaDireccionModal') eliminarEmpresaLicenciaDireccionModal;
  @ViewChild('eliminarEmpresaLicenciaArmaModal') eliminarEmpresaLicenciaArmaModal;

  constructor(private modalService: NgbModal, private empresaLicenciaService: EmpresaLicenciasService, private toastService: ToastService,
              private route: ActivatedRoute, private formBuilder: FormBuilder, private empresaPersonalService: EmpresaPersonalService,
              private domiciliosService: DomiciliosService, private armasService: ArmasService, private empresaModalidadService: EmpresaModalidadesService,
              private validacionService: ValidacionService, private reporteoService: ReporteoService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaLicenciasComponent
    }

    this.crearDireccionForm = this.formBuilder.group({
      direccion: ['', Validators.required]
    })

    this.crearArmaForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      clase: ['', Validators.required],
      marca: ['', Validators.required],
      calibre: ['', [Validators.required, Validators.maxLength(10)]],
      bunker: ['', Validators.required],
      status: ['', Validators.required],
      serie: ['', [Validators.required, Validators.maxLength(30)]],
      matricula: ['', [Validators.required, Validators.maxLength(30)]]
    });

    this.editarArmaForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      clase: ['', Validators.required],
      marca: ['', Validators.required],
      calibre: ['', [Validators.required, Validators.maxLength(10)]],
      bunker: ['', Validators.required],
      status: ['', Validators.required],
      serie: ['', [Validators.maxLength(30)]],
      matricula: ['', [Validators.required, Validators.maxLength(30)]]
    });

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionArmaForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.empresaLicenciaService.obtenerLicenciasColectivas().subscribe((data: EmpresaLicenciaColectiva[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.domiciliosService.obtenerDomicilios().subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.armasService.obtenerArmaMarcas().subscribe((data: ArmaMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.armasService.obtenerArmaClases().subscribe((data: ArmaClase[]) => {
      this.clases = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaModalidadService.obtenerModalidades().subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las modalidades de la empresa. ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarLicenciaDetallesModal);
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  mostrarFormularioNuevaDireccion() {
    this.showDireccionForm = !this.showDireccionForm;
  }

  mostrarFormularioArma() {
    this.showArmaForm = !this.showArmaForm;
    if(!this.showArmaForm) {
      this.crearArmaForm.reset();
    }
    if(this.editandoArma) {
      this.editandoArma = false;
      this.arma = undefined;
    }
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

  mostrarModalDetalles(rowData, modal) {
    let licenciaUuid = rowData.uuid;
    this.modal = this.modalService.open(modal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaLicenciaService.obtenerLicenciaColectivaPorUuid(licenciaUuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;

      this.empresaLicenciaService.obtenerDomiciliosPorLicenciaColectiva(this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios de la licencia colectiva. Motivo: {error}`,
          ToastType.ERROR
        )
      })

      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `Las armas de la licencia colectiva no se pudieron descargar. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de la licencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  seleccionarModalidad(uuid) {
    this.modalidad = this.modalidades.filter(x => x.uuid === uuid)[0];
  }

  quitarModalidad() {
    this.modalidad = undefined;
  }

  custodiaArma() {
    if(this.model.editorData === '') {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de agregar mas informacion en el relato de hechos`,
        ToastType.INFO
      );
      return;
    }

    let formData = new FormData();
    formData.append('relatoHechos', JSON.stringify(this.model.editorData));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaLicenciaService.custodiaArma(this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha cambiado el status del arma con exito",
        ToastType.SUCCESS
      );
      this.mostrarCambioStatusForm(undefined);
      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las armas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido cambiar el status del arma. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarCambioStatusForm(uuid) {
    this.mostrarModificarStatusArma = !this.mostrarModificarStatusArma;
    if(uuid !== undefined) {
      this.arma = this.armas.filter(x => uuid === x.uuid)[0];
    }
  }

  descargarLicencia(uuid, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaLicenciaService.descargarLicenciaPdf(this.licencia.uuid).subscribe((data: Blob) => {
      this.pdfBlob = data;
      this.convertirPdf(data);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el PDF. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

  toggleColumn(field: string) {
    let columnDefinitionIndex = this.columnDefs.findIndex(s => s.field === field);
    if(columnDefinitionIndex === -1) {
      let columnDefinition = this.allColumnDefs.filter(s => s.field === field)[0];

      let newColumnDef = {
        headerName: columnDefinition.headerName,
        field: columnDefinition.field,
        sortable: true,
        filter: true
      };

      //this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  mostrarModificarArmaForm(uuid) {
    this.arma = this.armas.filter(x => x.uuid === uuid)[0];
    this.modal = this.modalService.open(this.editarArmaModal, {size: "xl", backdrop: "static"})
    this.editandoArma = true;

    let cuipRegexSerie = /^[a-zA-Z0-9]{3,20}$/g;
    if(!cuipRegexSerie.test(this.arma.matricula)) {
      this.matriculaValida = false;
    } else {
      this.matriculaValida = true;
    }

    this.editarArmaForm.patchValue({
      tipo: this.arma.tipo,
      clase: this.arma.clase.uuid,
      marca: this.arma.marca.uuid,
      calibre: this.arma.calibre,
      bunker: this.arma.bunker.uuid,
      serie: this.arma.serie,
      matricula: this.arma.matricula,
      status: this.arma.status
    });

    this.status = this.arma.status;
  }

  mostrarFormularioNuevaArma() {
    this.modal = this.modalService.open(this.agregarArmaModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalEliminarDomicilio(uuid) {
    this.tempUuidDireccion = uuid;
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaDireccionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarArma(uuid) {
    this.tempUuidArma = uuid;
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaArmaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  crearArma(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos sin rellenar",
        ToastType.WARNING
      );
      return;
    }

    if(!this.matriculaValida) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La matricula no es valida`,
        ToastType.WARNING
      )
      return;
    }

    if(this.existeArma?.existe) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Esta arma ya cuenta con modelo o matricula registrada`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el arma",
      ToastType.INFO
    );

    let formData: Arma = form.value;

    formData.bunker = this.domicilios.filter(x => x.uuid === form.value.bunker)[0];
    formData.clase = this.clases.filter(x => x.uuid === form.value.clase)[0];
    formData.marca = this.marcas.filter(x => x.uuid === form.value.marca)[0];
    formData.personal = this.personal.filter(x => x.uuid === form.value.personal)[0];

    if(this.editandoArma) {
      this.empresaLicenciaService.modificarArma(this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.modal.close();
        this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armas = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las armas. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el arma. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaLicenciaService.guardarArma(this.licencia.uuid, formData).subscribe((data: Arma) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.modal.close();
        this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armas = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las armas. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `no se pudo guarar el arma. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  confirmarEliminarLicenciaArma(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido",
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidArma === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del arma a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el arma de la licencia colectiva",
      ToastType.INFO
    );

    let formValue: Arma = form.value;
    formValue.fechaBaja = formatDate(new Date(), "yyyy-MM-dd", "en")

    let formData = new FormData();
    formData.append('arma', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaLicenciaService.eliminarArma(this.licencia.uuid, this.tempUuidArma, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener las armas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El arma de la licencia colectiva no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cerrarModalCrearArma() {
    this.modal.close();
  }

  private getDismissReason(reason: any): string {
    if (reason == ModalDismissReasons.ESC) {
      return `by pressing ESC`;
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return `by clicking on a backdrop`;
    } else {
      return `with ${reason}`;
    }
  }

  consultarArmaMatricula(event) {
    let matricula = event.value;
    let cuipRegexSerie = /^[a-zA-Z0-9]{3,20}$/g;
    if(!cuipRegexSerie.test(matricula)) {
      this.toastService.showGenericToast(
        "Espera un momento",
        `La matricula no es validas. Favor de revisarla`,
        ToastType.WARNING
      );
      this.matriculaValida = false;
      return;
    } else {
      this.matriculaValida = true;
    }

    this.existeArma = undefined;
    let existeArma: ExisteArma = new ExisteArma();
    existeArma.matricula = event.value;

    this.validacionService.validarArma(existeArma).subscribe((existeArma: ExisteArma) => {
      this.existeArma = existeArma;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido consultar la existencia del arma. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarAcuerdoPdf() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "licencia.pdf";
    link.click();
  }

  generarReporteExcelLicencias() {
    this.reporteoService.generarReporteLicenciasColectivas().subscribe((data) => {
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

  generarReporteExcelArmas() {
    this.reporteoService.generarReporteArmas().subscribe((data) => {
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

}
