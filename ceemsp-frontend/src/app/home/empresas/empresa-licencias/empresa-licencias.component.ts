import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastService} from "../../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaLicenciaColectiva from "../../../_models/EmpresaLicenciaColectiva";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Arma from "../../../_models/Arma";
import {faCheck, faEdit, faHandPaper, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import Persona from "../../../_models/Persona";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import ArmaMarca from "../../../_models/ArmaMarca";
import ArmaClase from "../../../_models/ArmaClase";
import {ArmasService} from "../../../_services/armas.service";
import {
  BotonEmpresaLicenciasComponent
} from "../../../_components/botones/boton-empresa-licencias/boton-empresa-licencias.component";
import ExisteArma from "../../../_models/ExisteArma";
import {ValidacionService} from "../../../_services/validacion.service";

@Component({
  selector: 'app-empresa-licencias',
  templateUrl: './empresa-licencias.component.html',
  styleUrls: ['./empresa-licencias.component.css']
})
export class EmpresaLicenciasComponent implements OnInit {

  mostrandoArmasEliminadas: boolean = false;
  mostrandoDomiciliosEliminadas: boolean = false;

  modalidad: EmpresaModalidad;

  existeArma: ExisteArma;

  private gridApi;
  private gridColumnApi;

  faHandPaper = faHandPaper;
  faEdit = faEdit;
  faTrash = faTrash;
  faCheck = faCheck;
  faPencil = faPencilAlt;

  fechaDeHoy = new Date().toISOString().split('T')[0];

  marcas: ArmaMarca[] = [];
  clases: ArmaClase[] = [];

  tempFile;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Numero de oficio', field: 'numeroOficio', sortable: true, filter: true },
    {headerName: 'Fecha de Inicio', field: 'fechaInicio', sortable: true, filter: true},
    {headerName: 'Fecha de Término', field: 'fechaFin', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
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
  armasNoEliminadas: Arma[];
  armasEliminadas: Arma[];
  domicilios: EmpresaDomicilio[];
  domiciliosLicenciaColectiva: EmpresaDomicilio[];
  personal: Persona[] = [];
  status: string = "ACTIVA";

  rowDataClicked = {
    uuid: undefined
  };

  pdfActual;

  modalidadQuery: string;

  modalidades: EmpresaModalidad[];
  crearEmpresaLicenciaForm: FormGroup;
  modificarStatusArmaForm: FormGroup;
  crearDireccionForm: FormGroup;
  crearArmaForm: FormGroup;
  modalidadSearchForm: FormGroup;
  motivosEliminacionForm: FormGroup;
  motivosEliminacionArmaForm: FormGroup;
  mostrarModificarStatusArma: boolean = false;

  showDireccionForm: boolean = false;
  showArmaForm: boolean = false;

  tempUuidDireccion: string;
  tempUuidArma: string;

  arma: Arma;
  editandoArma: boolean;

  model = {
    editorData: '<p>Escribe con detalle el relato de los hechos. Toma en cuenta que al finalizar se creara una incidencia de manera automatica y el arma quedara EN CUSTODIA.</p>'
  }

  @ViewChild('mostrarLicenciaDetallesModal') mostrarLicenciaDetallesModal;
  @ViewChild('modificarLicenciaModal') modificarLicenciaModal;
  @ViewChild('eliminarEmpresaLicenciaModal') eliminarEmpresaLicenciaModal;
  @ViewChild('eliminarEmpresaLicenciaDireccionModal') eliminarEmpresaLicenciaDireccionModal;
  @ViewChild('eliminarEmpresaLicenciaArmaModal') eliminarEmpresaLicenciaArmaModal;

  constructor(private modalService: NgbModal, private empresaService: EmpresaService, private toastService: ToastService,
              private route: ActivatedRoute, private formBuilder: FormBuilder, private armaService: ArmasService,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaLicenciasComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEmpresaLicenciaForm = this.formBuilder.group({
      numeroOficio: ['', Validators.required],
      submodalidad: [''],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      archivo: ['', Validators.required]
    });

    this.modificarStatusArmaForm = this.formBuilder.group({
      status: ['', Validators.required],
      motivo: [''],
      personalAsignado: ['']
    });

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
      personal: [''],
      serie: ['', [Validators.maxLength(30)]],
      matricula: ['', [Validators.required, Validators.maxLength(30)]]
    })

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

    this.empresaService.obtenerLicenciasColectivas(this.uuid).subscribe((data: EmpresaLicenciaColectiva[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `no se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.armaService.obtenerArmaMarcas().subscribe((data: ArmaMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
          ToastType.ERROR
      );
    })

    this.armaService.obtenerArmaClases().subscribe((data: ArmaClase[]) => {
      this.clases = data;
    }, (error) => {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
          ToastType.ERROR
      );
    });

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las modalidades de la empresa. ${error}`,
        ToastType.ERROR
      );
    })
  }

  seleccionarModalidad(uuid) {
    this.modalidad = this.modalidades.filter(x => x.uuid === uuid)[0];
  }

  quitarModalidad() {
    this.modalidad = undefined;
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarLicenciaDetallesModal);
  }

  mostrarArmasEliminadas() {
    this.mostrandoArmasEliminadas = true;
    this.armas = this.armasEliminadas;
  }

  ocultarArmasEliminadas() {
    this.mostrandoArmasEliminadas = false;
    this.armas = this.armasNoEliminadas;
  }

  mostrarDomiciliosEliminados() {
    this.mostrandoDomiciliosEliminadas = true;
  }

  ocultarDomiciliosEliminados() {
    this.mostrandoDomiciliosEliminadas = false;
  }

  editar(rowData) {
    this.empresaService.obtenerLicenciaColectivaPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;
      this.mostrarModificarLicenciaModal();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la licencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.empresaService.obtenerLicenciaColectivaPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;
      this.mostrarEliminarLicenciaModal();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la licencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  seleccionarStatus(event) {
    this.status = event.value;
  }

  seleccionarPersona(event) {
    this.status = event.value;
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

  guardarDireccion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    let data: EmpresaDomicilio = this.domicilios.filter(x => x.uuid === form.value.direccion)[0];
    let existeDomicilio = this.domiciliosLicenciaColectiva.filter(x => x.uuid === form.value.direccion)[0];

    if(existeDomicilio !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio ya se encuentra registrado en la licencia colectiva`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el domicilio en la licencia colectiva",
      ToastType.INFO
    );

    this.empresaService.guardarDomicilioEnLicenciaColectiva(this.uuid, this.licencia.uuid, data).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.mostrarFormularioNuevaDireccion();
      this.empresaService.obtenerDomiciliosPorLicenciaColectiva(this.uuid, this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha guardado el domicilio en la licencia colectiva. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  consultarArmaModelo(event) {
    this.existeArma = undefined;
    let existeArma: ExisteArma = new ExisteArma();
    existeArma.serie = event.value;

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

  consultarArmaMatricula(event) {
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

  modificarLicencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario es invalido`,
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;
    let licencia: EmpresaLicenciaColectiva = new EmpresaLicenciaColectiva();

    licencia.numeroOficio = formValue.numeroOficio;
    licencia.modalidad = this.modalidades.filter(x => x.uuid === this.modalidad.uuid)[0].modalidad;
    licencia.submodalidad = this.modalidades.filter(x => x.uuid === this.modalidad.uuid)[0].submodalidad;
    licencia.fechaInicio = formValue.fechaInicio;
    licencia.fechaFin = formValue.fechaFin;

    let fechaInicio = new Date(licencia.fechaInicio);
    let fechaFin = new Date(licencia.fechaFin);
    if(fechaInicio > fechaFin) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La fecha de inicio es mayor que la del final",
        ToastType.WARNING
      )
      return;
    }
    let existeModalidad = this.rowData.filter(x => x.modalidad.uuid === licencia.modalidad.uuid)[0];
    if(existeModalidad !== undefined && existeModalidad?.uuid !== this.licencia.uuid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La modalidad ya se encuentra registrada en esta licencia colectiva",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos modificando la licencia",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('licencia', JSON.stringify(licencia));
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null);
    }

    this.empresaService.modificarLicenciaColectiva(this.uuid, this.licencia.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha actualizado la licencia colectiva con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la licencia colectiva. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  mostrarModalDetalles(rowData, modal) {
    let licenciaUuid = rowData.uuid;
    this.modal = this.modalService.open(modal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaService.obtenerLicenciaColectivaPorUuid(this.uuid, licenciaUuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;

      this.empresaService.obtenerDomiciliosPorLicenciaColectiva(this.uuid, this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios de la licencia colectiva. Motivo: ${error}`,
          ToastType.ERROR
        )
      })

      this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
        this.armasNoEliminadas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `Las armas de la licencia colectiva no se pudieron descargar. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaService.obtenerArmasEliminadasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armasEliminadas = data;
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

    this.empresaService.custodiaArma(this.uuid, this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha cambiado el status del arma con exito",
        ToastType.SUCCESS
      );
      this.mostrarCambioStatusForm(undefined);
      this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
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

  guardarLicencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no estan siendo llenados",
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;
    let licencia: EmpresaLicenciaColectiva = new EmpresaLicenciaColectiva();

    console.log(this.modalidades.filter(x => x.uuid === this.modalidad.uuid)[0]);
    licencia.numeroOficio = formValue.numeroOficio;
    licencia.modalidad = this.modalidades.filter(x => x.uuid === this.modalidad.uuid)[0].modalidad;
    licencia.submodalidad = this.modalidades.filter(x => x.uuid === this.modalidad.uuid)[0].submodalidad;
    licencia.fechaInicio = formValue.fechaInicio;
    licencia.fechaFin = formValue.fechaFin;

    let fechaInicio = new Date(licencia.fechaInicio);
    let fechaFin = new Date(licencia.fechaFin);
    if(fechaInicio > fechaFin) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La fecha de inicio es mayor que la del final",
        ToastType.WARNING
      )
      return;
    }
    let existeModalidad = this.rowData.filter(x => x.modalidad.uuid === licencia.modalidad.uuid)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La modalidad ya se encuentra registrada en esta licencia colectiva",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('licencia', JSON.stringify(licencia));

    this.empresaService.guardarLicenciaColectiva(this.uuid, formData).subscribe((data: EmpresaLicenciaColectiva) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la licencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la licencia. ${error}`,
        ToastType.ERROR
      );
    });
  }

  descargarLicencia(uuid, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaService.descargarLicenciaPdf(this.uuid, this.licencia.uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      // TODO: Manejar esta opcion para descargar
      /*let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "licencia-colectiva-" + this.licencia.uuid;
      link.click();*/
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

      this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  mostrarModificarArmaForm(index) {
    this.arma = this.armas[index];
    this.mostrarFormularioArma();
    this.editandoArma = true;
    console.log(this.arma);
    console.log(this.personal);
    this.crearArmaForm.patchValue({
      tipo: this.arma.tipo,
      clase: this.arma.clase.uuid,
      serie: this.arma.serie,
      marca: this.arma.marca.uuid,
      calibre: this.arma.calibre,
      bunker: this.arma.bunker.uuid,
      matricula: this.arma.matricula,
      personal: this.arma.personal?.uuid,
      status: this.arma.status
    });

    this.status = this.arma.status;
  }

  mostrarModificarLicenciaModal() {
    this.crearEmpresaLicenciaForm.patchValue({
      numeroOficio: this.licencia.numeroOficio,
      submodalidad: this.licencia?.submodalidad?.uuid,
      fechaInicio: this.licencia.fechaInicio,
      fechaFin: this.licencia.fechaFin
    });
    this.modalidad = this.modalidades.filter(x => x.modalidad.uuid === this.licencia.modalidad.uuid)[0];;
    this.crearEmpresaLicenciaForm.controls['archivo'].clearValidators();
    this.crearEmpresaLicenciaForm.controls['archivo'].updateValueAndValidity();
    this.modal = this.modalService.open(this.modificarLicenciaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarEliminarLicenciaModal() {
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarFormularioNuevaArma() {
    this.showArmaForm = !this.showArmaForm;
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

    if(formData.status === 'ACTIVA' && (form.value.personal === undefined || form.value.personal === null)) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando un arma se configura como ACTIVA, se requiere asignar un personal",
        ToastType.WARNING
      );
      return;
    }

    formData.bunker = this.domicilios.filter(x => x.uuid === form.value.bunker)[0];
    formData.clase = this.clases.filter(x => x.uuid === form.value.clase)[0];
    formData.marca = this.marcas.filter(x => x.uuid === form.value.marca)[0];
    formData.personal = this.personal.filter(x => x.uuid === form.value.personal)[0];

    if(this.editandoArma) {
      this.empresaService.modificarArma(this.uuid, this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
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
      this.empresaService.guardarArma(this.uuid, this.licencia.uuid, formData).subscribe((data: Arma) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.empresaService.obtenerArmasEliminadasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armasEliminadas = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se pudiieron descargar las armas eliminadas. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
        this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armas = data;
          this.armasNoEliminadas = data;
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

  confirmarEliminarLicencia(form) {

    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia colectiva",
      ToastType.INFO
    );

    let formValue: EmpresaDomicilio = form.value;

    let formData = new FormData();
    formData.append('licencia', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarLicenciaColectiva(this.uuid, this.licencia.uuid, formData).subscribe((data: EmpresaLicenciaColectiva) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la licencia colectiva. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

    let formValue: EmpresaDomicilio = form.value;

    let formData = new FormData();
    formData.append('arma', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarArma(this.uuid, this.licencia.uuid, this.tempUuidArma, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerArmasEliminadasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armasEliminadas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudiieron descargar las armas eliminadas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
      this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
        this.armasNoEliminadas = data;
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

  confirmarEliminarLicenciaDomicilio() {
    if(this.tempUuidDireccion === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del domicilio a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el domicilio de la licencia colectiva",
      ToastType.INFO
    );

    this.empresaService.eliminarDomicilioEnLicenciaColectiva(this.uuid, this.licencia.uuid, this.tempUuidDireccion).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerDomiciliosPorLicenciaColectiva(this.uuid, this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudieron descargar los domicilios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudieron descargar las armas. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio de la licencia colectiva no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

}
