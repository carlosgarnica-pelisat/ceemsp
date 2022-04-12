import {Component, OnInit} from '@angular/core';
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
import {faTrash} from "@fortawesome/free-solid-svg-icons";

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

  pestanaActualInvolucramiento: string = 'PERSONAL';
  incidenciaActualTab: string = 'COMENTARIOS';

  editorData: string = "<p>Favor de escribir con detalle el relato de la incidencia</p>"

  incidencia: Incidencia;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService) { }

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
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarInvolucramientoCliente(target) {
    this.clienteInvolucrado = target.value === 'true';
  }

  modify() {

  }

  delete() {

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

  mostrarModalDetalles(rowData, modal) {
    console.log(rowData);
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

    this.empresaService.guardarIncidencia(this.uuid, formValue).subscribe((data: Incidencia) => {
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
