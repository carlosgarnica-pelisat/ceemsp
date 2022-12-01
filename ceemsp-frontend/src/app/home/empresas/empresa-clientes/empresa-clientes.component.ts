import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../../_enums/ToastType";
import Cliente from "../../../_models/Cliente";
import Stepper from "bs-stepper";
import ClienteDomicilio from "../../../_models/ClienteDomicilio";
import {TipoInfraestructuraService} from "../../../_services/tipo-infraestructura.service";
import TipoInfraestructura from "../../../_models/TipoInfraestructura";
import validateRfc from "validate-rfc/src";
import {EstadosService} from "../../../_services/estados.service";
import {CalleService} from "../../../_services/calle.service";
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import Calle from "../../../_models/Calle";
import Colonia from "../../../_models/Colonia";
import Localidad from "../../../_models/Localidad";
import {faPencilAlt, faTrash, faMapPin} from "@fortawesome/free-solid-svg-icons";
import {
  BotonEmpresaClientesComponent
} from "../../../_components/botones/boton-empresa-clientes/boton-empresa-clientes.component";
import Empresa from "../../../_models/Empresa";
import Persona from "../../../_models/Persona";
import ClienteAsignacionPersonal from "../../../_models/ClienteAsignacionPersonal";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import ClienteModalidad from "../../../_models/ClienteModalidad";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import {AgmGeocoder} from "@agm/core";
import GeocoderResult = google.maps.GeocoderResult;

@Component({
  selector: 'app-empresa-clientes',
  templateUrl: './empresa-clientes.component.html',
  styleUrls: ['./empresa-clientes.component.css']
})
export class EmpresaClientesComponent implements OnInit {

  editandoModal: boolean = false;

  empresa: Empresa;

  uuid: string;

  faTrash = faTrash;
  faPencilAlt = faPencilAlt;
  faMapPin = faMapPin;

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
  clientes: Cliente[] = [];
  clientesEliminados: Cliente[] = [];

  showDomicilioForm: boolean = false;
  showAsignacionForm: boolean = false;
  showModalidadForm: boolean = false;

  stepper: Stepper;

  private gridApi;
  private gridColumnApi;

  geocodeResult: GeocoderResult;
  domicilioUbicado: boolean = false;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
    {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true },
    {headerName: 'RFC', field: 'rfc', sortable: true, filter: true},
    {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true},
    {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
    {headerName: 'Sucursales', field: 'numeroSucursales', sortable: true, filter: true},
    {headerName: 'Elementos', field: 'numeroElementosAsignados', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
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

  domicilios: ClienteDomicilio[] = [];
  domicilio: ClienteDomicilio;

  asignacion: ClienteAsignacionPersonal;

  tiposInfraestructura: TipoInfraestructura[] = [];
  tipoInfraestructura: TipoInfraestructura = undefined;
  personal: Persona[] = [];

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
  tempUuidCliente: string = "";

  editandoDomicilio: boolean = false;
  editandoAsignacionCliente: boolean = false;

  clienteGuardado: boolean = false;
  domiciliosGuardados: boolean = false;

  temporaryIndex: number;

  @ViewChild('visualizarContratoModal') visualizarContratoModal;
  @ViewChild('clienteDetallesModal') clienteDetallesModal;
  @ViewChild('eliminarClienteModal') eliminarClienteModal;
  @ViewChild('eliminarDomicilioClienteModal') eliminarDomicilioClienteModal;
  @ViewChild('modificarClienteModal') modificarClienteModal;
  @ViewChild('quitarDomicilioClienteModal') quitarDomicilioClienteModal;
  @ViewChild('eliminarDomicilioAsignacionModal') eliminarDomicilioAsignacionModal;
  @ViewChild('eliminarDomicilioModalidadModal') eliminarDomicilioModalidadModal;
  @ViewChild('mostrarUbicacionModal') mostrarUbicacionModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private tipoInfraestructuraService: TipoInfraestructuraService,
              private estadoService: EstadosService, private calleService: CalleService,
              private geocodeService: AgmGeocoder) { }

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
      matriz: ['', Validators.required],
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
      domicilio: ['', [Validators.required]],
      personal: ['', [Validators.required]]
    })

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerClientes(this.uuid).subscribe((data: Cliente[]) => {
      this.rowData = data;
      this.clientes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerClientesEliminados(this.uuid).subscribe((data: Cliente[]) => {
      this.clientesEliminados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes eliminados. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.empresaModalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las modalidades. Motivo: ${error}`,
          ToastType.ERROR
      )
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

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.clienteDetallesModal)
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

    this.empresaService.obtenerClientePorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Cliente) => {
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

    this.empresaService.obtenerClientePorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Cliente) => {
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

    this.empresaService.obtenerClienteContrato(this.uuid, this.cliente.uuid).subscribe((data: Blob) => {
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

  modificarDomicilio(i) {
    this.domicilio = this.domicilios[i];
    this.domicilios.splice(i, 1);
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      matriz: this.domicilio.matriz,
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
    this.modal.close();
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

  mostrarModalCrear(crearDomicilioModal) {
    this.modal = this.modalService.open(crearDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

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

        this.empresaService.guardarCliente(this.uuid, formData).subscribe((data: Cliente) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el cliente con exito",
            ToastType.SUCCESS
          );
          this.clienteGuardado = true;
          this.desactivarCamposCliente();
          this.cliente = data;
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

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando los domicilios",
          ToastType.INFO
        );

        this.empresaService.guardarDomicilioCliente(this.uuid, this.cliente.uuid, this.domicilios).subscribe((data) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se han guardado los domicilios con exito",
            ToastType.SUCCESS
          );
          this.domiciliosGuardados = true;
          this.desactivarCamposDireccion();
          this.cliente.domicilios = this.domicilios;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          );
        });
        break;
      case "MODALIDADES":
        this.stepper.next()
        break;
      case "RESUMEN":
        this.stepper.next();
        break;
    }
  }

  previous() {
    this.stepper.previous();
  }

  mostrarModalDetalles(rowData, modal) {

    let clienteUuid = rowData.uuid;

    this.empresaService.obtenerClientePorUuid(this.uuid, clienteUuid).subscribe((data: Cliente) => {
      this.cliente = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
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

  guardarAsignacion(form) {
    console.log(form)

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
    let cap = new ClienteAsignacionPersonal();
    cap.domicilio = this.cliente?.domicilios.filter(x => x.uuid === value.domicilio)[0]
    cap.personal = this.personal.filter(x => x.uuid === value.personal)[0]

    if(this.editandoAsignacionCliente) {
      this.empresaService.modificarAsignacionCliente(this.uuid, this.cliente.uuid, this.asignacion?.uuid, cap).subscribe((data: ClienteAsignacionPersonal) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la asignacion con exito`,
          ToastType.SUCCESS
        );
        this.mostrarNuevaAsignacionForm();
        this.empresaService.obtenerAsignacionesCliente(this.uuid, this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
          this.cliente.asignaciones = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las asignaciones. Motivo: ${error}`,
            ToastType.ERROR
          );
        });
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar los cambios de la asignacion. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaService.guardarAsignacionCliente(this.uuid, this.cliente.uuid, cap).subscribe((data: ClienteAsignacionPersonal) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la asignacion del cliente con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevaAsignacionForm();
        this.empresaService.obtenerAsignacionesCliente(this.uuid, this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
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
          `No se pudo guardar la asigancion del cliente. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarModalidad(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El formulario no es valido`,
          ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
        "Espere un momento",
        `Estamos guardando la modalidad`,
        ToastType.INFO
    );

    let clienteModalidad: ClienteModalidad = new ClienteModalidad();
    clienteModalidad.modalidad = this.empresaModalidades.filter(x => x.uuid === form.value.modalidad)[0]

    this.empresaService.guardarModalidadCliente(this.uuid, this.cliente.uuid, clienteModalidad).subscribe((data: ClienteModalidad) => {
      this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado la modalidad con exito`,
          ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la modalidad. Motivo: ${error}`,
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

      this.empresaService.modificarDomicilioCliente(this.uuid, this.cliente.uuid, this.domicilio.uuid, domicilio).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se guardo el domicilio con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevoDomicilioForm();
        this.empresaService.obtenerClienteDomicilios(this.uuid, this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
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
      let tempArray: ClienteDomicilio[] = [domicilio];

      this.empresaService.guardarDomicilioCliente(this.uuid, this.cliente.uuid, tempArray).subscribe((data: ClienteDomicilio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el domicilio del cliente con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevoDomicilioForm();
        this.empresaService.obtenerClienteDomicilios(this.uuid, this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
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

    let tipoInfraestructura: TipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === form.value.tipoInfraestructura)[0];

    if(tipoInfraestructura !== undefined && tipoInfraestructura.nombre === "Otro" && domicilio.tipoInfraestructuraOtro === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando el tipo de infraestructura es otro, se debe especificar el tipo. Favor de especificarlo",
        ToastType.WARNING
      );
      return;
    }

    this.domicilios.push(formData);
    this.nuevoClienteDomicilioForm.reset();
    this.nuevoClienteDomicilioForm.patchValue({
      pais: 'Mexico'
    })

    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;
    this.calle = undefined;
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

  mostrarEditarDomicilioCliente(index) {
    this.domicilio = this.cliente.domicilios[index];
    this.mostrarNuevoDomicilioForm();
    this.editandoDomicilio = true;
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      matriz: this.domicilio.matriz,
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

  mostrarModalEliminarCliente(uuid) {
    this.tempUuidDomicilioCliente = uuid;
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

    this.empresaService.modificarCliente(this.uuid, this.cliente.uuid, formValue).subscribe((data: Cliente) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el cliente con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerClientePorUuid(this.uuid, this.cliente.uuid).subscribe((data: Cliente) => {
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

    this.empresaService.eliminarCliente(this.uuid, this.cliente.uuid, formData).subscribe((data: Cliente) => {
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

    this.empresaService.eliminarModalidadCliente(this.uuid, this.cliente.uuid, this.tempUuidModalidadCliente).subscribe((data: ClienteModalidad) => {
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

    this.empresaService.eliminarAsignacionCliente(this.uuid, this.cliente?.uuid, this.tempUuidAsignacionCliente).subscribe((data: ClienteAsignacionPersonal) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la asignacion con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerAsignacionesCliente(this.uuid, this.cliente.uuid).subscribe((data: ClienteAsignacionPersonal[]) => {
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

    this.empresaService.eliminarDomicilioCliente(this.uuid, this.cliente.uuid, this.tempUuidDomicilioCliente).subscribe((data: ClienteDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerClienteDomicilios(this.uuid, this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
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
    this.nuevoClienteDomicilioForm.controls['matriz'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoFijo'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoMovil'].disable();
    this.nuevoClienteDomicilioForm.controls['correoElectronico'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructura'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructuraOtro'].disable();
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

}
