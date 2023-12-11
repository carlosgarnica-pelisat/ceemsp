import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {UsuariosService} from "../../../_services/usuarios.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {BuzonInternoService} from "../../../_services/buzon-interno.service";
import Empresa from "../../../_models/Empresa";
import {ToastType} from "../../../_enums/ToastType";
import Usuario from "../../../_models/Usuario";
import BuzonSalida from "../../../_models/BuzonSalida";
import {BotonBuzonSalidaComponent} from "../../../_components/botones/boton-buzon-salida/boton-buzon-salida.component";
import BuzonInternoDestinatario from "../../../_models/BuzonInternoDestinatario";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-buzon-salida',
  templateUrl: './buzon-salida.component.html',
  styleUrls: ['./buzon-salida.component.css']
})
export class BuzonSalidaComponent implements OnInit {

  private gridApi;
  private gridColumnApi;
  private actionInProgress: boolean = false;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Motivo', field: 'motivo', sortable: true, autoHeight: true, wrapText: true, filter: true },
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  rowData = [];

  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

  tipo: string;

  empresas: Empresa[];
  usuarios: Usuario[];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };
  crearMensajeForm: FormGroup;
  crearDestinatarioForm: FormGroup;
  mensaje: BuzonSalida;

  destinatarios: BuzonInternoDestinatario[] = [];
  showDestinatarioForm: boolean = false;
  editandoDestinatario: boolean = false;

  editorData: string = "Favor de escribir el motivo por el cual manda el mensaje."

  empresa: Empresa = undefined;
  usuario: Usuario = undefined;

  empresaQuery: string = "";
  usuarioQuery: string = "";
  tipoDestinatario: string = "";

  pestanaActual: string = "DETALLES";

  destinatario: BuzonInternoDestinatario;
  temporaryUuid: string;

  @ViewChild('crearMensajeModal') crearMensajeModal;
  @ViewChild('mostrarMensajeModal') mostrarMensajeModal;
  @ViewChild('eliminarBuzonSalidaModal') eliminarBuzonSalidaModal;
  @ViewChild('eliminarBuzonSalidaDestinatario') eliminarBuzonSalidaDestinatarioModal;
  @ViewChild('modificarMensajeModal') modificarMensajeModal;

  constructor(private modalService: NgbModal, private toastService: ToastService, private empresaService: EmpresaService,
              private usuarioService: UsuariosService, private formBuilder: FormBuilder, private buzonInternoService: BuzonInternoService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonBuzonSalidaComponent
    }

    this.crearMensajeForm = this.formBuilder.group({
      motivo: ['', [Validators.required, Validators.maxLength(255)]]
    })

    this.crearDestinatarioForm = this.formBuilder.group({
      tipoDestinatario: ['', Validators.required],
      email: ['', [Validators.required]]
    })

    this.buzonInternoService.obtenerBuzonInterno().subscribe((data: BuzonSalida[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los mensajes. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerEmpresas().subscribe((data: Empresa[]) => {
      this.empresas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las empresas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.usuarioService.obtenerUsuariosInternos().subscribe((data: Usuario[]) => {
      this.usuarios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los usuarios. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData)
  }

  editar(rowData) {

    this.buzonInternoService.obtenerMensajePorUuid(rowData.rowData?.uuid).subscribe((data: BuzonSalida) => {
      this.mensaje = data;
      this.uuid = data?.uuid;
      this.mostrarModalEditar();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.buzonInternoService.obtenerMensajePorUuid(rowData.rowData?.uuid).subscribe((data: BuzonSalida) => {
      this.mensaje = data;
      this.uuid = data?.uuid;
      this.mostrarModalEliminar();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  mostrarNuevoMensajeModal() {
    this.modal = this.modalService.open(this.crearMensajeModal, {'size' : 'xl', 'backdrop': 'static'})
  }

  mostrarModalEditar() {
    this.crearMensajeForm.patchValue({
      motivo: this.mensaje.motivo
    })
    this.editorData = this.mensaje.mensaje;
    this.modal = this.modalService.open(this.modificarMensajeModal, {'size': 'xl', backdrop: 'static'});
  }

  mostrarModalEliminar() {
    this.modal = this.modalService.open(this.eliminarBuzonSalidaModal, {'size': 'lg', 'backdrop': 'static'})
  }

  confirmarEliminarMensaje() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el mensaje`,
      ToastType.INFO
    );

    this.buzonInternoService.eliminarMensajePorUuid(this.uuid).subscribe((data: BuzonSalida) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado el mensaje con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el mensaje. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarFormularioDestinatario() {
    this.showDestinatarioForm = !this.showDestinatarioForm;

    if(!this.showDestinatarioForm) {
      this.crearDestinatarioForm.reset();
    }
  }

  seleccionarEmpresa(uuid) {
    this.empresa = this.empresas.filter(x => x.uuid === uuid)[0];
    this.crearDestinatarioForm.patchValue({
      email: this.empresa.usuario.email
    })
  }

  seleccionarUsuario(uuid) {
    this.usuario = this.usuarios.filter(x => x.uuid === uuid)[0];
    this.crearDestinatarioForm.patchValue({
      email: this.usuario.email
    })
  }

  eliminarEmpresa() {

  }

  eliminarUsuario() {

  }

  agregarDestinatario(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario es invalido`,
        ToastType.WARNING
      );
      return;
    }

    let buzonDestinatario: BuzonInternoDestinatario = new BuzonInternoDestinatario();
    buzonDestinatario.tipoDestinatario = this.tipoDestinatario;
    buzonDestinatario.empresa = this.empresa;
    buzonDestinatario.usuario = this.usuario;
    buzonDestinatario.email = form.value.email;

    this.destinatarios.push(buzonDestinatario);
    this.mostrarFormularioDestinatario();
    this.empresa = undefined;
    this.usuario = undefined;
    this.tipoDestinatario = "";
  }

  cambiarTipoDestinatario(event) {
    this.tipoDestinatario = event.value;
  }

  checkForDetails(data) {
    //this.modal = this.modalService.open(showCustomerDetailsModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;
    this.buzonInternoService.obtenerMensajePorUuid(this.uuid).subscribe((data: BuzonSalida) => {
      this.mensaje = data;
      this.modal = this.modalService.open(this.mostrarMensajeModal, {'size' : 'xl'})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el mensaje. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  modificarMensaje(mensajeForm) {
    if(!mensajeForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos obligatorios que no se han rellenado. Favor de rellenarlos`,
        ToastType.WARNING
      );
      return;
    }

    if(this.editorData.length < 1) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El contenido del mensaje parece estar vacio`,
        ToastType.WARNING
      );
      return;
    }


    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos modificando el mensaje en la base de datos`,
      ToastType.INFO
    );

    let formData: BuzonSalida = mensajeForm.value;
    formData.mensaje = this.editorData;

    this.buzonInternoService.modificarMensajePorUuid(this.mensaje.uuid, formData).subscribe((data: BuzonSalida) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha modificado el mensaje con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido enviar el mensaje. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  enviarMensaje(mensajeForm) {
    this.actionInProgress = true;
    if(!mensajeForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos obligatorios que no se han rellenado. Favor de rellenarlos`,
        ToastType.WARNING
      );
      this.actionInProgress = false;
      return;
    }

    if(this.destinatarios.length < 1) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Necesitas por lo menos un destinatario al cual enviarle el mensaje`,
        ToastType.WARNING
      );
      this.actionInProgress = false;
      return;
    }

    if(this.editorData.length < 1) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El contenido del mensaje parece estar vacio`,
        ToastType.WARNING
      );
      this.actionInProgress = false;
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos guardando el mensaje en la base de datos`,
      ToastType.INFO
    );

    let formData: BuzonSalida = mensajeForm.value;
    formData.mensaje = this.editorData;
    formData.destinatarios = this.destinatarios;

    this.buzonInternoService.guardarMensaje(formData).subscribe((data: BuzonSalida) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha mandado el mensaje con exito`,
        ToastType.SUCCESS
      );
      this.actionInProgress = false;
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido enviar el mensaje. Motivo: ${error}`,
        ToastType.ERROR
      );
      this.actionInProgress = false;
    })
  }

  mostrarModalEditarDestinatario(index) {
    this.destinatario = this.mensaje.destinatarios[index];
    this.mostrarAgregarDestinatarioForm();
    this.editandoDestinatario = true;
    this.tipoDestinatario = this.destinatario.tipoDestinatario;
    this.crearDestinatarioForm.patchValue({
      tipoDestinatario: this.destinatario.tipoDestinatario,
      email: this.destinatario.email
    })
    this.empresa = this.empresas.filter(x => x.uuid === this.destinatario.empresa?.uuid)[0]
  }

  mostrarModalEliminarDestinatario(uuid) {
    this.temporaryUuid = uuid;
    this.modal = this.modalService.open(this.eliminarBuzonSalidaDestinatarioModal, {size: 'lg', backdrop: "static"})
  }

  confirmarEliminarDestinatario() {
    this.buzonInternoService.eliminarDestinatario(this.uuid, this.temporaryUuid).subscribe((data: BuzonInternoDestinatario) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el destinatario con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.buzonInternoService.obtenerMensajePorUuid(this.uuid).subscribe((data: BuzonSalida) => {
        this.mensaje = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar el mensaje. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el destinatario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarAgregarDestinatarioForm() {
    this.showDestinatarioForm = !this.showDestinatarioForm;

    if(!this.showDestinatarioForm) {
      this.crearDestinatarioForm.reset();
    }

    if(this.editandoDestinatario) {
      this.editandoDestinatario = false;
      //this.coloresTemp.push(this.colorVehiculo);
    }
  }

  guardarDestinatario(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos requeridos que no se han rellanado`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el nuevo destinatario`,
      ToastType.INFO
    );

    let buzonDestinatario: BuzonInternoDestinatario = new BuzonInternoDestinatario();
    buzonDestinatario.tipoDestinatario = this.tipoDestinatario;
    buzonDestinatario.empresa = this.empresa;
    buzonDestinatario.usuario = this.usuario;
    buzonDestinatario.email = form.value.email;

    if(this.editandoDestinatario) {
      this.buzonInternoService.modificarDestinatario(this.uuid, this.destinatario.uuid, buzonDestinatario).subscribe((data: BuzonInternoDestinatario) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha modificado el destinatario con exito`,
          ToastType.SUCCESS
        );
        this.mostrarAgregarDestinatarioForm();
        this.buzonInternoService.obtenerMensajePorUuid(this.uuid).subscribe((data: BuzonSalida) => {
          this.mensaje = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar el mensaje. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido editar el destinatario. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.buzonInternoService.agregarDestinatario(this.uuid, buzonDestinatario).subscribe((data: BuzonInternoDestinatario) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado el destinatario con exito`,
          ToastType.SUCCESS
        );
        this.mostrarAgregarDestinatarioForm();
        this.buzonInternoService.obtenerMensajePorUuid(this.uuid).subscribe((data: BuzonSalida) => {
          this.mensaje = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar el mensaje. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido agregar el destinatario`,
          ToastType.ERROR
        );
      });
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
