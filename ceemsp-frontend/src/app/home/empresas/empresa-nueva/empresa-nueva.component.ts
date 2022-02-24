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
import {faPencilAlt, faTrash, faUsers, faChevronLeft, faChevronRight} from "@fortawesome/free-solid-svg-icons";
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

@Component({
  selector: 'app-empresa-nueva',
  templateUrl: './empresa-nueva.component.html',
  styleUrls: ['./empresa-nueva.component.css']
})
export class EmpresaNuevaComponent implements OnInit {

  empresaCreacionForm: FormGroup;
  empresaModalidadForm: FormGroup;
  empresaDomiciliosForm: FormGroup;
  nuevaEscrituraForm: FormGroup;

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

  tempFile;
  pestanaActual = 'SOCIOS';

  existeEmpresa: ExisteEmpresa;

  closeResult: string;
  modal: NgbModalRef;

  porcentaje: number = 0.00;

  constructor(private formBuilder: FormBuilder, private modalidadService: ModalidadesService,
              private toastService: ToastService, private empresaService: EmpresaService,
              private publicService: PublicService, private validacionService: ValidacionService,
              private modalService: NgbModal) { }

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
      domicilio1: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio2: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio3: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio4: ['', Validators.maxLength],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      estado: ['', Validators.required],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required],
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
    });

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', [Validators.required, Validators.maxLength(10)]],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', [Validators.required, Validators.maxLength(60)]],
      tipoFedatario: ['', Validators.required],
      numero: ['', Validators.required],
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
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', Validators.required]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required]
    })

    this.empresaFormaEjecucionForm = this.formBuilder.group({
      formaEjecucion: ['', Validators.required]
    });
  }

  eliminarSocio(index) {
    this.empresaEscritura.socios.splice(index, 1);
  }

  eliminarApoderado(index) {
    this.empresaEscritura.apoderados.splice(index, 1);
  }

  eliminarRepresentante(index) {
    this.empresaEscritura.representantes.splice(index, 1);
  }

  eliminarConsejo(index) {
    this.empresaEscritura.consejos.splice(index, 1);
  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
  }

  mostrarFormularioNuevoConsejo() {
    this.showConsejoForm = !this.showConsejoForm;
  }

  mostrarFormularioNuevoRepresentante() {
    this.showRepresentanteForm = !this.showRepresentanteForm;
  }

  mostrarFormularioNuevoApoderado() {
    this.showApoderadoForm = !this.showApoderadoForm;
  }

  guardarSocio(form) {
    console.log(form);
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    let empresaSocio: EmpresaEscrituraSocio = form.value;

    if(this.empresaEscritura.socios.length > 0) {
      this.porcentaje = 0.00;
      this.empresaEscritura.socios.forEach((s) => {
        this.porcentaje += parseInt(String(s.porcentajeAcciones));
      })

      this.porcentaje += parseInt(String(empresaSocio.porcentajeAcciones));
      console.log(this.porcentaje);

      if(this.porcentaje > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    }

    this.empresaEscritura.socios.push(empresaSocio);
    this.nuevoSocioForm.reset();
    this.mostrarFormularioNuevoSocio();
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

    this.empresaEscritura.apoderados.push(empresaApoderado);
    this.nuevoApoderadoForm.reset();
    this.mostrarFormularioNuevoApoderado();
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
    this.empresaEscritura.representantes.push(empresaRepresentante);
    this.nuevoRepresentanteForm.reset();
    this.mostrarFormularioNuevoRepresentante();
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
    this.empresaEscritura.consejos.push(empresaConsejo);
    this.nuevoConsejoAdministracionForm.reset();
    this.mostrarFormularioNuevoConsejo();
  }

  eliminarEscritura(index) {
    this.empresaEscrituras.splice(index, 1);
  }

  eliminarFormaEjecucion(index) {
    this.empresaFormasEjecucion.splice(index, 1);
  }

  next(stepName: string, form) {

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
        /*if(this.existeEmpresa.existe) {
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
        empresa.registro = `CESP/${this.tipoTranite}/${formData.registro}/${this.year}`;
        if(empresa.tipoPersona === 'MORAL') {
          empresa.sexo = 'NA';
        } else {
          empresa.sexo = formData.sexo;
        }

        empresa.curp = formData.curp;

        empresa.modalidades = this.empresaModalidades;

        this.empresaService.guardarEmpresa(empresa).subscribe((data: Empresa) => {
          this.empresa = data;
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
        })*/
        this.stepper.next();
        break;
      case 'LEGAL':
        /*if(this.empresaDomicilios.length < 1) {
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
          this.domiciliosGuardados = true;
        }*/
        this.stepper.next();
        break;
      case 'FORMAS_EJECUCION':
        /*if(this.empresaEscrituras.length < 1) {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            "No hay escrituras creados aun para la empresa.",
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
          this.stepper.next();
        }*/
        this.stepper.next();
        break;
      case 'RESUMEN':
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
          this.stepper.next();
        }
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Este paso no ha sido implementado aun. Que intentas hacer",
          ToastType.ERROR
        )
    }
  }

  previous() {
    this.stepper.previous()
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

  eliminarModalidad(index) {
    this.empresaModalidades.splice(index, 1);
  }

  eliminarDomicilio(index) {
    this.empresaDomicilios.splice(index, 1);
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

    let formData: EmpresaDomicilio = form.value;
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
    console.log(this.empresaEscrituras);
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
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

    if(existeEmpresa.curp.length !== 18) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "No se puede consultar la empresa en la base de datos. El CURP tiene longitud invalida",
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
    let existeEmpresa: ExisteEmpresa = new ExisteEmpresa();
    existeEmpresa.rfc = event.value;

    if(existeEmpresa.rfc.length !== 12 && existeEmpresa.rfc.length !== 13) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se puede consultar la empresa en la base de datos. El RFC tiene longitud invalida`,
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

  private desactivarCamposEmpresa() {
    this.empresaCreacionForm.controls['tipoTramite'].disable();
    this.empresaCreacionForm.controls['registro'].disable();
    this.empresaCreacionForm.controls['tipoPersona'].disable();
    this.empresaCreacionForm.controls['razonSocial'].disable();
    this.empresaCreacionForm.controls['nombreComercial'].disable();
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
