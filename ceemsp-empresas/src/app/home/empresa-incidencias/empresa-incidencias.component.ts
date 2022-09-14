import {Component, OnInit, ViewChild} from '@angular/core';
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

import {faTrash} from "@fortawesome/free-solid-svg-icons";
import Incidencia from "../../_models/Incidencia";
import {EmpresaPersonalService} from "../../_services/empresa-personal.service";
import {EmpresaClientesService} from "../../_services/empresa-clientes.service";
import {EmpresaArmasService} from "../../_services/empresa-armas.service";
import {EmpresaCanesService} from "../../_services/empresa-canes.service";
import {EmpresaVehiculosService} from "../../_services/empresa-vehiculos.service";
import IncidenciaComentario from "../../_models/IncidenciaComentario";
import {EmpresaIncidenciasService} from "../../_services/empresa-incidencias.service";

@Component({
  selector: 'app-empresa-incidencias',
  templateUrl: './empresa-incidencias.component.html',
  styleUrls: ['./empresa-incidencias.component.css']
})
export class EmpresaIncidenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faTrash = faTrash;

  columnDefs = [
    {headerName: 'Numero', field: 'numero', sortable: true, filter: true },
    {headerName: 'Asignado', field: 'asignado === null ? Sin asignar : asignado.nombre', sortable: true, filter: true },
    {headerName: 'Fecha', field: 'fechaIncidencia', sortable: true, filter: true },
    {headerName: 'Status', field: 'status', sortable: true, filter: true}
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

  tempUuid: string;

  @ViewChild('responderIncidenciaModal') responderIncidenciaModal;

  @ViewChild('eliminarIncidenciaPersonaModal') eliminarIncidenciaPersonaModal;
  @ViewChild('eliminarIncidenciaArmaModal') eliminarIncidenciaArmaModal;
  @ViewChild('eliminarIncidenciaCanModal') eliminarIncidenciaCanModal;
  @ViewChild('eliminarIncidenciaVehiculoModal') eliminarIncidenciaVehiculoModal;
  @ViewChild('eliminarIncidenciaArchivoModal') eliminarIncidenciaArchivoModal;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaPersonalService: EmpresaPersonalService, private empresaClientesService: EmpresaClientesService,
              private empresaArmaService: EmpresaArmasService, private empresaCanesService: EmpresaCanesService,
              private empresaVehiculoService: EmpresaVehiculosService, private empresaIncidenciaService: EmpresaIncidenciasService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

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

    this.responderIncidenciaForm = this.formBuilder.group({
      'status': ['', Validators.required]
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

    formValue.comentarios.push(comentario);
    this.empresaIncidenciaService.agregarComentario(this.incidencia.uuid, formValue).subscribe((data: Incidencia) => {
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

  mostrarModalDetalles(rowData, modal) {
    let uuid = rowData.uuid;
    this.empresaIncidenciaService.obtenerIncidenciaPorUuid(uuid).subscribe((data: Incidencia) => {
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

  quitarPersonaIncidencia(index) {
    this.personalInvolucrado.splice(index, 1);
  }

  quitarArmaIncidencia(index) {
    this.armasInvolucradas.splice(index, 1);
  }

  quitarCanIncidencia(index) {
    this.canesInvolucrados.splice(index, 1);
  }

  quitarVehiculoIncidencia(index) {
    this.vehiculosInvolucrados.splice(index, 1);
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

    this.empresaIncidenciaService.agregarArmaIncidencia(this.incidencia.uuid, arma).subscribe((data: Arma) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se guardo el arma a la incidencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

  confirmarEliminarArchivo() {

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
