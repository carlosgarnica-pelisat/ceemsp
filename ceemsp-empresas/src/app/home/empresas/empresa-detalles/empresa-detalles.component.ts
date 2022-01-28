import {Component, OnInit} from '@angular/core';
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

@Component({
  selector: 'app-empresa-detalles',
  templateUrl: './empresa-detalles.component.html',
  styleUrls: ['./empresa-detalles.component.css']
})
export class EmpresaDetallesComponent implements OnInit {

  faPencil = faPencilAlt;
  faTrash = faTrash;

  empresa: Empresa;
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

  empresaModalidades: EmpresaModalidad[];
  empresaFormasEjecucion: EmpresaFormaEjecucion[];
  modalidades: Modalidad[];
  modalidad: Modalidad;

  formularioModalidad: boolean = false;
  formularioFormasEjecucion: boolean = false;

  constructor(private toastService: ToastService, private empresaService: EmpresaService,
              private route: ActivatedRoute, private modalService: NgbModal,
              private formBuilder: FormBuilder, private modalidadService: ModalidadesService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;

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

    this.empresaFormaEjecucionForm = this.formBuilder.group({
      formaEjecucion: ['', Validators.required]
    });

    this.empresaModalidadForm = this.formBuilder.group({
      modalidad: ['', Validators.required],
      submodalidad: [''],
      numeroRegistroFederal: [''],
      fechaInicio: [''],
      fechaFin: ['']
    });

    this.empresaCreacionForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      razonSocial: ['', Validators.required],
      nombreComercial: ['', Validators.required],
      rfc: ['', Validators.required],
      curp: [''],
      sexo: [''],
      correoElectronico: ['', Validators.required],
      telefono: ['', Validators.required]
    })

    this.empresaCambioStatusForm = this.formBuilder.group({
      status: ['', Validators.required],
      observaciones: ['', Validators.required]
    });

    this.empresaUsuarioForm = this.formBuilder.group({
      'email': ['', Validators.required],
      'password': [''],
      'nombres': ['', Validators.required],
      'apellidos': ['', Validators.required]
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

  mostrarModalCambiarStatus(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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

  seleccionarModalidad(event) {
    this.modalidad = this.modalidades.filter(m => m.uuid === event.value)[0];
    if(this.modalidad.submodalidades.length > 0) {
      this.modalidad.tieneSubmodalidades = true;
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

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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

  mostrarEditarEmpresaModal(modal) {
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
    if(!empresaModalidadForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la modalidad en la empresa",
      ToastType.INFO
    );

    let formData: EmpresaModalidad = empresaModalidadForm.value;

    this.empresaService.guardarModalidad(this.uuid, formData).subscribe((data: EmpresaModalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la modalidad en la empresa con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la modalidad en la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
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

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la forma de ejecucion",
      ToastType.INFO
    );

    let value: EmpresaFormaEjecucion = form.value;

    this.empresaService.guardarFormaEjecucion(this.uuid, value).subscribe((data: EmpresaFormaEjecucion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la forma de ejecucion con exito",
        ToastType.SUCCESS
      )
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la forma de ejecucion. Motivo: ${error}`,
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
