import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../_enums/ToastType";
import Cliente from "../../_models/Cliente";
import Stepper from "bs-stepper";
import ClienteDomicilio from "../../_models/ClienteDomicilio";
import {TipoInfraestructuraService} from "../../_services/tipo-infraestructura.service";
import TipoInfraestructura from "../../_models/TipoInfraestructura";
import {EmpresaClientesService} from "../../_services/empresa-clientes.service";
import {EstadosService} from "../../_services/estados.service";
import Estado from "../../_models/Estado";
import {CalleService} from "../../_services/calle.service";
import Calle from "../../_models/Calle";
import {faMapPin, faPencilAlt, faPlaceOfWorship, faTrash} from "@fortawesome/free-solid-svg-icons";
import Municipio from "../../_models/Municipio";
import Colonia from "../../_models/Colonia";
import Localidad from "../../_models/Localidad";
import validateRfc from "validate-rfc/src";
import {EmpresaPersonalService} from "../../_services/empresa-personal.service";
import Persona from "../../_models/Persona";
import ClienteModalidad from "../../_models/ClienteModalidad";
import EmpresaModalidad from "../../_models/EmpresaModalidad";
import ClienteAsignacionPersonal from "../../_models/ClienteAsignacionPersonal";
import EmpresaFormaEjecucion from "../../_models/EmpresaFormaEjecucion";
import {EmpresaModalidadesService} from "../../_services/empresa-modalidades.service";
import {EmpresaFormasEjecucionService} from "../../_services/empresa-formas-ejecucion.service";
import {Table} from "primeng/table";
import ClienteFormaEjecucion from "../../_models/ClienteFormaEjecucion";
import {
  BotonEmpresaClientesComponent
} from "../../_components/botones/boton-empresa-clientes/boton-empresa-clientes.component";
import {formatDate} from "@angular/common";
import {AgmGeocoder} from "@agm/core";
import GeocoderResult = google.maps.GeocoderResult;
import {ReporteoService} from "../../_services/reporteo.service";

@Component({
  selector: 'app-empresa-clientes',
  templateUrl: './empresa-clientes.component.html',
  styleUrls: ['./empresa-clientes.component.css']
})
export class EmpresaClientesComponent implements OnInit {

  editandoModal: boolean = false;

  uuid: string;

  faTrash = faTrash;
  faPencilAlt = faPencilAlt;
  faMapPin = faMapPin;
  faPlaceOfWorship = faPlaceOfWorship;

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

  empresaModalidades: EmpresaModalidad[] = [];

  estado: Estado;
  municipio: Municipio;
  localidad: Localidad;
  colonia: Colonia;
  calle: Calle;

  tipoPersona: string;

  estadoQuery: string = '';
  municipioQuery: string = '';
  localidadQuery: string = '';
  coloniaQuery: string = '';
  calleQuery: string = '';

  mostrandoEliminados: boolean = false;

  obtenerCallesTimeout = undefined;

  fechaDeHoy = new Date().toISOString().split('T')[0];
  modal: NgbModalRef;
  closeResult: string;
  cliente: Cliente;
  domicilioMatriz: ClienteDomicilio;
  clientes: Cliente[] = [];
  clientesEliminados: Cliente[] = [];

  showDomicilioForm: boolean = false;
  showAsignacionForm: boolean = false;
  showModalidadForm: boolean = false;
  showFormaEjecucionForm: boolean = false;

  modificandoDomicilio: boolean = false;

  stepper: Stepper;

  private gridApi;
  private gridColumnApi;

  geocodeResult: GeocoderResult;
  domicilioUbicado: boolean = false;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true},
    {
      headerName: "Cons.",
      valueGetter: "node.rowIndex + 1",
      pinned: "left",
      width: 70
    },
    {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true },
    {headerName: 'RFC', field: 'rfc', sortable: true, filter: true},
    {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true},
    {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
    {headerName: 'Sucursales', field: 'numeroSucursales', sortable: true, filter: true},
    {headerName: 'Elementos', field: 'numeroElementosAsignados', sortable: true, filter: true},
    {headerName: 'Status de captura', sortable: true, resizable: true, filter: true, valueGetter: function(params) {
        if(params.data.domicilioCapturado && params.data.modalidadCapturada && params.data.formaEjecucionCapturada) {
          return 'COMPLETA'
        } else {
          return 'INCOMPLETA'
        }
      }},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  allColumnDefs = Cliente.obtenerTodasLasColumnas();
  rowData = [];

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  nuevoClienteForm: FormGroup;
  modificarClienteForm: FormGroup;
  nuevoClienteDomicilioForm: FormGroup;
  nuevaAsignacionForm: FormGroup;
  nuevaModalidadForm: FormGroup;
  nuevaFormaEjecucionForm: FormGroup;

  domicilios: ClienteDomicilio[] = [];
  domicilio: ClienteDomicilio;

  asignacion: ClienteAsignacionPersonal;

  clienteModalidad: ClienteModalidad;

  tiposInfraestructura: TipoInfraestructura[] = [];
  tipoInfraestructura: TipoInfraestructura = undefined;
  personal: Persona[] = [];
  formasEjecucion: EmpresaFormaEjecucion[] = [];

  pestanaActual: string = "DETALLES";
  validacionRFCResponse = undefined;

  tempFile;
  pdfActual;

  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;
  motivosEliminacionForm: FormGroup;

  tempUuidDomicilioCliente: string = "";
  tempUuidAsignacionCliente: string = "";
  tempUuidModalidadCliente: string = "";
  tempUuidFormaEjecucionCliente: string = "";
  tempUuidCliente: string = "";

  editandoDomicilio: boolean = false;
  editandoAsignacionCliente: boolean = false;
  editandoModalidad: boolean = false;

  clienteGuardado: boolean = false;
  domiciliosGuardados: boolean = false;
  elementosGuardados: boolean = false;
  formasEjecucionGuardadas: boolean = false;

  temporaryIndex: number;

  modalidad: EmpresaModalidad;
  persona: Persona;
  modalidadQuery: string = "";
  personaQuery: string = "";
  pdfBlob;

  @ViewChild('visualizarContratoModal') visualizarContratoModal;
  @ViewChild('clienteDetallesModal') clienteDetallesModal;
  @ViewChild('eliminarClienteModal') eliminarClienteModal;
  @ViewChild('eliminarFormaEjecucionModal') eliminarFormaEjecucionModal;
  @ViewChild('eliminarDomicilioClienteModal') eliminarDomicilioClienteModal;
  @ViewChild('modificarClienteModal') modificarClienteModal;
  @ViewChild('quitarDomicilioClienteModal') quitarDomicilioClienteModal;
  @ViewChild('eliminarDomicilioAsignacionModal') eliminarDomicilioAsignacionModal;
  @ViewChild('eliminarDomicilioModalidadModal') eliminarDomicilioModalidadModal;
  @ViewChild('mostrarUbicacionModal') mostrarUbicacionModal;
  @ViewChild('ubicarDomicilioModal') ubicarDomicilioModal;
  @ViewChild('crearDomicilioClienteModal') crearDomicilioClienteModal;
  @ViewChild('crearAsignacionClienteModal') crearAsignacionClienteModal;
  @ViewChild('crearModalidadClienteModal') crearModalidadClienteModal;
  @ViewChild('crearFormaEjecucionClienteModal') crearFormaEjecucionClienteModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaClienteService: EmpresaClientesService,
              private formBuilder: FormBuilder, private tipoInfraestructuraService: TipoInfraestructuraService,
              private estadoService: EstadosService, private calleService: CalleService,
              private empresaPersonaService: EmpresaPersonalService, private empresaModalidadService: EmpresaModalidadesService,
              private empresaFormaEjecucionService: EmpresaFormasEjecucionService, private geocodeService: AgmGeocoder,
              private reporteoService: ReporteoService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaClientesComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevoClienteForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      nombreComercial: ['', [Validators.maxLength(100)]],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      canes: ['', Validators.required],
      armas: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      archivo: ['']
    });

    this.modificarClienteForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      canes: ['', Validators.required],
      armas: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });

    this.nuevoClienteDomicilioForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: ['', Validators.maxLength(100)],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      telefonoFijo: ['', []],
      telefonoMovil: ['', []],
      correoElectronico: ['', [Validators.email, Validators.maxLength(100)]],
      tipoInfraestructura: ['', Validators.required],
      tipoInfraestructuraOtro: ['', Validators.maxLength(30)]
    })

    this.nuevaModalidadForm = this.formBuilder.group({
      modalidad: ['', [Validators.required]]
    })

    this.estadoSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.municipioSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.localidadSearchForm = this.formBuilder.group({
      nombre: ['']
    })

    this.coloniaSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.calleSearchForm = this.formBuilder.group({
      nombre: ['']
    })

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: ['', Validators.required],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.nuevaAsignacionForm = this.formBuilder.group({
      domicilio: ['', [Validators.required]]
    })

    this.nuevaFormaEjecucionForm = this.formBuilder.group({
      formaEjecucion: ['', [Validators.required]]
    })

    this.empresaClienteService.obtenerClientes().subscribe((data: Cliente[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaModalidadService.obtenerModalidades().subscribe((data: EmpresaModalidad[]) => {
      this.empresaModalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las modalidades. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.empresaFormaEjecucionService.obtenerFormasEjecucionEmpresa().subscribe((data: EmpresaFormaEjecucion[]) => {
      this.formasEjecucion = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las formas de ejecucion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.tipoInfraestructuraService.obtenerTiposInfraestructura().subscribe((data: TipoInfraestructura[]) => {
      this.tiposInfraestructura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los tipos de infraestructura. Motivo: ${error}`,
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

    this.calleService.obtenerCallesPorLimite(10).subscribe((response: Calle[]) => {
      this.calles = response;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrió un problema",
        `No se pudieron descargar los clientes. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.empresaPersonaService.obtenerPersonalSinAsignar().subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.route.queryParams.subscribe((qp) => {
      if(qp.uuid !== undefined) {
        this.mostrarModalDetalles(qp)
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros no es valido`,
        ToastType.ERROR
      );
    })
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

    this.empresaClienteService.obtenerClientePorUuid(rowData.rowData?.uuid).subscribe((data: Cliente) => {
      this.cliente = data;
      this.editandoModal = false;

      this.modal = this.modalService.open(this.modificarClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"})

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })

      this.modificarClienteForm.patchValue({
        tipoPersona: this.cliente.tipoPersona,
        rfc: this.cliente.rfc,
        nombreComercial: this.cliente.nombreComercial,
        razonSocial: this.cliente.razonSocial,
        canes: this.cliente.canes,
        armas: this.cliente.armas,
        fechaInicio: this.cliente.fechaInicio,
        fechaFin: this.cliente.fechaFin
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el cliente. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

    this.empresaClienteService.obtenerClientePorUuid(rowData.rowData?.uuid).subscribe((data: Cliente) => {
      this.cliente = data;
      this.mostrarModalEliminarCliente(this.uuid);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el cliente. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEliminados() {
    this.mostrandoEliminados = true;
    this.rowData = this.clientesEliminados;
  }

  ocultarEliminados() {
    this.mostrandoEliminados = false;
    this.rowData = this.clientes;
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
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

  descargarContratoPrestacion() {
    this.modal = this.modalService.open(this.visualizarContratoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaClienteService.obtenerClienteContrato(this.cliente.uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      this.pdfBlob = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el PDF. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  modificarDomicilio(i) {
    this.domicilio = this.domicilios[i];
    this.domicilios.splice(i, 1);
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      numeroExterior: this.domicilio.numeroExterior,
      numeroInterior: this.domicilio.numeroInterior,
      domicilio4: this.domicilio.domicilio4,
      codigoPostal: this.domicilio.codigoPostal,
      pais: this.domicilio.pais,
      telefonoFijo: this.domicilio.telefonoFijo,
      telefonoMovil: this.domicilio.telefonoMovil,
      correoElectronico: this.domicilio.correoElectronico,
      tipoInfraestructura: this.domicilio.tipoInfraestructura.uuid,
      tipoInfraestructuraOtro: this.domicilio.tipoInfraestructuraOtro
    });

    this.calle = this.domicilio.calleCatalogo;
    this.colonia = this.domicilio.coloniaCatalogo;
    this.localidad = this.domicilio.localidadCatalogo;
    this.municipio = this.domicilio.municipioCatalogo;
    this.estado = this.domicilio.estadoCatalogo;
  }

  mostrarQuitarClienteDomicilioModal(i) {
    this.temporaryIndex = i;
    this.modal = this.modalService.open(this.quitarDomicilioClienteModal, {size: "lg"});
  }

  confirmarQuitarDomicilioCliente() {
    this.domicilios.splice(this.temporaryIndex, 1);
    this.modal?.close();
  }

  seleccionarEstado(estadoUuid) {
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

  eliminarEstado() {
    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;

    this.nuevoClienteDomicilioForm.patchValue({
      estado: undefined,
      domicilio3: undefined,
      domicilio2: undefined
    })
  }

  seleccionarMunicipio(municipioUuid) {
    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

    this.estadoService.obtenerColoniasPorMunicipioYEstado(this.estado.uuid, municipioUuid).subscribe((data: Colonia[]) => {
      this.colonias = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las colonias. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

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

  eliminarMunicipio() {
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;
    this.calle = undefined;
  }

  seleccionarLocalidad(localidadUuid) {
    this.localidad = this.localidades.filter(x => x.uuid === localidadUuid)[0];
  }

  eliminarLocalidad() {
    this.localidad = undefined;
  }

  seleccionarColonia(coloniaUuid) {
    this.colonia = this.colonias.filter(x => x.uuid === coloniaUuid)[0];
    this.nuevoClienteDomicilioForm.patchValue({
      codigoPostal: this.colonia.codigoPostal
    })
  }

  eliminarColonia() {
    this.colonia = undefined;
  }

  obtenerCalles(event) {
    if(this.obtenerCallesTimeout !== undefined) {
      clearTimeout(this.obtenerCallesTimeout);
    }

    this.obtenerCallesTimeout = setTimeout(() => {
      if(this.calleQuery === '' || this.calleQuery === undefined) {
        this.calleService.obtenerCallesPorLimite(10).subscribe((response: Calle[]) => {
          this.calles = response;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrió un problema",
            `No se pudieron descargar los clientes. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      } else {
        this.calleService.obtenerCallesPorQuery(this.calleQuery).subscribe((response: Calle[]) => {
          this.calles = response;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `Los clientes no se pudieron obtener. Motivo: ${error}`,
            ToastType.ERROR
          )
        });
      }
    }, 1000);
  }

  seleccionarCalle(calleUuid) {
    this.calle = this.calles.filter(x => x.uuid === calleUuid)[0];
  }

  eliminarCalle() {
    this.calle = undefined;
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  validarRfc(event) {
    this.validacionRFCResponse = validateRfc(event.value);
    if(!this.validacionRFCResponse.isValid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El RFC ingresado no es valido. Motivo: ${this.validacionRFCResponse.errors}`,
        ToastType.WARNING
      );
      return;
    }
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  mostrarModalCrear(crearClienteModal) {
    this.modalService.open(crearClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    })

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  seleccionarTipoInfraestructura(target) {
    let uuid = target.value;
    this.tipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === uuid)[0];
  }

  finalizar() {
    window.location.reload();
  }

  next(stepName: string, form) {
    switch (stepName) {
      case "DOMICILIOS":
        if(this.clienteGuardado) {
          this.stepper.next();
          return;
        }

        if(!form.valid) {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            "Faltan algunos campos obligatorios por llenarse",
            ToastType.WARNING
          );
          return;
        }

        let formValue: Cliente = form.value;
        let formData = new FormData();

        if(this.tipoPersona === 'MORAL' && (formValue.nombreComercial === null) || formValue.nombreComercial === undefined) {
          this.toastService.showGenericToast(
            "Espere un momento",
            `Favor de agregar razon social a personas morales`,
            ToastType.WARNING
          );
          return;
        }

        this.toastService.showGenericToast(
          "Espere un momento",
          `Estamos guardando el cliente`,
          ToastType.INFO
        );

        formData.append('cliente', JSON.stringify(formValue));

        if(this.tempFile !== undefined) {
          formData.append('archivo', this.tempFile, this.tempFile.name);
        } else {
          formData.append('archivo', null)
        }

        this.empresaClienteService.guardarCliente(formData).subscribe((data: Cliente) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el cliente con exito",
            ToastType.SUCCESS
          );
          this.clienteGuardado = true;
          this.desactivarCamposCliente();
          this.cliente = data;
          this.cliente.formasEjecucion = [];
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el cliente. ${error}`,
            ToastType.ERROR
          )
        });
        break;
      case "ELEMENTOS":
        if(this.domiciliosGuardados) {
          this.stepper.next();
          return;
        }

        if(this.domicilios.length < 1) {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            "No hay domicilios creados aun para la empresa.",
            ToastType.WARNING
          );
          return;
        }

        this.domiciliosGuardados = true;
        this.desactivarCamposDireccion();
        this.cliente.domicilios = this.domicilios;
        this.stepper.next();
        break;
      case "MODALIDADES":
        if(this.elementosGuardados) {
          this.stepper.next();
          return;
        }

        this.elementosGuardados = true;
        this.stepper.next();
        break;
      case "FORMAS_EJECUCION":
        if(this.formasEjecucionGuardadas) {
          this.stepper.next();
          return;
        }

        this.formasEjecucionGuardadas = true;
        this.stepper.next();
        break;
      case "RESUMEN":
        this.stepper.next();
        break;
    }
  }

  previous() {
    this.stepper.previous();
  }

  mostrarModalDetalles(rowData) {

    let clienteUuid = rowData.uuid;

    this.empresaClienteService.obtenerClientePorUuid(clienteUuid).subscribe((data: Cliente) => {
      this.cliente = data;
      this.domicilioMatriz = this.cliente.domicilios.filter(x => x.matriz === true)[0];

      this.modal = this.modalService.open(this.clienteDetallesModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del cliente. ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarNuevoDomicilioForm() {
    this.showDomicilioForm = !this.showDomicilioForm;
    if(!this.showDomicilioForm) {
      this.nuevoClienteDomicilioForm.reset();
      this.estado = undefined;
      this.municipio = undefined;
      this.localidad = undefined;
      this.colonia = undefined;
      this.calle = undefined;
    }
  }

  mostrarNuevaAsignacionForm() {
    this.showAsignacionForm = !this.showAsignacionForm;
    if(!this.showAsignacionForm) {
      this.nuevaAsignacionForm.reset();
    }
  }

  mostrarNuevaModalidadForm() {
    this.showModalidadForm = !this.showModalidadForm;
    if(!this.showModalidadForm) {
      this.nuevaModalidadForm.reset();
    }
  }

  mostrarNuevaFormaDeEjecucionForm() {
    this.showFormaEjecucionForm = !this.showFormaEjecucionForm;
    if(!this.showFormaEjecucionForm) {
      this.nuevaFormaEjecucionForm.reset();
    }
  }

  clear(table: Table) {
    table.clear();
  }

  cambiarDomicilioAMatriz(uuid: string) {
    if(uuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Uno de los datos es invalido`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos actualizando el domicilio matriz`,
      ToastType.INFO
    );

    this.empresaClienteService.cambiarDomicilioMatriz(this.cliente.uuid, uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha cambiado el domicilio matriz con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido cambiar el domicilio matriz. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAgregarDomicilio() {
    this.modal = this.modalService.open(this.crearDomicilioClienteModal, {size: "xl", backdrop: "static"});
  }

  mostrarModalAsignarPersonal() {
    this.modal = this.modalService.open(this.crearAsignacionClienteModal, {size: 'xl', backdrop: 'static'})

    this.empresaPersonaService.obtenerPersonalSinAsignar().subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalCrearModalidad() {
    this.modal = this.modalService.open(this.crearModalidadClienteModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalCrearFormaEjecucion() {
    this.modal = this.modalService.open(this.crearFormaEjecucionClienteModal, {size: 'xl', backdrop: 'static'})
  }

  guardarAsignacion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la asignacion del cliente",
      ToastType.INFO
    );

    let value = form.value;

    let existeCliente = this.cliente?.asignaciones?.filter(x => x.personal.uuid === value.personal)[0];

    if(existeCliente !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Esta persona ya se encuentra asignada con este cliente`,
        ToastType.WARNING
      );
      return;
    }

    let cap = new ClienteAsignacionPersonal();
    cap.domicilio = this.cliente?.domicilios.filter(x => x.uuid === value.domicilio)[0]
    cap.personal = this.persona

    if(this.editandoAsignacionCliente) {
      this.empresaClienteService.modificarAsignacionCliente(this.cliente.uuid, this.asignacion?.uuid, cap).subscribe((data: ClienteAsignacionPersonal) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la asignacion con exito`,
          ToastType.SUCCESS
        );
        this.mostrarNuevaAsignacionForm();
        this.modal?.close();
        this.empresaClienteService.obtenerAsignacionesCliente(this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
          this.cliente.asignaciones = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las asignaciones. Motivo: ${error}`,
            ToastType.ERROR
          );
        });

        this.empresaPersonaService.obtenerPersonalSinAsignar().subscribe((data: Persona[]) => {
          this.personal = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar el personal. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar los cambios de la asignacion. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaClienteService.guardarAsignacionCliente(this.cliente.uuid, cap).subscribe((data: ClienteAsignacionPersonal) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la asignacion del cliente con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevaAsignacionForm();
        this.modal?.close();
        this.empresaClienteService.obtenerAsignacionesCliente(this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
          this.cliente.asignaciones = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las asignaciones del cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })

        this.empresaPersonaService.obtenerPersonalSinAsignar().subscribe((data: Persona[]) => {
          this.personal = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar el personal. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        console.log(error);
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudo guardar la asigancion del cliente. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarFormaEjecucion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario no es valido`,
        ToastType.WARNING
      );
      return;
    }

    let value = form.value;

    // Checando duplicados
    let existeFormaEjecucion = this.cliente.formasEjecucion.filter(x => x.formaEjecucion === value.formaEjecucion)[0];

    if(existeFormaEjecucion !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Esta forma de ejecucion ya esta asignada a este cliente`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos guardando la forma de ejecucion`,
      ToastType.INFO
    );

    let formaEjecucionCliente: ClienteFormaEjecucion = form.value;

    this.empresaClienteService.guardarFormaEjecucionCliente(this.cliente.uuid, formaEjecucionCliente).subscribe((data: ClienteFormaEjecucion) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la forma de ejecucion con exito`,
        ToastType.SUCCESS
      );
      this.mostrarNuevaFormaDeEjecucionForm();
      this.modal?.close();
      this.empresaClienteService.obtenerFormasEjecucionCliente(this.cliente.uuid).subscribe((data: ClienteFormaEjecucion[]) => {
        this.cliente.formasEjecucion = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las formas de ejecucion del cliente. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la forma de ejecucion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalEliminarFormaEjecucionCliente(uuid) {
    this.tempUuidFormaEjecucionCliente = uuid;
    this.modal = this.modalService.open(this.eliminarFormaEjecucionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarModalidad(form) {
    if(this.modalidad === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No hay una modalidad seleccionada`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando la modalidad`,
      ToastType.INFO
    );

    let existeModalidad = this.cliente?.modalidades?.filter(x => x.modalidad.uuid === this.modalidad.uuid)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La modalidad ya se encuentra registrada en este cliente",
        ToastType.WARNING
      );
      return;
    }

    let clienteModalidad: ClienteModalidad = new ClienteModalidad();
    clienteModalidad.modalidad = this.modalidad;

    if(this.editandoModalidad) {
      this.empresaClienteService.modificarModalidadCliente(this.cliente.uuid, this.clienteModalidad.uuid, clienteModalidad).subscribe((data: ClienteModalidad) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la modalidad con exito`,
          ToastType.SUCCESS
        );
        this.mostrarNuevaModalidadForm();
        this.modal?.close();
        this.editandoModalidad = false;
        this.clienteModalidad = undefined;
        this.modalidad = undefined;
        this.empresaClienteService.obtenerModalidadesCliente(this.cliente.uuid).subscribe((data: ClienteModalidad[]) => {
          this.cliente.modalidades = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las modalidades. Motivo: ${error}`,
            ToastType.SUCCESS
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar la modalidad. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaClienteService.guardarModalidadCliente(this.cliente.uuid, clienteModalidad).subscribe((data: ClienteModalidad) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la modalidad con exito`,
          ToastType.SUCCESS
        );
        this.mostrarNuevaModalidadForm();
        this.modal?.close();
        this.clienteModalidad = undefined;
        this.modalidad = undefined;
        this.empresaClienteService.obtenerModalidadesCliente(this.cliente.uuid).subscribe((data: ClienteModalidad[]) => {
          this.cliente.modalidades = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las modalidades. Motivo: ${error}`,
            ToastType.SUCCESS
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la modalidad. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  localizarDomicilio(form) {
    if(!form.valid || this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Favor de proporcionar mas datos para hacer la busqueda mas precisa",
        ToastType.WARNING
      );
      return;
    }

    let clienteDomicilio: ClienteDomicilio = form.value;
    clienteDomicilio.estadoCatalogo = this.estado;
    clienteDomicilio.municipioCatalogo = this.municipio;
    clienteDomicilio.localidadCatalogo = this.localidad;
    clienteDomicilio.coloniaCatalogo = this.colonia;
    clienteDomicilio.calleCatalogo = this.calle;

    let query = `${clienteDomicilio?.calleCatalogo?.nombre} ${clienteDomicilio?.numeroExterior} ${clienteDomicilio?.numeroInterior} ${clienteDomicilio?.coloniaCatalogo.nombre} ${clienteDomicilio?.municipioCatalogo?.nombre} ${clienteDomicilio?.estadoCatalogo?.nombre}`

    this.geocodeService.geocode({
      address: query
    }).subscribe((data: GeocoderResult[]) => {
      this.geocodeResult = data[0];
      this.domicilioUbicado = true;
      //this.modal = this.modalService.open(this.mostrarUbicacionModal, {size: 'xl'})
    }, (error) => {
      this.domicilioUbicado = false;
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `Ocurrio un problema cuando el domicilio era ubicado en el mapa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  ubicarDomicilio(form) {
    if(!form.valid || this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Favor de proporcionar mas datos para hacer la busqueda mas precisa",
        ToastType.WARNING
      );
      return;
    }

    let clienteDomicilio: ClienteDomicilio = form.value;
    clienteDomicilio.estadoCatalogo = this.estado;
    clienteDomicilio.municipioCatalogo = this.municipio;
    clienteDomicilio.localidadCatalogo = this.localidad;
    clienteDomicilio.coloniaCatalogo = this.colonia;
    clienteDomicilio.calleCatalogo = this.calle;

    let query = `${clienteDomicilio?.calleCatalogo?.nombre} ${clienteDomicilio?.numeroExterior} ${clienteDomicilio?.numeroInterior} ${clienteDomicilio?.coloniaCatalogo.nombre} ${clienteDomicilio?.municipioCatalogo?.nombre} ${clienteDomicilio?.estadoCatalogo?.nombre}`

    this.geocodeService.geocode({
      address: query
    }).subscribe((data: GeocoderResult[]) => {
      this.geocodeResult = data[0];
      this.domicilioUbicado = true;
      this.modal = this.modalService.open(this.ubicarDomicilioModal, {size: 'xl'})
    }, (error) => {
      this.domicilioUbicado = false;
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `Ocurrio un problema cuando el domicilio era ubicado en el mapa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el domicilio del cliente",
      ToastType.INFO
    );

    let domicilio: ClienteDomicilio = form.value;
    let tipoInfraestructura: TipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === form.value.tipoInfraestructura)[0];

    if(tipoInfraestructura !== undefined && tipoInfraestructura.nombre === "Otro" && domicilio.tipoInfraestructuraOtro === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando el tipo de infraestructura es otro, se debe especificar el tipo. Favor de especificarlo",
        ToastType.WARNING
      );
      return;
    }

    domicilio.tipoInfraestructura = tipoInfraestructura;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;
    domicilio.localidadCatalogo = this.localidad;

    if(this.geocodeResult !== undefined) {
      domicilio.latitud = this.geocodeResult.geometry.location.lat().toString()
      domicilio.longitud = this.geocodeResult.geometry.location.lng().toString()
    }

    if(this.editandoDomicilio) {
      domicilio.id = this.domicilio.id;
      domicilio.uuid = this.domicilio.uuid;

      this.empresaClienteService.modificarDomicilioCliente(this.cliente.uuid, this.domicilio.uuid, domicilio).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se guardo el domicilio con exito",
          ToastType.SUCCESS
        );
        this.modal?.close();
        this.domicilioUbicado = false;
        this.mostrarNuevoDomicilioForm();
        this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
          this.cliente.domicilios = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el domicilio del cliente. Motivo: ${error}`,
          ToastType.ERROR
        )
      });
    } else {
      this.empresaClienteService.guardarDomicilioCliente(this.cliente.uuid, domicilio).subscribe((data: ClienteDomicilio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el domicilio del cliente con exito",
          ToastType.SUCCESS
        );
        this.modal?.close();
        this.domicilioUbicado = false;
        this.mostrarNuevoDomicilioForm();
        this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
          this.cliente.domicilios = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el domicilio. Motivo: ${error}`,
          ToastType.ERROR
        )
      });
    }
  }

  agregarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenar",
        ToastType.WARNING
      );
      return;
    }

    let formData: ClienteDomicilio = form.value;
    formData.tipoInfraestructura = this.tipoInfraestructura;
    if(formData.tipoInfraestructura.nombre === "Otro" && formData.tipoInfraestructuraOtro === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El tipo de infraestructura es otro y no se especifico el tipo",
        ToastType.WARNING
      );
      return;
    }

    // Checar catalogos
    if(this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Alguno de los campos catalogo a llenar no esta lleno",
        ToastType.WARNING
      );
      return;
    }

    let domicilio: ClienteDomicilio = form.value;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.localidadCatalogo = this.localidad;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;

    if(this.geocodeResult !== undefined) {
      formData.latitud = this.geocodeResult.geometry.location.lat().toString()
      formData.longitud = this.geocodeResult.geometry.location.lng().toString()
    }

    let tipoInfraestructura: TipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === form.value.tipoInfraestructura)[0];

    if(tipoInfraestructura !== undefined && tipoInfraestructura.nombre === "Otro" && domicilio.tipoInfraestructuraOtro === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando el tipo de infraestructura es otro, se debe especificar el tipo. Favor de especificarlo",
        ToastType.WARNING
      );
      return;
    }

    if(this.modificandoDomicilio) {
      this.empresaClienteService.modificarDomicilioCliente(this.cliente.uuid, formData?.uuid, formData).subscribe((data: ClienteDomicilio) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha modificado el domicilio con exito`,
          ToastType.SUCCESS
        );
        this.modificandoDomicilio = false;

        this.domicilios.push(data);
        this.nuevoClienteDomicilioForm.reset();
        this.nuevoClienteDomicilioForm.patchValue({
          pais: 'Mexico'
        })

        this.estado = undefined;
        this.municipio = undefined;
        this.localidad = undefined;
        this.colonia = undefined;
        this.calle = undefined;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el domicilio del cliente. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaClienteService.guardarDomicilioCliente(this.cliente?.uuid, formData).subscribe((data: ClienteDomicilio) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado el domicilio con exito`,
          ToastType.SUCCESS
        );

        this.domicilios.push(data);
        this.nuevoClienteDomicilioForm.reset();
        this.nuevoClienteDomicilioForm.patchValue({
          pais: 'Mexico'
        })

        this.estado = undefined;
        this.municipio = undefined;
        this.localidad = undefined;
        this.colonia = undefined;
        this.calle = undefined;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el domicilio. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  mostrarModalEditarCliente() {
    this.editandoModal = true;

    this.modal = this.modalService.open(this.modificarClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })

    this.modificarClienteForm.patchValue({
      tipoPersona: this.cliente.tipoPersona,
      rfc: this.cliente.rfc,
      nombreComercial: this.cliente.nombreComercial,
      razonSocial: this.cliente.razonSocial,
      canes: this.cliente.canes,
      armas: this.cliente.armas,
      fechaInicio: this.cliente.fechaInicio
    })
  }

  mostrarEditarAsignacionCliente(index) {
    this.asignacion = this.cliente?.asignaciones[index];
    this.mostrarNuevaAsignacionForm();
    this.editandoAsignacionCliente = true;
    this.nuevaAsignacionForm.patchValue({
      domicilio: this.asignacion?.domicilio?.uuid,
      personal: this.asignacion?.personal?.uuid
    })
  }

  mostrarEditarDomicilioCliente(uuid) {
    this.domicilio = this.cliente.domicilios.filter(x => x.uuid === uuid)[0];
    this.modal = this.modalService.open(this.crearDomicilioClienteModal, {size: 'xl', backdrop: 'static'})
    this.mostrarNuevoDomicilioForm();
    this.editandoDomicilio = true;
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      numeroExterior: this.domicilio.numeroExterior,
      numeroInterior: this.domicilio.numeroInterior,
      domicilio4: this.domicilio.domicilio4,
      codigoPostal: this.domicilio.codigoPostal,
      pais: this.domicilio.pais,
      telefonoFijo: this.domicilio.telefonoFijo,
      telefonoMovil: this.domicilio.telefonoMovil,
      correoElectronico: this.domicilio.correoElectronico,
      tipoInfraestructura: this.domicilio.tipoInfraestructura.uuid,
      tipoInfraestructuraOtro: this.domicilio.tipoInfraestructuraOtro
    });

    this.calle = this.domicilio.calleCatalogo;
    this.colonia = this.domicilio.coloniaCatalogo;
    this.localidad = this.domicilio.localidadCatalogo;
    this.municipio = this.domicilio.municipioCatalogo;
    this.estado = this.domicilio.estadoCatalogo;
  }

  mostrarEditarModalidadCliente(uuid) {
    this.clienteModalidad = this.cliente.modalidades.filter(x => x.uuid === uuid)[0];
    this.modal = this.modalService.open(this.crearModalidadClienteModal, {size: 'xl', backdrop: "static"})
    this.modalidad = this.clienteModalidad.modalidad;
    this.mostrarNuevaModalidadForm();
    this.editandoModalidad = true;
  }

  mostrarModalEliminarCliente(uuid) {
    this.tempUuidDomicilioCliente = uuid;
    this.motivosEliminacionForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionForm.controls['fechaBaja'].disable();
    this.modal = this.modalService.open(this.eliminarClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarUbicacionDomicilioModal(uuid) {
    this.domicilio = this.cliente.domicilios.filter(x => x.uuid === uuid)[0]
    this.modal = this.modalService.open(this.mostrarUbicacionModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalEliminarAsignacionCliente(uuid) {
    this.tempUuidAsignacionCliente = uuid;
    this.modal = this.modalService.open(this.eliminarDomicilioAsignacionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarModalidadCliente(uuid) {
    this.tempUuidModalidadCliente = uuid;
    this.modal = this.modalService.open(this.eliminarDomicilioModalidadModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarDomicilioCliente(uuid) {
    this.tempUuidDomicilioCliente = uuid;
    this.modal = this.modalService.open(this.eliminarDomicilioClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarCambiosCliente(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario esta invalido. Favor de verificarlo",
        ToastType.WARNING
      );
      return;
    }

    let formValue: Cliente = form.value;

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el cliente",
      ToastType.INFO
    );

    this.empresaClienteService.modificarCliente(this.cliente.uuid, formValue).subscribe((data: Cliente) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el cliente con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal?.close();
        this.empresaClienteService.obtenerClientePorUuid(this.cliente.uuid).subscribe((data: Cliente) => {
          this.cliente = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido obtener el cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      } else {
        window.location.reload();
      }

    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el cliente. ${error}`,
        ToastType.ERROR
      )
    });
  }

  confirmarEliminarCliente(form) {
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
      "Se esta eliminando el cliente",
      ToastType.INFO
    );

    let formValue: Cliente = form.value;

    let formData = new FormData();
    formData.append('cliente', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaClienteService.eliminarCliente(this.cliente.uuid, formData).subscribe((data: Cliente) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el cliente con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El cliente no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarFormaEjecucionCliente() {
    if(this.tempUuidFormaEjecucionCliente === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la forma de ejecucion del cliente a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la forma de ejecucion del cliente",
      ToastType.INFO
    );

    this.empresaClienteService.eliminarFormaEjecucionCliente(this.cliente?.uuid, this.tempUuidFormaEjecucionCliente).subscribe((data: ClienteFormaEjecucion) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado la forma de ejecucion del cliente`,
        ToastType.SUCCESS
      );
      this.modal?.close();
      this.empresaClienteService.obtenerFormasEjecucionCliente(this.cliente?.uuid).subscribe((data: ClienteFormaEjecucion[]) => {
        this.cliente.formasEjecucion = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener las formas de ejecucion del cliente. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la forma de ejecucion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarModalidad() {
    if(this.tempUuidAsignacionCliente === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la asignacion del cliente a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la modalidad del cliente",
      ToastType.INFO
    );

    this.empresaClienteService.eliminarModalidadCliente(this.cliente.uuid, this.tempUuidModalidadCliente).subscribe((data: ClienteModalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado la modalidad con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la modalidad. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarAsignacionCliente() {
    if(this.tempUuidAsignacionCliente === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la asignacion del cliente a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la asignacion del cliente",
      ToastType.INFO
    );

    this.empresaClienteService.eliminarAsignacionCliente(this.cliente?.uuid, this.tempUuidAsignacionCliente).subscribe((data: ClienteAsignacionPersonal) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la asignacion con exito",
        ToastType.SUCCESS
      );
      this.modal?.close();
      this.empresaClienteService.obtenerAsignacionesCliente(this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
        this.cliente.asignaciones = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las asignaciones del cliente. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido eliminar la asignacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarDomicilioCliente() {
    if(this.tempUuidDomicilioCliente === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del domicilio del cliente a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el domicilio del cliente",
      ToastType.INFO
    );

    this.empresaClienteService.eliminarDomicilioCliente(this.cliente.uuid, this.tempUuidDomicilioCliente).subscribe((data: ClienteDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.modal?.close();
      this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
        this.cliente.domicilios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio del cliente no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  seleccionarModalidad(uuid) {
    this.modalidad = this.empresaModalidades.filter(x => x.uuid === uuid)[0];
  }

  seleccionarPersona(uuid) {
    this.persona = this.personal.filter(x => x.uuid === uuid)[0];
  }

  quitarModalidad() {
    this.modalidad = undefined;
  }

  quitarPersona() {
    this.persona = undefined;
  }

  private desactivarCamposCliente() {
    this.nuevoClienteForm.controls['tipoPersona'].disable();
    this.nuevoClienteForm.controls['rfc'].disable();
    this.nuevoClienteForm.controls['nombreComercial'].disable();
    this.nuevoClienteForm.controls['razonSocial'].disable();
    this.nuevoClienteForm.controls['canes'].disable();
    this.nuevoClienteForm.controls['armas'].disable();
    this.nuevoClienteForm.controls['fechaInicio'].disable();
    this.nuevoClienteForm.controls['archivo'].disable();
  }

  private desactivarCamposDireccion() {
    this.nuevoClienteDomicilioForm.controls['nombre'].disable();
    this.nuevoClienteDomicilioForm.controls['numeroExterior'].disable();
    this.nuevoClienteDomicilioForm.controls['numeroInterior'].disable();
    this.nuevoClienteDomicilioForm.controls['domicilio4'].disable();
    this.nuevoClienteDomicilioForm.controls['codigoPostal'].disable();
    this.nuevoClienteDomicilioForm.controls['pais'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoFijo'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoMovil'].disable();
    this.nuevoClienteDomicilioForm.controls['correoElectronico'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructura'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructuraOtro'].disable();
  }

  private desactivarCamposAsignacionPersonal() {
    this.nuevaAsignacionForm.controls['domicilio'].disable();
    this.nuevaAsignacionForm.controls['personal'].disable();
  }

  convertStringToNumber(input: string) {
    if (!input) return NaN;
    if (input.trim().length==0) {
      return NaN;
    }
    return Number(input);
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

  private hacerFalsoUuid(longitud) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < longitud; i++ ) {
      result += characters.charAt(Math.floor(Math.random() *
        charactersLength));
    }
    return result;
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

  generarReporteExcel() {
    this.reporteoService.generarReporteClientes().subscribe((data) => {
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

  descargarContratoPdf() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "contrato.pdf";
    link.click();
  }

}
