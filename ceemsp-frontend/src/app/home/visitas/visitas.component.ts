import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../_services/empresa.service";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {EstadosService} from "../../_services/estados.service";
import {ValidacionService} from "../../_services/validacion.service";
import Empresa from "../../_models/Empresa";
import Usuario from "../../_models/Usuario";
import {ToastType} from "../../_enums/ToastType";
import {UsuariosService} from "../../_services/usuarios.service";
import Visita from "../../_models/Visita";
import {VisitaService} from "../../_services/visita.service";
import {faDownload, faTrash} from "@fortawesome/free-solid-svg-icons";
import {CalleService} from "../../_services/calle.service";
import Estado from "../../_models/Estado";
import Calle from "../../_models/Calle";
import Municipio from "../../_models/Municipio";
import Colonia from "../../_models/Colonia";
import Localidad from "../../_models/Localidad";

@Component({
  selector: 'app-visitas',
  templateUrl: './visitas.component.html',
  styleUrls: ['./visitas.component.css']
})
export class VisitasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faTrash = faTrash;
  faDownload = faDownload;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Tipo', field: 'tipoVisita', sortable: true, filter: true },
    {headerName: 'Num. Orden', field: 'numeroOrden', sortable: true, filter: true},
    {headerName: 'Fecha de visita', field: 'fechaVisita', sortable: true, filter: true}
  ];
  rowData = [];

  uuid: string;

  tempFile;
  closeResult: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  pestanaActual: string = 'DETALLES';

  crearVisitaForm: FormGroup;
  crearRequerimientoForm: FormGroup;
  crearArchivoVisitaForm: FormGroup;

  tipoVisita: string = undefined;
  existeEmpresa: boolean;
  hayRequerimiento: boolean;
  showArchivoForm: boolean = false;

  anio = new Date().getFullYear();

  tempUuid: string;

  empresas: Empresa[] = [];
  usuarios: Usuario[] = [];

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

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

  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;

  visita: Visita;
  empresa: Empresa;
  usuario: Usuario;

  obtenerCallesTimeout = undefined;

  editorData: string = "<p>Favor de escribir con detalle el requerimiento. Puede utilizar los botones arriba para darle formato al documento</p>"

  @ViewChild("crearVisitaModal") crearVisitaModal;
  @ViewChild("mostrarVisitaDetallesModal") mostrarVisitaDetallesModal;
  @ViewChild("modificarRequerimientoModal") modificarRequerimientoModal;
  @ViewChild("eliminarVisitaModal") eliminarVisitaModal;
  @ViewChild("eliminarVisitaArchivoModal") eliminarVisitaArchivoModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private estadoService: EstadosService,
              private validacionService: ValidacionService, private usuarioService: UsuariosService,
              private visitaService: VisitaService, private calleService: CalleService) { }

  ngOnInit(): void {
    this.crearVisitaForm = this.formBuilder.group({
      empresa: [''],
      responsable: ['', [Validators.required]],
      tipoVisita: ['', [Validators.required]],
      numeroRegistro: [''],
      numeroOrden: ['', [Validators.required]],
      fechaVisita: ['', [Validators.required]],
      existeEmpresa: [''],
      nombreComercial: ['', [Validators.required]],
      razonSocial: ['', [Validators.required]],
      numeroExterior: [''],
      numeroInterior: [''],
      codigoPostal: [''],
      domicilio4: ['']
    })

    this.crearRequerimientoForm = this.formBuilder.group({
      requerimiento: ['', Validators.required],
      fechaTermino: ['', Validators.required]
    })

    this.crearArchivoVisitaForm = this.formBuilder.group({
      file: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.visitaService.obtenerVisitas().subscribe((data: Visita[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las visitas. Motivo: ${error}`,
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

    this.usuarioService.obtenerUsuarios().subscribe((data: Usuario[]) => {
      this.usuarios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los usuarios. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

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
        `No se pudieron descargar las calles. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  seleccionarEstado(estadoUuid) {
    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
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
  }

  seleccionarMunicipio(municipioUuid) {
    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

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

  cambiarExistenciaRequerimiento(event) {
    this.hayRequerimiento = (event.value === 'true');
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    //this.modal = this.modalService.open(showCustomerDetailsModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.uuid = data.uuid;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  mostrarModalDetalles(rowData) {
    let visitaUuid = rowData.uuid;
    this.visitaService.obtenerVisitaPorUuid(visitaUuid).subscribe((data: Visita) => {
      this.visita = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la visita. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.modal = this.modalService.open(this.mostrarVisitaDetallesModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', scrollable: true});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  cambiarExistenciaEmpresa(event) {
    this.existeEmpresa = (event.value === 'true');

    if(this.existeEmpresa) {
      this.crearVisitaForm.controls['numeroRegistro'].disable();
      this.crearVisitaForm.controls['nombreComercial'].disable();
      this.crearVisitaForm.controls['razonSocial'].disable();
    } else {
      this.crearVisitaForm.controls['numeroRegistro'].enable();
      this.crearVisitaForm.controls['nombreComercial'].enable();
      this.crearVisitaForm.controls['razonSocial'].enable();
    }
  }

  cambiarTipoVisita(event) {
    this.tipoVisita = event.value;

    if(this.tipoVisita === 'EXTRAORDINARIA') {
      this.crearVisitaForm.controls['numeroRegistro'].enable();
      this.crearVisitaForm.controls['nombreComercial'].enable();
      this.crearVisitaForm.controls['razonSocial'].enable();
    } else {
      this.crearVisitaForm.controls['numeroRegistro'].disable();
      this.crearVisitaForm.controls['nombreComercial'].disable();
      this.crearVisitaForm.controls['razonSocial'].disable();
    }
  }

  mostrarModalModificar() {

  }

  mostrarModalRequerimiento() {
    this.crearRequerimientoForm.patchValue({
      requerimiento: this.visita.requerimiento,
      fechaTermino: this.visita.fechaTermino
    })
    this.hayRequerimiento = this.visita.requerimiento;
    this.editorData = this.visita.detallesRequerimiento;

    this.modal = this.modalService.open(this.modificarRequerimientoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarVisita() {
    this.modal = this.modalService.open(this.eliminarVisitaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarArchivo(tempUuid) {
    this.tempUuid = tempUuid;
    this.modal = this.modalService.open(this.eliminarVisitaArchivoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalNuevaVisita() {
    this.modal = this.modalService.open(this.crearVisitaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  seleccionarUsuario(event) {
    let uuid = event.value;
    this.usuario = this.usuarios.filter(x => x.uuid === uuid)[0];
  }

  seleccionarEmpresa(event) {
    let uuid = event.value;
    this.empresa = this.empresas.filter(x => x.uuid === uuid)[0];
    this.crearVisitaForm.patchValue({
      nombreComercial: this.empresa.nombreComercial,
      razonSocial: this.empresa.razonSocial,
      numeroRegistro: this.empresa.registro
    })
  }

  confirmarEliminarArchivoVisita() {
    if(this.tempUuid === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del archivo a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el archivo de la visita",
      ToastType.INFO
    );

    this.visitaService.eliminarArchivoVisita(this.visita.uuid, this.tempUuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el archivo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el archivo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarVisita() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando la visita",
      ToastType.INFO
    );

    this.visitaService.eliminarVisita(this.visita.uuid).subscribe((data: Visita) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la visita con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la visita. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarArchivo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos pendientes que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el archivo de la visita",
      ToastType.INFO
    );

    let formValue = form.value;
    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('metadataArchivo', JSON.stringify(formValue));

    this.visitaService.guardarArchivoVisita(this.visita.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la fotografia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la fotografia. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarFormularioNuevoArchivo() {
    this.showArchivoForm = !this.showArchivoForm;
  }

  descargarArchivo(uuid) {
    this.visitaService.descargarArchivoVisita(this.visita.uuid, uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "archivo";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el archivo de la visita`,
        ToastType.ERROR
      )
    })
  }

  guardarCambiosRequerimiento(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos no llenados aun",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Guadando el detalle del requerimiento.",
      ToastType.INFO
    )

    let value: Visita = form.value;

    if(this.hayRequerimiento) {
      value.requerimiento = true;
      value.detallesRequerimiento = this.editorData;
    } else {
      value.requerimiento = false;
      value.detallesRequerimiento = "";
      value.fechaTermino = "";
    }

    this.visitaService.modificarVisitaRequerimiento(this.visita.uuid, value).subscribe((data: Visita) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el requerimiento",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la informacion del requerimiento. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarVisita(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos que faltan por rellenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la visita",
      ToastType.INFO
    );

    let formValue: Visita = form.value;

    if(this.tipoVisita === 'ORDINARIA' || (this.tipoVisita === 'EXTRAORDINARIA' && this.existeEmpresa)) {
      formValue.nombreComercial = this.empresa.nombreComercial;
      formValue.razonSocial = this.empresa.razonSocial;
      formValue.numeroRegistro = this.empresa.registro;
    }
    formValue.empresa = this.empresa;
    formValue.responsable = this.usuario;
    formValue.numeroOrden = `CESP/EXT/${formValue.numeroOrden}/${this.anio}`

    this.visitaService.guardarVisita(formValue).subscribe((data: Visita) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la visita con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la visita. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
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
