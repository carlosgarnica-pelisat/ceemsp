import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Stepper from "bs-stepper";
import Modalidad from "../../../_models/Modalidad";
import {ModalidadesService} from "../../../_services/modalidades.service";
import {ToastService} from "../../../_services/toast.service";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Empresa from "../../../_models/Empresa";
import {EmpresaService} from "../../../_services/empresa.service";
import {PublicService} from "../../../_services/public.service";
import ProximoRegistro from "../../../_models/ProximoRegistro";
import {faChevronLeft, faChevronRight, faPencilAlt, faTrash, faUsers} from "@fortawesome/free-solid-svg-icons";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import ExisteEmpresa from "../../../_models/ExisteEmpresa";
import {ValidacionService} from "../../../_services/validacion.service";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscrituraSocio from "../../../_models/EmpresaEscrituraSocio";
import EmpresaEscrituraApoderado from "../../../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../../../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../../../_models/EmpresaEscrituraConsejo";
import EmpresaFormaEjecucion from "../../../_models/EmpresaFormaEjecucion";
import validateRfc from "validate-rfc/src";
import curp from 'curp';
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import Localidad from "../../../_models/Localidad";
import Colonia from "../../../_models/Colonia";
import Calle from "../../../_models/Calle";
import {EstadosService} from "../../../_services/estados.service";
import {CalleService} from "../../../_services/calle.service";
import {Router} from "@angular/router";
import ExisteEscritura from "../../../_models/ExisteEscritura";

@Component({
  selector: 'app-empresa-nueva',
  templateUrl: './empresa-nueva.component.html',
  styleUrls: ['./empresa-nueva.component.css']
})
export class EmpresaNuevaComponent implements OnInit {

  fechaDeHoy = new Date().toISOString().split('T')[0];

  empresaCreacionForm: FormGroup;
  empresaModalidadForm: FormGroup;
  empresaDomiciliosForm: FormGroup;
  nuevaEscrituraForm: FormGroup;

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

  stepper: Stepper;

  // binding data
  tipoPersona: string;
  tipoTranite: string;
  year = new Date().getFullYear();

  modalidad: Modalidad;
  modalidades: Modalidad[] = [];
  empresaModalidades: EmpresaModalidad[] = [];

  empresa: Empresa;
  empresaDomicilios: EmpresaDomicilio[] = [];
  empresaEscrituras: EmpresaEscritura[] = [];
  empresaFormasEjecucion: EmpresaFormaEjecucion[] = [];
  empresaEscritura: EmpresaEscritura;

  faTrash = faTrash;
  faPencil = faPencilAlt;
  faUsers = faUsers;
  faChevronLeft = faChevronLeft;
  faChevronRight = faChevronRight;

  showSocioForm: boolean;
  showApoderadoForm: boolean;
  showRepresentanteForm: boolean;
  showConsejoForm: boolean;

  empresaGuardada: boolean = false;
  domiciliosGuardados: boolean = false;
  escriturasGuardadas: boolean = false;
  formasEjecucionGuardadas: boolean = false;

  nuevoSocioForm: FormGroup;
  nuevoApoderadoForm: FormGroup;
  nuevoRepresentanteForm: FormGroup;
  nuevoConsejoAdministracionForm: FormGroup;
  empresaFormaEjecucionForm: FormGroup;

  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;

  tempFile;
  pestanaActual = 'SOCIOS';

  existeEmpresa: ExisteEmpresa;
  existeEscritura: ExisteEscritura;

  closeResult: string;
  modal: NgbModalRef;

  porcentaje: number = 0.00;

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

  rfcVerificationResponse = undefined;
  curpValida: boolean = false;
  obtenerCallesTimeout = undefined;

  temporaryIndex: number;

  stepName: string = "EMPRESA";

  editandoSocio: boolean = false;
  editandoApoderado: boolean = false;
  editandoRepresentante: boolean = false;
  editandoConsejo: boolean = false;

  socio: EmpresaEscrituraSocio;
  apoderado: EmpresaEscrituraApoderado;
  representante: EmpresaEscrituraRepresentante;
  consejo: EmpresaEscrituraConsejo;

  constructor(private formBuilder: FormBuilder, private modalidadService: ModalidadesService,
              private toastService: ToastService, private empresaService: EmpresaService,
              private publicService: PublicService, private validacionService: ValidacionService,
              private modalService: NgbModal, private estadoService: EstadosService,
              private calleService: CalleService, private router: Router) { }

  ngOnInit(): void {
    this.empresaCreacionForm = this.formBuilder.group({
      tipoTramite: ['', Validators.required],
      registro: ['', [Validators.required, Validators.maxLength(5), Validators.minLength(5)]],
      tipoPersona: ['', Validators.required],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]],
      sexo: [''],
      correoElectronico: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      telefono: ['', [Validators.required]]
    })

    this.empresaModalidadForm = this.formBuilder.group({
      modalidad: ['', Validators.required],
      submodalidad: [''],
      numeroRegistroFederal: ['', Validators.maxLength(30)],
      fechaInicio: [''],
      fechaFin: ['']
    });

    this.empresaDomiciliosForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: [''],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
    });

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', [Validators.required, Validators.maxLength(10)]],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', [Validators.required, Validators.maxLength(60)]],
      tipoFedatario: ['', Validators.required],
      numero: ['', [Validators.required, Validators.min(1), Validators.max(9999)]],
      nombreFedatario: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', Validators.required],
      archivo: ['', Validators.required]
    });

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    });

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]],
    })

    this.empresaFormaEjecucionForm = this.formBuilder.group({
      formaEjecucion: ['', Validators.required]
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

  eliminarSocio(index) {
    this.empresaEscritura.socios.splice(index, 1);
    this.modal.close();
  }

  eliminarApoderado(index) {
    this.empresaEscritura.apoderados.splice(index, 1);
    this.modal.close()
  }

  eliminarRepresentante(index) {
    this.empresaEscritura.representantes.splice(index, 1);
    this.modal.close()
  }

  eliminarConsejo(index) {
    this.empresaEscritura.consejos.splice(index, 1);
    this.modal.close()
  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
    if(!this.showSocioForm) {
      this.nuevoSocioForm.reset();
    }

    if(this.editandoSocio) {
      this.editandoSocio = false;
      this.empresaEscritura.socios.push(this.socio);
    }
  }

  mostrarFormularioNuevoConsejo() {
    this.showConsejoForm = !this.showConsejoForm;
    if(!this.showConsejoForm) {
      this.nuevoConsejoAdministracionForm.reset();
    }

    if(this.editandoConsejo) {
      this.editandoConsejo = false;
      this.empresaEscritura.consejos.push(this.consejo);
    }
  }

  mostrarFormularioNuevoRepresentante() {
    this.showRepresentanteForm = !this.showRepresentanteForm;
    if(!this.showRepresentanteForm) {
      this.nuevoRepresentanteForm.reset();
    }

    if(this.editandoRepresentante) {
      this.editandoRepresentante = false;
      this.empresaEscritura.representantes.push(this.representante);
    }
  }

  mostrarFormularioNuevoApoderado() {
    this.showApoderadoForm = !this.showApoderadoForm;
    if(!this.showApoderadoForm) {
      this.nuevoApoderadoForm.reset();
    }

    if(this.editandoApoderado) {
      this.editandoApoderado = false;
      this.empresaEscritura.apoderados.push(this.apoderado);
    }
  }

  seleccionarEstado(estadoUuid) {
    // DELETING EVERYTHING!
    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
    this.empresaDomiciliosForm.patchValue({
      estado: this.estado.nombre
    })
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

    this.empresaDomiciliosForm.patchValue({
      estado: undefined,
      domicilio3: undefined,
      domicilio2: undefined
    })
  }

  seleccionarMunicipio(municipioUuid) {
    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

    if(this.stepName === 'LEGAL') {
      this.nuevaEscrituraForm.patchValue({
        ciudad: this.municipio.nombre
      })
    }

    if(this.stepName === 'DOMICILIOS') {
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
    this.empresaDomiciliosForm.patchValue({
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

  guardarSocio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaSocio: EmpresaEscrituraSocio = form.value;

    if(!curp.validar(empresaSocio.curp)) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
      );
      return;
    }

    let existeSocioRfc = this.empresaEscritura.socios.filter(x => x.curp === empresaSocio.curp)
    if(existeSocioRfc.length > 0) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un socio registrado con este CURP",
          ToastType.WARNING
      );
      return;
    }

    if(this.empresaEscritura.socios.length > 0) {
      this.porcentaje = 0.00;
      this.empresaEscritura.socios.forEach((s) => {
        this.porcentaje += parseInt(String(s.porcentajeAcciones));
      })

      this.porcentaje += parseInt(String(empresaSocio.porcentajeAcciones));

      if(this.porcentaje > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `La suma del porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    } else {
      if(empresaSocio.porcentajeAcciones > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `La suma del porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    }

    this.empresaEscritura.socios.push(empresaSocio);
    this.nuevoSocioForm.reset();
    this.editandoSocio = false;
    this.showSocioForm = false;
  }

  guardarApoderado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaApoderado: EmpresaEscrituraApoderado = form.value;

    if(!curp.validar(empresaApoderado.curp)) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
      );
      return;
    }

    let existeApoderadoRfc = this.empresaEscritura.apoderados.filter(x => x.curp === empresaApoderado.curp)
    if(existeApoderadoRfc.length > 0) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un apoderado registrado con este CURP",
          ToastType.WARNING
      );
      return;
    }

    let fechaInicio = new Date(empresaApoderado.fechaInicio);
    let fechaFin = new Date(empresaApoderado.fechaFin);
    if(fechaInicio > fechaFin) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La fecha de inicio es mayor que la del final",
        ToastType.WARNING
      )
      return;
    }

    let string = "";
    validateRfc(string);

    this.empresaEscritura.apoderados.push(empresaApoderado);
    this.nuevoApoderadoForm.reset();
    this.editandoApoderado = false;
    this.showApoderadoForm = false;
  }

  guardarRepresentante(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaRepresentante: EmpresaEscrituraRepresentante = form.value;
    let existeRepresentanteRfc = this.empresaEscritura.representantes.filter(x => x.curp === empresaRepresentante.curp)
    if(existeRepresentanteRfc.length > 0) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un representante registrado con este CURP",
          ToastType.WARNING
      );
      return;
    }

    if(!curp.validar(empresaRepresentante.curp)) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
      );
      return;
    }
    this.empresaEscritura.representantes.push(empresaRepresentante);
    this.nuevoRepresentanteForm.reset();
    this.editandoRepresentante = false;
    this.showRepresentanteForm = false;
  }

  agregarFormaEjecucion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaFormaEjecucion: EmpresaFormaEjecucion = form.value;

    let formasEjecucionExistentes = this.empresaFormasEjecucion.filter(x => x.formaEjecucion === empresaFormaEjecucion.formaEjecucion)

    if(formasEjecucionExistentes.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Esta forma de ejecucion ya se encuentra registrada en la empresa",
        ToastType.WARNING
      );
      return;
    }

    this.empresaFormasEjecucion.push(empresaFormaEjecucion);
    this.empresaFormaEjecucionForm.reset();
  }

  guardarConsejo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaConsejo: EmpresaEscrituraConsejo = form.value;
    let existeConsejoCurp = this.empresaEscritura.consejos.filter(x => x.curp === empresaConsejo.curp)
    if(existeConsejoCurp.length > 0) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un miembro del consejo registrado con este CURP",
          ToastType.WARNING
      );
      return;
    }

    let existeConsejoPuesto = this.empresaEscritura.consejos.filter(x => x.puesto === empresaConsejo.puesto)
    if(existeConsejoPuesto.length > 0) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un miembro del consejo registrado con este puesto",
          ToastType.WARNING
      );
      return;
    }

    if(!curp.validar(empresaConsejo.curp)) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
      );
      return;
    }



    this.empresaEscritura.consejos.push(empresaConsejo);
    this.nuevoConsejoAdministracionForm.reset();
    this.editandoConsejo = false;
    this.showConsejoForm = false;
  }

  eliminarEscritura(index) {
    this.empresaEscrituras.splice(index, 1);
    this.modal.close();
  }

  eliminarFormaEjecucion(index) {
    this.empresaFormasEjecucion.splice(index, 1);
    this.modal.close();
  }

  next(stepName: string, form) {
    this.stepName = stepName;
    /*if(form !== undefined && !form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificar los errores antes de continuar",
        ToastType.WARNING
      );
      return;
    }*/

    switch (stepName) {
      case 'DOMICILIOS':
        /*if(this.empresaGuardada) {
          this.stepper.next();
        } else {
          if(this.existeEmpresa !== undefined && this.existeEmpresa.existe) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `La empresa ya se encuentra dada de alta por CURP o RFC`,
              ToastType.WARNING
            );
            return;
          }

          let formData = form.value;

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando la empresa en la base de datos",
            ToastType.INFO
          );

          let empresa = new Empresa();
          empresa.tipoTramite = formData.tipoTramite;
          empresa.rfc = formData.rfc;
          empresa.nombreComercial = formData.nombreComercial;
          empresa.razonSocial = formData.razonSocial;
          empresa.tipoPersona = formData.tipoPersona;
          empresa.correoElectronico = formData.correoElectronico;
          empresa.telefono = formData.telefono;
          empresa.registro = `CESP/${this.tipoTranite}/${formData.registro}/${this.year}`;
          if(empresa.tipoPersona === 'MORAL') {
            empresa.sexo = 'NA';
            empresa.curp = undefined;
          } else {
            empresa.sexo = formData.sexo;
            empresa.curp = formData.curp;
          }

          empresa.modalidades = this.empresaModalidades;

          this.empresaService.guardarEmpresa(empresa).subscribe((data: Empresa) => {
            this.empresa = data;
            this.empresaGuardada = true;
            this.desactivarCamposEmpresa();
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la empresa con exito",
              ToastType.SUCCESS
            );
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la empresa. Motivo: ${error}`,
              ToastType.ERROR
            )
          })
        }*/
        break;
      case 'LEGAL':
        console.log(this.domiciliosGuardados);
        /*if(this.domiciliosGuardados) {
          this.stepper.next();
        } else {
          if(this.empresaDomicilios.length < 1) {
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

          let allAddressesStored: boolean = true;

          this.empresaDomicilios.forEach(ed => {
            this.empresaService.guardarDomicilio(this.empresa.uuid, ed).subscribe((data) => {
              this.toastService.showGenericToast(
                "Listo",
                `Se ha guardado el domicilio ${ed.domicilio1} con exito`,
                ToastType.SUCCESS
              );
            }, (error) => {
              this.toastService.showGenericToast(
                "Ocurrio un problema",
                `No se ha podido guardar el domicilio ${ed.domicilio1}. Motivo: ${error}`,
                ToastType.ERROR
              );
              allAddressesStored = false;
            });
          })

          if(allAddressesStored) {
            this.stepper.next();
            this.desactivarCamposDireccion();
            this.domiciliosGuardados = true;
          }
        }*/

        break;
      case 'FORMAS_EJECUCION':
        /*if(this.escriturasGuardadas) {
          this.stepper.next();
        } else {
          if(this.empresaEscrituras.length < 1) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "No hay escrituras creadas aun para la empresa.",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando las escrituras",
            ToastType.INFO
          );

          let todasLasEscriturasGuardadas: boolean = true;

          this.empresaEscrituras.forEach(ee => {
            let formData = new FormData();
            formData.append('archivo', this.tempFile, this.tempFile.name);
            formData.append('escritura', JSON.stringify(ee));

            this.empresaService.guardarEscritura(this.empresa.uuid, formData).subscribe((data) => {
              this.toastService.showGenericToast(
                "Listo",
                `Se ha guardado la escritura con exito`,
                ToastType.SUCCESS
              );
            }, (error) => {
              this.toastService.showGenericToast(
                "Ocurrio un problema",
                `No se ha podido guardar la escritura. Motivo: ${error}`,
                ToastType.ERROR
              );
              todasLasEscriturasGuardadas = false;
            });
          })

          if(todasLasEscriturasGuardadas) {
            this.escriturasGuardadas = true;
            this.desactivarCamposEscrituras();
            this.stepper.next();
          }
        }*/

        break;
      case 'RESUMEN':
        /*if(this.formasEjecucionGuardadas) {
          this.stepper.next();
        } else {
          if(this.empresaFormasEjecucion.length < 1) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "No hay formas de ejecucion creados aun para la empresa.",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando las formas de ejecucion",
            ToastType.INFO
          );

          let todasLasFormasEjecucionGuardadas: boolean = true;

          this.empresaFormasEjecucion.forEach(ef => {

            this.empresaService.guardarFormaEjecucion(this.empresa.uuid, ef).subscribe((data) => {
              this.toastService.showGenericToast(
                "Listo",
                `Se ha guardado la forma de ejecucion con exito`,
                ToastType.SUCCESS
              );
            }, (error) => {
              this.toastService.showGenericToast(
                "Ocurrio un problema",
                `No se ha podido guardar la forma de ejecucion. Motivo: ${error}`,
                ToastType.ERROR
              );
              todasLasFormasEjecucionGuardadas = false;
            });
          })

          if(todasLasFormasEjecucionGuardadas) {
            this.formasEjecucionGuardadas = true;
            this.desactivarCamposFormasEjecucion();
            this.stepper.next();
          }
        }*/
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Este paso no ha sido implementado aun",
          ToastType.ERROR
        )
    }
    this.stepper.next();
  }

  previous() {
    this.stepper.previous()
  }

  editarDomicilio(index) {
    let domicilio = this.empresaDomicilios[index];
    this.empresaDomicilios.splice(index, 1);
    this.empresaDomiciliosForm.patchValue({
      nombre: domicilio.nombre,
      numeroExterior: domicilio.numeroExterior,
      numeroInterior: domicilio.numeroInterior,
      domicilio4: domicilio.domicilio4,
      codigoPostal: domicilio.codigoPostal,
      pais: domicilio.pais,
      matriz: domicilio.matriz,
      telefonoFijo: domicilio.telefonoFijo,
      telefonoMovil: domicilio.telefonoMovil
    })
  }

  mostrarEditarSocioForm(index) {
    this.socio = this.empresaEscritura.socios[index];
    this.empresaEscritura.socios.splice(index, 1);
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

  mostrarEditarApoderadoForm(index) {
    this.apoderado = this.empresaEscritura.apoderados[index];
    this.empresaEscritura.apoderados.splice(index, 1);
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
    })
  }

  mostrarEditarRepresentanteForm(index) {
    this.representante = this.empresaEscritura.representantes[index];
    this.empresaEscritura.representantes.splice(index, 1);
    this.mostrarFormularioNuevoRepresentante();
    this.editandoRepresentante = true;
    this.nuevoRepresentanteForm.patchValue({
      nombres: this.representante.nombres,
      apellidos: this.representante.apellidos,
      apellidoMaterno: this.representante.apellidoMaterno,
      curp: this.representante.curp,
      sexo: this.representante.sexo
    })
  }

  mostrarEditarConsejoForm(index) {
    this.consejo = this.empresaEscritura.consejos[index];
    this.empresaEscritura.consejos.splice(index, 1);
    this.mostrarFormularioNuevoConsejo();
    this.editandoConsejo = true;
    this.nuevoConsejoAdministracionForm.patchValue({
      nombres: this.consejo.nombres,
      apellidos: this.consejo.apellidos,
      apellidoMaterno: this.consejo.apellidoMaterno,
      curp: this.consejo.curp,
      sexo: this.consejo.sexo,
      puesto: this.consejo.puesto
    });
  }

  // Funciones para la primera pagina (informacion de la empresa)
  cambiarTipoTramite(event) {
    this.tipoTranite = event.value
    this.empresaModalidades = [];
    this.publicService.obtenerSiguienteNumero({tipo: this.tipoTranite}).subscribe((data: ProximoRegistro) => {
      this.empresaCreacionForm.patchValue({
        registro: data.numeroSiguiente
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se ha podido descargar el siguiente numero",
        ToastType.ERROR
      );
    })

    this.modalidadService.obtenerModalidadesPorFiltro("TIPO", this.tipoTranite).subscribe((data: Modalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido obtener los tipos de tramite. ${error}`,
        ToastType.ERROR
      )
    })
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
  }

  mostrarModalEliminar(modal, temporaryIndex) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});
    this.temporaryIndex = temporaryIndex;
  }

  eliminarModalidad(index) {
    this.empresaModalidades.splice(index, 1);
    this.modal.close();
  }

  eliminarDomicilio(index) {
    this.empresaDomicilios.splice(index, 1);
    this.modal.close();
  }

  agregarEscritura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario tiene campos incorrectos, favor de verificarlos",
        ToastType.WARNING
      );
      return;
    }

    let formData: EmpresaEscritura = form.value;
    formData.socios = [];
    formData.apoderados = [];
    formData.representantes = [];
    formData.consejos = [];
    formData.uuid = this.hacerFalsoUuid(12);
    this.empresaEscrituras.push(formData);
    this.estado = undefined;
    this.municipio = undefined;
    form.reset();
  }

  agregarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario tiene campos incorrectos. Favor de verificarlos",
        ToastType.WARNING
      );
      return;
    }

    if(this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Alguno de los campos catalogo a llenar no esta lleno",
        ToastType.WARNING
      );
      return;
    }

    let formData: EmpresaDomicilio = form.value;
    formData.estadoCatalogo = this.estado;
    formData.municipioCatalogo = this.municipio;
    formData.localidadCatalogo = this.localidad;
    formData.coloniaCatalogo = this.colonia;
    formData.calleCatalogo = this.calle;
    this.empresaDomicilios.push(formData);
    form.reset();
  }

  mostrarModalEmpresaRegistrada(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  mostrarModalDetallesEscritura(modal, uuid) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', scrollable: true});
    this.empresaEscritura = this.empresaEscrituras.filter(x => x.uuid === uuid)[0];

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });
  }

  consultarEmpresaCurp(event) {
    let existeEmpresa: ExisteEmpresa = new ExisteEmpresa();
    existeEmpresa.curp = event.value;

    this.curpValida = curp.validar(existeEmpresa.curp);

    if(!this.curpValida) {
      this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No se puede consultar la empresa en la base de datos. La CURP es invalida",
          ToastType.WARNING
      );
      return;
    }
    this.validacionService.validarEmpresa(existeEmpresa).subscribe((existeEmpresa: ExisteEmpresa) => {
      this.existeEmpresa = existeEmpresa;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido consultar la existencia de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  consultarEmpresaRfc(event) {
    this.existeEmpresa = undefined;
    let existeEmpresa: ExisteEmpresa = new ExisteEmpresa();
    existeEmpresa.rfc = event.value;

    this.rfcVerificationResponse = validateRfc(existeEmpresa.rfc);
    if(!this.rfcVerificationResponse.isValid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El RFC ingresado no es valido. Motivo: ${this.rfcVerificationResponse.errors}`,
        ToastType.WARNING
      );
      return;
    } else {
      this.rfcVerificationResponse = undefined;
    }

    this.validacionService.validarEmpresa(existeEmpresa).subscribe((existeEmpresa: ExisteEmpresa) => {
      this.existeEmpresa = existeEmpresa;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido consultar la existencia de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cambiarPestanaLegal(nombrePestana) {
    this.pestanaActual = nombrePestana;
  }

  agregarModalidad(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que estan vacios",
        ToastType.WARNING
      )
      return;
    }

    let formData = form.value;

    let empresaModalidad = new EmpresaModalidad();
    // Validando las fechas
    if(formData.fechaInicio !== undefined && formData.fechaFin !== undefined) {
      let fechaInicio = new Date(formData.fechaInicio);
      let fechaFin = new Date(formData.fechaFin);
      if(fechaInicio > fechaFin) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La fecha de inicio es mayor que la del final",
          ToastType.WARNING
        )
        return;
      }
    }

    empresaModalidad.modalidad = this.modalidad;
    if(this.modalidad.tieneSubmodalidades) {
      empresaModalidad.submodalidad = this.modalidad.submodalidades.filter((x => x.uuid === formData.submodalidad))[0];
    }

    empresaModalidad.fechaInicio = formData.fechaInicio;
    empresaModalidad.fechaFin = formData.fechaFin;
    empresaModalidad.numeroRegistroFederal = formData.numeroRegistroFederal;

    this.empresaModalidades.push(empresaModalidad);
    form.reset();
    this.modalidad = undefined;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
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

  seleccionarModalidad(event) {
    let existeModalidad = this.empresaModalidades.filter(m => m.modalidad.uuid === event.value)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Esta modalidad ya se encuentra registrada en la empresa. Favor de seleccionar otra",
        ToastType.WARNING
      );
      this.modalidad = undefined;
      return;
    }

    this.modalidad = this.modalidades.filter(m => m.uuid === event.value)[0];
    if(this.modalidad.submodalidades.length > 0) {
      this.modalidad.tieneSubmodalidades = true;
    } else {
      this.empresaModalidadForm.patchValue({
        submodalidad: undefined
      });
    }
  }

  redireccionarEmpresas() {
    this.router.navigate(['/home/empresas']);
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

  private desactivarCamposEmpresa() {
    this.empresaCreacionForm.controls['tipoTramite'].disable();
    this.empresaCreacionForm.controls['registro'].disable();
    this.empresaCreacionForm.controls['tipoPersona'].disable();
    this.empresaCreacionForm.controls['razonSocial'].disable();
    this.empresaCreacionForm.controls['nombreComercial'].disable();
    this.empresaCreacionForm.controls['rfc'].disable();
    this.empresaCreacionForm.controls['curp'].disable();
    this.empresaCreacionForm.controls['correoElectronico'].disable();
    this.empresaCreacionForm.controls['telefono'].disable();

    this.empresaModalidadForm.controls['modalidad'].disable();
    this.empresaModalidadForm.controls['submodalidad'].disable();
    this.empresaModalidadForm.controls['fechaInicio'].disable();
    this.empresaModalidadForm.controls['fechaFin'].disable();
    this.empresaModalidadForm.controls['numeroRegistroFederal'].disable();
  }

  private desactivarCamposDireccion() {
    this.empresaDomiciliosForm.controls['nombre'].disable();
    this.empresaDomiciliosForm.controls['domicilio1'].disable();
    this.empresaDomiciliosForm.controls['numeroExterior'].disable();
    this.empresaDomiciliosForm.controls['numeroInterior'].disable();
    this.empresaDomiciliosForm.controls['domicilio2'].disable();
    this.empresaDomiciliosForm.controls['domicilio3'].disable();
    this.empresaDomiciliosForm.controls['domicilio4'].disable();
    this.empresaDomiciliosForm.controls['codigoPostal'].disable();
    this.empresaDomiciliosForm.controls['estado'].disable();
    this.empresaDomiciliosForm.controls['pais'].disable();
    this.empresaDomiciliosForm.controls['matriz'].disable();
    this.empresaDomiciliosForm.controls['telefonoFijo'].disable();
    this.empresaDomiciliosForm.controls['telefonoMovil'].disable();
  }

  private desactivarCamposEscrituras() {
    this.nuevaEscrituraForm.controls['numeroEscritura'].disable();
    this.nuevaEscrituraForm.controls['fechaEscritura'].disable();
    this.nuevaEscrituraForm.controls['ciudad'].disable();
    this.nuevaEscrituraForm.controls['tipoFedatario'].disable();
    this.nuevaEscrituraForm.controls['numero'].disable();
    this.nuevaEscrituraForm.controls['nombreFedatario'].disable();
    this.nuevaEscrituraForm.controls['descripcion'].disable();
    this.nuevaEscrituraForm.controls['archivo'].disable();
  }

  private desactivarCamposFormasEjecucion() {
    this.empresaFormaEjecucionForm.controls['formaEjecucion'].disable();
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
