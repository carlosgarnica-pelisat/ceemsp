import {Component, NgZone, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import Persona from "../../../_models/Persona";
import Vehiculo from "../../../_models/Vehiculo";
import Can from "../../../_models/Can";
import Arma from "../../../_models/Arma";
import {EmpresaService} from "../../../_services/empresa.service";
import Cliente from "../../../_models/Cliente";
import {ToastType} from "../../../_enums/ToastType";
import Incidencia from "../../../_models/Incidencia";
import IncidenciaComentario from "../../../_models/IncidenciaComentario";
import {faDownload, faTrash} from "@fortawesome/free-solid-svg-icons";
import {UsuariosService} from "../../../_services/usuarios.service";
import Usuario from "../../../_models/Usuario";
import {
  BotonEmpresaIncidenciasComponent
} from "../../../_components/botones/boton-empresa-incidencias/boton-empresa-incidencias.component";
import Empresa from "../../../_models/Empresa";
import {MapsAPILoader} from "@agm/core";

@Component({
  selector: 'app-empresa-incidencias',
  templateUrl: './empresa-incidencias.component.html',
  styleUrls: ['./empresa-incidencias.component.css']
})
export class EmpresaIncidenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faTrash = faTrash;
  faDownload = faDownload;

  columnDefs = [
    {headerName: 'Numero', field: 'numero', sortable: true, filter: true },
    {headerName: 'Asignado', sortable: true, filter: true, valueGetter: function (params) {if(params.data.asignado === null) { return 'Sin asignar' } return params.data.asignado?.nombres + " " + params.data.asignado?.apellidos} },
    {headerName: 'Fecha de incidencia', field: 'fechaIncidencia', sortable: true, filter: true },
    {headerName: 'Fecha de captura', field: 'fechaCreacion', sortable: true, filter: true },
    {headerName: 'Status', field: 'status', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        cambiarAsignado: this.cambiarAsignado.bind(this),
        cambiarStatus: this.cambiarStatus.bind(this)
      }}
  ];
  rowData = [];

  tempFile;

  uuid: string;
  empresa: Empresa;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };
  clienteInvolucrado: boolean = false;

  crearIncidenciaForm: FormGroup;
  crearPersonalIncidenciaForm: FormGroup;
  crearVehiculoIncidenciaForm: FormGroup;
  crearCanIncidenciaForm: FormGroup;
  crearArmaIncidenciaForm: FormGroup;
  crearArchivoIncidenciaForm: FormGroup;

  cambiarAsignacionTicketForm: FormGroup;
  responderIncidenciaForm: FormGroup;

  cliente: Cliente;
  personalInvolucrado: Persona[] = [];
  vehiculosInvolucrados: Vehiculo[] = [];
  canesInvolucrados: Can[] = [];
  armasInvolucradas: Arma[] = [];

  clientes: Cliente[] = [];
  armas: Arma[] = [];
  canes: Can[] = [];
  vehiculos: Vehiculo[] = [];
  personales: Persona[] = [];
  usuarios: Usuario[] = [];

  mostrarAgregarCanForm: boolean = false;
  mostrarAgregarVehiculoForm: boolean = false;
  mostrarAgregarArmaForm: boolean = false;
  mostrarAgregarPersonalForm: boolean = false;
  mostrarAgregarArchivoForm: boolean = false;

  pestanaActualInvolucramiento: string = 'PERSONAL';
  incidenciaActualTab: string = 'COMENTARIOS';

  editorData: string = "<p>Favor de escribir con detalle el relato de la incidencia</p>"

  incidencia: Incidencia;
  comentario: IncidenciaComentario;

  tempUuid: string;
  tempIndex: number;

  latitude: number;
  longitude: number;
  zoom: number;
  address: string;
  private geoCoder;

  @ViewChild('busquedaDireccion') searchElementRef;

  @ViewChild('mostrarIncidenciaDetallesModal') mostrarIncidenciaDetallesModal;
  @ViewChild('responderIncidenciaModal') responderIncidenciaModal;
  @ViewChild('seleccionarAsignadoModal') seleccionarAsignadoModal;

  @ViewChild('quitarIncidenciaPersonaModal') quitarIncidenciaPersonaModal;
  @ViewChild('quitarIncidenciaArmaModal') quitarIncidenciaArmaModal;
  @ViewChild('quitarIncidenciaCanModal') quitarIncidenciaCanModal;
  @ViewChild('quitarIncidenciaVehiculoModal') quitarIncidenciaVehiculoModal;

  @ViewChild('eliminarIncidenciaPersonaModal') eliminarIncidenciaPersonaModal;
  @ViewChild('eliminarIncidenciaArmaModal') eliminarIncidenciaArmaModal;
  @ViewChild('eliminarIncidenciaCanModal') eliminarIncidenciaCanModal;
  @ViewChild('eliminarIncidenciaVehiculoModal') eliminarIncidenciaVehiculoModal;
  @ViewChild('eliminarIncidenciaArchivoModal') eliminarIncidenciaArchivoModal;

  @ViewChild('editarComentarioIncidenciaModal') editarComentarioIncidenciaModal;
  @ViewChild('eliminarIncidenciaComentarioModal') eliminarIncidenciaComentarioModal;

  @ViewChild('seleccionarUbicacionModal') seleccionarUbicacionModal;
  @ViewChild('mostrarUbicacionModal') mostrarUbicacionModal;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService, private usuariosService: UsuariosService,
              private mapsApiLoader: MapsAPILoader, private ngZone: NgZone) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaIncidenciasComponent
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

    this.crearIncidenciaForm = this.formBuilder.group({
      'fechaIncidencia': ['', Validators.required],
      'clienteInvolucrado': ['', Validators.required],
      'cliente': ['']
    });

    this.crearPersonalIncidenciaForm = this.formBuilder.group({
      'personaInvolucrada': ['', Validators.required]
    });

    this.crearArmaIncidenciaForm = this.formBuilder.group({
      'armaInvolucrada': ['', Validators.required]
    });

    this.crearVehiculoIncidenciaForm = this.formBuilder.group({
      'vehiculoInvolucrado': ['', Validators.required]
    });

    this.crearCanIncidenciaForm = this.formBuilder.group({
      'canInvolucrado': ['', Validators.required]
    });

    this.cambiarAsignacionTicketForm = this.formBuilder.group({
      'asignado': ['', Validators.required]
    })

    this.responderIncidenciaForm = this.formBuilder.group({
      'status': ['', Validators.required]
    })

    this.crearArchivoIncidenciaForm = this.formBuilder.group({
      'archivo': ['', Validators.required]
    })

    this.empresaService.obtenerClientes(this.uuid).subscribe((data: Cliente[]) => {
      this.clientes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerArmas(this.uuid).subscribe((data: Arma[]) => {
      this.armas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerCanes(this.uuid).subscribe((data: Can[]) => {
      this.canes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los canes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerVehiculos(this.uuid).subscribe((data: Vehiculo[]) => {
      this.vehiculos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personales = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los personales. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerIncidenciasPorEmpresa(this.uuid).subscribe((data: Incidencia[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las incidencias. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.usuariosService.obtenerUsuariosInternos().subscribe((data: Usuario[]) => {
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
    this.mostrarModalDetalles(rowData.rowData, this.mostrarIncidenciaDetallesModal)
  }

  cambiarStatus(rowData) {
    console.log(rowData);
    this.empresaService.obtenerIncidenciaPorUuid(this.uuid, rowData.rowData.uuid).subscribe((data: Incidencia) => {
      this.incidencia = data;
      this.mostrarModalResponder();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  cambiarAsignado(rowData) {
    this.empresaService.obtenerIncidenciaPorUuid(this.uuid, rowData.rowData.uuid).subscribe((data: Incidencia) => {
      this.incidencia = data;
      this.mostrarModalAsignar();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  agregarArchivo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se estan guardando los cambios",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);

    this.empresaService.agregarArchivoIncidencia(this.uuid, this.incidencia.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la incidencia con exito`,
        ToastType.SUCCESS
      );
      this.conmutarAgregarArchivoForm();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `No se ha podido descargar el archivo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarArchivo(uuid) {
    this.empresaService.descargarArchivoIncidencia(this.uuid, this.incidencia.uuid, uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "incidencia" + this.incidencia.uuid;
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `No se ha podido descargar el archivo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  agregarComentario(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    if(this.editorData.length < 30) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El campo descripcion esta muy corto o vacio`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se estan guardando los cambios",
      ToastType.INFO
    );

    let formValue: Incidencia = form.value;
    formValue.comentarios = [];

    let comentario = new IncidenciaComentario();
    comentario.comentario = this.editorData;

    formValue.comentarios.push(comentario);
    this.empresaService.agregarComentario(this.uuid, this.incidencia.uuid, formValue).subscribe((data: Incidencia) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha agregado el comentario con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido agregar el comentario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

  }

  confirmarAsignacion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se estan guardando los cambios",
      ToastType.INFO
    );

    let formValue = form.value;
    let usuario = this.usuarios.filter(x => x.uuid === formValue.asignado)[0];

    this.empresaService.asignarIncidencia(this.uuid, this.incidencia.uuid, usuario).subscribe((data: Incidencia) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha asignado la incidencia",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido asignar la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  mostrarModalEliminarArchivo(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarIncidenciaArchivoModal, {size: "lg"})
  }

  mostrarModalEliminarPersona(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarIncidenciaPersonaModal, {size: "lg"})
  }

  mostrarModalEliminarArma(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarIncidenciaArmaModal, {size: "lg"})
  }

  mostrarModalEliminarCan(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarIncidenciaCanModal, {size: "lg"})
  }

  mostrarModalEliminarVehiculo(uuid) {
    this.tempUuid = uuid;

    this.modal = this.modalService.open(this.eliminarIncidenciaVehiculoModal, {size: "lg"})
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalAsignar() {
    this.modal = this.modalService.open(this.seleccionarAsignadoModal, {size: 'lg'})
  }

  mostrarModalResponder() {
    this.modal = this.modalService.open(this.responderIncidenciaModal, {size: 'lg'})
  }

  cambiarInvolucramientoCliente(target) {
    this.clienteInvolucrado = target.value === 'true';
  }

  autoasignar() {

  }

  conmutarAgregarPersonalForm() {
    this.mostrarAgregarPersonalForm = !this.mostrarAgregarPersonalForm;
  }

  conmutarAgregarArmaForm() {
    this.mostrarAgregarArmaForm = !this.mostrarAgregarArmaForm;
  }

  conmutarAgregarCanForm() {
    this.mostrarAgregarCanForm = !this.mostrarAgregarCanForm;
  }

  conmutarAgregarVehiculoForm() {
    this.mostrarAgregarVehiculoForm = !this.mostrarAgregarVehiculoForm;
  }

  conmutarAgregarArchivoForm() {
    this.mostrarAgregarArchivoForm = !this.mostrarAgregarArchivoForm;
  }

  mostrarModalDetalles(rowData, modal) {
    let uuid = rowData.uuid;
    this.empresaService.obtenerIncidenciaPorUuid(this.uuid, uuid).subscribe((data: Incidencia) => {
      this.incidencia = data;

      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  agregarArma(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    this.armasInvolucradas.push(this.armas.filter(x => x.uuid === formValue.armaInvolucrada)[0]);
    this.conmutarAgregarArmaForm();
  }

  agregarCan(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    this.canesInvolucrados.push(this.canes.filter(x => x.uuid === formValue.canInvolucrado)[0]);
    this.conmutarAgregarCanForm();
  }

  mostrarModalQuitarPersonaIncidencia(index) {
    this.tempIndex = index;
    this.modal = this.modalService.open(this.quitarIncidenciaPersonaModal, {size: "lg"})
  }

  mostrarModalQuitarArmaIncidencia(index) {
    this.tempIndex = index;
    this.modal = this.modalService.open(this.quitarIncidenciaArmaModal, {size: "lg"})
  }

  mostrarModalQuitarCanIncidencia(index) {
    this.tempIndex = index;
    this.modal = this.modalService.open(this.quitarIncidenciaCanModal, {size: "lg"})
  }

  mostrarModalQuitarVehiculoIncidencia(index) {
    this.tempIndex = index;
    this.modal = this.modalService.open(this.quitarIncidenciaVehiculoModal, {size: "lg"})
  }

  quitarPersonaIncidencia() {
    this.personalInvolucrado.splice(this.tempIndex, 1);
    this.modal.close();
  }

  quitarArmaIncidencia() {
    this.armasInvolucradas.splice(this.tempIndex, 1);
    this.modal.close();
  }

  quitarCanIncidencia() {
    this.canesInvolucrados.splice(this.tempIndex, 1);
    this.modal.close();
  }

  quitarVehiculoIncidencia() {
    this.vehiculosInvolucrados.splice(this.tempIndex, 1);
    this.modal.close();
  }

  cambiarIncidenciaActualTab(tab) {
    this.incidenciaActualTab = tab;
  }

  seleccionarCliente(event) {
    let uuid = event.value;
    this.cliente = this.clientes.filter(x => x.uuid === uuid)[0];
  }

  agregarPersonaIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario viene invalido",
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    let existePersona = this.incidencia.personasInvolucradas.filter(x => x.uuid === formValue.personaInvolucrada)

    if(existePersona.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra esta persona en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la persona en la incidencia",
      ToastType.INFO
    );

    let persona = this.personales.filter(x => x.uuid === formValue.personaInvolucrada)[0]

    this.empresaService.agregarPersonaIncidencia(this.uuid, this.incidencia.uuid, persona).subscribe((data: Persona) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo la persona a la incidencia con exito",
        ToastType.SUCCESS
      );
      this.conmutarAgregarPersonalForm();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la persona en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  agregarArmaIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario viene invalido",
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    let existeArma = this.incidencia.armasInvolucradas.filter(x => x.uuid === formValue.armaInvolucrada)

    if(existeArma.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra esta arma en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el arma en la incidencia",
      ToastType.INFO
    );

    let arma = this.armas.filter(x => x.uuid === formValue.armaInvolucrada)[0]

    this.empresaService.agregarArmaIncidencia(this.uuid, this.incidencia.uuid, arma).subscribe((data: Arma) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el arma a la incidencia con exito",
        ToastType.SUCCESS
      );
      this.conmutarAgregarArmaForm();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el arma en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  agregarCanIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario viene invalido",
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    let existeCan = this.incidencia.canesInvolucrados.filter(x => x.uuid === formValue.canInvolucrado)

    if(existeCan.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra este can en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el can en la incidencia",
      ToastType.INFO
    );

    let can = this.canes.filter(x => x.uuid === formValue.canInvolucrado)[0]

    this.empresaService.agregarCanIncidencia(this.uuid, this.incidencia.uuid, can).subscribe((data: Can) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el can a la incidencia con exito",
        ToastType.SUCCESS
      );
      this.conmutarAgregarCanForm();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el can en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalSeleccionarUbicacion() {
    this.modal = this.modalService.open(this.seleccionarUbicacionModal, {size: 'xl', backdrop: 'static'})

    this.mapsApiLoader.load().then(() => {
      this.setCurrentLocation();
      this.geoCoder = new google.maps.Geocoder()
      console.log(this.searchElementRef)
      let autocomplete = new google.maps.places.Autocomplete(this.searchElementRef.nativeElement)
      autocomplete.addListener("place_changed", () => {
        this.ngZone.run(() => {
          let place: google.maps.places.PlaceResult = autocomplete.getPlace();

          if(place.geometry === undefined || place.geometry === null) {
            return;
          }

          this.latitude = place.geometry.location.lat();
          this.longitude = place.geometry.location.lng();
          this.zoom = 12
        });
      })
    })
  }

  agregarVehiculoIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario viene invalido",
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    let existeVehiculo = this.incidencia.vehiculosInvolucrados.filter(x => x.uuid === formValue.vehiculoInvolucrado)

    if(existeVehiculo.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra este vehiculo en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el vehiculo en la incidencia",
      ToastType.INFO
    );

    let vehiculo = this.vehiculos.filter(x => x.uuid === formValue.vehiculoInvolucrado)[0]

    this.empresaService.agregarVehiculoIncidencia(this.uuid, this.incidencia.uuid, vehiculo).subscribe((data: Can) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el vehiculo a la incidencia con exito",
        ToastType.SUCCESS
      );
      this.conmutarAgregarVehiculoForm();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el vehiculo en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  agregarPersona(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    this.personalInvolucrado.push(this.personales.filter(x => x.uuid === formValue.personaInvolucrada)[0]);
    this.conmutarAgregarPersonalForm();
  }

  agregarVehiculo(form) {
    console.log(form.value);
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    this.vehiculosInvolucrados.push(this.vehiculos.filter(x => x.uuid === formValue.vehiculoInvolucrado)[0]);
    this.conmutarAgregarVehiculoForm();
  }

  cambiarPestanaInvolucramientos(pestana) {
    this.pestanaActualInvolucramiento = pestana;
  }

  guardarIncidencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos requeridos que no han sido rellenados`,
        ToastType.WARNING
      );
      return;
    }

    if(this.editorData.length < 30) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El campo descripcion esta muy corto o vacio`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos registrando la incidencia",
      ToastType.INFO
    );

    let formValue: Incidencia = form.value;
    formValue.armasInvolucradas = this.armasInvolucradas;
    formValue.vehiculosInvolucrados = this.vehiculosInvolucrados;
    formValue.personasInvolucradas = this.personalInvolucrado;
    formValue.canesInvolucrados = this.canesInvolucrados;
    formValue.comentarios = [];

    let comentario = new IncidenciaComentario();
    comentario.comentario = this.editorData;

    formValue.comentarios.push(comentario);
    formValue.cliente = this.cliente;

    if(this.longitude !== undefined) {
      formValue.longitud = this.longitude.toString()
    }

    if(this.latitude !== undefined) {
      formValue.latitud = this.latitude.toString()
    }

    let formData = new FormData();
    formData.append('incidencia', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.guardarIncidencia(this.uuid, formData).subscribe((data: Incidencia) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la incidencia con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la incidencia en la base de datos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  revelarUbicacion() {
    this.modal = this.modalService.open(this.mostrarUbicacionModal, {size: "xl", backdrop: "static"})
  }

  confirmarEliminarPersona() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la persona a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la persona de la incidencia",
      ToastType.INFO
    );

    this.empresaService.eliminarPersonaIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la persona con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La persona no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarArma() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del arma a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el arma de la incidencia",
      ToastType.INFO
    );

    this.empresaService.eliminarArmaIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El arma no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarCan() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del can a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el can de la incidencia",
      ToastType.INFO
    );

    this.empresaService.eliminarCanIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el can con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El can no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarVehiculo() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del vehiculo a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el vehiculo de la incidencia",
      ToastType.INFO
    );

    this.empresaService.eliminarVehiculoIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el vehiculo con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El vehiculo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarArchivo() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el archivo`,
      ToastType.INFO
    );

    this.empresaService.eliminarArchivoIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el archivo con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerIncidenciaPorUuid(this.uuid, this.incidencia.uuid).subscribe((data: Incidencia) => {
        this.incidencia = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la incidencia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `No se ha podido eliminar el archivo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalEditarComentario(uuid) {
    this.comentario = this.incidencia.comentarios.filter(x => x.uuid === uuid)[0];
    this.editorData = this.comentario?.comentario;
    this.modal = this.modalService.open(this.editarComentarioIncidenciaModal, {size: "xl", backdrop: "static"})
  }

  mostrarModalEliminarComentario(uuid) {
    this.tempUuid = uuid;
    this.modal = this.modalService.open(this.eliminarIncidenciaComentarioModal, {size: "lg", backdrop: "static"})
  }

  confirmarEliminarIncidencia() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el comentario`,
      ToastType.INFO
    );

    this.empresaService.eliminarComentarioIncidencia(this.uuid, this.incidencia.uuid, this.tempUuid).subscribe((data: IncidenciaComentario) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se elimino el comentario con exito",
        ToastType.SUCCESS
      );
      this.modal.close();

      this.empresaService.obtenerComentariosIncidencia(this.uuid, this.incidencia?.uuid).subscribe((data: IncidenciaComentario[]) => {
        this.incidencia.comentarios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener los comentarios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  private setCurrentLocation() {
    if('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 8;
        this.getAddress(this.latitude, this.longitude)
      }, () => {
        this.latitude = 20.6681644;
        this.longitude = -103.3482356;
        this.zoom = 8;
        this.getAddress(this.latitude, this.longitude);
      })
    }
  }

  markerDragEnd($event: google.maps.MouseEvent) {
    this.latitude = $event.latLng.lat();
    this.longitude = $event.latLng.lng();
    this.getAddress(this.latitude, this.longitude)
  }

  getAddress(latitude, longitude) {
    this.geoCoder.geocode({
      'location': {
        lat: latitude,
        lng: longitude
      }
    }, (results, status) => {
      if(status === 'OK') {
        if(results[0]) {
          this.zoom = 12;
          this.address = results[0].formatted_address;
        } else {
          window.alert("No se encontraron resultados");
        }
      } else {
        window.alert("El geolocalizador ha fallado.")
      }
    });
  }

  quitarUbicacion() {
    this.latitude = undefined;
    this.longitude = undefined;
    this.modal.close();
  }

  guardarCambiosComentario() {
    if(this.editorData.length < 30) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El campo descripcion esta muy corto o vacio`,
        ToastType.WARNING
      );
      return;
    }

    let incidenciaComentario = this.comentario;
    incidenciaComentario.comentario = this.editorData;

    this.empresaService.modificarComentarioIncidencia(this.uuid, this.incidencia.uuid, this.comentario.uuid, incidenciaComentario).subscribe((data: IncidenciaComentario) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha modificado el comentario con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerComentariosIncidencia(this.uuid, this.incidencia?.uuid).subscribe((data: IncidenciaComentario[]) => {
        this.incidencia.comentarios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los comentarios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el comentario de la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
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

  convertStringToNumber(input: string) {
    if (!input) return NaN;
    if (input.trim().length==0) {
      return NaN;
    }
    return Number(input);
  }

}
