import {Component, NgZone, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import Persona from "../../_models/Persona";
import Vehiculo from "../../_models/Vehiculo";
import Can from "../../_models/Can";
import Arma from "../../_models/Arma";
import {EmpresaService} from "../../_services/empresa.service";
import Cliente from "../../_models/Cliente";
import {ToastType} from "../../_enums/ToastType";

import {faTrash, faDownload} from "@fortawesome/free-solid-svg-icons";
import Incidencia from "../../_models/Incidencia";
import {EmpresaPersonalService} from "../../_services/empresa-personal.service";
import {EmpresaClientesService} from "../../_services/empresa-clientes.service";
import {EmpresaArmasService} from "../../_services/empresa-armas.service";
import {EmpresaCanesService} from "../../_services/empresa-canes.service";
import {EmpresaVehiculosService} from "../../_services/empresa-vehiculos.service";
import IncidenciaComentario from "../../_models/IncidenciaComentario";
import {EmpresaIncidenciasService} from "../../_services/empresa-incidencias.service";
import Usuario from "../../_models/Usuario";
import {UsuariosService} from "../../_services/usuarios.service";
import ClienteDomicilio from "../../_models/ClienteDomicilio";
import {AgmGeocoder, MapsAPILoader} from "@agm/core";
import GeocoderResult = google.maps.GeocoderResult;
import {BotonEmpresaCanesComponent} from "../../_components/botones/boton-empresa-canes/boton-empresa-canes.component";
import {
  BotonEmpresaIncidenciasComponent
} from "../../_components/botones/boton-empresa-incidencias/boton-empresa-incidencias.component";

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
    {headerName: 'Numero', field: 'numero', sortable: true, filter: true, resizable: true },
    {headerName: 'Asignado', sortable: true, filter: true, resizable: true, valueGetter: function (params) {if(params.data.asignado === null) { return 'Sin asignar' } return params.data.asignado?.nombres + " " + params.data.asignado?.apellidos} },
    {headerName: 'Fecha de incidencia', field: 'fechaIncidencia', sortable: true, filter: true, resizable: true },
    {headerName: 'Fecha de captura', field: 'fechaCreacion', sortable: true, filter: true, resizable: true },
    {headerName: 'Status', field: 'status', sortable: true, filter: true, resizable: true },
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer',resizable: true, cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];
  rowData = [];

  tempFile;

  uuid: string;
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

  mostrarAgregarCanForm: boolean = false;
  mostrarAgregarVehiculoForm: boolean = false;
  mostrarAgregarArmaForm: boolean = false;
  mostrarAgregarPersonalForm: boolean = false;
  mostrarAgregarArchivoForm: boolean = false;

  pestanaActualInvolucramiento: string = 'PERSONAL';
  incidenciaActualTab: string = 'COMENTARIOS';

  editorData: string = "<p>Favor de escribir con detalle el relato de la incidencia</p>"

  incidencia: Incidencia;
  geocodeResult;
  usuarioActual: Usuario;
  tempUuid: string;
  fechaHoyDate = new Date()
  fechaDeHoy = new Date(this.fechaHoyDate.getFullYear(), this.fechaHoyDate.getMonth(), this.fechaHoyDate.getDate()).toISOString().split('T')[0];
  fechaTresDiasAntes = new Date(this.fechaHoyDate.getFullYear(), this.fechaHoyDate.getMonth(), this.fechaHoyDate.getDate() - 3).toISOString().split('T')[0];
  clienteDomicilio: ClienteDomicilio;
  clienteDomicilios: ClienteDomicilio[] = [];
  ubicacionCliente: boolean = false;
  latitude: number;
  longitude: number;
  zoom: number;
  tempIndex: number;
  private geoCoder;
  address: string;

  @ViewChild('busquedaDireccion', {static: true}) searchElementRef;
  @ViewChild('responderIncidenciaModal') responderIncidenciaModal;
  @ViewChild('mostrarIncidenciaDetallesModal') mostrarIncidenciaDetallesModal;
  @ViewChild('eliminarIncidenciaPersonaModal') eliminarIncidenciaPersonaModal;
  @ViewChild('eliminarIncidenciaArmaModal') eliminarIncidenciaArmaModal;
  @ViewChild('eliminarIncidenciaCanModal') eliminarIncidenciaCanModal;
  @ViewChild('eliminarIncidenciaVehiculoModal') eliminarIncidenciaVehiculoModal;
  @ViewChild('eliminarIncidenciaArchivoModal') eliminarIncidenciaArchivoModal;
  @ViewChild('mostrarUbica cionModal') mostrarUbicacionModal;
  @ViewChild('mostrarUbicacionClienteModal') mostrarUbicacionClienteModal;
  @ViewChild('seleccionarUbicacionModal') seleccionarUbicacionModal;
  @ViewChild('quitarIncidenciaPersonaModal') quitarIncidenciaPersonaModal;
  @ViewChild('quitarIncidenciaArmaModal') quitarIncidenciaArmaModal;
  @ViewChild('quitarIncidenciaCanModal') quitarIncidenciaCanModal;
  @ViewChild('quitarIncidenciaVehiculoModal') quitarIncidenciaVehiculoModal;



  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaPersonalService: EmpresaPersonalService, private empresaClientesService: EmpresaClientesService,
              private empresaArmaService: EmpresaArmasService, private empresaCanesService: EmpresaCanesService,
              private empresaVehiculoService: EmpresaVehiculosService, private empresaIncidenciaService: EmpresaIncidenciasService,
              private usuarioService: UsuariosService, private geocodeService: AgmGeocoder,
              private mapsApiLoader: MapsAPILoader, private ngZone: NgZone,) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaIncidenciasComponent
    }

    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearIncidenciaForm = this.formBuilder.group({
      'fechaIncidencia': ['', Validators.required],
      'clienteInvolucrado': ['', Validators.required],
      'cliente': [''],
      'clienteDomicilio': ['']
    });

    this.crearPersonalIncidenciaForm = this.formBuilder.group({
      'personaInvolucrada': ['', Validators.required]
    });

    this.crearArmaIncidenciaForm = this.formBuilder.group({
      'armaInvolucrada': ['', Validators.required],
      'status': ['', Validators.required]
    });

    this.crearVehiculoIncidenciaForm = this.formBuilder.group({
      'vehiculoInvolucrado': ['', Validators.required]
    });

    this.crearCanIncidenciaForm = this.formBuilder.group({
      'canInvolucrado': ['', Validators.required]
    });

    this.responderIncidenciaForm = this.formBuilder.group({
      'status': ['']
    })

    this.crearArchivoIncidenciaForm = this.formBuilder.group({
      'archivo': ['', Validators.required]
    })

    this.empresaClientesService.obtenerClientes().subscribe((data: Cliente[]) => {
      this.clientes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaArmaService.obtenerArmas().subscribe((data: Arma[]) => {
      this.armas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaCanesService.obtenerCanes().subscribe((data: Can[]) => {
      this.canes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los canes. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaVehiculoService.obtenerVehiculos().subscribe((data: Vehiculo[]) => {
      this.vehiculos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaPersonalService.obtenerPersonal().subscribe((data: Persona[]) => {
      this.personales = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los personales. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaIncidenciaService.obtenerIncidenciasPorEmpresa().subscribe((data: Incidencia[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las incidencias. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

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

    this.empresaIncidenciaService.agregarArchivoIncidencia(this.incidencia.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha guardado la incidencia con exito`,
        ToastType.SUCCESS
      );
      this.tempFile = undefined;
      this.conmutarAgregarArchivoForm();
      this.empresaIncidenciaService.obtenerIncidenciaPorUuid(this.incidencia.uuid).subscribe((data: Incidencia) => {
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

  agregarComentario(form) {
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

    let formValue: Incidencia = form.value;
    formValue.comentarios = [];

    let comentario = new IncidenciaComentario();
    comentario.comentario = this.editorData;
    formValue.status = "ACCION_PENDIENTE"

    formValue.comentarios.push(comentario);

    let formData = new FormData();
    formData.append('comentario', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaIncidenciaService.agregarComentario(this.incidencia.uuid, formData).subscribe((data: Incidencia) => {
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

  descargarArchivo(uuid) {
    this.empresaIncidenciaService.descargarArchivoIncidencia(this.incidencia.uuid, uuid).subscribe((data) => {
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

  mostrarModalResponder() {
    this.modal = this.modalService.open(this.responderIncidenciaModal, {size: 'lg'})
  }

  cambiarInvolucramientoCliente(target) {
    this.clienteInvolucrado = target.value === 'true';
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

  mostrarModalDetalles(rowData) {
    let uuid = rowData.uuid;
    this.empresaIncidenciaService.obtenerIncidenciaPorUuid(uuid).subscribe((data: Incidencia) => {
      this.incidencia = data;

      this.modal = this.modalService.open(this.mostrarIncidenciaDetallesModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

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

    let existeArma = this.armasInvolucradas.filter(x => x.uuid === formValue.armaInvolucrada)

    if(existeArma.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra esta arma en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    let arma: Arma = this.armas.filter(x => x.uuid === formValue.armaInvolucrada)[0];
    console.log(formValue);
    arma.status = formValue.status;

    this.armasInvolucradas.push(arma);
    form.reset();
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

    let existeCan = this.canesInvolucrados.filter(x => x.uuid === formValue.canInvolucrado)

    if(existeCan.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra este can en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    this.canesInvolucrados.push(this.canes.filter(x => x.uuid === formValue.canInvolucrado)[0]);
    form.reset();
    this.conmutarAgregarCanForm();
  }

  cambiarIncidenciaActualTab(tab) {
    this.incidenciaActualTab = tab;
  }

  seleccionarCliente(event) {
    let uuid = event.value;
    this.cliente = this.clientes.filter(x => x.uuid === uuid)[0]
    this.empresaClientesService.obtenerClienteDomicilios(this.cliente?.uuid).subscribe((data: ClienteDomicilio[]) => {
      this.clienteDomicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  revelarUbicacion() {
    this.modal = this.modalService.open(this.mostrarUbicacionModal, {size: "xl", backdrop: "static"})
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

    this.empresaIncidenciaService.agregarPersonaIncidencia(this.incidencia.uuid, persona).subscribe((data: Persona) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo la persona a la incidencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la persona en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cerrarModalIncidencia(modal) {
    this.incidencia = undefined;
    this.canesInvolucrados = [];
    this.armasInvolucradas = [];
    this.personalInvolucrado = [];
    this.vehiculosInvolucrados = [];
    modal.close();
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

    let arma: Arma = this.armas.filter(x => x.uuid === formValue.armaInvolucrada)[0]
    arma.status = formValue.status;

    this.empresaIncidenciaService.agregarArmaIncidencia(this.incidencia.uuid, arma).subscribe((data: Arma) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el arma a la incidencia con exito",
        ToastType.SUCCESS
      );
      this.conmutarAgregarArmaForm();
      form.reset();
      this.empresaArmaService.obtenerArmas().subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener las armas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
      this.empresaIncidenciaService.obtenerIncidenciaPorUuid(this.incidencia.uuid).subscribe((data: Incidencia) => {
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

    this.empresaIncidenciaService.agregarCanIncidencia(this.incidencia.uuid, can).subscribe((data: Can) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el can a la incidencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el can en la incidencia. Motivo: ${error}`,
        ToastType.ERROR
      );
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

    this.empresaIncidenciaService.agregarVehiculoIncidencia(this.incidencia.uuid, vehiculo).subscribe((data: Can) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el vehiculo a la incidencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

    let existePersona = this.personalInvolucrado.filter(x => x.uuid === formValue.personaInvolucrada)

    if(existePersona.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra esta persona en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    form.reset();
    this.personalInvolucrado.push(this.personales.filter(x => x.uuid === formValue.personaInvolucrada)[0]);
    this.conmutarAgregarPersonalForm();
  }

  agregarVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        'Ocurrio un problema',
        'Hay campos requeridos sin rellenar. Favor de rellenarlos',
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;

    let existeVehiculo = this.vehiculosInvolucrados.filter(x => x.uuid === formValue.vehiculoInvolucrado)

    if(existeVehiculo.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya se encuentra este vehiculo en la incidencia",
        ToastType.WARNING
      );
      return;
    }

    form.reset();
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
        `Favor de describir de manera detallada la relatoria de hechos.`,
        ToastType.WARNING
      );
      return;
    }

    if(this.personalInvolucrado.length < 1) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Para registrar la incidencia es necesario involucrar a, por lo menos, una persona`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempFile === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de subir un archivo o documento fundatorio`,
        ToastType.WARNING
      );
      return;
    }

    if(this.longitude === undefined || this.latitude === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de seleccionar una ubicacion para continuar con la creacion de la incidencia`,
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
    formValue.clienteDomicilio = this.clienteDomicilio;

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

    this.empresaIncidenciaService.guardarIncidencia(formData).subscribe((data: Incidencia) => {
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

    this.empresaIncidenciaService.eliminarPersonaIncidencia(this.incidencia.uuid, this.tempUuid).subscribe((data) => {
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

    this.empresaIncidenciaService.eliminarArmaIncidencia(this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

    this.empresaIncidenciaService.eliminarCanIncidencia(this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el can con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

    this.empresaIncidenciaService.eliminarVehiculoIncidencia(this.incidencia.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el vehiculo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El vehiculo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  convertStringToNumber(input: string) {
    if (!input) return NaN;
    if (input.trim().length==0) {
      return NaN;
    }
    return Number(input);
  }

  confirmarEliminarArchivo() {

  }

  seleccionarClienteDomicilio(event) {
    let uuid = event.value;
    this.clienteDomicilio = this.clienteDomicilios.filter(x => x.uuid === uuid)[0];

    let query = `${this.clienteDomicilio?.calleCatalogo?.nombre} ${this.clienteDomicilio?.numeroExterior} ${this.clienteDomicilio?.numeroInterior} ${this.clienteDomicilio?.coloniaCatalogo.nombre} ${this.clienteDomicilio?.municipioCatalogo?.nombre} ${this.clienteDomicilio?.estadoCatalogo?.nombre}`

    this.geocodeService.geocode({
      address: query
    }).subscribe((data: GeocoderResult[]) => {
      this.geocodeResult = data[0];
      this.latitude = this.geocodeResult.geometry.location.lat();
      this.longitude = this.geocodeResult.geometry.location.lng();
      this.ubicacionCliente = true;
    }, (error) => {
      this.ubicacionCliente = false;
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `Ocurrio un problema cuando el domicilio era ubicado en el mapa. Motivo: ${error}`,
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

  mostrarModalSeleccionarUbicacion() {
    this.modal = this.modalService.open(this.seleccionarUbicacionModal, {size: 'xl', backdrop: 'static'})

    this.mapsApiLoader.load().then(() => {
      this.setCurrentLocation();
      this.geoCoder = new google.maps.Geocoder()
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

  quitarUbicacion() {
    this.latitude = undefined;
    this.longitude = undefined;
    this.modal.close();
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
      console.log(status);
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

  desactivarFecha() {
    return false;
  }

  mostrarModalVerUbicacion() {
    this.modal = this.modalService.open(this.mostrarUbicacionClienteModal, {size: 'xl', backdrop: 'static'})
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
