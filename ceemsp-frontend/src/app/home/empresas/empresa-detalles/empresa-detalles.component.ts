import {Component, OnInit, ViewChild} from '@angular/core';
import Empresa from "../../../_models/Empresa";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Modalidad from "../../../_models/Modalidad";
import {ModalidadesService} from "../../../_services/modalidades.service";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import EmpresaFormaEjecucion from "../../../_models/EmpresaFormaEjecucion";
import Usuario from "../../../_models/Usuario";
import * as sha256 from "js-sha256";
import ExisteUsuario from "../../../_models/ExisteUsuario";
import {ValidacionService} from "../../../_services/validacion.service";
import Visita from "../../../_models/Visita";
import Submodalidad from "../../../_models/Submodalidad";
import {Table} from "primeng/table";
import {AuthenticationService} from "../../../_services/authentication.service";

@Component({
  selector: 'app-empresa-detalles',
  templateUrl: './empresa-detalles.component.html',
  styleUrls: ['./empresa-detalles.component.css']
})
export class EmpresaDetallesComponent implements OnInit {

  mostrarContrasena: boolean = false;

  faPencil = faPencilAlt;
  faTrash = faTrash;

  logo: any;

  empresa: Empresa;
  year: string;
  tipoTramite: string;
  domicilios: EmpresaDomicilio[];
  escrituras: EmpresaEscritura[];
  uuid: string;

  modal: NgbModalRef;
  closeResult: string;

  tipoPersona: string;

  empresaModalidadForm: FormGroup;
  empresaCreacionForm: FormGroup;
  empresaFormaEjecucionForm: FormGroup;
  empresaCambioStatusForm: FormGroup;
  empresaUsuarioForm: FormGroup;

  empresaModalidades: EmpresaModalidad[] = [];
  empresaFormasEjecucion: EmpresaFormaEjecucion[] = [];
  modalidades: Modalidad[];
  visitas: Visita[] = [];
  modalidad: Modalidad;
  submodalidad: Submodalidad;

  formularioModalidad: boolean = false;
  formularioFormasEjecucion: boolean = false;

  uuidModalidadTemporal: string;
  uuidFormaEjecucionTemporal: string;

  modalidadQuery: string = '';
  submodalidadQuery: string = '';

  tempFile;
  tempLogo;
  pdfActual;

  existeUsuario: ExisteUsuario;
  usuarioActual: Usuario;
  pdfBlob;

  @ViewChild('eliminarModalidadModal') eliminarDomicilioModal: any;
  @ViewChild('eliminarFormaEjecucionModal') eliminarFormaEjecucionModal: any;
  @ViewChild('agregarModalidadModal') agregarModalidadModal: any;
  @ViewChild('visualizarDocumentoRegistroFederal') visualizarDocumentoRegistroFederal: any;
  @ViewChild('agregarFormaEjecucionModal') agregarFormaEjecucionModal: any;

  constructor(private toastService: ToastService, private empresaService: EmpresaService,
              private route: ActivatedRoute, private modalService: NgbModal,
              private formBuilder: FormBuilder, private modalidadService: ModalidadesService,
              private validacionService: ValidacionService, private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    let usuario = this.authenticationService.currentUserValue;
    this.usuarioActual = usuario.usuario;
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;

      this.empresaService.obtenerEmpresaLogo(this?.uuid).subscribe((data: Blob) => {
        this.convertirImagenLogo(data);
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la fotografia. Motivo: ${error}`,
          ToastType.ERROR
        )
      })

      if(this.empresa?.tipoTramite === 'AP') {
        this.year = this.empresa?.registro.split('/')[4];
      } else {
        this.year = this.empresa?.registro.split('/')[3];
      }

      this.tipoTramite = this.empresa?.tipoTramite;

      this.empresaService.obtenerDomicilios(this.empresa.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domicilios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `no se han podido descargar los domicilios de la empresa. Motivo: ${error}`,
          ToastType.ERROR
        );
      });

      this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
        this.empresaModalidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se pudo descargar la informacion de las modalidades de la empresa. ${error}`,
            ToastType.ERROR
        )
      });

      this.empresaService.obtenerFormasEjecucion(this.uuid).subscribe((data: EmpresaFormaEjecucion[]) => {
        this.empresaFormasEjecucion = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las formas de ejecucion. Motivo: ${error}`,
          ToastType.ERROR
        )
      })

      this.empresaService.obtenerEscrituras(this.empresa.uuid).subscribe((data: EmpresaEscritura[]) => {
        this.escrituras = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las escrituras de la empresa. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la emprsa. ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerVisitas(this.uuid).subscribe((data: Visita[]) => {
      this.visitas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las visitas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaFormaEjecucionForm = this.formBuilder.group({
      formaEjecucion: ['', Validators.required]
    });

    this.empresaModalidadForm = this.formBuilder.group({
      numeroRegistroFederal: ['', Validators.maxLength(30)],
      fechaInicio: [''],
      fechaFin: ['']
    });

    this.empresaCreacionForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]],
      sexo: [''],
      correoElectronico: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      telefono: ['', [Validators.required]],
      observaciones: [''],
      registro: ['', Validators.required],
      registroFederal: [''],
      fechaInicio: [''],
      fechaFin: ['']
    })

    this.empresaCambioStatusForm = this.formBuilder.group({
      status: ['', Validators.required],
      observaciones: ['', Validators.required]
    });

    this.empresaUsuarioForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      password: ['', [Validators.minLength(8), Validators.maxLength(15)]],
      usuario: ['', [Validators.required, Validators.maxLength(20)]]
    })
  }

  mostrarModalFormasEjecucion(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalAgregarFormaEjecucion() {
    this.modal = this.modalService.open(this.agregarFormaEjecucionModal, {size: 'xl', backdrop: 'static', keyboard: false})
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
  }

  mostrarModalModalidades(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })

    this.modalidadService.obtenerModalidadesPorFiltro("TIPO", this.empresa.tipoTramite).subscribe((data: Modalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las modalidades. ${error}`,
        ToastType.ERROR
      )
    })
  }

  verificarRegistro($event) {

  }

  actualizarLogo(event) {
    this.tempLogo = event.target.files[0]
  }

  clear(table: Table) {
    table.clear();
  }

  convertirImagenLogo(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.logo = reader.result
    });

    if(imagen) {
      reader.readAsDataURL(imagen)
    }
  }

  mostrarModalAgregarModalidad() {
    this.modal = this.modalService.open(this.agregarModalidadModal, {size: 'xl', backdrop: 'static'})
  }

  seleccionarSubmodalidad(uuid) {
    let existeSubmodalidad = this.empresaModalidades.filter(m => m.submodalidad?.uuid === uuid)[0];
    if(existeSubmodalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Esta submodalidad ya esta registrada en esta empresa",
        ToastType.WARNING
      );
      this.modalidad = undefined;
      return;
    }
    this.submodalidad = this.modalidad.submodalidades?.filter(s => s.uuid === uuid)[0];
  }

  seleccionarModalidad(event) {
    let existeModalidad = this.empresaModalidades.filter(m => m.modalidad.uuid === event)[0];

    if(existeModalidad !== undefined) {
      let modalidad = this.modalidades.filter(m => m.uuid === event)[0];
      if(modalidad.submodalidades.length < 1) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Esta modalidad ya se encuentra registrada en la empresa. Favor de seleccionar otra",
          ToastType.WARNING
        );
        this.modalidad = undefined;
        return;
      }
    }

    this.modalidad = this.modalidades.filter(m => m.uuid === event)[0];
    if(this.modalidad.submodalidades.length > 0) {
      this.modalidad.tieneSubmodalidades = true;
    } else {
      this.empresaModalidadForm.patchValue({
        submodalidad: undefined
      });
    }

    if(this.modalidad.tipo === 'EAFJAL') {
      this.empresaModalidadForm.patchValue({
        numeroRegistroFederal: this.empresa.registroFederal,
        fechaInicio: this.empresa.fechaInicio,
        fechaFin: this.empresa.fechaFin
      })

      this.empresaModalidadForm.controls['numeroRegistroFederal'].disable();
      this.empresaModalidadForm.controls['fechaInicio'].disable();
      this.empresaModalidadForm.controls['fechaFin'].disable();
    } else {
      this.empresaModalidadForm.patchValue({
        numeroRegistroFederal: '',
        fechaInicio: '',
        fechaFin: ''
      })

      this.empresaModalidadForm.controls['numeroRegistroFederal'].enable();
      this.empresaModalidadForm.controls['fechaInicio'].enable();
      this.empresaModalidadForm.controls['fechaFin'].enable();
    }
  }

  mostrarModalCrearUsuario(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalCambioInicioSesion(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.empresaUsuarioForm.patchValue({
      email: this.empresa?.usuario?.email,
      usuario: this.empresa?.usuario?.username
    })

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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

  mostrarModalDocumentoRegistroFederal() {
    this.empresaService.obtenerDocumentoRegistroFederal(this.empresa?.uuid).subscribe((data: Blob) => {
      this.modal = this.modalService.open(this.visualizarDocumentoRegistroFederal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})
      this.pdfBlob = data;
      this.convertirPdf(data);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el documento del registro federal. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  guardarUsuario(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han completado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el usuario de la empresa",
      ToastType.INFO
    );

    let value: Usuario = form.value;
    if(value.password !== undefined) {
      value.password = sha256.sha256(value.password);
    }

    this.empresaService.goardarUsuario(this.uuid, value).subscribe((data: Empresa) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el usuario con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar o actualizar el usuario. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  cambiarStatusEmpresa(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han completado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el nuevo status de la empresa",
      ToastType.INFO
    );

    let value: Empresa = form.value;

    this.empresaService.cambiarStatusEmpresa(this.uuid, value).subscribe((data: Empresa) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha cambiado el status de la empresa",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido cambiar el status de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  consultarEmail(event) {
    let existeUsuario: ExisteUsuario = new ExisteUsuario();
    existeUsuario.email = event.value;

    this.validacionService.validarUsuario(existeUsuario).subscribe((existeUsuario: ExisteUsuario) => {
      this.existeUsuario = existeUsuario;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido consultar la existencia de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEditarEmpresaModal(modal) {
    let numero: string;
    if(this.tipoTramite === 'AP') {
      numero = this.empresa?.registro.split('/')[3]
    } else {
      numero =  this.empresa?.registro.split('/')[2]
    }

    this.empresaCreacionForm.patchValue({
      tipoPersona: this.empresa.tipoPersona,
      nombreComercial: this.empresa.nombreComercial,
      razonSocial: this.empresa.razonSocial,
      rfc: this.empresa.rfc,
      registro: numero,
      sexo: this.empresa.sexo,
      curp: this.empresa.curp,
      correoElectronico: this.empresa.correoElectronico,
      telefono: this.empresa.telefono,
      observaciones: this.empresa.observaciones,
      registroFederal: this.empresa.registroFederal,
      fechaInicio: this.empresa.fechaInicio,
      fechaFin: this.empresa.fechaFin
    })

    if(this.tipoTramite === 'EAFJAL') {
      this.empresaCreacionForm.controls['registroFederal'].setValidators([Validators.required])
      this.empresaCreacionForm.controls['fechaInicio'].setValidators([Validators.required])
      this.empresaCreacionForm.controls['fechaFin'].setValidators([Validators.required])

      this.empresaCreacionForm.controls['registroFederal'].updateValueAndValidity();
      this.empresaCreacionForm.controls['fechaInicio'].updateValueAndValidity();
      this.empresaCreacionForm.controls['fechaFin'].updateValueAndValidity();
    } else {
      this.empresaCreacionForm.controls['registroFederal'].setValidators([])
      this.empresaCreacionForm.controls['fechaInicio'].setValidators([])
      this.empresaCreacionForm.controls['fechaFin'].setValidators([])

      this.empresaCreacionForm.controls['registroFederal'].updateValueAndValidity();
      this.empresaCreacionForm.controls['fechaInicio'].updateValueAndValidity();
      this.empresaCreacionForm.controls['fechaFin'].updateValueAndValidity();
    }

    this.tipoPersona = this.empresa.tipoPersona;

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarFormularioModalidad() {
    this.formularioModalidad = true;
  }

  ocultarFormularioModalidad() {
    this.formularioModalidad = false;
  }

  mostrarFormularioFormaEjecucion() {
    this.formularioFormasEjecucion = !this.formularioFormasEjecucion;
  }

  agregarModalidad(empresaModalidadForm) {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la modalidad en la empresa",
      ToastType.INFO
    );

    let formData = empresaModalidadForm.value;

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

    formData.modalidad = this.modalidad;

    if(this.modalidad.tieneSubmodalidades) {
      if(this.submodalidad === undefined) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `Favor de seleccionar una submodalidad`,
          ToastType.WARNING
        );
        return;
      }

      formData.submodalidad = this.submodalidad;
    }

    this.empresaService.guardarModalidad(this.uuid, formData).subscribe((data: EmpresaModalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la modalidad en la empresa con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaModalidadForm.reset();
      this.modalidad = undefined;
      this.submodalidad = undefined;
      this.empresaService.obtenerModalidades(this.empresa.uuid).subscribe((data: EmpresaModalidad[]) => {
        this.empresaModalidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las modalidades. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la modalidad en la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEliminarFormaEjecucionModal(uuid) {
    this.uuidFormaEjecucionTemporal = uuid;

    this.modal = this.modalService.open(this.eliminarFormaEjecucionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarModalidadModal(uuid) {
    this.uuidModalidadTemporal = uuid;

    this.modal = this.modalService.open(this.eliminarDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  agregarFormaEjecucion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario no ha sido completado`,
        ToastType.WARNING
      );
      return;
    }

    let value: EmpresaFormaEjecucion = form.value;
    if(this.empresaFormasEjecucion.length > 0) {
      let formasEjecucionExistentes = this.empresaFormasEjecucion.filter(x => x.formaEjecucion === value.formaEjecucion)

      if(formasEjecucionExistentes.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Esta forma de ejecucion ya se encuentra registrada en la empresa",
          ToastType.WARNING
        );
        return;
      }
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la forma de ejecucion",
      ToastType.INFO
    );

    this.empresaService.guardarFormaEjecucion(this.uuid, value).subscribe((data: EmpresaFormaEjecucion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la forma de ejecucion con exito",
        ToastType.SUCCESS
      );
      this.mostrarFormularioFormaEjecucion();
      this.modal.close();
      this.empresaService.obtenerPorUuid(this?.uuid).subscribe((data: Empresa) => {
        this.empresa = data;
        this.empresaService.obtenerFormasEjecucion(this.empresa.uuid).subscribe((data: EmpresaFormaEjecucion[]) => {
          this.empresaFormasEjecucion = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las formas de ejecucion. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la empresa. Motivo: ${error}`,
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

  modificarEmpresa(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificarlo",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos modificando la empresa",
      ToastType.INFO
    );

    let formValue: Empresa = form.value;
    console.log(this.tipoTramite);
    if(this.tipoTramite === 'AP') {
      formValue.registro = `CESP/AP/SPSMD/${formValue.registro}/${this.year}`;
    } else {
      formValue.registro = `CESP/${this.tipoTramite}/${formValue.registro}/${this.year}`;
    }

    let formDataEmpresa = new FormData();

    if(this.tempFile !== undefined) {
      formDataEmpresa.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formDataEmpresa.append('archivo', null)
    }

    if(this.tempLogo !== undefined) {
      formDataEmpresa.append('logo', this.tempLogo, this.tempLogo.name);
    } else {
      formDataEmpresa.append('logo', null);
    }

    formDataEmpresa.append('empresa', JSON.stringify(formValue));

    this.empresaService.modificarEmpresa(this.empresa.uuid, formDataEmpresa).subscribe((data: Empresa) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha actualizado la empresa con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarFormaEjecucion() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el domicilio",
      ToastType.INFO
    );

    this.empresaService.eliminarFormaEjecucion(this.uuid, this.uuidFormaEjecucionTemporal).subscribe((data: EmpresaFormaEjecucion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se elimino la forma de ejecucion con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
        this.empresa = data;
        this.empresaService.obtenerFormasEjecucion(this.uuid).subscribe((data: EmpresaFormaEjecucion[]) => {
          this.empresaFormasEjecucion = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las formas de ejecucion. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la informacion de la empresa. Motivo: ${error}`,
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
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el domicilio",
      ToastType.INFO
    );

    this.empresaService.eliminarModalidad(this.uuid, this.uuidModalidadTemporal).subscribe((data: EmpresaModalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerModalidades(this.empresa.uuid).subscribe((data: EmpresaModalidad[]) => {
        this.empresaModalidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las modalidades. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido elimimar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  conmutarMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  autogenerarContrasena() {
    this.empresaUsuarioForm.patchValue({
      password: this.hacerFalsoUuid(12)
    })
  }

  quitarModalidad() {

  }

  quitarSubmodalidad() {

  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  descargarDocumentoRegistroFederal() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "registro-federal.pdf";
    link.click();
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
