import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import PersonalNacionalidad from "../../../_models/PersonalNacionalidad";
import PersonalPuestoTrabajo from "../../../_models/PersonalPuestoTrabajo";
import {PersonalService} from "../../../_services/personal.service";
import {ToastType} from "../../../_enums/ToastType";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Stepper from "bs-stepper";
import Persona from "../../../_models/Persona";
import PersonaCertificacion from "../../../_models/PersonaCertificacion";
import PersonalSubpuestoTrabajo from "../../../_models/PersonalSubpuestoTrabajo";
import Modalidad from "../../../_models/Modalidad";
import {faDownload, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import Calle from "../../../_models/Calle";
import Colonia from "../../../_models/Colonia";
import Localidad from "../../../_models/Localidad";
import {EstadosService} from "../../../_services/estados.service";
import {CalleService} from "../../../_services/calle.service";
import ExistePersona from "../../../_models/ExistePersona";
import {ValidacionService} from "../../../_services/validacion.service";
import PersonaFotografiaMetadata from "../../../_models/PersonaFotografiaMetadata";
import {
  BotonEmpresaPersonalComponent
} from "../../../_components/botones/boton-empresa-personal/boton-empresa-personal.component";
import Empresa from "../../../_models/Empresa";
import Can from "../../../_models/Can";
import Vehiculo from "../../../_models/Vehiculo";
import Arma from "../../../_models/Arma";
import PersonalCan from "../../../_models/PersonalCan";
import PersonalVehiculo from "../../../_models/PersonalVehiculo";
import PersonalArma from "../../../_models/PersonalArma";

@Component({
  selector: 'app-empresa-personal',
  templateUrl: './empresa-personal.component.html',
  styleUrls: ['./empresa-personal.component.css']
})
export class EmpresaPersonalComponent implements OnInit {
  private gridApi;
  private gridColumnApi;

  fechaDeHoy = new Date().toISOString().split('T')[0];

  faDownload = faDownload;
  faTrash = faTrash;
  faPencilAlt = faPencilAlt;

  cuipValida: boolean = false;

  @ViewChild('mostrarFotoPersonaModal') mostrarFotoPersonaModal: any;
  @ViewChild('visualizarCertificacionPersonaModal') visualizarCertificacionPersonaModal: any;
  @ViewChild('visualizarVolanteCuip') visualizarVolanteCuip: any;

  stepper: Stepper;
  pestanaActual: string = "DETALLES";

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Apellido paterno', field: 'apellidoPaterno', sortable: true, filter: true },
    {headerName: 'Apellido materno', field: 'apellidoMaterno', sortable: true, filter: true},
    {headerName: 'Nombre(s)', field: 'nombres', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  rowData = [];

  uuid: string;
  empresa: Empresa;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;

  modalidadSearchForm: FormGroup;
  nacionalidadSearchForm: FormGroup;
  motivosEliminacionForm: FormGroup;

  estado: Estado;
  municipio: Municipio;
  localidad: Localidad;
  colonia: Colonia;
  calle: Calle;

  estadoQuery: string = '';
  municipioQuery: string = '';
  localidadQuery: string = '';
  coloniaQuery: string = '';
  calleQuery: string = '';
  nacionalidadQuery: string = '';

  tempFile;
  imagenActual: any;
  imagenPrincipal: any;

  nacionalidades: PersonalNacionalidad[] = [];
  puestosTrabajo: PersonalPuestoTrabajo[] = [];
  domicilios: EmpresaDomicilio[] = [];
  modalidades: EmpresaModalidad[] = [];
  personalEliminado: Persona[] = [];
  personal: Persona[] = [];

  canes: Can[];
  armasCortas: Arma[];
  armasLargas: Arma[];
  vehiculos: Vehiculo[];

  puestoTrabajo: PersonalPuestoTrabajo;
  subpuestoTrabajo: PersonalSubpuestoTrabajo;

  crearPersonalForm: FormGroup;
  crearPersonalPuestoForm: FormGroup;
  crearPersonalCertificadoForm: FormGroup;
  crearPersonaFotografiaForm: FormGroup;
  asignarCanPersonalForm: FormGroup;
  asignarArmaCortaForm: FormGroup;
  asignarArmaLargaForm: FormGroup;
  asignarVehiculoPersonalForm: FormGroup;

  persona: Persona;
  modalidad: Modalidad;
  empresaModalidad: EmpresaModalidad;
  domicilio: EmpresaDomicilio;
  nacionalidad: PersonalNacionalidad;
  cuipStatus: string;

  showCertificadoForm: boolean;
  showFotografiaForm: boolean;
  showPuestoForm: boolean;

  obtenerCallesTimeout = undefined;

  existePersona: ExistePersona = undefined;

  tempUuidPersonal: string = "";
  tempUuidCapacitacion: string = "";
  tempUuidFotografia: string = "";

  editandoCapacitacion: boolean = false;

  personaCertificacion: PersonaCertificacion;
  pdfActual;
  mostrandoEliminados: boolean = false;

  editandoModal: boolean = false;
  personaCurso: PersonaCertificacion;
  personaFotografia: PersonaFotografiaMetadata;

  infoPersonalGuardada: boolean = false;
  infoPuestoGuardado: boolean = false;
  cursosGuardados: boolean = false;
  fotografiasGuardadas: boolean = false;

  @ViewChild('mostrarDetallesPersonaModal') mostrarDetallesPersonaModal;
  @ViewChild('eliminarCapacitacionesModal') eliminarCapacitacionesModal;
  @ViewChild('eliminarPersonalModal') eliminarPersonalModal;
  @ViewChild('eliminarFotografiasModal') eliminarFotografiasModal;
  @ViewChild('modificarPersonalModal') modificarPersonalModal;

  @ViewChild('asignarCanPersonaModal') asignarCanPersonaModal;
  @ViewChild('asignarVehiculoPersonaModal') asignarVehiculoPersonaModal;
  @ViewChild('asignarArmaCortaModal') asignarArmaCortaModal;
  @ViewChild('asignarArmaLargaModal') asignarArmaLargaModal;

  @ViewChild('desasignarCanPersonaModal') desasignarCanPersonaModal;
  @ViewChild('desasignarVehiculoPersonaModal') desasignarVehiculoPersonaModal;
  @ViewChild('desasignarArmaCortaPersonaModal') desasignarArmaCortaPersonaModal;
  @ViewChild('desasignarArmaLargaPersonaModal') desasignarArmaLargaPersonaModal;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private personalService: PersonalService, private empresaService: EmpresaService,
              private estadoService: EstadosService, private calleService: CalleService,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaPersonalComponent
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

    this.crearPersonalForm = this.formBuilder.group({
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]],
      rfc: ['', [Validators.minLength(13), Validators.maxLength(13)]],
      apellidoPaterno: ['', [Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      fechaNacimiento: ['', Validators.required],
      fechaIngreso: ['', Validators.required],
      sexo: ['', Validators.required],
      tipoSangre: ['', Validators.required],
      estadoCivil: ['', Validators.required],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: [''],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      telefono: ['', Validators.required],
      correoElectronico: ['', [Validators.email, Validators.maxLength(255)]]
    });

    this.crearPersonalPuestoForm = this.formBuilder.group({
      puesto: ['', Validators.required],
      subpuesto: ['', Validators.required],
      detallesPuesto: ['', Validators.required],
      domicilioAsignado: ['', Validators.required],
      estatusCuip: [''],
      cuip: [''],
      numeroVolanteCuip: [''],
      fechaVolanteCuip: [''],
      modalidad: ['']
    });

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.crearPersonalCertificadoForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      nombreInstructor: ['', [Validators.required, Validators.maxLength(100)]],
      duracion: ['', [Validators.required, Validators.min(1), Validators.max(9999)]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      archivo: ['', Validators.required]
    })

    this.crearPersonaFotografiaForm = this.formBuilder.group({
      file: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.nacionalidadSearchForm = this.formBuilder.group({
      nombre: ['']
    })

    this.asignarCanPersonalForm = this.formBuilder.group({
      can: ['', [Validators.required]],
      observaciones: ['']
    })

    this.asignarVehiculoPersonalForm = this.formBuilder.group({
      vehiculo: ['', [Validators.required]],
      observaciones: ['']
    })

    this.asignarArmaCortaForm = this.formBuilder.group({
      arma: ['', [Validators.required]],
      observaciones: ['']
    })

    this.asignarArmaLargaForm = this.formBuilder.group({
      arma: ['', [Validators.required]],
      observaciones: ['']
    })

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.rowData = data;
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar el personal. ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerPersonalEliminado(this.uuid).subscribe((data: Persona[]) => {
      this.personalEliminado = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar el personal eliminado. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.personalService.obtenerNacionalidades().subscribe((data: PersonalNacionalidad[]) => {
      this.nacionalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las nacionalidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.personalService.obtenerPuestosPersonal().subscribe((data: PersonalPuestoTrabajo[]) => {
      this.puestosTrabajo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los puestos de trabajo`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
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
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  seleccionarModalidad(uuid) {
    this.empresaModalidad = this.modalidades.filter(x => x.uuid === uuid)[0];
  }

  seleccionarNacionalidad(uuid) {
    this.nacionalidad = this.nacionalidades.filter(x => x.uuid === uuid)[0];
  }

  quitarModalidad() {
    this.empresaModalidad = undefined;
  }

  quitarNacionalidad() {
    this.nacionalidad = undefined;
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarDetallesPersonaModal)
  }

  mostrarEliminados() {
    this.mostrandoEliminados = true;
    this.rowData = this.personalEliminado;
  }

  ocultarEliminados() {
    this.mostrandoEliminados = false;
    this.rowData = this.personal;
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

    this.empresaService.obtenerPersonalPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Persona) => {
      this.persona = data;
      this.editandoModal = false;

      this.crearPersonalForm.patchValue({
        curp: this.persona.curp,
        rfc: this.persona?.rfc,
        nacionalidad: this.persona.nacionalidad.uuid,
        apellidoPaterno: this.persona.apellidoPaterno,
        apellidoMaterno: this.persona.apellidoMaterno,
        nombres: this.persona.nombres,
        fechaNacimiento: this.persona.fechaNacimiento,
        sexo: this.persona.sexo,
        tipoSangre: this.persona.tipoSangre,
        fechaIngreso: this.persona.fechaIngreso,
        estadoCivil: this.persona.estadoCivil,
        numeroExterior: this.persona.numeroExterior,
        numeroInterior: this.persona.numeroInterior,
        domicilio4: this.persona.domicilio4,
        codigoPostal: this.persona.codigoPostal,
        pais: this.persona.pais,
        telefono: this.persona.telefono,
        correoElectronico: this.persona.correoElectronico
      })

      this.nacionalidad = this.persona.nacionalidad;

      this.calle = this.persona.calleCatalogo;
      this.localidad = this.persona.localidadCatalogo;
      this.municipio = this.persona.municipioCatalogo;
      this.estado = this.persona.estadoCatalogo;
      this.colonia = this.persona.coloniaCatalogo;

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

      this.estadoService.obtenerColoniasPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Colonia[]) => {
        this.colonias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las colonias. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.modal = this.modalService.open(this.modificarPersonalModal,{ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: 'static'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el personal. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  eliminar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede eliminar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaService.obtenerPersonalPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Persona) => {
      this.persona = data;
      this.mostrarModalEliminarPersona();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la persona. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verificarPersonal(event) {
    let nacionalidadUuid = this.crearPersonalForm.controls.nacionalidad.value;
    let nacionalidad = this.nacionalidad;

    if(nacionalidad.nombre === 'Mexicana') {
      let existePersona: ExistePersona = new ExistePersona();
      existePersona.curp = event.value;

      if(existePersona.curp.length !== 18) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No se puede consultar la empresa en la base de datos. El CURP tiene longitud invalida",
          ToastType.WARNING
        );
        return;
      }

      this.validacionService.validarPersona(existePersona).subscribe((data: ExistePersona) => {
          this.existePersona = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido validar la persona en la base de datos.`,
            ToastType.ERROR
          );
        }
      )
    } else {
      this.existePersona = undefined;
    }
  }

  seleccionarEstado(estadoUuid) {
    // DELETING EVERYTHING!
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
    this.crearPersonalForm.patchValue({
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
          console.log(response);
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

  mostrarModalDetalles(data, modal) {
    this.empresaService.obtenerPersonalPorUuid(this.uuid, data.uuid).subscribe((data: Persona) => {
      this.persona = data;
      if(this.persona?.fotografias.length > 0) {
        let personaFoto = this.persona?.fotografias[0];
        this.empresaService.descargarPersonaFotografia(this?.uuid, this?.persona?.uuid, personaFoto.uuid).subscribe((data: Blob) => {
          this.convertirImagenPrincipal(data);
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar la fotografia. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      } else {
        this.imagenPrincipal = undefined;
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion del personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarPestana(nombrePestana) {
    this.pestanaActual = nombrePestana;
  }

  cancelarCambiosPersonal() {
    this.crearPersonalForm.reset();
    this.calle = undefined;
    this.colonia = undefined;
    this.localidad = undefined;
    this.municipio = undefined;
    this.estado = undefined;
    this.modal.close();
  }

  next(stepName: string, form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }
    switch (stepName) {
      case "INFORMACION_PUESTO":
        if(this.infoPersonalGuardada) {
          this.stepper.next();
        } else {
          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando la persona",
            ToastType.INFO
          );

          let formValue: Persona = form.value;

          formValue.nacionalidad = this.nacionalidad;
          formValue.calleCatalogo = this.calle;
          formValue.coloniaCatalogo = this.colonia;
          formValue.localidadCatalogo = this.localidad;
          formValue.municipioCatalogo = this.municipio;
          formValue.estadoCatalogo = this.estado;

          this.empresaService.guardarPersonal(this.uuid, formValue).subscribe((data: Persona) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la persona con exito",
              ToastType.SUCCESS
            );
            this.persona = data;
            this.infoPersonalGuardada = true;
            this.desactivarFormularioInfoPersonal()
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la persona. ${error}`,
              ToastType.ERROR
            )
          });
        }
        break;
      case "CURSOS":
        if(this.infoPuestoGuardado) {
          this.stepper.next();
        } else {
          if(!this.cuipValida && this.cuipStatus === 'TRAMITADO') {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `La CUIP no es valida. Favor de verificarla`,
              ToastType.WARNING
            );
            return;
          }

          let value: Persona = form.value;

          value.puestoDeTrabajo = this.puestoTrabajo;
          value.subpuestoDeTrabajo = this.subpuestoTrabajo;
          value.domicilioAsignado = this.domicilio;
          value.modalidad = this.modalidad;
          value.estatusCuip = this.cuipStatus;

          let puestoTrabajoFormData: FormData = new FormData();
          puestoTrabajoFormData.append('persona', JSON.stringify(value));
          if(this.tempFile !== undefined) {
            puestoTrabajoFormData.append('archivo', this.tempFile, this.tempFile.name);
          } else {
            puestoTrabajoFormData.append('archivo', null);
          }

          this.empresaService.modificarInformacionTrabajo(this.uuid, this.persona.uuid, puestoTrabajoFormData).subscribe((data: Persona) => {
            this.toastService.showGenericToast(
              "Listo",
              `Se ha guardado la informacion del trabajo con exito`,
              ToastType.SUCCESS
            );
            this.persona.puestoDeTrabajo = data.puestoDeTrabajo
            this.persona.subpuestoDeTrabajo = data?.subpuestoDeTrabajo
            this.persona.detallesPuesto = data?.detallesPuesto
            this.persona.estatusCuip = data?.estatusCuip
            this.persona.cuip = data?.cuip
            this.persona.numeroVolanteCuip = data?.numeroVolanteCuip
            this.persona.fechaVolanteCuip = data?.fechaVolanteCuip
            this.infoPuestoGuardado = true;
            this.desactivarFormularioInfoPuesto()
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido actualizar la informacion del trabajo. Motivo: ${error}`,
              ToastType.ERROR
            );
          })
        }
        break;
      case "FOTOGRAFIAS":
        if(this.fotografiasGuardadas) {
          this.stepper.next()
        } else {
          let personaCertificacion: PersonaCertificacion = form.value;

          let fechaInicio = new Date(personaCertificacion.fechaInicio);
          let fechaFin = new Date(personaCertificacion.fechaFin);
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
            "Estamos guardando la certificacion en el personal",
            ToastType.INFO
          );

          let certificacionFormData = new FormData();
          certificacionFormData.append('archivo', this.tempFile, this.tempFile.name);
          certificacionFormData.append("certificacion", JSON.stringify(personaCertificacion))

          this.empresaService.guardarPersonalCertificacion(this.uuid, this.persona.uuid, certificacionFormData).subscribe((data: PersonaCertificacion) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se guardo la certificacion con exito",
              ToastType.SUCCESS
            );
            this.personaCertificacion = data;
            this.cursosGuardados = true;
            this.desactivarFormularioCursos()
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se pudo guardar la certificacion del personal. Motivo: ${error}`,
              ToastType.ERROR
            );
          });
        }
        break;
      case "RESUMEN":
        if(this.fotografiasGuardadas) {
          this.stepper.next();
        } else {
          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando la fotografia del elemento",
            ToastType.INFO
          );

          let formValueFotografiaPersona = form.value;
          let formData = new FormData();
          formData.append('fotografia', this.tempFile, this.tempFile.name);
          formData.append('metadataArchivo', JSON.stringify(formValueFotografiaPersona));

          this.empresaService.guardarPersonaFotografia(this.uuid, this.persona.uuid, formData).subscribe((data: PersonaFotografiaMetadata) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la fotografia con exito",
              ToastType.SUCCESS
            );
            this.personaFotografia = data;
            this.fotografiasGuardadas = true;
            this.desactivarFormularioFotografias()
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la fotografia. Motivo: ${error}`,
              ToastType.ERROR
            )
          })
        }
        break;
    }
  }

  previous() {
    this.stepper.previous()
  }

  agregarCertificado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Alguno de los campos no es valido",
        ToastType.WARNING
      );
      return;
    }
  }

  mostrarModalCrear(modal) {

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

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

  cambiarPuestoTrabajo(event) {
    this.puestoTrabajo = this.puestosTrabajo.filter(x => x.uuid === event.value)[0];
  }

  cambiarModalidad(event) {
    this.modalidad = this.modalidades.filter(x => x?.modalidad?.uuid === event.value)[0].modalidad;
  }

  cambiarSubpuestoTrabajo(event) {
    this.subpuestoTrabajo = this.puestoTrabajo.subpuestos.filter(x => x.uuid === event.value)[0]
    if(!this.subpuestoTrabajo.portacion || !this.subpuestoTrabajo.cuip) {
      this.cuipStatus = "NA";
    }

    // Con la portacion, la cuip es completamente obligatoria
    if(this.subpuestoTrabajo.portacion) {
      this.cuipStatus = "TRAMITADA";
    }
  }

  cambiarStatusCuip(event) {
    this.cuipStatus = event.value;
  }

  cambiarDomicilio(event) {
    this.domicilio = this.domicilios.filter(x => x.uuid === event.value)[0];
  }

  actualizarPuesto(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    if(!this.cuipValida && this.cuipStatus === 'TRAMITADO') {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La CUIP proporcionada no es valida.`,
        ToastType.WARNING
      );
      return;
    }

    // Validando los tipos
    let value: Persona = form.value;

    value.puestoDeTrabajo = this.puestoTrabajo;
    value.subpuestoDeTrabajo = this.subpuestoTrabajo;
    value.domicilioAsignado = this.domicilio;
    value.modalidad = this.modalidad;
    value.estatusCuip = this.cuipStatus;

    let formData = new FormData();
    formData.append('persona', JSON.stringify(value));
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null);
    }

    this.empresaService.modificarInformacionTrabajo(this.uuid, this.persona.uuid, formData).subscribe((data: Persona) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la informacion del trabajo con exito`,
        ToastType.SUCCESS
      );
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona.uuid).subscribe((data: Persona) => {
        this.persona = data;
        this.mostrarFormularioInformacionPuesto();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la informmacion de la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la informacion del trabajo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarCapacitacion(uuid) {
    this.modal = this.modalService.open(this.visualizarCertificacionPersonaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaService.descargarCertificacionPdf(this.uuid, this.persona.uuid, uuid).subscribe((data: Blob) => {
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

  finalizar() {
    window.location.reload()
  }

  descargarFotografia(uuid) {
    this.empresaService.descargarPersonaFotografia(this.uuid, this.persona.uuid, uuid).subscribe((data) => {
      // @ts-ignore
      this.convertirImagen(data);
      this.modalService.open(this.mostrarFotoPersonaModal);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la fotografia de la persona`,
        ToastType.ERROR
      )
    })
  }

  convertirImagen(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imagenActual = reader.result;
    });

    if(imagen) {
      reader.readAsDataURL(imagen);
    }
  }

  convertirImagenPrincipal(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imagenPrincipal = reader.result
    });

    if(imagen) {
      reader.readAsDataURL(imagen)
    }
  }

  guardarCertificacion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let formValue: PersonaCertificacion = form.value;

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
      "Estamos guardando la certificacion en el personal",
      ToastType.INFO
    );

    if(this.editandoCapacitacion) {
      formValue.uuid = this.personaCertificacion.uuid;
      formValue.id = this.personaCertificacion.id;

      let certificacionFormData = new FormData();
      if(this.tempFile !== undefined) {
        certificacionFormData.append('archivo', this.tempFile, this.tempFile.name);
      } else {
        certificacionFormData.append('archivo', null)
      }
      certificacionFormData.append("certificacion", JSON.stringify(formValue))

      this.empresaService.modificarPersonalCertificacion(this.uuid, this.persona.uuid, this.personaCertificacion.uuid, certificacionFormData).subscribe((data: PersonaCertificacion) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se modifico la certificacion con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoCertificado();
        this.empresaService.obtenerCertificacionesPersonalPorUuid(this.uuid, this.persona.uuid).subscribe((data: PersonaCertificacion[]) => {
          this.persona.certificaciones = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las certificaciones. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudo modificar la certificacion del personal. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      let certificacionFormData = new FormData();
      certificacionFormData.append('archivo', this.tempFile, this.tempFile.name);
      certificacionFormData.append("certificacion", JSON.stringify(formValue))

      this.empresaService.guardarPersonalCertificacion(this.uuid, this.persona.uuid, certificacionFormData).subscribe((data: PersonaCertificacion) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se guardo la certificacion con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoCertificado();
        this.empresaService.obtenerCertificacionesPersonalPorUuid(this.uuid, this.persona.uuid).subscribe((data: PersonaCertificacion[]) => {
          this.persona.certificaciones = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las certificaciones. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudo guardar la certificacion del personal. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    }
  }

  mostrarModalEditarCapacitacion(index) {
    this.personaCertificacion = this.persona.certificaciones[index];
    this.mostrarFormularioNuevoCertificado();
    this.editandoCapacitacion = true;
    this.crearPersonalCertificadoForm.patchValue({
      nombre: this.personaCertificacion.nombre,
      nombreInstructor: this.personaCertificacion.nombreInstructor,
      duracion: this.personaCertificacion.duracion,
      fechaInicio: this.personaCertificacion.fechaInicio,
      fechaFin: this.personaCertificacion.fechaFin
    });

    this.crearPersonalCertificadoForm.controls['archivo'].clearValidators();
    this.crearPersonalCertificadoForm.controls['archivo'].updateValueAndValidity();
  }

  mostrarModificarPersonaModal(modal) {
    this.editandoModal = true;

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"});

    this.crearPersonalForm.patchValue({
      curp: this.persona.curp,
      rfc: this.persona.rfc,
      nacionalidad: this.persona.nacionalidad.uuid,
      apellidoPaterno: this.persona.apellidoPaterno,
      apellidoMaterno: this.persona.apellidoMaterno,
      nombres: this.persona.nombres,
      fechaNacimiento: this.persona.fechaNacimiento,
      sexo: this.persona.sexo,
      tipoSangre: this.persona.tipoSangre,
      fechaIngreso: this.persona.fechaIngreso,
      estadoCivil: this.persona.estadoCivil,
      numeroExterior: this.persona.numeroExterior,
      numeroInterior: this.persona.numeroInterior,
      domicilio4: this.persona.domicilio4,
      codigoPostal: this.persona.codigoPostal,
      pais: this.persona.pais,
      telefono: this.persona.telefono,
      correoElectronico: this.persona.correoElectronico
    })

    this.nacionalidad = this.persona.nacionalidad;

    this.calle = this.persona.calleCatalogo;
    this.localidad = this.persona.localidadCatalogo;
    this.municipio = this.persona.municipioCatalogo;
    this.estado = this.persona.estadoCatalogo;
    this.colonia = this.persona.coloniaCatalogo;

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

    this.estadoService.obtenerColoniasPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Colonia[]) => {
      this.colonias = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las colonias. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarCambiosPersonal(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario no esta valido. Favor de verificarlo`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se estan guardando los cambios de la persona",
      ToastType.INFO
    );

    let formValue: Persona = form.value;
    formValue.nacionalidad = this.nacionalidad;
    formValue.calleCatalogo = this.calle;
    formValue.coloniaCatalogo = this.colonia;
    formValue.localidadCatalogo = this.localidad;
    formValue.municipioCatalogo = this.municipio;
    formValue.estadoCatalogo = this.estado;

    this.empresaService.modificarPersonal(this.uuid, this.persona.uuid, formValue).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se modifico el personal con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona.uuid).subscribe((data: Persona) => {
          this.persona = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido obtener la persona. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el personal. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  mostrarModalEliminarPersona() {
    this.modal = this.modalService.open(this.eliminarPersonalModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarFotografia(tempUuid) {
    this.tempUuidFotografia = tempUuid;

    this.modal = this.modalService.open(this.eliminarFotografiasModal,  {ariaLabelledBy: 'modal-basic-title', size: "lg"})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarCapacitacion(tempUuid) {
    this.tempUuidCapacitacion = tempUuid;

    this.modal = this.modalService.open(this.eliminarCapacitacionesModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarFormularioNuevoCertificado() {
    this.showCertificadoForm = !this.showCertificadoForm;
    if(!this.showCertificadoForm) {
      this.crearPersonalCertificadoForm.reset();
    }
    if(this.editandoCapacitacion) {
      this.editandoCapacitacion = false;
    }
  }

  mostrarFormularioNuevaFotografia() {
    this.showFotografiaForm = !this.showFotografiaForm;
  }

  mostrarFormularioInformacionPuesto() {
    this.showPuestoForm = !this.showPuestoForm;

    this.personalService.obtenerSubpuestosTrabajo(this.persona.puestoDeTrabajo?.uuid).subscribe((data: PersonalSubpuestoTrabajo[]) => {
      this.cuipStatus = this.persona.estatusCuip;
      this.puestoTrabajo = this.persona.puestoDeTrabajo;
      this.subpuestoTrabajo = this.persona.subpuestoDeTrabajo;
      this.domicilio = this.persona.domicilioAsignado;

      this.puestoTrabajo.subpuestos = data;

      this.crearPersonalPuestoForm.patchValue({
        puesto: this.persona.puestoDeTrabajo?.uuid,
        subpuesto: this.persona.subpuestoDeTrabajo?.uuid,
        detallesPuesto: this.persona.detallesPuesto,
        domicilioAsignado: this.persona.domicilioAsignado?.uuid,
        estatusCuip: this.persona.estatusCuip,
        cuip: this.persona.cuip,
        numeroVolanteCuip: this.persona.numeroVolanteCuip,
        fechaVolanteCuip: this.persona.fechaVolanteCuip,
        modalidad: this.persona.modalidad?.uuid
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar los subpuestos de trabajo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verificarCuip(event) {
    let cuip = event.value;
    let cuipRegex = /^[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HM]{1}[0-9]{8}$/g;
    if(!cuipRegex.test(cuip)) {
      this.toastService.showGenericToast(
        "Espera un momento",
        `La CUIP no es valida. Revisa que tenga la composicion XAXX010101X00000000`,
        ToastType.WARNING
      );
      this.cuipValida = false;
    } else {
      this.cuipValida = true;
    }
  }

  convertirPdf(pdf) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  guardarFotografia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos pendientes",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la fotografia del elemento",
      ToastType.INFO
    );

    let formValue = form.value;
    let formData = new FormData();
    formData.append('fotografia', this.tempFile, this.tempFile.name);
    formData.append('metadataArchivo', JSON.stringify(formValue));

    this.empresaService.guardarPersonaFotografia(this.uuid, this.persona.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la fotografia con exito",
        ToastType.SUCCESS
      );
      this.crearPersonaFotografiaForm.reset();
      this.mostrarFormularioNuevaFotografia();
      this.empresaService.listarPersonaFotografias(this.uuid, this.persona.uuid).subscribe((data: PersonaFotografiaMetadata[]) => {
        this.persona.fotografias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un prboelma",
          `No se han podido descargar todas las fotografias. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la fotografia. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  confirmarEliminarPersonal(form) {
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
      "Se esta eliminando el personal",
      ToastType.INFO
    );

    let formValue: EmpresaDomicilio = form.value;

    let formData = new FormData();
    formData.append('persona', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }


    this.empresaService.eliminarPersonal(this.uuid, this.persona.uuid, formData).subscribe((data: Persona) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la persona con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La persona no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarFotografia() {
    if(this.tempUuidFotografia === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la fotografia a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la capacitacion",
      ToastType.INFO
    );

    this.empresaService.eliminarPersonaFotografia(this.uuid, this.persona.uuid, this.tempUuidFotografia).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la fotografia con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.listarPersonaFotografias(this.uuid, this.persona.uuid).subscribe((data: PersonaFotografiaMetadata[]) => {
        this.persona.fotografias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un prboelma",
          `No se han podido descargar todas las fotografias. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La fotografia no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarCapacitacion() {
    if(this.tempUuidCapacitacion === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la capacitacion a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la capacitacion",
      ToastType.INFO
    );

    this.empresaService.eliminarPersonalCertificacion(this.uuid, this.persona.uuid, this.tempUuidCapacitacion).subscribe((data: PersonaCertificacion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la capacitacion con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerCertificacionesPersonalPorUuid(this.uuid, this.persona.uuid).subscribe((data: PersonaCertificacion[]) => {
        this.persona.certificaciones = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las certificaciones. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La capacitacion no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarVolanteCuip() {
    this.empresaService.descargarVolanteCuip(this.uuid, this.persona?.uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      this.modal = this.modalService.open(this.visualizarVolanteCuip, {size: 'xl', backdrop: 'static'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el volante. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAsignarArmaCorta() {
    this.empresaService.obtenerArmasCortas(this.uuid).subscribe((data: Arma[]) => {
      this.armasCortas = data;
      this.modal = this.modalService.open(this.asignarArmaCortaModal, {size: 'lg', backdrop: 'static'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAsignarArmaLarga() {
    this.empresaService.obtenerArmasLargas(this.uuid).subscribe((data: Arma[]) => {
      this.armasLargas = data;
      this.modal = this.modalService.open(this.asignarArmaLargaModal, {size: 'lg', backdrop: 'static'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAsignarCan() {
    this.empresaService.obtenerCanesInstalaciones(this.uuid).subscribe((data: Can[]) => {
      this.canes = data;
      this.modal = this.modalService.open(this.asignarCanPersonaModal, {size: "lg", backdrop: "static"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los canes. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAsignarVehiculo() {
    this.empresaService.obtenerVehiculos(this.uuid).subscribe((data: Vehiculo[]) => {
      this.vehiculos = data;
      this.modal = this.modalService.open(this.asignarVehiculoPersonaModal, {size: "lg", backdrop: "static"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalDesasignarCan() {
    this.modal = this.modalService.open(this.desasignarCanPersonaModal, {size: 'lg', backdrop: 'static'})
  }

  mostrarModalDesasignarVehiculo() {
    this.modal = this.modalService.open(this.desasignarVehiculoPersonaModal, {size: 'lg', backdrop: 'static'});
  }

  mostrarModalDesasignarArmaCorta() {
    this.modal = this.modalService.open(this.desasignarArmaCortaPersonaModal, {size: 'lg', backdrop: 'static'})
  }

  mostrarModalDesasignarArmaLarga() {
    this.modal = this.modalService.open(this.desasignarArmaLargaPersonaModal, {size: 'lg', backdrop: 'static'})
  }

  asignarCanPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay algunos campos validos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos asignando el can al elemento`,
      ToastType.INFO
    );

    let formValue = form.value;
    let personaCan: PersonalCan = new PersonalCan();
    personaCan.can = this.canes.filter(x => x.uuid === formValue.can)[0]
    personaCan.observaciones = formValue.observaciones;

    this.empresaService.asignarCanPersona(this.uuid, this.persona?.uuid, personaCan).subscribe((data) => {
      this.toastService.showGenericToast(
        `Listo`,
        `Se ha guardado el can con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido asignar el can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  asignarVehiculoPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay algunos campos validos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos asignando el vehiculo al elemento`,
      ToastType.INFO
    );

    let formValue = form.value;
    let personalVehiculo: PersonalVehiculo = new PersonalVehiculo();
    personalVehiculo.vehiculo = this.vehiculos.filter(x => x.uuid === formValue.vehiculo)[0]
    personalVehiculo.observaciones = formValue.observaciones;

    this.empresaService.asignarVehiculoPersona(this.uuid, this.persona?.uuid, personalVehiculo).subscribe((data) => {
      this.toastService.showGenericToast(
        `Listo`,
        `Se ha guardado el vehiculo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido asignar el vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  asignarArmaCortaPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay algunos campos validos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos asignando el arma corta al elemento`,
      ToastType.INFO
    );

    let formValue = form.value;
    let personalArmaCorta: PersonalArma = new PersonalArma();
    personalArmaCorta.arma = this.armasCortas.filter(x => x.uuid === formValue.arma)[0]
    personalArmaCorta.observaciones = formValue.observaciones;

    this.empresaService.asignarArmaCortaPersona(this.uuid, this.persona?.uuid, personalArmaCorta).subscribe((data) => {
      this.toastService.showGenericToast(
        `Listo`,
        `Se ha asignado el arma corta con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido asignar el arma corta. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  asignarArmaLargaPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay algunos campos validos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos asignando el arma larga al elemento`,
      ToastType.INFO
    );

    let formValue = form.value;
    let personalArmaLarga: PersonalArma = new PersonalArma();
    personalArmaLarga.arma = this.armasLargas.filter(x => x.uuid === formValue.arma)[0]
    personalArmaLarga.observaciones = formValue.observaciones;

    this.empresaService.asignarArmaLargaPersona(this.uuid, this.persona?.uuid, personalArmaLarga).subscribe((data) => {
      this.toastService.showGenericToast(
        `Listo`,
        `Se ha asignado el arma larga con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido obtener la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido asignar el arma larga. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  desasignarCanPersona() {
    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos quitando la asignacion del can al elemento`,
      ToastType.INFO
    );

    this.empresaService.desasignarCanPersona(this.uuid, this.persona?.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha quitado la asignacion del can con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido quitar la asignacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  desasignarVehiculoPersona() {
    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos quitando la asignacion del vehiculo al elemento`,
      ToastType.INFO
    );

    this.empresaService.desasignarVehiculoPersona(this.uuid, this.persona?.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha quitado la asignacion del vehiculo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido quitar la asignacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  desasignarArmaCorta() {
    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos quitando la asignacion del arma al elemento`,
      ToastType.INFO
    );

    this.empresaService.desasignarArmaCortaPersona(this.uuid, this.persona?.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha quitado la asignacion del arma con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido quitar la asignacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  desasignarArmaLarga() {
    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos quitando la asignacion del arma al elemento`,
      ToastType.INFO
    );

    this.empresaService.desasignarArmaLargaPersona(this.uuid, this.persona?.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha quitado la asignacion del arma con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPersonalPorUuid(this.uuid, this.persona?.uuid).subscribe((data: Persona) => {
        this.persona = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la persona. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido quitar la asignacion. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  omitirPaso(paso) {
    switch(paso) {
      case "CURSOS":
        this.cursosGuardados = true;
        this.desactivarFormularioCursos();
        break;
    }
    this.stepper.next()
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

  private desactivarFormularioInfoPersonal() {
    this.crearPersonalForm.controls['curp'].disable();
    this.crearPersonalForm.controls['rfc'].disable();
    this.crearPersonalForm.controls['apellidoPaterno'].disable();
    this.crearPersonalForm.controls['apellidoMaterno'].disable();
    this.crearPersonalForm.controls['nombres'].disable();
    this.crearPersonalForm.controls['fechaNacimiento'].disable();
    this.crearPersonalForm.controls['fechaIngreso'].disable();
    this.crearPersonalForm.controls['tipoSangre'].disable();
    this.crearPersonalForm.controls['estadoCivil'].disable();
    this.crearPersonalForm.controls['numeroExterior'].disable();
    this.crearPersonalForm.controls['numeroInterior'].disable();
    this.crearPersonalForm.controls['domicilio4'].disable();
    this.crearPersonalForm.controls['codigoPostal'].disable();
    this.crearPersonalForm.controls['pais'].disable();
    this.crearPersonalForm.controls['telefono'].disable();
    this.crearPersonalForm.controls['correoElectronico'].disable();
  }

  private desactivarFormularioInfoPuesto() {
    this.crearPersonalPuestoForm.controls['puesto'].disable();
    this.crearPersonalPuestoForm.controls['subpuesto'].disable();
    this.crearPersonalPuestoForm.controls['detallesPuesto'].disable();
    this.crearPersonalPuestoForm.controls['domicilioAsignado'].disable();
    this.crearPersonalPuestoForm.controls['estatusCuip'].disable();
    this.crearPersonalPuestoForm.controls['cuip'].disable();
    this.crearPersonalPuestoForm.controls['numeroVolanteCuip'].disable();
    this.crearPersonalPuestoForm.controls['fechaVolanteCuip'].disable();
    this.crearPersonalPuestoForm.controls['modalidad'].disable();
  }

  private desactivarFormularioCursos() {
    this.crearPersonalCertificadoForm.controls['nombre'].disable();
    this.crearPersonalCertificadoForm.controls['nombreInstructor'].disable();
    this.crearPersonalCertificadoForm.controls['duracion'].disable();
    this.crearPersonalCertificadoForm.controls['fechaInicio'].disable();
    this.crearPersonalCertificadoForm.controls['fechaFin'].disable();
    this.crearPersonalCertificadoForm.controls['archivo'].disable();
  }

  private desactivarFormularioFotografias() {
    this.crearPersonaFotografiaForm.controls['file'].disable();
    this.crearPersonaFotografiaForm.controls['descripcion'].disable();
  }
}


