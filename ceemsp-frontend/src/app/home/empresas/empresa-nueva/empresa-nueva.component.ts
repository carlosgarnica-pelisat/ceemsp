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
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import ExisteEmpresa from "../../../_models/ExisteEmpresa";
import {ValidacionService} from "../../../_services/validacion.service";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

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
  empresaEscritura: EmpresaEscritura;

  faTrash = faTrash;
  faPencil = faPencilAlt;

  showSocioForm: boolean;
  showApoderadoForm: boolean;
  showRepresentanteForm: boolean;
  showConsejoForm: boolean;

  nuevoSocioForm: FormGroup;
  nuevoApoderadoForm: FormGroup;
  nuevoRepresentanteForm: FormGroup;
  nuevoConsejoAdministracionForm: FormGroup;

  tempFile;
  pestanaActual = 'SOCIOS';

  existeEmpresa: ExisteEmpresa;

  closeResult: string;
  modal: NgbModalRef;

  constructor(private formBuilder: FormBuilder, private modalidadService: ModalidadesService,
              private toastService: ToastService, private empresaService: EmpresaService,
              private publicService: PublicService, private validacionService: ValidacionService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.empresaCreacionForm = this.formBuilder.group({
      tipoTramite: ['', Validators.required],
      registro: ['', [Validators.required, Validators.max(5)]],
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
      numeroEscritura: ['', Validators.required],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', Validators.required],
      tipoFedatario: ['', Validators.required],
      numero: ['', Validators.required],
      nombreFedatario: ['', Validators.required],
      descripcion: ['', Validators.required]
    });

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    });

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', Validators.required]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required]
    })
  }

  eliminarSocio(index) {

  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
  }

  guardarSocio(form) {

  }

  next(stepName: string, form) {

    console.log(form.value.telefono.replace(/\D/g, '')); // TODO: Agregar esta expresion para reemplazar los valores enmascarados y validar la longitud

    if(form !== undefined && !form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }

    switch (stepName) {
      case 'DOMICILIOS':
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
        })
        break;
      case 'LEGAL':
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
        }
        break;
      case 'FORMAS_EJECUCION':
        this.stepper.next();
        break;
      case 'RESUMEN':
        this.stepper.next();
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
    //this.stepper.previous()
  }

  // Funciones para la primera pagina (informacion de la empresa)
  cambiarTipoTramite(event) {
    this.tipoTranite = event.value;
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

  agregarDomicilio(form) {
    console.log(form.value);
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos que estan vacios",
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

  consultarEmpresaRfc(event) {

    let existeEmpresa: ExisteEmpresa = new ExisteEmpresa();
    existeEmpresa.rfc = event.value;

    if(existeEmpresa.rfc.length !== 12 && existeEmpresa.rfc.length !== 13) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se puede consultar la empresa en la base de datos. El CURP tiene longitud invalida`,
        ToastType.WARNING
      );
      return;
    }

    this.validacionService.validarEmpresa(existeEmpresa).subscribe((existeEmpresa: ExisteEmpresa) => {
      this.existeEmpresa = existeEmpresa;
      console.log(this.existeEmpresa);
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
