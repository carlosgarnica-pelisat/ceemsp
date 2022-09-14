import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaEscrituraSocio from "../../../_models/EmpresaEscrituraSocio";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaEscrituraApoderado from "../../../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../../../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../../../_models/EmpresaEscrituraConsejo";
import {EstadosService} from "../../../_services/estados.service";
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import curp from 'curp';
import Localidad from "../../../_models/Localidad";
import ExisteEscritura from "../../../_models/ExisteEscritura";
import {ValidacionService} from "../../../_services/validacion.service";
import {
  BotonEmpresaLegalComponent
} from "../../../_components/botones/boton-empresa-legal/boton-empresa-legal.component";
import Empresa from "../../../_models/Empresa";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";

@Component({
  selector: 'app-empresa-legal',
  templateUrl: './empresa-legal.component.html',
  styleUrls: ['./empresa-legal.component.css']
})
export class EmpresaLegalComponent implements OnInit {
  mostrandoSociosEliminados: boolean = false;
  mostrandoApoderadosEliminados: boolean = false;
  mostrandoRepresentantesEliminados: boolean = false;
  mostrandoConsejosEliminados: boolean = false;

  editandoModal: boolean = false;
  tzoffset = (new Date()).getTimezoneOffset() * 60000; //offset in milliseconds
  localISOTime = (new Date(Date.now() - this.tzoffset)).toISOString().slice(0, -1);
  fechaDeHoy = this.localISOTime.split('T')[0];

  showSocioForm: boolean = false;
  showApoderadoForm: boolean = false;
  showRepresentanteForm: boolean = false;
  showConsejoForm: boolean = false;

  editandoSocio: boolean = false;
  editandoApoderado: boolean = false;
  editandoRepresentante: boolean = false;
  editandoConsejo: boolean = false;

  uuid: string;
  pestanaActual: string = "DETALLES";
  escrituras: EmpresaEscritura[];

  escrituraSocios: EmpresaEscrituraSocio[] = [];
  escrituraSociosEliminados: EmpresaEscrituraSocio[] = [];
  escrituraApoderados: EmpresaEscrituraApoderado[] = [];
  escrituraApoderadosEliminados: EmpresaEscrituraApoderado[] = [];
  escrituraRepresentantes: EmpresaEscrituraRepresentante[] = [];
  escrituraRepresentantesEliminados: EmpresaEscrituraRepresentante[] = [];
  escrituraConsejos: EmpresaEscrituraConsejo[] = [];
  escrituraConsejosEliminados: EmpresaEscrituraConsejo[] = [];

  socio: EmpresaEscrituraSocio;
  apoderado: EmpresaEscrituraApoderado;
  representante: EmpresaEscrituraRepresentante;
  consejo: EmpresaEscrituraConsejo;

  nuevaEscrituraForm: FormGroup;
  nuevoSocioForm: FormGroup;
  nuevoApoderadoForm: FormGroup;
  nuevoRepresentanteForm: FormGroup;
  nuevoConsejoAdministracionForm: FormGroup;
  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;

  motivosEliminacionSocioForm: FormGroup;
  motivosEliminacionAopderadoForm: FormGroup;
  motivosEliminacionRepresentanteForm: FormGroup;
  motivosEliminacionConsejoForm: FormGroup;

  modal: NgbModalRef;
  closeResult: string;
  escritura: EmpresaEscritura;
  existeEscritura: ExisteEscritura;

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'No. Instrumento', field: 'numeroEscritura', sortable: true, filter: true },
    {headerName: 'Fecha', field: 'fechaEscritura', sortable: true, filter: true },
    {headerName: 'Ciudad', sortable: true, filter: true, valueGetter: function (params) {return params.data.localidadCatalogo.nombre + ", " + params.data.estadoCatalogo.nombre}},
    {headerName: 'Nombre y Numero del fedatario', sortable: true, filter: true, valueGetter: function(params) {return `${params.data.numero} - ${params.data.nombreFedatario} ${params.data.apellidoPaterno} ${params.data.apellidoMaterno}`}},
    {headerName: 'Ciudad', field: 'ciudad', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'empresaLegalButtonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  allColumnDefs = EmpresaEscritura.obtenerTodasLasColumnas();
  rowData = [];

  tempFile;
  pdfActual;

  empresa: Empresa;

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  localidades: Localidad[] = [];

  estado: Estado;
  municipio: Municipio;
  localidad: Localidad;

  porcentaje: number = 0.00;

  tempUuidSocio: string;
  tempUuidApoderado: string;
  tempUuidRepresentante: string;
  tempUuidConsejo: string;

  estadoQuery: string = '';
  municipioQuery: string = '';
  localidadQuery: string = '';

  @ViewChild('mostrarDetallesEscrituraModal') mostrarDetallesEscrituraModal: any;
  @ViewChild('modificarEscrituraModal') modificarEscrituraModal: any;

  @ViewChild('eliminarEscrituraSocioModal') eliminarEscrituraSocioModal;
  @ViewChild('eliminarEscrituraApoderadoModal') eliminarEscrituraApoderadoModal;
  @ViewChild('eliminarEscrituraRepresentanteModal') eliminarEscrituraRepresentanteModal;
  @ViewChild('eliminarEscrituraConsejoModal') eliminarEscrituraConsejoModal;
  @ViewChild('eliminarEscrituraModal') eliminarEscrituraModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private estadoService: EstadosService,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {

    this.frameworkComponents = {
      empresaLegalButtonRenderer: BotonEmpresaLegalComponent
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

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', [Validators.required, Validators.maxLength(10)]],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', [Validators.required, Validators.maxLength(60)]],
      tipoFedatario: ['', Validators.required],
      numero: ['', [Validators.required, Validators.min(1), Validators.max(9999)]],
      nombreFedatario: ['', [Validators.required, Validators.maxLength(100)]],
      apellidoPaterno: ['', [Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]],
      descripcion: ['', Validators.required]
    })

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      fechaInicio: [''],
      fechaFin: [''],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.motivosEliminacionSocioForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionAopderadoForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionRepresentanteForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionConsejoForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.empresaService.obtenerEscrituras(this.uuid).subscribe((data: EmpresaEscritura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las escrituras. ${error}`,
        ToastType.ERROR
      )
    });

    this.estadoService.obtenerEstados().subscribe((data: Estado[]) => {
      this.estados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los estados. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  seleccionarEstado(estadoUuid) {
    this.localidad = undefined;
    this.municipio = undefined;

    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
    this.estadoService.obtenerEstadosPorMunicipio(estadoUuid).subscribe((data: Municipio[]) => {
      this.municipios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los municipios. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  seleccionarMunicipio(municipioUuid) {
    this.localidad = undefined;

    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

    this.estadoService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, municipioUuid).subscribe((data: Localidad[]) => {
      this.localidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las localidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  seleccionarLocalidad(localidadUuid) {
    this.localidad = this.localidades.filter(x => x.uuid === localidadUuid)[0];

    this.nuevaEscrituraForm.patchValue({
      ciudad: this.localidad.nombre
    })
  }

  validarEscritura(event) {
    let numeroEscritora = event.value;
    let existeEscritura = new ExisteEscritura();
    existeEscritura.numero = numeroEscritora

    this.validacionService.validarEscritura(existeEscritura).subscribe((data: ExisteEscritura) => {
      this.existeEscritura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido validar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarDetallesEscrituraModal)
  }

  editar(rowData) {
    this.empresaService.obtenerEscrituraPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
      this.editandoModal = false;
      this.nuevaEscrituraForm.setValue({
        numeroEscritura: this.escritura.numeroEscritura,
        fechaEscritura: this.escritura.fechaEscritura,
        ciudad: this.escritura.ciudad,
        tipoFedatario: this.escritura.tipoFedatario,
        numero: this.escritura.numero,
        nombreFedatario: this.escritura.nombreFedatario,
        apellidoPaterno: this.escritura.apellidoPaterno,
        apellidoMaterno: this.escritura.apellidoMaterno,
        curp: this.escritura.curp,
        descripcion: this.escritura.descripcion
      });

      this.estado = this.escritura.estadoCatalogo;
      this.municipio = this.escritura.municipioCatalogo;
      this.localidad = this.escritura.localidadCatalogo;

      this.estadoService.obtenerEstadosPorMunicipio(this.estado.uuid).subscribe((data: Municipio[]) => {
        this.municipios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los municipios relacionados. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.estadoService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Localidad[]) => {
        this.localidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las localidades. Motivo: ${error}`,
          ToastType.ERROR
        );
      });

      this.modal = this.modalService.open(this.modificarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.empresaService.obtenerEscrituraPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
      this.mostrarModalEliminarEscritura();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminarEstado() {
    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
  }

  eliminarMunicipio() {
    this.municipio = undefined;
    this.localidad = undefined;
  }

  eliminarLocalidad() {
    this.localidad = undefined;
  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
    if(!this.showSocioForm) {
      this.nuevoSocioForm.reset();
    }
    if(this.editandoSocio) {
      this.editandoSocio = false;
      this.socio = undefined;
    }
  }

  mostrarFormularioNuevoApoderado() {
    this.showApoderadoForm = !this.showApoderadoForm;
    if(!this.showSocioForm) {
      this.nuevoApoderadoForm.reset();
    }
    if(this.editandoApoderado) {
      this.editandoApoderado = false;
    }
  }

  mostrarFormularioNuevoRepresentante() {
    this.showRepresentanteForm = !this.showRepresentanteForm;
    if(!this.showRepresentanteForm) {
      this.nuevoRepresentanteForm.reset();
    }
    if(this.editandoRepresentante) {
      this.editandoRepresentante = false;
    }
  }

  mostrarFormularioNuevoConsejo() {
    this.showConsejoForm = !this.showConsejoForm;
    if(!this.showConsejoForm) {
      this.nuevoConsejoAdministracionForm.reset();
    }
    if(this.editandoConsejo) {
      this.editandoConsejo = false;
    }
  }

  mostrarEditarSocio(index) {
    this.socio = this.escritura.socios[index];
    this.mostrarFormularioNuevoSocio();
    this.editandoSocio = true;
    this.nuevoSocioForm.patchValue({
      nombres: this.socio.nombres,
      apellidos: this.socio.apellidos,
      apellidoMaterno: this.socio.apellidoMaterno,
      curp: this.socio.curp,
      sexo: this.socio.sexo,
      porcentajeAcciones: this.socio.porcentajeAcciones
    });
  }

  mostrarEditarApoderado(index) {
    this.apoderado = this.escritura.apoderados[index];
    this.mostrarFormularioNuevoApoderado();
    this.editandoApoderado = true;
    this.nuevoApoderadoForm.patchValue({
      nombres: this.apoderado.nombres,
      apellidos: this.apoderado.apellidos,
      apellidoMaterno: this.apoderado.apellidoMaterno,
      curp: this.apoderado.curp,
      sexo: this.apoderado.sexo,
      fechaInicio: this.apoderado.fechaInicio,
      fechaFin: this.apoderado.fechaFin
    });
  }

  mostrarEditarRepresentante(index) {
    this.representante = this.escritura.representantes[index];
    this.mostrarFormularioNuevoRepresentante();
    this.editandoRepresentante = true;
    this.nuevoRepresentanteForm.patchValue({
      nombres: this.representante.nombres,
      apellidos: this.representante.apellidos,
      apellidoMaterno: this.representante.apellidoMaterno,
      curp: this.representante.curp,
      sexo: this.representante.sexo
    });
  }

  mostrarEditarConsejo(index) {
    this.consejo = this.escritura.consejos[index];
    this.mostrarFormularioNuevoConsejo();
    this.editandoConsejo = true;
    this.nuevoConsejoAdministracionForm.patchValue({
      nombres: this.consejo.nombres,
      apellidos: this.consejo.apellidos,
      apellidoMaterno: this.consejo.apellidoMaterno,
      curp: this.consejo.curp,
      sexo: this.consejo.sexo,
      puesto: this.consejo.puesto
    })
  }

  mostrarModalEliminarEscritura() {
    this.modal = this.modalService.open(this.eliminarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }


  mostrarModalEliminarSocio(uuid) {
    this.tempUuidSocio = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraSocioModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarApoderado(uuid) {
    this.tempUuidApoderado = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraApoderadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarRepresentante(uuid) {
    this.tempUuidRepresentante = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraRepresentanteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarConsejo(uuid) {
    this.tempUuidConsejo = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraConsejoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarSocio(nuevoSocioform) {

    if(!nuevoSocioform.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el socio",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraSocio = nuevoSocioform.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }

      let existeSocioRfc = this.escritura.socios.filter(x => (x.curp === formValue.curp && x.uuid !== this.socio?.uuid))
      if(existeSocioRfc.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un socio registrado con este CURP",
          ToastType.WARNING
        );
        return;
      }
    }

    if(this.escritura.socios.length > 0) {
      this.porcentaje = 0.00;
      this.escritura.socios.forEach((s) => {
        if(this.editandoSocio && s.uuid === this.socio.uuid) {
          return;
        }
        this.porcentaje += parseInt(String(s.porcentajeAcciones));
      })

      this.porcentaje += parseInt(String(formValue.porcentajeAcciones));

      if(this.porcentaje > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    } else {
      if(formValue.porcentajeAcciones > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    }

    if(this.editandoSocio) {
      formValue.uuid = this.socio.uuid;
      formValue.id = this.socio.id;

      this.empresaService.modificarEscrituraSocio(this.uuid, this.escritura.uuid, this.socio.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el socio con exito",
          ToastType.SUCCESS
        );
        this.empresaService.obtenerEscrituraSocios(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
          this.escritura.socios = data;
          this.mostrarFormularioNuevoSocio();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los socios. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el socio. ${error}`,
          ToastType.ERROR
        );
      });

    } else {
      this.empresaService.guardarEscrituraSocio(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el socio con exito",
          ToastType.SUCCESS
        );
        this.empresaService.obtenerEscrituraSocios(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
          this.escritura.socios = data;
          this.mostrarFormularioNuevoSocio();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los socios. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el socio. ${error}`,
          ToastType.ERROR
        );
      });
    }
  }

  mostrarSociosEliminados() {
    this.mostrandoSociosEliminados = true;
    this.escritura.socios = this.escrituraSociosEliminados;
  }

  ocultarSociosEliminados() {
    this.mostrandoSociosEliminados = false;
    this.escritura.socios = this.escrituraSocios;
  }

  mostrarApoderadosEliminados() {
    this.mostrandoApoderadosEliminados = true;
    this.escritura.apoderados = this.escrituraApoderadosEliminados;
  }

  ocultarApoderadosEliminados() {
    this.mostrandoApoderadosEliminados = false;
    this.escritura.apoderados = this.escrituraApoderados;
  }

  mostrarRepresentantesEliminados() {
    this.mostrandoRepresentantesEliminados = true;
    this.escritura.representantes = this.escrituraRepresentantesEliminados;
  }

  ocultarRepresentantesEliminados() {
    this.mostrandoRepresentantesEliminados = false;
    this.escritura.representantes = this.escrituraRepresentantes;
  }

  mostrarConsejosEliminados() {
    this.mostrandoRepresentantesEliminados = true;
    this.escritura.consejos = this.escrituraConsejosEliminados;
  }

  ocultarConsejosEliminados() {
    this.mostrandoRepresentantesEliminados = false;
    this.escritura.consejos = this.escrituraConsejos;
  }

  guardarApoderado(nuevoApoderadoForm) {

    if(!nuevoApoderadoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    let formValue: EmpresaEscrituraApoderado = nuevoApoderadoForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }
    }

    let existeSocioRfc = this.escritura.apoderados.filter(x => (x.curp === formValue.curp && x.uuid !== this.apoderado?.uuid))
    if(existeSocioRfc.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya hay un apoderado registrado con este CURP",
        ToastType.WARNING
      );
      return;
    }

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

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo apoderado",
      ToastType.INFO
    );

    if(this.editandoApoderado) {
      formValue.uuid = this.apoderado.uuid;
      formValue.id = this.apoderado.id;

      this.empresaService.modificarEscrituraApoderado(this.uuid, this.escritura.uuid, this.apoderado.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el apoderado con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoApoderado();
        this.empresaService.obtenerEscriturasApoderados(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
          this.escritura.apoderados = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los apoderados. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el apoderado. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaService.guardarEscrituraApoderado(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el apoderado con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoApoderado();
        this.empresaService.obtenerEscriturasApoderados(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
          this.escritura.apoderados = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los apoderados. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el apoderado. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarConsejo(nuevoConsejoForm) {
    if(!nuevoConsejoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo consejo",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraConsejo = nuevoConsejoForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }
    }

    let existeSocioRfc = this.escritura.consejos.filter(x => (x.curp === formValue.curp && x.uuid !== this.consejo?.uuid))
    if(existeSocioRfc.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya hay un miembro del consejo registrado con este CURP",
        ToastType.WARNING
      );
      return;
    }

    let existeConsejoPuesto = this.escritura.consejos.filter(x => x.puesto === formValue.puesto && x.uuid !== this.consejo?.uuid)
    if(existeConsejoPuesto.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya hay un miembro del consejo registrado con este puesto",
        ToastType.WARNING
      );
      return;
    }

    if(this.editandoConsejo) {
      formValue.uuid = this.consejo.uuid;
      formValue.id = this.consejo.id;

      this.empresaService.modificarEscrituraConsejo(this.uuid, this.escritura.uuid, this.consejo.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoConsejo();
        this.empresaService.obtenerEscrituraConsejos(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
          this.escritura.consejos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los consejos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el miembro del consejo. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaService.guardarEscrituraConsejos(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoConsejo();
        this.empresaService.obtenerEscrituraConsejos(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
          this.escritura.consejos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los consejos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el miembro del consejo. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarRepresentante(nuevoRepresentanteForm) {

    if(!nuevoRepresentanteForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo representante",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraRepresentante = nuevoRepresentanteForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }
    }

    let existeSocioRfc = this.escritura.representantes.filter(x => (x.curp === formValue.curp && x.uuid !== this.representante?.uuid))
    if(existeSocioRfc.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya hay un representante registrado con este CURP",
        ToastType.WARNING
      );
      return;
    }

    if(this.editandoRepresentante) {
      formValue.uuid = this.representante.uuid;
      formValue.id = this.representante.id;

      this.empresaService.modificarEscrituraRepresentante(this.uuid, this.escritura.uuid, this.representante.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha actualizado el representante con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoRepresentante();
        this.empresaService.obtenerEscrituraRepresentantes(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
          this.escritura.representantes = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los representantes. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido actualizar el representante. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaService.guardarEscrituraRepresentante(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el representante con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoRepresentante();
        this.empresaService.obtenerEscrituraRepresentantes(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
          this.escritura.representantes = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los representantes. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el representante. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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

  mostrarModalDetalles(rowData, modal) {
    let escrituraUuid = rowData.uuid;
    this.empresaService.obtenerEscrituraPorUuid(this.uuid, escrituraUuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
      this.escrituraSocios = this.escritura.socios;
      this.escrituraApoderados = this.escritura.apoderados;
      this.escrituraRepresentantes = this.escritura.representantes;
      this.escrituraConsejos = this.escritura.consejos;

      this.empresaService.obtenerEscrituraRepresentantesTodos(this.uuid, escrituraUuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
        this.escrituraRepresentantesEliminados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los representantes. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaService.obtenerEscriturasApoderadosTodos(this.uuid, escrituraUuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
        this.escrituraApoderadosEliminados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los apoderados. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaService.obtenerEscrituraConsejosTodos(this.uuid, escrituraUuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
        this.escrituraConsejosEliminados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los representantes. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.empresaService.obtenerEscrituraSociosTodos(this.uuid, escrituraUuid).subscribe((data: EmpresaEscrituraSocio[]) => {
        this.escrituraSociosEliminados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar todos los socios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la escritura. ${error}`,
        ToastType.ERROR
      )
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', scrollable: true});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  guardarEscritura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no estan siendo llenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la escritura",
      ToastType.INFO
    );

    let formValue: EmpresaEscritura = form.value;
    formValue.estadoCatalogo = this.estado;
    formValue.municipioCatalogo = this.municipio;
    formValue.localidadCatalogo = this.localidad;

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('escritura', JSON.stringify(formValue));

    this.empresaService.guardarEscritura(this.uuid, formData).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la escritura. ${error}`,
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

  convertirPdf(pdf: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
  }

  mostrarEscritura(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaService.descargarEscrituraPdf(this.uuid, this.escritura.uuid).subscribe((data: Blob) => {
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

  mostrarModificarEscrituraModal() {
    this.editandoModal = true;

    this.nuevaEscrituraForm.setValue({
      numeroEscritura: this.escritura.numeroEscritura,
      fechaEscritura: this.escritura.fechaEscritura,
      ciudad: this.escritura.ciudad,
      tipoFedatario: this.escritura.tipoFedatario,
      numero: this.escritura.numero,
      nombreFedatario: this.escritura.nombreFedatario,
      apellidoPaterno: this.escritura.apellidoPaterno,
      apellidoMaterno: this.escritura.apellidoMaterno,
      curp: this.escritura.curp,
      descripcion: this.escritura.descripcion
    });

    this.estado = this.escritura.estadoCatalogo;
    this.municipio = this.escritura.municipioCatalogo;
    this.localidad = this.escritura.localidadCatalogo;

    this.estadoService.obtenerEstadosPorMunicipio(this.estado.uuid).subscribe((data: Municipio[]) => {
      this.municipios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los municipios relacionados. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.estadoService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Localidad[]) => {
      this.localidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las localidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.modal = this.modalService.open(this.modificarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  guardarCambiosEscritura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificar los campos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando los cambios en la escritura",
      ToastType.INFO
    );

    let escritura: EmpresaEscritura = form.value;
    escritura.localidadCatalogo = this.localidad;
    escritura.municipioCatalogo = this.municipio;
    escritura.estadoCatalogo = this.estado;

    let formData: FormData = new FormData();
    formData.append('escritura', JSON.stringify(escritura));
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.modificarEscritura(this.uuid, this.escritura.uuid, formData).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se han guardado los cambios de la escritura con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerEscrituraPorUuid(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscritura) => {
          this.escritura = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar la informacion de la escritura. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  confirmarEliminarSocio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidSocio === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del socio a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el socio",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraSocio = form.value;

    let formData = new FormData();
    formData.append('socio', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarEscrituraSocio(this.uuid, this.escritura.uuid, this.tempUuidSocio, formData).subscribe((data: EmpresaEscrituraSocio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el socio con exito",
        ToastType.SUCCESS
      );
      this.empresaService.obtenerEscrituraSocios(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
        this.escritura.socios = data;
        this.modal.close();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los socios. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El socio no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarApoderado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidApoderado === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del apoderado a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el apoderado",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraApoderado = form.value;

    let formData = new FormData();
    formData.append('apoderado', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarEscrituraApoderado(this.uuid, this.escritura.uuid, this.tempUuidApoderado, formData).subscribe((data: EmpresaEscrituraApoderado) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el apoderado con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerEscriturasApoderados(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
        this.escritura.apoderados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los apoderados. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El apoderado no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarEscritura() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la escritura",
      ToastType.INFO
    );

    this.empresaService.eliminarEscritura(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarRepresentante(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidRepresentante === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del representante a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el representante",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraRepresentante = form.value;

    let formData = new FormData();
    formData.append('representante', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarEscrituraRepresentante(this.uuid, this.escritura.uuid, this.tempUuidRepresentante, formData).subscribe((data: EmpresaEscrituraRepresentante) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el representante con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerEscrituraRepresentantes(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
        this.escritura.representantes = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar los representantes. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El representante no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarConsejo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidConsejo === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del miembro del consejo a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el miembro del consejo",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraConsejo = form.value;

    let formData = new FormData();
    formData.append('consejo', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarEscrituraConsejo(this.uuid, this.escritura.uuid, this.tempUuidConsejo, formData).subscribe((data: EmpresaEscrituraConsejo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el miembro consejo con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerEscrituraConsejos(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
        this.escritura.consejos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los consejos. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El miembro del consejo no se ha podido eliminar. Motivo: ${error}`,
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
